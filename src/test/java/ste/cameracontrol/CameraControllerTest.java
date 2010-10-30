package ste.cameracontrol;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.lang.reflect.Method;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import ste.cameracontrol.event.ConnectedEventListener;

/**
 * Unit test for simple App.
 */
public class CameraControllerTest
        extends TestCase {

    private CameraController CONTROLLER = null;

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
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void fireCameraConnectionEvent() throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("setConnected");
        m.setAccessible(true);
        m.invoke(CONTROLLER);
    }

    public void testCameraIsConnected() {
        LibusbJava.init(new CanonEOS1000D(true));
        assertTrue(new CameraController().isConnected());
    }

    public void testCameraIsNotConnected() {
        LibusbJava.init(new CanonEOS1000D(false));
        assertFalse(new CameraController().isConnected());
    }

    public void testNoFireConnectionEvent() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        CONTROLLER = new CameraController();
        fireCameraConnectionEvent();

        //
        // We should have no errors
        //
    }

    public void testFireConnectionEvent() throws Exception {
        CONTROLLER = new CameraController();
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
        CONTROLLER = new CameraController();
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
        CONTROLLER = new CameraController();
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
        
        CONTROLLER = new CameraController();
        ConnectedEventListener l = new ConnectedEventListener();
        CONTROLLER.addCameraListener(l);
        fireCameraConnectionEvent();

        assertEquals(devinfo.getVendorId(), l.device.getVendorId());
        assertEquals(devinfo.getProductId(), l.device.getProductId());
    }
}
