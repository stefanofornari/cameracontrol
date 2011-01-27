/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol;

import java.awt.image.BufferedImage;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author ste
 */
public class PhotoTest extends TestCase {
    
    public PhotoTest(String testName) {
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

    public void testConstructorsOK() {
        Photo p = new Photo("name");
        assertEquals("name", p.getName());
        assertNull(p.getRawData());
        
        p = new Photo("name", new byte[0]);
        assertEquals("name", p.getName());
        assertNotNull(p.getRawData());

        p = new Photo("name", new byte[] {64});
        assertEquals("name", p.getName());
        assertEquals(64, p.getRawData()[0]);
    }

    public void testConstructorsKO() {
        try {
            Photo p = new Photo(null);
            fail("name must be checked for null values");
        } catch (IllegalArgumentException e) {
            //
            // OK
            //
        }

        try {
            Photo p = new Photo("");
            fail("name must be checked for empty values");
        } catch (IllegalArgumentException e) {
            //
            // OK
            //
        }
    }

    public void testGetImage() throws Exception {
        byte[] data = IOUtils.toByteArray(
                          ClassLoader.getSystemResourceAsStream("images/camera-connect-24x24.png")
                      );

        Photo photo = new Photo("camera-connect-24x24.png");

        assertNull(photo.getImage());
        
        photo = new Photo("camera-connect-24x24.png", data);

        BufferedImage image = photo.getImage();

        assertEquals(24, image.getWidth());
        assertEquals(24, image.getHeight());
    }

}
