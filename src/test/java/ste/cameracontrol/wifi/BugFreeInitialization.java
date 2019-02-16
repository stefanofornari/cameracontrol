/*
 * cameracontrol
 * Copyright (C) 2018 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */
package ste.cameracontrol.wifi;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.BeforeClass;
import org.junit.Test;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.PTPException;
import ste.ptp.ip.Constants;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.InitEventRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;

/**
 *
 */
public class BugFreeInitialization {

    @BeforeClass
    public static void before_class() throws IOException {
        Socket.setSocketImplFactory(new CameraSimulatorFactory());
    }

    @Test
    public void start_tcp_session_ok() throws Exception {
        CameraUtils.givenStartedCamera();

        CameraController controller = new CameraController();
        controller.connect("localhost");

        then(controller.isConnected()).isTrue();
    }

    @Test
    public void start_tcp_session_no_camera_available() throws Exception {
        CameraUtils.givenNoCamera();

        CameraController controller = new CameraController();

        try {
            controller.connect("localhost");
            fail("connection not failed!");
        } catch (CameraNotAvailableException x) {
            then(controller.isConnected()).isFalse();
        }
    }

    @Test
    public void start_ptpip_session_handshake_ok() throws Exception {
        CameraUtils.givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, CameraUtils.GUID1);

        CameraController controller = new CameraController();
        controller.connect("localhost");

        //
        // Requests (PC -> camera)
        //
        CameraHost camera = CameraSimulatorFactory.CAMERA.get().get(0);
        PacketInputStream is = new PacketInputStream(new ByteArrayInputStream(camera.getWrittenBytes()));
        PTPIPContainer packet = is.readPTPContainer();
        then(packet.getSize()).isEqualTo(58);
        then(packet.type).isEqualTo(Constants.INIT_COMMAND_REQUEST);
        then(packet.payload).isInstanceOf(InitCommandRequest.class);

        InitCommandRequest icr = (InitCommandRequest)packet.payload;
        then(icr.guid).containsExactly(CameraController.CLIENT_ID);
        then(icr.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(icr.version).isEqualTo(CameraController.CLIENT_VERSION);

        camera = CameraSimulatorFactory.CAMERA.get().get(1);
        is = new PacketInputStream(new ByteArrayInputStream(camera.getWrittenBytes()));
        packet = is.readPTPContainer();
        then(packet.getSize()).isEqualTo(12);
        then(packet.type).isEqualTo(Constants.INIT_EVENT_REQUEST);
        then(packet.payload).isInstanceOf(InitEventRequest.class);

        InitEventRequest ier = (InitEventRequest)packet.payload;
        then(ier.sessionId).isEqualTo(1);

        //
        // Results
        //
        then(controller.getSessionId()).isEqualTo(0x00000001);
        then(controller.getCameraName()).isEqualTo("EOS4000D");
        then(controller.getCameraGUID()).containsExactly(CameraUtils.GUID1);
        then(controller.getCameraSwVersion()).isEqualTo("1.0");

        //
        // Another camera...
        //
        CameraUtils.givenStartedCamera("EOS1000D", CameraController.CLIENT_ID, CameraUtils.GUID2, "1.1", 0x0102);
        controller = new CameraController();
        controller.connect("localhost");

        //
        // Request
        //
        camera = CameraSimulatorFactory.CAMERA.get().get(0);
        is = new PacketInputStream(new ByteArrayInputStream(camera.getWrittenBytes()));
        packet = is.readPTPContainer();

        then(packet.getSize()).isEqualTo(58);
        then(packet.type).isEqualTo(1);
        then(packet.payload).isInstanceOf(InitCommandRequest.class);

        icr = (InitCommandRequest)packet.payload;
        then(icr.guid).containsExactly(CameraController.CLIENT_ID);
        then(icr.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(icr.version).isEqualTo(CameraController.CLIENT_VERSION);

        //
        // response
        //
        then(controller.getSessionId()).isEqualTo(0x0102);
        then(controller.getCameraName()).isEqualTo("EOS1000D");
        then(controller.getCameraGUID()).containsExactly(CameraUtils.GUID2);
        then(controller.getCameraSwVersion()).isEqualTo("1.1");
    }

    @Test
    public void connection_data_when_no_camera_is_connected() throws Exception {
        CameraUtils.givenNoCamera();

        CameraController controller = new CameraController();

        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (CameraNotAvailableException x) {
            then(x).hasMessage("0x00002000 camera not available");
        }
    }

    @Test
    public void command_initialization_error() throws Exception {
        CameraUtils.givenCameraWithError1(0x01020304);

        CameraController controller = new CameraController();
        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (PTPException x) {
            x.printStackTrace();
            then(x).hasMessage("0x01020304 command request error");
        }

        CameraUtils.givenCameraWithError1(0x0a0b0c0d);

        controller = new CameraController();
        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (PTPException x) {
            x.printStackTrace();
            then(x).hasMessage("0x0a0b0c0d command request error");
        }
    }

    @Test
    public void event_initialization_error() throws Exception {
        CameraUtils.givenCameraWithError2(0x01020304);

        CameraController controller = new CameraController();
        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (PTPException x) {
            then(x).hasMessage("0x01020304 event request error");
        }

        CameraUtils.givenCameraWithError2(0x0a0b0c0d);

        controller = new CameraController();
        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (PTPException x) {
            then(x).hasMessage("0x0a0b0c0d event request error");
        }
    }
}
