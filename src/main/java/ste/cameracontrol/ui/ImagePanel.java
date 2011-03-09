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

    private BufferedImage image;
    private BufferedImage transformedImage;
    private double scale;
    private int rotation;

    public ImagePanel() {
        image = null;
        scale = 1.0;
        rotation = 0;
    }

    public boolean hasImage() {
        return (image != null);
    }

    public void setImage(BufferedImage img) {
        this.image = img;

        transformedImage = (img == null)
                       ? null
                       : transform(img);

        repaint();
    }

    public Image getImage() {
        return image;
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

        transformedImage = (image == null)
                       ? null
                       : transform(image);
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
        this.rotation = (rotation + 360) % 360;

        transformedImage = (image == null)
                       ? null
                       : transform(image);

        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (transformedImage == null) {
            return super.getPreferredSize();
        } else {
            return new Dimension(
                           (int)(transformedImage.getWidth(null)),
                           (int)(transformedImage.getHeight(null))
                   );
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(transformedImage == null) {
            return;
        }

        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = getWidth() - x- insets.right;
        int h = getHeight() - y - insets.bottom;

        //
        // image should be already scaled and rotated at this point...
        //
        int imgW = transformedImage.getWidth(null);
        int imgH = transformedImage.getHeight(null);

        int dx = x + (w-imgW)/2;
        int dy = y + (h-imgH)/2;

        g.drawImage(transformedImage, dx, dy, null);
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
        int srcW = ((rotation == 90) || (rotation == 270))
                 ? img.getHeight(null)
                 : img.getWidth(null)
                 ;
        int srcH = ((rotation == 90) || (rotation == 270))
                 ? img.getWidth(null)
                 : img.getHeight(null)
                 ;

        int dstW = (int)(scale * srcW);
        int dstH = (int)(scale * srcH);

        BufferedImage destinationImage = new BufferedImage(dstW, dstH, img.getType());
        Graphics2D g = destinationImage.createGraphics();
        g.scale(scale, scale);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        switch (rotation) {
            case 90:
                g.rotate(Math.toRadians(rotation), srcW/2.0, srcW/2.0);
                break;
            case 180:
                g.rotate(Math.toRadians(rotation), srcW/2.0, srcH/2.0);
                break;
            case 270:
                g.rotate(Math.toRadians(rotation), srcH/2.0, srcH/2.0);
                break;
        }

        g.drawImage(img, 0, 0, null);
        g.dispose();

        return destinationImage;
    }

}
