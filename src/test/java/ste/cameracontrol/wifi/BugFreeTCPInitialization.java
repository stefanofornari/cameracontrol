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

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.PTPException;
import ste.ptp.ip.Constants;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.InitEventRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.xtest.concurrent.Condition;
import ste.xtest.concurrent.WaitFor;

/**
 *
 * @author ste
 */
public class BugFreeTCPInitialization {



    @Test
    public void start_tcp_session_ok() throws Exception {
        CameraUtils.givenStartedCamera();

        CameraController controller = new CameraController();
        controller.connect("localhost");

        then(controller.isConnected()).isTrue();
    }

    @Test
    public void start_tcp_session_no_camera_available() throws Exception {
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
        final CameraHost HOST1 = CameraUtils.givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, CameraUtils.GUID1);

        CameraController controller = new CameraController();
        controller.connect("localhost");

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return !HOST1.packets.isEmpty();
            }
        });

        //
        // Requests (PC -> camera)
        //
        PTPIPContainer packet = HOST1.packets.get(0);
        then(packet.getSize()).isEqualTo(58);
        then(packet.type).isEqualTo(Constants.PacketType.INIT_COMMAND_REQUEST.type());
        then(packet.payload).isInstanceOf(InitCommandRequest.class);

        InitCommandRequest icr = (InitCommandRequest)packet.payload;
        then(icr.guid).containsExactly(CameraController.CLIENT_ID);
        then(icr.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(icr.version).isEqualTo(CameraController.CLIENT_VERSION);

        packet = HOST1.packets.get(1);
        then(packet.getSize()).isEqualTo(12);
        then(packet.type).isEqualTo(Constants.PacketType.INIT_EVENT_REQUEST.type());
        then(packet.payload).isInstanceOf(InitEventRequest.class);

        InitEventRequest ier = (InitEventRequest)packet.payload;
        then(ier.sessionId).isEqualTo(HOST1.sessionId);

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

        final CameraHost HOST2 = CameraUtils.givenStartedCamera("EOS1000D", CameraController.CLIENT_ID, CameraUtils.GUID2);
        HOST2.sessionId = 10;
        HOST2.version = "1.1";

        controller = new CameraController();
        controller.connect("localhost");

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return !HOST2.packets.isEmpty();
            }
        });

        //
        // Request
        //
        packet = HOST2.packets.get(0);
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
        then(controller.getSessionId()).isEqualTo(11);
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

    @Test(timeout = 5000)
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

    @Test(timeout = 5000)
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
