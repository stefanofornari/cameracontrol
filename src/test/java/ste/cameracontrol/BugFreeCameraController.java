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

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.io.File;
import java.lang.reflect.Method;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ste.cameracontrol.event.ConnectedEventListener;
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

    private CameraController CONTROLLER = null;
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

        CONTROLLER = CameraController.getInstance();
        CONTROLLER.initialize(CONFIG);

        new File(IMAGE_DIR, IMAGE_NAME_JPG).delete();
        new File(IMAGE_DIR, IMAGE_NAME_CR2).delete();
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
        setCameraStatus(true);
        then(CONTROLLER.isConnected()).isTrue();
    }

    @Test
    public void camera_not_connected() throws Exception {
        setCameraStatus(false);
        then(CONTROLLER.isConnected()).isFalse();
    }

    @Test
    public void no_connection_event() throws Exception {
        setCameraStatus(true);
        fireCameraConnectionEvent();

        //
        // We should have no errors
        //
    }

    @Test
    public void initialize_with_configuration() {
        Configuration c = CONTROLLER.getConfiguration();
        then(c.getImageDir()).isEqualTo(IMAGE_DIR);
    }

    @Test
    public void initialize_without_configuration() {
        try {
            CONTROLLER.initialize(null);
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
    public void device_connect_and_disconnect() throws Exception {
        setCameraStatus(true);
        CONTROLLER.startCamera();

        LibusbJava.init(new CanonEOS1000D(false));
        try {
            CONTROLLER.startCamera();
            fail("sanity check should fail at this point!");
        } catch (PTPException e) {
            //
            // Expected behaviour
            //
        }

        LibusbJava.init(new CanonEOS1000D(true));
        CONTROLLER.startCamera();
    }


    @Test
    public void connection_event() throws Exception {
        setCameraStatus(true);
        ConnectedEventListener[] listeners = {
                new ConnectedEventListener(),
                new ConnectedEventListener()
        };

        for (ConnectedEventListener l: listeners) {
            CONTROLLER.addCameraListener(l);
        }

        fireCameraConnectionEvent();

        for (ConnectedEventListener l: listeners) {
            then(l.device).isNotNull();
        }
    }

    @Test
    public void start_camera_monitor() throws Exception {
        setCameraStatus(false);
        ConnectedEventListener l = new ConnectedEventListener();

        CONTROLLER.addCameraListener(l);
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        then(l.device).isNull();

        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
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
        setCameraStatus(true);
        ConnectedEventListener l = new ConnectedEventListener();

        //
        // By default the monitor does not run
        //
        CONTROLLER.addCameraListener(l);
        Thread.sleep(100);
        then(l.device).isNull();

        //
        // Let's start the monitor now
        //
        l.device = null;
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        then(l.device).isNotNull();

        //
        // Let's stop the monitor and detach the camera
        //
        l.device = null;
        LibusbJava.init(new CanonEOS1000D(false));
        CONTROLLER.stopCameraMonior();
        Thread.sleep(100);
        then(l.device).isNull();
    }

    @Test
    public void detect_valid_device() throws Exception  {
        CanonEOS1000D devinfo = new CanonEOS1000D(true);
        LibusbJava.init(devinfo);
        CONTROLLER.initialize();

        ConnectedEventListener l = new ConnectedEventListener();
        CONTROLLER.addCameraListener(l);
        fireCameraConnectionEvent();

        then(l.device.getVendorId()).isEqualTo(devinfo.getVendorId());
        then(l.device.getProductId()).isEqualTo(devinfo.getProductId());
    }

    @Test
    public void shoot_ok() throws Exception  {
        CONTROLLER.startCamera();
        CONTROLLER.shoot();

        then(EosInitiator.invoked).contains("initiateCapture");
    }

    @Test
    public void shoot_ko() throws Exception {
        EosInitiator.shootError = true;

        try {
            CONTROLLER.startCamera();
            CONTROLLER.shoot();
            fail("Error not thrown");
        } catch (PTPException e) {
            then(e.getErrorCode()).isEqualTo(Response.GeneralError);
        }
    }

    @Test
    public void download_photo() throws Exception {
        setCameraStatus(true);
        CONTROLLER.startCamera();

        Photo photo = new Photo(IMAGE_NAME);
        CONTROLLER.downloadPhoto(1, 256, photo, false);

        then(EosInitiator.invoked).contains("getPartialObject");
        then(EosInitiator.invoked).contains("transferComplete");

        then(photo).isNotNull();
        then(photo.getName()).isEqualTo(IMAGE_NAME);
        then(photo.hasJpeg()).isTrue();
        then(photo.hasRaw()).isFalse();

        CONTROLLER.downloadPhoto(1, 256, photo, true);
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

        CONTROLLER.startCamera();
        Photo[] photos = CONTROLLER.shootAndDownload();

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

        CONTROLLER.savePhoto(photo);
        then(fjpg).doesNotExist();
        then(fraw).doesNotExist();

        photo.setJpegData(new byte[] {32});

        CONTROLLER.savePhoto(photo);
        then(fjpg).exists();
        then(fjpg.length()).isEqualTo(1);
        then(fraw).doesNotExist();

        photo.setJpegData(new byte[] {32, 64});
        photo.setRawData(new byte[] {32, 64});
        CONTROLLER.savePhoto(photo);
        then(fjpg.length()).isEqualTo(2);
        then(fraw).exists();
        then(fraw.length()).isEqualTo(2);
    }

    // --------------------------------------------------------- private methods

    private void fireCameraConnectionEvent() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("setConnected");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }

    private void setCameraStatus(boolean connected) throws Exception {
        LibusbJava.init(new CanonEOS1000D(connected));
        CONTROLLER.initialize();
    }

    private void setCameraStatus(boolean connected, Configuration c) throws Exception {
        LibusbJava.init(new CanonEOS1000D(connected));
        CONTROLLER.initialize(c);
    }


}
