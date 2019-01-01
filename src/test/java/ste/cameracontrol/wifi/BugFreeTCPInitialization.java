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
import ste.ptp.ip.Introduction;
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
        CameraHost host = givenStartedCamera("caco");

        CameraController controller = new CameraController();
        controller.connect("localhost");

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return !host.packets.isEmpty();
            }
        });

        PTPIPContainer packet = host.packets.get(0);
        then(packet.size).isEqualTo(44);
        then(packet.type).isEqualTo(1);
        then(packet.payload).isInstanceOf(Introduction.class);

        Introduction i = (Introduction)packet.payload;
        then(i.guid).isEqualTo(CameraController.CLIENT_ID);
        then(i.hostname).isEqualTo(CameraController.CLIENT_NAME);
        then(i.version).containsExactly(CameraController.CLIENT_VERSION);
    }

    // --------------------------------------------------------- private methods


    private CameraHost givenStartedCamera() throws Exception {
        return givenStartedCamera(CameraController.CLIENT_ID);
    }

    private CameraHost givenStartedCamera(String clientId) throws Exception {
        CameraHost camera = new CameraHost();
        camera.acceptedClientId = clientId;

        camera.start();

        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return camera.isOn();
            }
        });

        return camera;
    }
}
