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
        assertNull(p.getJpegData());
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

    public void testSetJpegData() throws Exception {
        Photo  p = new Photo("name");

        try {
            p.setJpegData(null);
            fail("data cannot be null");
        } catch (IllegalArgumentException e) {
            //
            // Ok
            //
        }

        try {
            p.setJpegData(new byte[0]);
            fail("data cannot be empty");
        } catch (IllegalArgumentException e) {
            //
            // Ok
            //
        }

        p.setJpegData(new byte[] {64});
        assertEquals(64, p.getJpegData()[0]);
    }

    public void testSetRawData() throws Exception {
        Photo  p = new Photo("name");

        try {
            p.setRawData(null);
            fail("data cannot be null");
        } catch (IllegalArgumentException e) {
            //
            // Ok
            //
        }

        try {
            p.setRawData(new byte[0]);
            fail("data cannot be empty");
        } catch (IllegalArgumentException e) {
            //
            // Ok
            //
        }

        p.setRawData(new byte[] {32});
        assertEquals(32, p.getRawData()[0]);
    }

    public void testSetJpegAndRawData() {
        Photo  p = new Photo("name");
        p.setJpegData(new byte[] {64});
        p.setRawData(new byte[] {32});
        assertEquals(64, p.getJpegData()[0]);
        assertEquals(32, p.getRawData()[0]);
    }

    /**
     * We do test with JPEG only for now... I do not know how to generate a
     * small RAW file... :(
     *
     * @throws Exception
     */
    public void testGetImage() throws Exception {
        byte[] data = IOUtils.toByteArray(
                          ClassLoader.getSystemResourceAsStream("images/camera-connect-24x24.png")
                      );

        Photo photo = new Photo("camera-connect-24x24");

        assertNull(photo.getJpegImage());
        
        photo = new Photo("camera-connect-24x24");
        photo.setJpegData(data);

        BufferedImage image = photo.getJpegImage();

        assertEquals(24, image.getWidth());
        assertEquals(24, image.getHeight());
    }
}
