/*
 * cameracontrol
 * Copyright (C) 2010 Stefano Fornari
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

package ste.cameracontrol;

import java.io.File;
import java.lang.reflect.Method;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbHostManager;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ste.cameracontrol.event.ConnectedEventListener;
import ste.cameracontrol.usb.CanonEOS1000D;
import ste.cameracontrol.usb.VirtualUSBServices;
import ste.ptp.PTPException;
import ste.ptp.Response;
import ste.ptp.eos.EosEvent;
import ste.ptp.eos.EosInitiator;

/**
 *
 */
public class BugFreeCameraController {

    public final String IMAGE_DIR      = "/tmp/cameracontrol";
    public final String IMAGE_NAME     = "capture";
    public final String IMAGE_NAME_JPG = IMAGE_NAME + ".JPG";
    public final String IMAGE_NAME_CR2 = IMAGE_NAME + ".CR2";

    private Configuration    CONFIG     = null;

    @Before
    public void before() throws Exception {
        EosInitiator.shootError = false;
        EosInitiator.invoked.clear();
        EosInitiator.events.clear();
        File outDir = new File(IMAGE_DIR);
        if (!outDir.exists()) {
            outDir.mkdirs();
        } else {
            String[] files = outDir.list();
            for (int i=0; (files != null) && (i<files.length); ++i) {
                (new File(outDir, files[i])).delete();
            }
        }

        CONFIG = new Configuration();
        CONFIG.setImageDir(IMAGE_DIR);

        new File(IMAGE_DIR, IMAGE_NAME_JPG).delete();
        new File(IMAGE_DIR, IMAGE_NAME_CR2).delete();

        //
        // NOTE: we do not instatiate a global CameraController to make the
        // specs thread safe
        //
    }

    @After
    public void after() throws Exception {
        File outDir = new File(IMAGE_DIR);
        if (outDir.exists()) {
            String[] files = outDir.list();
            for (int i=0; (files != null) && (i<files.length); ++i) {
                (new File(outDir, files[i])).delete();
            }
            outDir.delete();
        }
    }

    @Test
    public void singleton() throws Exception {
        CameraController c1 = CameraController.getInstance();
        CameraController c2 = CameraController.getInstance();

        then(c1).isNotNull();
        then(c1).isSameAs(c2);
    }

    @Test
    public void camera_connected() throws Exception {
        final CameraController C = givenController();

        then(C.isConnected()).isTrue();
    }

    @Test
    public void camera_not_connected() throws Exception {
        final CameraController C = givenController(false);
        then(C.isConnected()).isFalse();
    }

    @Test
    public void no_connection_event() throws Exception {
        fireCameraConnectionEvent(givenController());

        //
        // We should have no errors
        //
    }

    @Test
    public void initialize_with_configuration() throws Exception {
        final CameraController C = givenController(false);
        Configuration c = C.getConfiguration();
        then(c.getImageDir()).isEqualTo(IMAGE_DIR);
    }

    @Test
    public void initialize_without_configuration() throws Exception {
        final CameraController C = givenController(false);
        try {
            C.initialize(null);
            fail("the configuration object cannot be null");
        } catch (IllegalArgumentException e) {
            //
            // OK!
            //
        }
    }

    /**
     * This methods tests that when the camera disconnects and reconnects, the
     * controller properly re-initialize it-self so to reflect that no camera is
     * connected.
     *
     * @throws Exception
     */
    @Test
    public void cammera_cannot_connect_twice() throws Exception {
        final CameraController C = givenController();

        C.startCamera();

        try {
            C.startCamera();
            fail("sanity check should fail at this point!");
        } catch (CameraAlreadyConnectedException e) {
            //
            // Expected behaviour
            //
        }
    }

    @Test
    public void connection_event() throws Exception {
        final CameraController C = givenController();
        ConnectedEventListener[] listeners = {
                new ConnectedEventListener(),
                new ConnectedEventListener()
        };

        for (ConnectedEventListener l: listeners) {
            C.addCameraListener(l);
        }

        C.startCamera(true); fireCameraConnectionEvent(C);

        for (ConnectedEventListener l: listeners) {
            then(l.device).isNotNull();
        }
    }

    @Test
    public void start_camera_monitor() throws Exception {
        final CameraController C = givenController(false);
        ConnectedEventListener l = new ConnectedEventListener();

        C.addCameraListener(l);
        C.startCameraMonitor();
        Thread.sleep(100);
        then(l.device).isNull();

        attachCamera(); Thread.sleep(100);
        then(l.device).isNotNull();

        //
        // It has to notify only changes
        //
        l.device = null;
        Thread.sleep(100);
        then(l.device).isNull();
    }

