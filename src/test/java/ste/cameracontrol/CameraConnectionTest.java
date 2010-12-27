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

import ch.ntb.usb.devinf.CanonEOS1000D;
import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbDevice;
import junit.framework.TestCase;

/**
 *
 * @author ste
 */
public class CameraConnectionTest extends TestCase {
    
    public CameraConnectionTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testConnectionTrue() {
        LibusbJava.init(new CanonEOS1000D());
        
        CameraConnection connection = new CameraConnection();

        assertTrue(connection.isConnected());
    }

    public void testConnectionFalse() {
        LibusbJava.init(new CanonEOS1000D(false));

        CameraConnection connection = new CameraConnection();

        assertFalse(connection.isConnected());
    }

    public void testFindExistingDevice() {
        CanonEOS1000D devinfo = new CanonEOS1000D(true);
        LibusbJava.init(devinfo);

        CameraConnection connection = new CameraConnection();
        UsbDevice dev = connection.findCamera();

        assertEquals(devinfo.getProductId(), dev.getDescriptor().getProductId());
        assertEquals(devinfo.getVendorId(), dev.getDescriptor().getVendorId());
    }

}
