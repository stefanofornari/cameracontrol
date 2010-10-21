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

import ch.ntb.usb.USB;
import ch.ntb.usb.demo.AbstractDeviceInfo;

public class Canon1000D extends AbstractDeviceInfo {

    private boolean connected;

    @Override
    public void initValues() {
        setIdVendor((short) 0x8235);
        setIdProduct((short) 0x0222);
        setTimeout(2000);
        setConfiguration(1);
        setInterface(0);
        setAltinterface(-1);
        setOutEPBulk(0x01);
        setInEPBulk(0x82);
        setOutEPInt(0x03);
        setInEPInt(0x84);
        setSleepTimeout(2000);
        setMaxDataSize(USB.FULLSPEED_MAX_BULK_PACKET_SIZE);
        setMode(TransferMode.Bulk);
        setManufacturer("Canon");
        setProduct("1000D");
        setSerialVersion("00.10.00");
        setBusName("/dev/bus/usb/001");
        setFilename("001");
        setInterfaceClass((byte)0x00);
        setInterfaceSubClass((byte) 0x00);
        setInterfaceProtocol((byte) 0x00);
    }

    public Canon1000D(boolean connected) {
        this.connected = connected;
        setInterfaceClass((byte)((connected) ? 0x06 : 0x00));

    }

    public Canon1000D() {
        this(true);
    }

}
