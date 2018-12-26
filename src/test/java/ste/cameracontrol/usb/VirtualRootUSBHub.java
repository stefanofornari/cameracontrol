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

import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.Port;
import javax.usb.UsbConfiguration;
import javax.usb.UsbConst;
import javax.usb.UsbControlIrp;
import javax.usb.UsbDevice;
import javax.usb.UsbDeviceDescriptor;
import javax.usb.UsbException;
import javax.usb.UsbHub;
import javax.usb.UsbPort;
import javax.usb.UsbStringDescriptor;
import javax.usb.event.UsbDeviceListener;
import javax.usb.util.DefaultUsbControlIrp;
import org.usb4java.javax.descriptors.SimpleUsbDeviceDescriptor;

/**
 *
 * @author ste
 */
public class VirtualRootUSBHub implements UsbHub {

    private static final String HUB_MANUFACTURER = "cameracontrol";
    private static final String HUB_PRODUCT = "virtual root usb hub";
    private static final String HUB_SERIAL_NUMBER = "1.0.0";

    /**
     * The configurations.
     */
    private final List<UsbConfiguration> configurations
            = new ArrayList<UsbConfiguration>(1);

    /**
     * The device descriptor.
     */
    private final UsbDeviceDescriptor descriptor
            = new SimpleUsbDeviceDescriptor(
                    UsbConst.DESCRIPTOR_MIN_LENGTH_DEVICE,
                    UsbConst.DESCRIPTOR_TYPE_DEVICE,
                    (short) 0x101,
                    UsbConst.HUB_CLASSCODE,
                    (byte) 0,
                    (byte) 0,
                    (byte) 8,
                    (short) 0xffff,
                    (short) 0xffff,
                    (byte) 0,
                    (byte) 1,
                    (byte) 2,
                    (byte) 3,
                    (byte) 1);

    private final List<UsbDevice> devices = new ArrayList<>();

    /**
     * The device listeners.
     */
    private final DeviceListeners listeners = new DeviceListeners();

    /**
     * Constructor.
     */
    public VirtualRootUSBHub(boolean connected) {
        this.configurations.add(new VirtualRootHubConfiguration(this));
        if (connected) {
            this.devices.add(new CanonEOS1000D());
        }
    }

    @Override
    public UsbPort getParentUsbPort() {
        return null;
    }

    @Override
    public boolean isUsbHub() {
        return true;
    }

    @Override
    public String getManufacturerString() {
        return HUB_MANUFACTURER;
    }

    @Override
    public String getSerialNumberString() {
        return HUB_SERIAL_NUMBER;
    }

    @Override
    public String getProductString() {
        return HUB_PRODUCT;
    }

    @Override
    public Object getSpeed() {
        return UsbConst.DEVICE_SPEED_UNKNOWN;
    }

    @Override
    public List<UsbConfiguration> getUsbConfigurations() {
        return this.configurations;
    }

    @Override
    public UsbConfiguration getUsbConfiguration(final byte number) {
        if (number != 1) {
            return null;
        }
        return this.configurations.get(0);
    }

    @Override
    public boolean containsUsbConfiguration(final byte number) {
        return number == 1;
    }

    @Override
    public byte getActiveUsbConfigurationNumber() {
        return 1;
    }

    @Override
    public UsbConfiguration getActiveUsbConfiguration() {
        return this.configurations.get(0);
    }

    @Override
    public boolean isConfigured() {
        return true;
    }

    @Override
    public UsbDeviceDescriptor getUsbDeviceDescriptor() {
        return this.descriptor;
    }

    @Override
    public UsbStringDescriptor getUsbStringDescriptor(final byte index)
            throws UsbException {
        return null;
    }

    @Override
    public String getString(final byte index) throws UsbException {
        return "to be implemented";
    }

    @Override
    public UsbPort getUsbPort(byte b) {
        return null;
    }

    @Override
    public void syncSubmit(final UsbControlIrp irp) throws UsbException {
        throw new UsbException("Can't syncSubmit on virtual device");
    }

    @Override
    public void asyncSubmit(final UsbControlIrp irp) throws UsbException {
        throw new UsbException("Can't asyncSubmit on virtual device");
    }

    @Override
    public void syncSubmit(final List list) throws UsbException {
        throw new UsbException("Can't syncSubmit on virtual device");
    }

    @Override
    public void asyncSubmit(final List list) throws UsbException {
        throw new UsbException("Can't asyncSubmit on virtual device");
    }

    @Override
    public UsbControlIrp createUsbControlIrp(final byte bmRequestType,
            final byte bRequest,
            final short wValue, final short wIndex) {
        return new DefaultUsbControlIrp(bmRequestType, bRequest, wValue,
                wIndex);
    }

    @Override
    public void addUsbDeviceListener(final UsbDeviceListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeUsbDeviceListener(final UsbDeviceListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public byte getNumberOfPorts() {
        return 0;
    }

    @Override
    public List<Port> getUsbPorts() {
        return new ArrayList<Port>();
    }

    @Override
    public List<UsbDevice> getAttachedUsbDevices() {
        return devices;
    }

    @Override
    public boolean isRootUsbHub() {
        return true;
    }

    @Override
    public String toString() {
        return this.getManufacturerString() + " " + this.getProductString()
                + " " + this.getSerialNumberString();
    }
}
