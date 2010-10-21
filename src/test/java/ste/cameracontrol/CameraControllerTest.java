package ste.cameracontrol;

import ch.ntb.usb.Canon1000D;
import ch.ntb.usb.LibusbJava;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    public void testCameraIsConnected() {
        LibusbJava.init(new Canon1000D(true));
        assertTrue(CONTROLLER.isConnected());
    }

    public void testCameraIsNotConnected() {
        LibusbJava.init(new Canon1000D(false));
        assertFalse(CONTROLLER.isConnected());
    }
}
