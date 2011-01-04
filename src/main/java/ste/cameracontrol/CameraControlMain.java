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

import ch.ntb.usb.Device;
import ch.ntb.usb.DeviceDatabase;
import java.io.File;
import ste.cameracontrol.ui.CameraControlWindow;
import ste.ptp.PTPBusyException;

/**
 * Hello world!
 *
 */
public class CameraControlMain implements CameraListener {

    private CameraController controller;
    private CameraControlWindow window;

    private DeviceDatabase deviceDatabase;

    public CameraControlMain() {
        deviceDatabase = new DeviceDatabase();

        Configuration c = new Configuration();
        for (String property: System.getProperties().stringPropertyNames()) {
            if (property.startsWith("ste.")) {
                c.put(property, System.getProperty(property));
            }
        }

        //
        // If the image directory is not configured, the default is used.
        // If the image directory does not exists, it is created.
        //
        String imageDirName = c.getImageDir();
        if (imageDirName == null) {
            imageDirName = "images";
        }
        File imageDir = new File(imageDirName);
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        
        controller = new CameraController(c);
        controller.addCameraListener(this);
        
        window = new CameraControlWindow();
        window.setController(controller);
        window.setVisible(true);

        controller.startCameraMonitor();
    }

    @Override
    public void cameraConnected(Device device) {
        //
        // Soon after the USB device is detected, the system may keep the device
        // busy for a little while, Therefore we try 3 times before giving up.
        //
        for (int i = 0; i<3; ++i) {
            try {
                controller.startCamera();
            } catch (PTPBusyException e) {
                continue;
            } catch (Exception e) {
                window.setConnectionStatus(null);
                window.error(null, e);
                return;
            }
        }
        window.setConnectionStatus(device.getDisplayName());
    }

    @Override
    public void cameraDisconnected(Device device) {
        window.setConnectionStatus(null);
    }

    public static void main(String[] args) throws Exception {
        new CameraControlMain();
    }
}
