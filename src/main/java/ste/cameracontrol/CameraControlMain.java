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
import ste.cameracontrol.ui.CameraControlWindow;
import ste.ptp.PTPException;

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

        controller = new CameraController();
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
        // busy for a little while. We wait a few seconds before trying to
        // get the connecion
        //
        try {
            controller.startCamera();
        } catch (Exception e) {
            window.error(null, e);
        }
        window.setStatus(device.getDisplayName());
    }

    @Override
    public void cameraDisconnected(Device device) {
        window.setStatus(null);
    }

    public static void main(String[] args) throws Exception {
        new CameraControlMain();
    }
}
