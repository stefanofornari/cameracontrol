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

import javax.usb.event.UsbDeviceDataEvent;
import javax.usb.event.UsbDeviceErrorEvent;
import javax.usb.event.UsbDeviceEvent;
import javax.usb.event.UsbDeviceListener;

/**
 *
 * @author ste
 */
public class DeviceListeners extends
        EventListeners<UsbDeviceListener> implements UsbDeviceListener {

    /**
     * Constructs a new USB device listener list.
     */
    DeviceListeners() {
        super();
    }

    @Override
    public UsbDeviceListener[] toArray() {
        return getListeners().toArray(
                new UsbDeviceListener[getListeners().size()]);
    }

    @Override
    public void usbDeviceDetached(final UsbDeviceEvent event) {
        for (final UsbDeviceListener listener : toArray()) {
            listener.usbDeviceDetached(event);
        }
    }

    @Override
    public void errorEventOccurred(final UsbDeviceErrorEvent event) {
        for (final UsbDeviceListener listener : toArray()) {
            listener.errorEventOccurred(event);
        }
    }

    @Override
    public void dataEventOccurred(final UsbDeviceDataEvent event) {
        for (final UsbDeviceListener listener : toArray()) {
            listener.dataEventOccurred(event);
        }
    }
}
