/*
 * cameracontrol
 * Copyright (C) 2011 Stefano Fornari
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

import java.util.HashMap;

/**
 * This class represents a Configuration object that can be passed to other
 * classes to access configuration properties.
 *
 * @author ste
 */
public class Configuration extends HashMap {

    public static final String CONFIG_IMAGEDIR = "ste.cameracontrol.image-dir";

    public void setImageDir(String dir) {
        put(CONFIG_IMAGEDIR, dir);
    }

    public String getImageDir() {
        return (String)get(CONFIG_IMAGEDIR);
    }
}
