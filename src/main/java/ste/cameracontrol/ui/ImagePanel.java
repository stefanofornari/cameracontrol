/*
 * cameracontrol
 * Copyright (C) 2010 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */
package ste.cameracontrol.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author ste
 */
public class ImagePanel extends JComponent {

    private BufferedImage img;
    private BufferedImage displayedImage;
    private double scale;
    private int rotation;

    public ImagePanel() {
        img = null;
        scale = 1.0;
        rotation = 0;
    }

    public boolean hasImage() {
        return (img != null);
    }

    public void setImage(BufferedImage img) {
        this.img = img;

        displayedImage = (img == null)
                       ? null
                       : transform(img);

        repaint();
    }

    public Image getImage() {
        return img;
    }

    /**
     * @return the scale
     */
    public double getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(double scale) {
        this.scale = scale;

        displayedImage = (img == null)
                       ? null
                       : transform(img);
        repaint();
    }


    /**
     * @return the rotation
     */
    public int getRotation() {
        return rotation;
    }

    /**
     * @param rotation the rotation to set
     */
    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    @Override
    public Dimension getPreferredSize() {
        if (img == null) {
            return super.getPreferredSize();
        } else {
            return new Dimension((int)(img.getWidth(null)*scale), (int)(img.getHeight(null)*scale));
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(displayedImage == null) {
            return;
        }

        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;

        //
        // image should be already scaled at this point...
        //
        int imgW = displayedImage.getWidth(null);
        int imgH = displayedImage.getHeight(null);

        int dx = x + (w-imgW)/2;
        int dy = y + (h-imgH)/2;

        g.drawImage(displayedImage, dx, dy, dx+imgW, dy+imgH, 0, 0, imgW, imgH, null);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates a new BufferedImage applying scale and rotation to the given
     * image as per the scaling and rotation factors currently set.
     *
     * @param img the source image
     *
     * @return a new image resulting by applying scaling and rotation
     */
    private BufferedImage transform(BufferedImage img) {
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;

        int srcW = img.getWidth(null);
        int srcH = img.getHeight(null);

        int dstW = (int)(scale * srcW);
        int dstH = (int)(scale * srcH);

        BufferedImage destinationImage = new BufferedImage(dstW, dstH, img.getType());
        Graphics2D g = destinationImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, dstW, dstH, 0, 0, srcW, srcH, null);
        g.dispose();

        return destinationImage;
    }

}
