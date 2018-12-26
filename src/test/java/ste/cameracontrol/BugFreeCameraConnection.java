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

import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHostManager;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import ste.cameracontrol.usb.CanonEOS1000D;
import ste.cameracontrol.usb.VirtualUSBServices;

/**
 *
 * @author ste
 */
public class BugFreeCameraConnection {

    private final CanonEOS1000D INFO = new CanonEOS1000D();

    @Test
    public void conected_ok() throws UsbException {
        givenUSBConnectionStatus(true);

        then(new CameraConnection().isConnected()).isTrue();
    }

    @Test
    public void not_connected() throws UsbException {
        givenUSBConnectionStatus(false);
        then(new CameraConnection().isConnected()).isFalse();
    }

    @Test
    public void find_exising_device() throws UsbException {
        givenUSBConnectionStatus(true);

        CameraConnection connection = new CameraConnection();
        UsbDevice dev = connection.findCamera();

        UsbDeviceDescriptor dd = dev.getUsbDeviceDescriptor();

        then(dd.idProduct()).isEqualTo(INFO.productId);
        then(dd.idVendor()).isEqualTo(INFO.vendorId);
    }

    // --------------------------------------------------------- private methods

    /*
       Connect/Disconnect virtual USBs; please note this means parallelism must
       not be at method level.
    */
    private void givenUSBConnectionStatus(boolean connected) throws UsbException {
        VirtualUSBServices usb = (VirtualUSBServices) UsbHostManager.getUsbServices();
        usb.setConnectionStatus(connected);
    }

}
