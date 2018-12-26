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

import javax.usb.event.UsbServicesEvent;
import javax.usb.event.UsbServicesListener;

public class ServicesListeners extends
        EventListeners<UsbServicesListener> implements UsbServicesListener {

    /**
     * Constructs a new USB services listener list.
     */
    public ServicesListeners() {
        super();
    }

    @Override
    public UsbServicesListener[] toArray() {
        return getListeners().toArray(
                new UsbServicesListener[getListeners().size()]);
    }

    @Override
    public void usbDeviceAttached(final UsbServicesEvent event) {
        for (final UsbServicesListener listener : toArray()) {
            listener.usbDeviceAttached(event);
        }
    }

    @Override
    public void usbDeviceDetached(final UsbServicesEvent event) {
        for (final UsbServicesListener listener : toArray()) {
            listener.usbDeviceDetached(event);
        }
    }
}
