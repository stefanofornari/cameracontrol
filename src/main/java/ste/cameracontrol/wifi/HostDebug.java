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

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class HostDebug {

    public final static int PORT = 15740;


    public static void main(String... args) throws Exception {
        System.out.println("listening on " + PORT);
        ServerSocket server = new ServerSocket(PORT);
        Socket s = server.accept();

        InputStream in = s.getInputStream();

        int b = 0, c=0;
        while ((b = in.read()) >= 0) {
            if ((c++ % 16) == 0) {
                System.out.println();
            }
            System.out.printf("%#04x ", (byte)b);
        }

        s.close(); server.close();

        System.out.println("done, exiting... ");
    }

    // --------------------------------------------------------- private methods
}
