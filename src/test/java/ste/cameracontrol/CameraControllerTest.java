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
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.io.IOUtils;
import ste.cameracontrol.event.ConnectedEventListener;
import ste.ptp.PTPException;
import ste.ptp.Response;
import ste.ptp.eos.EosEvent;
import ste.ptp.eos.EosInitiator;

/**
 * Unit test for simple App.
 */
public class CameraControllerTest
        extends TestCase {

    public final String IMAGE_DIR      = "/tmp/cameracontrol";
    public final String IMAGE_NAME_JPG = "capture.jpg";
    public final String IMAGE_NAME_CR2 = "capture.cr2";
    
    private CameraController CONTROLLER = null;
    private Configuration    CONFIG     = null;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public CameraControllerTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(CameraControllerTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

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
        CONTROLLER.setConfiguration(CONFIG);
        resetController();

        new File(IMAGE_DIR, IMAGE_NAME_JPG).delete();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        File outDir = new File(IMAGE_DIR);
        if (outDir.exists()) {
            String[] files = outDir.list();
            for (int i=0; (files != null) && (i<files.length); ++i) {
                (new File(outDir, files[i])).delete();
            }
            outDir.delete();
        }
    }

    private void fireCameraConnectionEvent() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("setConnected");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }

    private void resetController() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("reset");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }

    private void sanityCheck() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("sanityCheck");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }


    public void testSingleton() throws Exception {
        CameraController c1 = CameraController.getInstance();
        CameraController c2 = CameraController.getInstance();

        assertNotNull(c1);
        assertSame(c1, c2);
    }

    public void testCameraIsConnected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        resetController();
        assertTrue(CONTROLLER.isConnected());
    }

    public void testCameraIsNotConnected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(false));
        resetController();
        assertFalse(CONTROLLER.isConnected());
    }

    public void testNoFireConnectionEvent() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        resetController();
        fireCameraConnectionEvent();

        //
        // We should have no errors
        //
    }

    public void testInitializeWithConfiguration() {
        Configuration c = CONTROLLER.getConfiguration();
        assertEquals(IMAGE_DIR, c.getImageDir());
    }

    public void testInitializeWithWrongConfiguration() {
        try {
            CONTROLLER.setConfiguration(null);
            fail("the configuration object cannot be null");
        } catch (IllegalArgumentException e) {
            //
            // OK!
            //
        }
    }

    /**
     * This methods tests that when the camera disconnects, the controller
     * properly re-initialize it-self so to reflect that no camera is connected.
     * Sanity check on it should then fail.
     * 
     * @throws Exception
     */
    public void testDeviceDisconnect() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        resetController();
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
    }
        

    public void testFireConnectionEvent() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        resetController();
        ConnectedEventListener[] listeners = {
                new ConnectedEventListener(),
                new ConnectedEventListener()
        };

        for (ConnectedEventListener l: listeners) {
            CONTROLLER.addCameraListener(l);
        }

        fireCameraConnectionEvent();

        for (ConnectedEventListener l: listeners) {
            assertNotNull(l.device);
        }
    }

    public void testStartCameraMonitor() throws Exception {
        ConnectedEventListener l = new ConnectedEventListener();

        LibusbJava.init(new CanonEOS1000D(false));
        CONTROLLER.addCameraListener(l);
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        assertNull(l.device);
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(l.device);
        //
        // It has to notify only changes
        //
        l.device = null;
        Thread.sleep(100);
        assertNull(l.device);
    }

    public void testStopCameraMonitor() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        resetController();
        ConnectedEventListener l = new ConnectedEventListener();

        //
        // By default the monitor does not run
        //
        CONTROLLER.addCameraListener(l);
        Thread.sleep(100);
        assertNull(l.device);

        //
        // Let's start the monitor now
        //
        l.device = null;
        CONTROLLER.startCameraMonitor();
        Thread.sleep(100);
        assertNotNull(l.device);

        //
        // Let's stop the monitor and detach the camera
        //
        l.device = null;
        LibusbJava.init(new CanonEOS1000D(false));
        CONTROLLER.stopCameraMonior();
        Thread.sleep(100);
        assertNull(l.device);
    }

    public void testDetectValidDevice() throws Exception  {
        CanonEOS1000D devinfo = new CanonEOS1000D(true);
        LibusbJava.init(devinfo);
        resetController();
        
        ConnectedEventListener l = new ConnectedEventListener();
        CONTROLLER.addCameraListener(l);
        fireCameraConnectionEvent();

        assertEquals(devinfo.getVendorId(), l.device.getVendorId());
        assertEquals(devinfo.getProductId(), l.device.getProductId());
    }

    public void testShootOK() throws Exception  {
        CONTROLLER.startCamera();
        CONTROLLER.shoot();

        assertTrue(EosInitiator.invoked.contains("initiateCapture"));
    }

    public void testShootKO() throws Exception {
        EosInitiator.shootError = true;

        try {
            CONTROLLER.startCamera();
            CONTROLLER.shoot();
            fail("Error not thrown");
        } catch (PTPException e) {
            assertEquals(Response.GeneralError, e.getErrorCode());
        }
    }

    public void testDownloadObject() throws Exception {
        CONTROLLER.startCamera();

        Photo photo = CONTROLLER.downloadPhoto(1, 256, IMAGE_NAME_JPG);

        assertTrue(EosInitiator.invoked.contains("getPartialObject"));
        assertTrue(EosInitiator.invoked.contains("transferComplete"));

        assertNotNull(photo);
        assertEquals(IMAGE_NAME_JPG, photo.getName());
    }

    public void testShootAndCapture() throws Exception {
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

        assertTrue(EosInitiator.invoked.contains("initiateCapture"));
        assertEquals(2, photos.length);
        assertEquals(IMAGE_NAME_JPG, photos[0].getName());
        assertEquals(IMAGE_NAME_CR2, photos[1].getName());
    }

    public void testSavePhoto() throws Exception {
        byte[] data = IOUtils.toByteArray(ClassLoader.getSystemResourceAsStream("images/about.png"));
        Photo photo = new Photo(IMAGE_NAME_JPG, data);

        CONTROLLER.savePhoto(photo);

        File f = new File(IMAGE_DIR, IMAGE_NAME_JPG);
        assertTrue(f.exists());
        assertEquals(179083, f.length());

    }
}
