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
import java.awt.Image;
import java.awt.Insets;
import javax.swing.JComponent;

/**
 *
 * @author ste
 */
public class ImagePanel extends JComponent {

    private Image img;
    private double scale;

    public ImagePanel() {
        img = null;
        scale = 1.0;
    }

    public boolean hasImage() {
        return (img != null);
    }

    public void setImage(Image img) {
        this.img = img;
        repaint();
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
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        if (img == null) {
            return super.getPreferredSize();
        } else {
            return new Dimension((int)(img.getWidth(this)*scale), (int)(img.getHeight(this)*scale));
        }
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(img == null) {
            return;
        }

        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top;

        int w = getWidth() - insets.left - insets.right;
        int h = getHeight() - insets.top - insets.bottom;

        int src_w = img.getWidth(null);
        int src_h = img.getHeight(null);

        int dst_w = (int)(scale * src_w);
        int dst_h = (int)(scale * src_h);

        int dx = x + (w-dst_w)/2;
        int dy = y + (h-dst_h)/2;

        g.drawImage(img, dx, dy, dx+dst_w, dy+dst_h, 0, 0, src_w, src_h, null);
    }

    
}
