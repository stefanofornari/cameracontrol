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

import java.io.File;
import javax.usb.UsbDevice;
import ste.cameracontrol.ui.AboutDialog;
import ste.cameracontrol.ui.CameraControlWindow;
import ste.ptp.PTPBusyException;

/**
 *
 *
 */
public class CameraControlMain implements CameraListener {

    private CameraControlWindow window;

    public CameraControlMain() {
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

        CameraController controller = CameraController.getInstance();
        controller.initialize(c);
        controller.addCameraListener(this);

        window = new CameraControlWindow();
        window.setVisible(true);

        controller.startCameraMonitor();
    }

    @Override
    public void cameraConnected(UsbDevice device) {
        //
        // Soon after the USB device is detected, the system may keep the device
        // busy for a little while, Therefore we try 3 times before giving up.
        //
        String cameraName = null;
        for (int i = 0; i<10; ++i) {
            try {
                CameraController.getInstance().startCamera();
                cameraName = device.getManufacturerString();
                window.enableCameraControls();
                break;
            } catch (PTPBusyException e) {
                try { Thread.sleep(2000); } catch (Exception ignore) {}
                continue;
            } catch (Exception e) {
                window.error(null, e);
                break;
            }
        }
        window.setConnectionStatus(cameraName);
    }

    @Override
    public void cameraDisconnected(UsbDevice device) {
        window.setConnectionStatus(null);
        window.disableCameraControls();
    }

    // -------------------------------------------------------------------- main

    public static void main(String[] args) throws Exception {
        for (String s: args) {
            if (s.equals("--about")) {
                new AboutDialog(null, true).setVisible(true);
                return;
            }
        }

        new CameraControlMain();
    }


}
