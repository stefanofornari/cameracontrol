/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol;

import ch.ntb.usb.Canon1000D;
import ch.ntb.usb.LibusbJava;
import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class CameraConnectionTest extends TestCase {
    
    public CameraConnectionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConnectionTrue() {
        LibusbJava.init(new Canon1000D());
        
        CameraConnection connection = new CameraConnection();

        assertTrue(connection.isConnected());
    }

    public void testConnectionFalse() {
        LibusbJava.init(new Canon1000D(false));

        CameraConnection connection = new CameraConnection();

        assertFalse(connection.isConnected());
    }

}
