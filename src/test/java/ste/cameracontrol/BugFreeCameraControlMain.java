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

import java.io.File;
import java.lang.reflect.Field;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import ste.cameracontrol.ui.CameraControlWindow;
import ste.xtest.reflect.PrivateAccess;

/**
 *
 */
public class BugFreeCameraControlMain {

    @Rule
    public final ProvideSystemProperty IMAGEDIR
	 = new ProvideSystemProperty(Configuration.CONFIG_IMAGEDIR, "/tmp/cameracontrol");

    @Test
    public void controller_configuration() throws Exception {
        CameraControlMain cameraControl = new CameraControlMain();
        CameraController camera = (CameraController)PrivateAccess.getInstanceValue(cameraControl, "camera");
        Configuration c = camera.getConfiguration();
        then(c.getImageDir()).isNotNull();
        then((new File(c.getImageDir()))).exists();
    }

    public void camera_connected() throws Exception {
        CameraControlWindow window = getWindow();
        System.out.println("/testCameraConnected");
        Thread.sleep(100);
        System.out.println("testCameraConnected/");
        then(window.status).isNotNull();
        then(window.cameraControlsEnabled).isTrue();
    }

    public void camera_disconnected() throws Exception {
        //
        // we need to simulate a connection otherwise the status will not change
        //
        CameraControlWindow window = getWindow();
        Thread.sleep(100);
        window.status = null;
        Thread.sleep(100);
        then(window.status).isNull();
        then(window.cameraControlsEnabled).isFalse();
    }

    public void camera_name_detected() throws Exception {
        CameraControlWindow window = getWindow();
        Thread.sleep(100);
        then(window.status).isNotNull();
        then(window.status.indexOf("1000D")).isGreaterThanOrEqualTo(0);
        // "found " + window.status
    }

    // --------------------------------------------------------- private methods

    private CameraControlWindow getWindow() throws Exception {
        Field f = CameraControlMain.class.getDeclaredField("window");
        f.setAccessible(true);

        return (CameraControlWindow)f.get(new CameraControlMain());
    }


}
