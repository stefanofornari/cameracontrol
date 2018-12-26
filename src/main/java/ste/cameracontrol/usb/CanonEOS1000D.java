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

package ste.cameracontrol.usb;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import javax.usb.UsbClaimException;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConfigurationDescriptor;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbDisconnectedException;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfaceDescriptor;
import javax.usb.UsbInterfacePolicy;
import javax.usb.UsbNotActiveException;
import javax.usb.UsbPort;
import javax.usb.UsbStringDescriptor;
import javax.usb.event.UsbDeviceListener;
import org.usb4java.LibUsb;

public class CanonEOS1000D implements UsbDevice {

    public static final short VENDOR  = 0x04a9;
    public static final short PRODUCT = 0x317b;

    public final static short        vendorId          = (short) 0x04a9;
    public final static short        productId         = (short) 0x317b;
    public final static String       filename          = "001";
    public final static String       busName           = "/dev/bus/usb/001";
    public final static int          timeout           = 2000;
    public final static int          configuration     = 1;
    public final static int          iface             = 0;
    public final static int          altinterface      = -1;
    public final static int          outEPBulk         = 0x01;
    public final static int          inEPBulk          = 0x82;
    public final static int          outEPInt          = 0x03;
    public final static int          inEPInt           = 0x84;
    public final static int          sleepTimeout      = 2000;
    public final static int          maxDataSize       = 64;
    public final static short        mode              = 0x00;  // 0 - bulk; 1 - interrupt
    public final static boolean      compareData       = true;
    public final static String       manufacturer      = "Canon";
    public final static String       product           = "1000D";
    public final static String       serialNumber      = "00.10.00";
    public final static byte         interfaceClass    = 0x00;
    public final static byte         interfaceSubClass = 0x00;
    public final static byte         interfaceProtocol = 0x00;
    public final static String       speed             = "10 Mbs";

    @Override
    public UsbPort getParentUsbPort() throws UsbDisconnectedException {
        return null;
    }

    @Override
    public boolean isUsbHub() {
        return false;
    }

