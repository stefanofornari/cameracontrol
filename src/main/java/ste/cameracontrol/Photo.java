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

package ste.cameracontrol;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class associates an image name to a BufferedImage
 *
 * @author ste
 */
public class Photo {
    private String name;
    private byte[] data;

    /**
     * Creates a Photo object given the name and data
     *
     * @param name the photo name - NOT EMPTY
     * @param data the photo raw data
     *
     * @throws IllegalArgumentException if name is null or empty
     */
    public Photo(String name, byte[] data) {
        if ((name == null) || (name.length() == 0)) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        this.name = name;
        this.data = data;
    }

    public Photo(String name) {
        this(name, null);
    }
    
    public String getName() {
        return name;
    }

    public byte[] getRawData() {
        return data;
    }

    public BufferedImage getImage() {
        if (data == null) {
            return null;
        }

        BufferedImage ret = null;
        try {
            ret = ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            //
            // Do nothing, ret will be null
            //
            e.printStackTrace();
        }

        return ret;
    }
}