    @Test
    public void stop_camera_monitor() throws Exception {
        final CameraController C = givenController();
        ConnectedEventListener l = new ConnectedEventListener();

        //
        // By default the monitor does not run
        //
        C.addCameraListener(l);
        Thread.sleep(100);
        then(l.device).isNull();

        //
        // Let's start the monitor now
        //
        l.device = null;
        C.startCameraMonitor();
        Thread.sleep(100);
        then(l.device).isNotNull();

        //
        // Let's stop the monitor and detach the camera
        //
        l.device = null;
        C.stopCameraMonior();
        Thread.sleep(100);
        then(l.device).isNull();
    }

    @Test
    public void detect_valid_device() throws Exception  {
        final CameraController C = givenController(false);

        CanonEOS1000D devinfo = new CanonEOS1000D();
        C.initialize(); C.startCameraMonitor();

        ConnectedEventListener l = new ConnectedEventListener();
        C.addCameraListener(l);

        attachCamera(); Thread.sleep(250);

        UsbDeviceDescriptor dd = l.device.getUsbDeviceDescriptor();

        then(dd.idVendor()).isEqualTo(devinfo.vendorId);
        then(dd.idProduct()).isEqualTo(devinfo.productId);
    }

    @Test
    public void shoot_ok() throws Exception  {
        final CameraController C = givenController();

        C.startCamera();
        C.shoot();

        then(EosInitiator.invoked).contains("initiateCapture");
    }

    @Test
    public void shoot_ko() throws Exception {
        final CameraController C = givenController();

        EosInitiator.shootError = true;

        try {
            C.startCamera();
            C.shoot();
            fail("Error not thrown");
        } catch (PTPException e) {
            then(e.getErrorCode()).isEqualTo(Response.GeneralError);
        }
    }

    @Test
    public void download_photo() throws Exception {
        final CameraController C = givenController();

        C.startCamera();

        Photo photo = new Photo(IMAGE_NAME);
        C.downloadPhoto(1, 256, photo, false);

        then(EosInitiator.invoked).contains("getPartialObject");
        then(EosInitiator.invoked).contains("transferComplete");

        then(photo).isNotNull();
        then(photo.getName()).isEqualTo(IMAGE_NAME);
        then(photo.hasJpeg()).isTrue();
        then(photo.hasRaw()).isFalse();

        C.downloadPhoto(1, 256, photo, true);
        then(photo.hasRaw()).isTrue();
    }

    @Test
    public void shoot_and_save() throws Exception {
        EosEvent photo1 = new EosEvent();
        EosEvent photo2 = new EosEvent();

        photo1.setCode(EosEvent.EosEventObjectAddedEx);
        photo2.setCode(EosEvent.EosEventObjectAddedEx);
        photo1.setParam(1, 1); photo2.setParam(1, 1);
        photo1.setParam(5, 256); photo2.setParam(5, 256);
        photo1.setParam(6, IMAGE_NAME_JPG); photo2.setParam(6, IMAGE_NAME_CR2);

        EosInitiator.events.add(photo1); EosInitiator.events.add(photo2);

        final CameraController C = givenController();

        C.startCamera();
        Photo[] photos = C.shootAndDownload();

        then(EosInitiator.invoked).contains("initiateCapture");
        then(photos).hasSize(1);
        then(photos[0].getName()).isEqualTo(IMAGE_NAME);
        then(photos[0].getJpegData()).isNotNull();
        then(photos[0].getRawData()).isNotNull();
    }

    @Test
    public void save_photo() throws Exception {
        Photo photo = new Photo(IMAGE_NAME);
        File fjpg = new File(IMAGE_DIR, IMAGE_NAME_JPG);
        File fraw = new File(IMAGE_DIR, IMAGE_NAME_CR2);

        final CameraController C = givenController();

        C.savePhoto(photo);
        then(fjpg).doesNotExist();
        then(fraw).doesNotExist();

        photo.setJpegData(new byte[] {32});

        C.savePhoto(photo);
        then(fjpg).exists();
        then(fjpg.length()).isEqualTo(1);
        then(fraw).doesNotExist();

        photo.setJpegData(new byte[] {32, 64});
        photo.setRawData(new byte[] {32, 64});
        C.savePhoto(photo);
        then(fjpg.length()).isEqualTo(2);
        then(fraw).exists();
        then(fraw.length()).isEqualTo(2);
    }

    // --------------------------------------------------------- private methods

    private CameraController givenController() throws Exception {
        return givenController(true);
    }

    private CameraController givenController(boolean connected) throws Exception {
        final CameraController C = new CameraController(CONFIG);

        VirtualUSBServices usb = (VirtualUSBServices)UsbHostManager.getUsbServices();
        usb.setConnectionStatus(connected);

        return C;
    }

    private void fireCameraConnectionEvent(final CameraController C) throws Exception {
        Method m = C.getClass().getDeclaredMethod("setConnected");
        m.setAccessible(true);
        m.invoke(C);
    }

    private void attachCamera() throws Exception {
        VirtualUSBServices usb = (VirtualUSBServices)UsbHostManager.getUsbServices();
        usb.setConnectionStatus(true);
    }
}
