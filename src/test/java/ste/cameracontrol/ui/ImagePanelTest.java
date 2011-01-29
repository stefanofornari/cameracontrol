/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol.ui;

import java.awt.Dimension;
import java.awt.Image;
import javax.imageio.ImageIO;
import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class ImagePanelTest extends TestCase {

    private final String TESTFILE = "images/about.png";
    
    public ImagePanelTest(String testName) {
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

    public void testHasImage() throws Exception {
        ImagePanel p = new ImagePanel();

        assertFalse(p.hasImage());

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        assertTrue(p.hasImage());
    }

    public void testSetImage() throws Exception {
         ImagePanel p = new ImagePanel();

         p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

         Dimension d = p.getPreferredSize();

         assertEquals(300, d.width);
         assertEquals(225, d.height);
    }

    public void testGetImage() throws Exception {
        ImagePanel p = new ImagePanel();

        assertNull(p.getImage());

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        Image i = p.getImage();
        assertEquals(300, i.getWidth(null));
        assertEquals(225, i.getHeight(null));
    }

}
