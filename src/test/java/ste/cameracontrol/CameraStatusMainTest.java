/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.lang.reflect.Field;
import junit.framework.TestCase;

import ste.cameracontrol.ui.CameraControlWindow;

/**
 *
 * @author ste
 */
public class CameraStatusMainTest extends TestCase {

    private CameraStatusMain cameraStatus = null;
    private CameraControlWindow window = null;
    
    public CameraStatusMainTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LibusbJava.init(new CanonEOS1000D(false));

        cameraStatus = new CameraStatusMain();

        window = getWindow();

        //
        // status monitor should be now running
        //
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private CameraControlWindow getWindow() throws Exception {
        Field f = CameraStatusMain.class.getDeclaredField("window");
        f.setAccessible(true);

        return (CameraControlWindow)f.get(cameraStatus);
    }

    public void testCameraConnected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(window.status);
    }

    public void testCameraDisconnected() throws Exception {
        //
        // we need to simulate a connection otherwise the status will not change
        //
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        window.status = null;
        LibusbJava.init(new CanonEOS1000D(false));
        Thread.sleep(100);
        assertNotNull(window.status);
    }

    public void testCameraNameDetected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(window.camera);
        assertTrue("found " + window.camera, window.camera.indexOf("1000D") >= 0);
    }

}
