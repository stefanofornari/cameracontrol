/*
 * cameracontrol
 * Copyright (C) 2018 Stefano Fornari
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
package ste.cameracontrol.wifi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.ip.Introduction;
import ste.ptp.ip.PTPIPContainer;

/**
 * See http://www.gphoto.org/doc/ptpip.php for some information on PTPIP protocol
 */
public class CameraController {

    public static final int TIMEOUT_CONNECT = 2000;
    public static final int PORT = 15740;

    public static final String CLIENT_ID = "cc"; // TODO to be turned into a 16 bytes GUID
    public static final String CLIENT_NAME = "Camera Control";
    public static final byte[] CLIENT_VERSION = new byte[] {0, 1};

    private Socket socket;

    public void connect(String host) throws CameraNotAvailableException {
        OutputStream out = null;
        try {
            if (InetAddress.getByName(host).isReachable(TIMEOUT_CONNECT)) {
                socket = new Socket(host, PORT);
                out = socket.getOutputStream();

                out.write(new PTPIPContainer(new Introduction(CLIENT_ID, CLIENT_NAME, CLIENT_VERSION)).toByteArray());

                return;
            }
        } catch (IOException x) {
        } finally {
            if (out != null) {
                try {out.close();} catch (IOException x) {};
            }
        }

        throw new CameraNotAvailableException();
    }

    public boolean isConnected() {
        return ((socket != null) && socket.isConnected());
    }

}
