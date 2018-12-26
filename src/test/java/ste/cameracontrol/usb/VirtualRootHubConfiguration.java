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
import javax.usb.UsbConfiguration;
import javax.usb.UsbConfigurationDescriptor;
import javax.usb.UsbConst;
import javax.usb.UsbDevice;
import javax.usb.UsbInterface;
import org.usb4java.javax.descriptors.SimpleUsbConfigurationDescriptor;

public class VirtualRootHubConfiguration implements UsbConfiguration {
    private final List<UsbInterface> interfaces = new ArrayList<UsbInterface>();

    private final UsbDevice device;

    private final UsbConfigurationDescriptor descriptor =
        new SimpleUsbConfigurationDescriptor(
             UsbConst.DESCRIPTOR_MIN_LENGTH_CONFIGURATION,
             UsbConst.DESCRIPTOR_TYPE_CONFIGURATION,
             (byte) (UsbConst.DESCRIPTOR_MIN_LENGTH_CONFIGURATION
                + UsbConst.DESCRIPTOR_MIN_LENGTH_INTERFACE),
             (byte) 1,
             (byte) 1,
             (byte) 0,
             (byte) 0x80,
             (byte) 0);

    /**
     * Constructor.
     *
     * @param device
     *            The device this configuration belongs to.
     */
    public VirtualRootHubConfiguration(final UsbDevice device) {
        this.device = device;
        this.interfaces.add(new VirtualRootHubInterface(this));
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public List<UsbInterface> getUsbInterfaces() {
        return this.interfaces;
    }

    @Override
    public UsbInterface getUsbInterface(final byte number) {
        if (number != 0) {
            return null;
        }
        return this.interfaces.get(0);
    }

    @Override
    public boolean containsUsbInterface(final byte number) {
        return number == 0;
    }

    @Override
    public UsbDevice getUsbDevice() {
        return this.device;
    }

    @Override
    public UsbConfigurationDescriptor getUsbConfigurationDescriptor() {
        return this.descriptor;
    }

    @Override
    public String getConfigurationString() {
        return null;
    }
}