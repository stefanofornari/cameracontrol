/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
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

    private BufferedImage getTransformedImage(ImagePanel p) throws Exception {
        Field f = ImagePanel.class.getDeclaredField("transformedImage");

        f.setAccessible(true);

        return (BufferedImage)f.get(p);
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

    public void testScaleBeforeSettingImage() throws Exception {
        ImagePanel p = new ImagePanel();

        p.setScale(0.5);
        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        Image i = getTransformedImage(p);
        assertEquals(150, i.getWidth(null));
        assertEquals(112, i.getHeight(null));
    }

    public void testRotation() throws Exception {
        ImagePanel p = new ImagePanel();

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));
        p.setRotation(90);

        Image i = getTransformedImage(p);
        assertEquals(225, i.getWidth(null));
        assertEquals(300, i.getHeight(null));

        p.setRotation(-90);
        i = getTransformedImage(p);
        assertEquals(225, i.getWidth(null));
        assertEquals(300, i.getHeight(null));

        p.setRotation(180);
        i = getTransformedImage(p);
        assertEquals(300, i.getWidth(null));
        assertEquals(225, i.getHeight(null));

        p.setRotation(270);
        i = getTransformedImage(p);
        assertEquals(225, i.getWidth(null));
        assertEquals(300, i.getHeight(null));

        p.setRotation(0);
        i = getTransformedImage(p);
        assertEquals(300, i.getWidth(null));
        assertEquals(225, i.getHeight(null));
    }

}
