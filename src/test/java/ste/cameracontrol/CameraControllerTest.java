package ste.cameracontrol;

import ch.ntb.usb.Canon1000D;
import ch.ntb.usb.LibusbJava;
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

        CONTROLLER = new CameraController();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private void fireCameraConnectionEvent(boolean status) throws Exception {
        Method m = CONTROLLER.getClass().getDeclaredMethod("setConnected", boolean.class);
        m.setAccessible(true);
        m.invoke(CONTROLLER, status);
    }

    public void testCameraIsConnected() {
        LibusbJava.init(new Canon1000D(true));
        assertTrue(CONTROLLER.isConnected());
    }

    public void testCameraIsNotConnected() {
        LibusbJava.init(new Canon1000D(false));
        assertFalse(CONTROLLER.isConnected());
    }

    public void testNoFireConnectionEvent() throws Exception {
        fireCameraConnectionEvent(true);

        //
        // We should have no errors
        //
    }

    public void testFireConnectionEvent() throws Exception {
        ConnectedEventListener[] listeners = {
                new ConnectedEventListener(),
                new ConnectedEventListener()
        };

        for (ConnectedEventListener l: listeners) {
            CONTROLLER.addCameraListener(l);
        }

        fireCameraConnectionEvent(true);

        for (ConnectedEventListener l: listeners) {
            assertTrue(l.fired);
        }
    }

    public void testStartCameraMonitor() throws Exception {
        ConnectedEventListener l = new ConnectedEventListener();

        LibusbJava.init(new Canon1000D(false));
        CONTROLLER.addCameraListener(l);
        CONTROLLER.startCameraMonitor();
        Thread.sleep(Math.round(Math.random()*150));
        assertFalse(l.fired);
        LibusbJava.init(new Canon1000D(true));
        Thread.sleep(Math.round(Math.random()*150));
        assertTrue(l.fired);
        //
        // It has to notify only changes
        //
        l.fired = false;
        Thread.sleep(Math.round(Math.random()*150));
        assertFalse(l.fired);
    }

    public void testStopCameraMonitor() throws Exception {
        ConnectedEventListener l = new ConnectedEventListener();

        //
        // By default the monitor does not run
        //
        LibusbJava.init(new Canon1000D(true));
        CONTROLLER.addCameraListener(l);
        Thread.sleep(Math.round(Math.random()*150));
        assertFalse(l.fired);

        //
        // Let's start the monitor now
        //
        l.fired = false;
        CONTROLLER.startCameraMonitor();
        Thread.sleep(Math.round(Math.random()*150));
        assertTrue(l.fired);

        //
        // Let's stop the monitor and detach the camera
        //
        l.fired = false;
        LibusbJava.init(new Canon1000D(false));
        CONTROLLER.stopCameraMonior();
        Thread.sleep(Math.round(Math.random()*150));
        assertFalse(l.fired);
    }
}