    @Override
    public String getManufacturerString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return manufacturer;
    }

    @Override
    public String getSerialNumberString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return serialNumber;
    }

    @Override
    public String getProductString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return product;
    }

    @Override
    public Object getSpeed() {
            return speed;
    }

    @Override
    public List getUsbConfigurations() {
        return new ArrayList();
    }

    @Override
    public UsbConfiguration getUsbConfiguration(byte b) {
        return null;
    }

    @Override
    public boolean containsUsbConfiguration(byte b) {
        return false;
    }

    @Override
    public byte getActiveUsbConfigurationNumber() {
        return 1;
    }

    @Override
    public UsbConfiguration getActiveUsbConfiguration() {
        return new CanonEOS1000DConfiguration(this);
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public UsbDeviceDescriptor getUsbDeviceDescriptor() {
        return new CanoncEOS1000DDescriptor();
    }

    @Override
    public UsbStringDescriptor getUsbStringDescriptor(byte b) throws UsbException, UsbDisconnectedException {
        return null;
    }

    @Override
    public String getString(byte b) throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return "";
    }

    @Override
    public void syncSubmit(UsbControlIrp uci) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
    }

    @Override
    public void asyncSubmit(UsbControlIrp uci) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
    }

    @Override
    public void syncSubmit(List list) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
    }

    @Override
    public void asyncSubmit(List list) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
    }

    @Override
    public UsbControlIrp createUsbControlIrp(byte b, byte b1, short s, short s1) {
        return null;
    }

    @Override
    public void addUsbDeviceListener(UsbDeviceListener ul) {
    }

    @Override
    public void removeUsbDeviceListener(UsbDeviceListener ul) {
    }

    // ------------------------------------------------- CanonEOS1000DDescriptor

    private class CanoncEOS1000DDescriptor implements UsbDeviceDescriptor {
        @Override
        public short bcdUSB() {
            return 0;
        }

        @Override
        public byte bDeviceClass() {
            return 0;
        }

        @Override
        public byte bDeviceSubClass() {
            return 0;
        }

        @Override
        public byte bDeviceProtocol() {
            return 0;
        }

        @Override
        public byte bMaxPacketSize0() {
            return 0;
        }

        @Override
        public short idVendor() {
            return vendorId;
        }

        @Override
        public short idProduct() {
            return productId;
        }

        @Override
        public short bcdDevice() {
            return 0x00;
        }

        @Override
        public byte iManufacturer() {
            return 0x00;
        }

        @Override
        public byte iProduct() {
            return 0x00;
        }

        @Override
        public byte iSerialNumber() {
            return 0x00;
        }

        @Override
        public byte bNumConfigurations() {
            return 0x00;
        }

        @Override
        public byte bLength() {
            return 0x00;
        }

        @Override
        public byte bDescriptorType() {
            return 0x00;
        }
    }

    // ---------------------------------------------- CanonEOS1000DConfiguration

    private class CanonEOS1000DConfiguration implements UsbConfiguration {

        private final UsbDevice device;
        private final List interfaces = new ArrayList();

        public CanonEOS1000DConfiguration(UsbDevice device) {
            this.device = device;

            interfaces.add(new CanonEOS1000DInterface());
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public List getUsbInterfaces() {
            return interfaces;
        }

        @Override
        public UsbInterface getUsbInterface(byte b) {
            return (UsbInterface)interfaces.get(0);
        }

        @Override
        public boolean containsUsbInterface(byte b) {
            return false;
        }

        @Override
        public UsbDevice getUsbDevice() {
            return device;
        }

        @Override
        public UsbConfigurationDescriptor getUsbConfigurationDescriptor() {
            return null;
        }

        @Override
        public String getConfigurationString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
            return null;
        }
    }

    // -------------------------------------------------- CanonEOS1000DInterface

    private class CanonEOS1000DInterface implements UsbInterface {

        @Override
        public void claim() throws UsbClaimException, UsbException, UsbNotActiveException, UsbDisconnectedException {
        }

        @Override
        public void claim(UsbInterfacePolicy uip) throws UsbClaimException, UsbException, UsbNotActiveException, UsbDisconnectedException {
        }

        @Override
        public void release() throws UsbClaimException, UsbException, UsbNotActiveException, UsbDisconnectedException {
        }

        @Override
        public boolean isClaimed() {
            return true;
        }

        @Override
        public boolean isActive() {
            return true;
        }

        @Override
        public int getNumSettings() {
            return 0;
        }

        @Override
        public byte getActiveSettingNumber() throws UsbNotActiveException {
            return 0;
        }

        @Override
        public UsbInterface getActiveSetting() throws UsbNotActiveException {
            return null;
        }

        @Override
        public UsbInterface getSetting(byte b) {
            return null;
        }

        @Override
        public boolean containsSetting(byte b) {
            return false;
        }

        @Override
        public List getSettings() {
            return new ArrayList();
        }

        @Override
        public List getUsbEndpoints() {
            return new ArrayList();
        }

        @Override
        public UsbEndpoint getUsbEndpoint(byte b) {
            return null;
        }

        @Override
        public boolean containsUsbEndpoint(byte b) {
            return false;
        }

        @Override
        public UsbConfiguration getUsbConfiguration() {
            return null;
        }

        @Override
        public UsbInterfaceDescriptor getUsbInterfaceDescriptor() {
            return new CanonEOS1000DInterfaceDescriptor();
        }

        @Override
        public String getInterfaceString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
            return this.toString();
        }
    }

    // ---------------------------------------- CanonEOS1000DInterfaceDescriptor

    private class CanonEOS1000DInterfaceDescriptor implements UsbInterfaceDescriptor {

        @Override
        public byte bInterfaceNumber() {
            return 0;
        }

        @Override
        public byte bAlternateSetting() {
            return 0;
        }

        @Override
        public byte bNumEndpoints() {
            return 0;
        }

        @Override
        public byte bInterfaceClass() {
            return LibUsb.CLASS_IMAGE;
        }

        @Override
        public byte bInterfaceSubClass() {
            return 0;
        }

        @Override
        public byte bInterfaceProtocol() {
            return 0;
        }

        @Override
        public byte iInterface() {
            return 0;
        }

        @Override
        public byte bLength() {
            return 0;
        }

        @Override
        public byte bDescriptorType() {
            return 0;
        }
    }

}
