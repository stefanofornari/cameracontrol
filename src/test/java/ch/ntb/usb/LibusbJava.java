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
package ch.ntb.usb;

import ch.ntb.usb.demo.AbstractDeviceInfo;
import java.io.UnsupportedEncodingException;

/**
 * This a mock of the original LibusbJava in usbjava:jniusbjava.
 * 
 */
public class LibusbJava {

    private static AbstractDeviceInfo devInfo;
    private static UsbBus bus;
    private static byte[] buffer;

    public static void init(AbstractDeviceInfo devInfo) {
        UsbDeviceDescriptor devDesc = new UsbDeviceDescriptor();
        UsbConfigDescriptor confDesc = new UsbConfigDescriptor();
        UsbInterface irf = new UsbInterface();
        UsbInterfaceDescriptor intDesc = new UsbInterfaceDescriptor();
        intDesc.setInterfaceClass(devInfo.getInterfaceClass());
        intDesc.setInterfaceSubClass(devInfo.getInterfaceSubClass());
        intDesc.setInterfaceProtocol(devInfo.getInterfaceProtocol());
        UsbEndpointDescriptor epDesc = new UsbEndpointDescriptor();

        devDesc.setVendorId(devInfo.getIdVendor());
        devDesc.setProductId(devInfo.getIdProduct());

        epDesc.setMaxPacketSize((short)devInfo.getMaxDataSize());
        intDesc.setEndpoints(new UsbEndpointDescriptor[] {epDesc});
        irf.setAlternateSetting(new UsbInterfaceDescriptor[] {intDesc});
        confDesc.setInterfaces(new UsbInterface[] {irf});

        UsbDevice device = new UsbDevice(
                                devInfo.getFilename(),
                                devDesc,
                                new UsbConfigDescriptor[] {confDesc},
                                (byte)1,
                                (byte)0,
                                null);
        bus = new UsbBus(devInfo.getBusName(), device, 0);
        device.setBus(bus);

        LibusbJava.devInfo = devInfo;
     }

    public static void usb_set_debug(int level) {
    }

    public static void usb_init() {
    }

    public static int usb_find_busses() {
        return 1;
    }

    public static int usb_find_devices() {
        return 1;
    }

    public static UsbBus usb_get_busses() {
        return bus;
    }

    public static long usb_open(UsbDevice dev) {
        return 1234;
    }

    public static int usb_close(long dev_handle) {
        return 1;
    }

    public static int usb_set_configuration(long dev_handle, int configuration) {
        return (configuration > 4) ? 1 : 0;
    }

    public static int usb_set_altinterface(long dev_handle, int alternate) {
        return 1;
    }

    public static int usb_clear_halt(long dev_handle, int ep) {
        return 1;
    }

    public static int usb_reset(long dev_handle) {
        return 1;
    }

    public static int usb_claim_interface(long dev_handle, int interface_) {
        return 1;
    }

    public static int usb_release_interface(long dev_handle, int interface_) {
        return 1;
    }

    public static int usb_control_msg(
        long dev_handle, int requesttype, int request, int value, int index,
        byte[] bytes, int size, int timeout) {

        int ret = bytes.length;

        if (request == USB.REQ_GET_STATUS) {
            if (((requesttype & (int)USB.REQ_TYPE_RECIP_INTERFACE) != 0) ||
                ((requesttype & (int)USB.REQ_TYPE_RECIP_ENDPOINT) != 0)) {
                bytes[0] = 0;
            } else {
                bytes[0] = 1;
            }
            bytes[1] = 0;
        } else if (request == USB.REQ_GET_CONFIGURATION) {
            bytes[0] = (byte)devInfo.getConfiguration();
        } else if (request == USB.REQ_GET_DESCRIPTOR) {
            if (value == (1 << 8)) {
                byte[] desc = new byte[] {
                    18, 1, 0, 0x02, (byte)0xff, (byte)0xff,
                    (byte)0xff, 64, 0x35, (byte)0x82, 0x22, 0x02,
                    0x00, 0x10, 0x01, 0x02, 0x03, 0x01
                };
                System.arraycopy(desc, 0, bytes, 0, desc.length);
                ret = desc.length;
            } else if (value == ((3 << 8) + 1)) {
                ret = buildString(bytes, devInfo.getManufacturer());
            } else if (value == ((3 << 8) + 2)) {
                ret = buildString(bytes, devInfo.getProduct());
            } else if (value == ((3 << 8) + 3)) {
                ret = buildString(bytes, devInfo.getSerialVersion());
            }
        }

        return ret;
    }

    public static String usb_get_string(
            long dev_handle, int index, int langid) {
        return null;
    }

    public static String usb_get_string_simple(long dev_handle, int index) {
        return null;
    }

    public static String usb_get_descriptor(
            long dev_handle, byte type, byte index, int size) {
        return null;
    }

    public static String usb_get_descriptor_by_endpoint(
            long dev_handle, int ep, byte type, byte index, int size) {
        return null;
    }

    public static int usb_bulk_write(
        long dev_handle, int ep, byte[] bytes, int size, int timeout
    ) {
        buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        return bytes.length;
    }

    public static int usb_bulk_read(
        long dev_handle, int ep, byte[] bytes, int size, int timeout)
    {
        System.arraycopy(buffer, 0, bytes, 0, size);
        return Math.max(size, buffer.length);
    }

    public static int usb_interrupt_write(
        long dev_handle, int ep, byte[] bytes, int size, int timeout
    ) {
        buffer = new byte[bytes.length];
        System.arraycopy(bytes, 0, buffer, 0, bytes.length);
        return bytes.length;
    }

    public static int usb_interrupt_read(
        long dev_handle, int ep, byte[] bytes, int size, int timeout)
    {
        System.arraycopy(buffer, 0, bytes, 0, size);
        return Math.max(size, buffer.length);
    }

    public static String usb_strerror() {
        return null;
    }

    private static int buildString(byte[] bytes, String s) {
        bytes[0] = bytes[1] = 3;
        byte[] str = new byte[0];

        try {
            str = s.getBytes("UTF-16LE");
        } catch(UnsupportedEncodingException e) {
            
        }

        System.arraycopy(str, 0, bytes, 2, str.length);

        return str.length+2;
    }
}
