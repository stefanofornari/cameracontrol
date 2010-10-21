/*
 * Camera Control
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

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbConfigDescriptor;
import ch.ntb.usb.UsbDevice;
import ch.ntb.usb.UsbInterface;

/**
 * This class represents the connection with a camera.
 *
 * @author ste
 */
public class CameraConnection {

    public CameraConnection() {
        LibusbJava.usb_init();
    }

    public boolean isConnected() {
        LibusbJava.usb_find_busses();
        LibusbJava.usb_find_devices();

        UsbBus bus = LibusbJava.usb_get_busses();

        return (findCamera(bus) != null);
    }

    // --------------------------------------------------------- private methods

    private UsbDevice findCamera(UsbBus bus) {
        while (bus != null) {
            UsbDevice device = bus.getDevices();

            while (device != null) {
                UsbConfigDescriptor[] descriptors = device.getConfig();

                if (descriptors == null) {
                    continue;
                }

                for (UsbConfigDescriptor descriptor: descriptors) {
                    if (descriptor.getInterfaceByClass(UsbInterface.CLASS_IMAGE) != null) {
                        return device;
                    }
                }

                device = device.getNext();
            }

            bus = bus.getNext();
        }

        return null;
    }
}
