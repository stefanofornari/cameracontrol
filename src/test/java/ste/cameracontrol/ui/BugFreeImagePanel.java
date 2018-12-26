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
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;

/**
 *
 */
public class BugFreeImagePanel {

    private final String TESTFILE = "images/about.png";

    @Test
    public void has_image() throws Exception {
        ImagePanel p = new ImagePanel();

        then(p.hasImage()).isFalse();

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        then(p.hasImage()).isTrue();
    }

    @Test
    public void set_image() throws Exception {
         ImagePanel p = new ImagePanel();

         p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

         Dimension d = p.getPreferredSize();

         then(d.width).isEqualTo(300);
         then(d.height).isEqualTo(225);
    }

    @Test
    public void get_image() throws Exception {
        ImagePanel p = new ImagePanel();

        then(p.getImage()).isNull();

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        Image i = p.getImage();
        then(i.getWidth(null)).isEqualTo(300);
        then(i.getHeight(null)).isEqualTo(225);
    }

    @Test
    public void scale_before_setting_image() throws Exception {
        ImagePanel p = new ImagePanel();

        p.setScale(0.5);
        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));

        Image i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(150);
        then(i.getHeight(null)).isEqualTo(112);
    }

    @Test
    public void rotation() throws Exception {
        ImagePanel p = new ImagePanel();

        p.setImage(ImageIO.read(ClassLoader.getSystemResourceAsStream(TESTFILE)));
        p.setRotation(90);

        Image i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(225);
        then(i.getHeight(null)).isEqualTo(300);

        p.setRotation(-90);
        i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(225);
        then(i.getHeight(null)).isEqualTo(300);

        p.setRotation(180);
        i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(300);
        then(i.getHeight(null)).isEqualTo(225);

        p.setRotation(270);
        i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(225);
        then(i.getHeight(null)).isEqualTo(300);

        p.setRotation(0);
        i = getTransformedImage(p);
        then(i.getWidth(null)).isEqualTo(300);
        then(i.getHeight(null)).isEqualTo(225);
    }

    // --------------------------------------------------------- private methods

    private BufferedImage getTransformedImage(ImagePanel p) throws Exception {
        Field f = ImagePanel.class.getDeclaredField("transformedImage");

        f.setAccessible(true);

        return (BufferedImage)f.get(p);
    }


}
