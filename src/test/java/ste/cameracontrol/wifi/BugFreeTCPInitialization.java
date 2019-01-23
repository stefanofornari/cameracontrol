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
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.xtest.concurrent.Condition;
import ste.xtest.concurrent.WaitFor;

/**
 *
 * @author ste
 */
public class BugFreeTCPInitialization {

    public static final byte[] GUID1 = new byte[] {
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
        (byte)0x00, (byte)0x01, (byte)0xf4, (byte)0xa9,
        (byte)0x97, (byte)0xfa, (byte)0x6a, (byte)0xac
    };
    public static final byte[] GUID2 = new byte[] {
        (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04,
        (byte)0x10, (byte)0x20, (byte)0x30, (byte)0x40,
        (byte)0x0a, (byte)0x0b, (byte)0x0c, (byte)0x0d,
        (byte)0xa0, (byte)0xb0, (byte)0xc0, (byte)0xd0
    };


    @Test
    public void start_tcp_session_ok() throws Exception {
        givenStartedCamera();

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
    public void start_tcp_session_handshake_ok() throws Exception {
        final CameraHost HOST1 = givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);

        CameraController controller = new CameraController();
        controller.connect("localhost");

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return !HOST1.packets.isEmpty();
            }
        });

        //
        // Request
        //
        PTPIPContainer packet = HOST1.packets.get(0);
        then(packet.getSize()).isEqualTo(58);
        then(packet.type).isEqualTo(1);
        then(packet.payload).isInstanceOf(InitCommandRequest.class);

        InitCommandRequest i = (InitCommandRequest)packet.payload;
        then(i.guid).containsExactly(CameraController.CLIENT_ID);
        then(i.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(i.version).isEqualTo(CameraController.CLIENT_VERSION);

        //
        // response
        //
        then(controller.getConnectionNumber()).isEqualTo(0x00000001);
        then(controller.getCameraName()).isEqualTo("EOS4000D");
        then(controller.getCameraGUID()).containsExactly(GUID1);
        then(controller.getCameraSwVersion()).isEqualTo("1.0");

        final CameraHost HOST2 = givenStartedCamera("EOS1000D", CameraController.CLIENT_ID, GUID2);

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

        i = (InitCommandRequest)packet.payload;
        then(i.guid).containsExactly(CameraController.CLIENT_ID);
        then(i.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(i.version).isEqualTo(CameraController.CLIENT_VERSION);

        //
        // response
        //
        then(controller.getConnectionNumber()).isEqualTo(0x00000001);
        then(controller.getCameraName()).isEqualTo("EOS1000D");
        then(controller.getCameraGUID()).containsExactly(GUID2);
    }

    @Test
    public void connection_data_when_no_camera_is_connected() throws Exception {
        givenNoCamera();

        CameraController controller = new CameraController();

        try {
            controller.connect("localhost");
            fail("missing error");
        } catch (CameraNotAvailableException x) {
            then(x).hasMessage("Camera not available");
        }
    }

    // --------------------------------------------------------- private methods


    private CameraHost givenStartedCamera() throws Exception {
        return givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);
    }

    private CameraHost givenStartedCamera(String name, byte[] clientId, byte[] guid) throws Exception {
        CameraHost camera = new CameraHost();
        camera.acceptedClientId = clientId;
        camera.name = name;
        camera.cameraId = guid;

        camera.start();

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return camera.isOn();
            }
        });

        return camera;
    }

    private CameraHost givenNoCamera() throws Exception {
        return new CameraHost();
    }
}
