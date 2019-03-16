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
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.BeforeClass;
import org.junit.Test;
import ste.ptp.Command;
import ste.ptp.ip.Constants;
import ste.ptp.ip.OperationRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;

/**
 *
 */
public class BugFreeStartRemoteSession {

    @BeforeClass
    public static void before_class() throws IOException {
        Socket.setSocketImplFactory(new CameraSimulatorFactory());
    }

    @Test
    public void start_session_ok() throws Exception {
        CameraUtils.givenStartedCameraWithSession();

        CameraController controller = new CameraController();
        controller.connect("localhost");
        controller.startRemoteSesssion();

        then(controller.getTransactionId()).isEqualTo(1);

        CameraHost camera = CameraSimulatorFactory.CAMERA.get().get(2);
        PacketInputStream is = new PacketInputStream(new ByteArrayInputStream(camera.getWrittenBytes()));
        PTPIPContainer packet = is.readPTPContainer();
        then(packet.type).isEqualTo(Constants.OPERATION_REQUEST);
        then(packet.payload).isInstanceOf(OperationRequest.class);
        then(((OperationRequest)packet.payload).operation.code).isEqualTo(Command.OpenSession);
        then(((OperationRequest)packet.payload).dataPhaseInfo).isEqualTo(0x01);
        then(((OperationRequest)packet.payload).transaction).isEqualTo(0x00);

        //
        // I have no idea what this code means, but seems to be required when
        // starting a new session on a D2000
        //
        camera = CameraSimulatorFactory.CAMERA.get().get(3);
        is = new PacketInputStream(new ByteArrayInputStream(camera.getWrittenBytes()));
        packet = is.readPTPContainer();
        then(packet.type).isEqualTo(Constants.OPERATION_REQUEST);
        then(packet.payload).isInstanceOf(OperationRequest.class);
        then(((OperationRequest)packet.payload).operation.code).isEqualTo(Command.Canon902f);
        then(((OperationRequest)packet.payload).dataPhaseInfo).isEqualTo(0x01);
        then(((OperationRequest)packet.payload).transaction).isEqualTo(0x01);


    }
}
