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

import javax.usb.*;
import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

/**
 *
 * @author ste
 */
public class VirtualUSBServices implements UsbServices {

    private static final String IMPLEMENTATION_DESCRIPTION = "cameracontrol";
    private static final String IMPLEMENTATION_VERSION = "1.0.0";
    private static final String API_VERSION = "1.0.2";

    private final ServicesListeners listeners = new ServicesListeners();

    private VirtualRootUSBHub rootHub;

    /**
     * Returns the virtual USB services.
     *
     * @return The virtual USB services.
     */
    static VirtualUSBServices getInstance() {
        try {
            return (VirtualUSBServices) UsbHostManager.getUsbServices();
        } catch (ClassCastException x) {
            throw new RuntimeException("Looks like virtual USB is not the "
                    + "configured USB services implementation: " + x, x);
        } catch (UsbException x) {
            throw new RuntimeException("Unable to create USB services: " + x, x);
        }
    }

    /**
     *
     */
    public VirtualUSBServices() {
        setConnectionStatus(false);
    }

    public void setConnectionStatus(boolean connected) {
        this.rootHub = new VirtualRootUSBHub(connected);
    }

    /**
     * Informs listeners about a new attached device.
     *
     * @param device the new attached device.
     */
    public void attach(final UsbDevice device) {
        this.listeners.usbDeviceAttached(new UsbServicesEvent(this, device));
    }

    /**
     * Informs listeners about a detached device.
     *
     * @param device the detached device.
     */
    public void detach(final UsbDevice device) {
        this.listeners.usbDeviceDetached(new UsbServicesEvent(this, device));
    }

    // ------------------------------------------------------------- UsbServices

    @Override
    public UsbHub getRootUsbHub() {
        return this.rootHub;
    }

    @Override
    public void addUsbServicesListener(final UsbServicesListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeUsbServicesListener(final UsbServicesListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public String getApiVersion() {
        return API_VERSION;
    }

    @Override
    public String getImpVersion() {
        return IMPLEMENTATION_VERSION;
    }

    @Override
    public String getImpDescription() {
        return IMPLEMENTATION_DESCRIPTION;
    }
}
