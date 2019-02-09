/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ste.cameracontrol.wifi;

import ste.xtest.concurrent.Condition;
import ste.xtest.concurrent.WaitFor;

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

    public static CameraHost givenStartedCamera() throws Exception {
        return givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);
    }

    public static CameraHost givenStartedCamera(String name, byte[] clientId, byte[] guid) throws Exception {
        CameraHost camera = new CameraHost();
        camera.acceptedClientId = clientId;
        camera.name = name;
        camera.cameraId = guid;
        camera.version = "1.0";
        camera.start();
        new WaitFor(1000, new Condition() {
            @Override
            public boolean check() {
                return camera.isOn();
            }
        });
        return camera;
    }

    public static CameraHost givenCameraWithError1(int error) throws Exception {
        final CameraHost camera = givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);
        camera.error1 = error;
        return camera;
    }

    public static CameraHost givenCameraWithError2(int error) throws Exception {
        final CameraHost camera = givenStartedCamera("EOS4000D", CameraController.CLIENT_ID, GUID1);
        camera.error2 = error;
        return camera;
    }

    public static CameraHost givenNoCamera() throws Exception {
        return new CameraHost();
    }

}
