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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.input.TeeInputStream;
import ste.ptp.ip.Introduction;
import ste.ptp.ip.PTPIPContainer;

/**
 *
 */
public class CameraHost implements Runnable {

    public String acceptedClientId;
    public ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public List<PTPIPContainer> packets = new ArrayList<>();

    private Thread worker;

    public boolean isOn() {
        return worker.isAlive();
    }

    @Override
    public void run() {
        try {
            ServerSocket ss = new ServerSocket(15740);
            Socket s = ss.accept();

            InputStream in = new TeeInputStream(s.getInputStream(), buffer);

            PTPIPContainer packet = new PTPIPContainer();

            //
            // length of the packet
            //
            packet.size = readLittleEndianInt(in);
            System.out.println("packet length: " + packet.size);

            //
            // type of the packet
            //
            packet.type = readLittleEndianInt(in);
            System.out.println("packet type: " + packet.type);

            //
            // read introduction payload
            //
            packet.payload = readIntroduction(in);

            packets.add(packet);


        } catch (IOException x) {
            x.printStackTrace();
        }
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    // --------------------------------------------------------- private methods

    private int readLittleEndianInt(InputStream is) throws IOException {
        return (is.read() & 0xff)
             | ((is.read() & 0xff)<<8)
             | ((is.read() & 0xff)<<16)
             | ((is.read() & 0xff)<<24);
    }

    private Introduction readIntroduction(InputStream is) throws IOException {

        //
        // read guid
        //
        byte[] guid = new byte[] { (byte)is.read(), (byte)is.read() };
        System.out.println("introduction - guid: " + new String(guid));

        //
        // read hostname + UTF16(0) (max 256 chars)
        //
        StringBuilder hostname = new StringBuilder();
        for (int i=0; i<256; ++i) {
            char c = (char)(is.read() | (is.read()<<8));

            if (c == 0) {
                break; // 0-terminated string
            }
            hostname.append(c);
        }
        System.out.println("introduction - hostname: " + hostname);

        //
        // client version number
        //
        byte[] version = new byte[2];
        is.read(version);
        System.out.println("introduction - version: " + version[0] + "." + version[1]);

        return new Introduction(new String(guid), hostname.toString(), version);
    }
}
