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

import java.util.List;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import javax.usb.UsbHub;
import javax.usb.UsbInterface;
import org.usb4java.LibUsb;

/**
 * This class represents the connection with a camera.
 *
 * @author ste
 */
public class CameraConnection {

    public CameraConnection() {
    }

    public boolean isConnected() {
        return (findCamera() != null);
    }

    public UsbDevice findCamera() {
        try {
            return findCamera(UsbHostManager.getUsbServices().getRootUsbHub());
        } catch (UsbException x) {
            x.printStackTrace();
        }

        return  null;
    }

    // --------------------------------------------------------- private methods

    private UsbDevice findCamera(UsbHub root) {
        for (UsbDevice device: (List<UsbDevice>) root.getAttachedUsbDevices()) {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            System.out.println("Found on USB: idVendor: " + desc.idVendor() + ", idProduct: " + desc.idProduct());
            //
            // If the device is not configured there is nothing we can do about it...
            //
            if (device.isConfigured()) {
                List ifaces = device.getActiveUsbConfiguration().getUsbInterfaces();
                for (int i=0; i<ifaces.size(); i++) {
                    /* All objects in the List are guaranteed to be UsbInterface objects. */
                    UsbInterface iface = (UsbInterface)ifaces.get(i);

                    /* See FindUsbDevice for notes about comparing unsigned numbers, note this is an unsigned byte. */
                    if (iface.getUsbInterfaceDescriptor().bInterfaceClass() == LibUsb.CLASS_IMAGE) {
                        return device;
                    }
                }
            }
            if (device.isUsbHub()) {
                device = findCamera((UsbHub) device);
                if (device != null) {
                    return device;
		}
            }
        }

        return null;
    }
}
