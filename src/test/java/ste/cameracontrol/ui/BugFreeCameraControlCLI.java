/*
 * cameracontrol
 * Copyright (C) 2019 Stefano Fornari
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
package ste.cameracontrol.ui;

import java.net.Socket;
import org.apache.commons.codec.binary.Hex;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ste.cameracontrol.wifi.CameraSimulatorFactory;
import ste.cameracontrol.wifi.CameraUtils;
import ste.xtest.cli.BugFreeCLI;

/**
 *
 */
public class BugFreeCameraControlCLI extends BugFreeCLI {

    @BeforeClass
    public static void before_class() throws Exception {
        Socket.setSocketImplFactory(new CameraSimulatorFactory());
    }

    @Before
    public void before() throws Exception {
        STDOUT.clearLog();
    }

    @Test(timeout=3000)
    public void static_invocation() throws Exception {
        CameraControlCLI.main();
        then(STDOUT.getLog()).contains("Usage: ste.cameracontrol.ui.CameraControlCLI");
    }

    @Test
    public void show_syntax() throws Exception {
        new CameraControlCLI().launch();

        then(STDOUT.getLog()).contains("Usage: ste.cameracontrol.ui.CameraControlCLI");
    }

    @Test
    public void show_syntax_if_invalid_command() throws Exception {
        final String[][] ARGS = new String[][]{
            new String[]{},
            new String[]{"invalid"}
        };

        for (String[] A : ARGS) {
            STDOUT.clearLog();
            new CameraControlCLI().launch(A);
            if (A.length > 0) {
                then(STDOUT.getLog()).contains("Invalid arguments").contains("Usage:");
            } else {
                then(STDOUT.getLog()).contains("Usage:");
            }
        }
    }

    @Test
    public void show_help_if_command_is_help() throws Exception {
        new CameraControlCLI().launch("help");
        then(STDOUT.getLog()).contains("Usage:");
    }

    @Test
    public void show_upnp_descriptor() throws Exception {
        //
        // TODO
        //
    }

    @Test(/*timeout=5000*/)
    public void connect_ok() throws Exception {
        CameraUtils.givenStartedCamera();

        new CameraControlCLI().launch("connect", "localhost");
        then(STDOUT.getLog())
            .contains("Found EOS4000D on localhost/127.0.0.1\n")
            .contains("GUID: " + Hex.encodeHexString(CameraUtils.GUID1))
            .contains("SW version: 1.0");
    }
}
