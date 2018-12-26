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
import javax.usb.UsbConst;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbInterfaceDescriptor;
import javax.usb.UsbInterfacePolicy;
import org.usb4java.javax.descriptors.SimpleUsbInterfaceDescriptor;

public class VirtualRootHubInterface implements UsbInterface {
    private final List<UsbEndpoint> endpoints = new ArrayList<UsbEndpoint>(0);
    private final List<UsbInterface> settings = new ArrayList<UsbInterface>(0);
    private final UsbConfiguration configuration;
    private final UsbInterfaceDescriptor descriptor =
        new SimpleUsbInterfaceDescriptor(
            UsbConst.DESCRIPTOR_MIN_LENGTH_INTERFACE,
            UsbConst.DESCRIPTOR_TYPE_INTERFACE,
            (byte) 0,
            (byte) 0,
            (byte) 0,
            UsbConst.HUB_CLASSCODE,
            (byte) 0,
            (byte) 0,
            (byte) 0);

    /**
     * Constructor.
     *
     * @param configuration the USB configuration.
     */
    VirtualRootHubInterface(final UsbConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public void claim() throws UsbException {
        throw new UsbException("Virtual interfaces can't be claimed");
    }

    @Override
    public void claim(final UsbInterfacePolicy policy) throws UsbException {
    }

    @Override
    public void release() throws UsbException {
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
    public byte getActiveSettingNumber() {
        return 0;
    }

    @Override
    public UsbInterface getActiveSetting() {
        return this;
    }

    @Override
    public UsbInterface getSetting(final byte number) {
        return this;
    }

    @Override
    public boolean containsSetting(final byte number) {
        return false;
    }

    @Override
    public List<UsbInterface> getSettings() {
        return this.settings;
    }

    @Override
    public List<UsbEndpoint> getUsbEndpoints() {
        return this.endpoints;
    }

    @Override
    public UsbEndpoint getUsbEndpoint(final byte address) {
        return null;
    }

    @Override
    public boolean containsUsbEndpoint(final byte address) {
        return false;
    }

    @Override
    public UsbConfiguration getUsbConfiguration() {
        return this.configuration;
    }

    @Override
    public UsbInterfaceDescriptor getUsbInterfaceDescriptor() {
        return this.descriptor;
    }

    @Override
    public String getInterfaceString() {
        return null;
    }
}
