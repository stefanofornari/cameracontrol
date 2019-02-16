/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ste.cameracontrol.wifi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import ste.ptp.ip.InitCommandAcknowledge;
import ste.ptp.ip.InitError;
import ste.ptp.ip.InitEventAcknowledge;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketOutputStream;

/**
 *
 * @author ste
 */
public class CameraUtils {

    public static final byte[] GUID1 = new byte[] {
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x01, (byte) 0xf4, (byte) 0xa9,
        (byte) 0x97, (byte) 0xfa, (byte) 0x6a, (byte) 0xac
    };
    public static final byte[] GUID2 = new byte[] {
        (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
        (byte) 0x10, (byte) 0x20, (byte) 0x30, (byte) 0x40,
        (byte) 0x0a, (byte) 0x0b, (byte) 0x0c, (byte) 0x0d,
        (byte) 0xa0, (byte) 0xb0, (byte) 0xc0, (byte) 0xd0
    };

    public static void givenStartedCamera() throws Exception {
        givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);
    }

    public static void givenStartedCamera(String name, byte[] clientId, byte[] guid) throws Exception {
        givenStartedCamera(name, clientId, guid, "1.0", 1);
    }

    public static void givenStartedCamera(String name, byte[] clientId, byte[] guid, String version, int sessionId) throws Exception {
        reset();

        ArrayList<CameraHost> list = new ArrayList<>();
        CameraSimulatorFactory.CAMERA.set(list);

        CameraHost camera = new CameraHost();
        camera.response = toBytes(
            new PTPIPContainer(
                new InitCommandAcknowledge(sessionId, guid, name, version)
            )
        );
        list.add(camera);

        camera = new CameraHost();
        camera.response = toBytes(
            new PTPIPContainer(
                new InitEventAcknowledge()
            )
        );
        list.add(camera);
    }

    public static void givenCameraWithError1(int error) throws Exception {
        reset();

        ArrayList<CameraHost> list = new ArrayList<>();
        CameraSimulatorFactory.CAMERA.set(list);

        CameraHost camera = new CameraHost();
        camera.response = toBytes(new PTPIPContainer(new InitError(error)));

        list.add(camera);
    }

    public static void givenCameraWithError2(int error) throws Exception {
        reset();

        ArrayList<CameraHost> list = new ArrayList<>();
        CameraSimulatorFactory.CAMERA.set(list);

        CameraHost camera = new CameraHost();
        camera.response = toBytes(
            new PTPIPContainer(
                new InitCommandAcknowledge(0x01020304, GUID1, "camera", "1.0")
            )
        );
        list.add(camera);
        camera = new CameraHost();
        camera.response = toBytes(new PTPIPContainer(new InitError(error)));
        list.add(camera);
    }

    public static void givenNoCamera() throws Exception {
        reset();

        ArrayList<CameraHost> list = new ArrayList<>();
        CameraSimulatorFactory.CAMERA.set(list);

        CameraHost camera = new CameraHost();
        camera.error = new UnknownHostException();
        list.add(camera);
    }

    public static byte[] toBytes(PTPIPContainer packet) throws IOException {
        final ByteArrayOutputStream BAOS = new ByteArrayOutputStream();

        final PacketOutputStream POS = new PacketOutputStream(BAOS);
        POS.write(packet); POS.flush();

        return BAOS.toByteArray();
    }

    public static void reset() {
        List list = CameraSimulatorFactory.CAMERA.get();
        if (list != null) {
            list.clear();
        }
        CameraSimulatorFactory.step = 0;
    }

}
