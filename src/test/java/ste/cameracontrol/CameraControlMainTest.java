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

package ste.cameracontrol;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.lang.reflect.Field;
import junit.framework.TestCase;

import ste.cameracontrol.ui.CameraControlWindow;

/**
 *
 * @author ste
 */
public class CameraControlMainTest extends TestCase {

    private CameraControlMain cameraControl = null;
    private CameraControlWindow window = null;
    
    public CameraControlMainTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LibusbJava.init(new CanonEOS1000D(false));

        cameraControl = new CameraControlMain();

        window = getWindow();

        //
        // status monitor should be now running
        //
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private CameraControlWindow getWindow() throws Exception {
        Field f = CameraControlMain.class.getDeclaredField("window");
        f.setAccessible(true);

        return (CameraControlWindow)f.get(cameraControl);
    }

    public void testCameraConnected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(window.status);
    }

    public void testCameraDisconnected() throws Exception {
        //
        // we need to simulate a connection otherwise the status will not change
        //
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        window.status = null;
        LibusbJava.init(new CanonEOS1000D(false));
        Thread.sleep(100);
        assertNotNull(window.status);
    }

    public void testCameraNameDetected() throws Exception {
        LibusbJava.init(new CanonEOS1000D(true));
        Thread.sleep(100);
        assertNotNull(window.camera);
        assertTrue("found " + window.camera, window.camera.indexOf("1000D") >= 0);
    }

}
