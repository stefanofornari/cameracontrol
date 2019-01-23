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
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.input.TeeInputStream;
import ste.ptp.ip.InitCommandAcknowledge;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;
import ste.ptp.ip.PayloadWriter;

/**
 *
 */
public class CameraHost implements Runnable {

    private static final byte[] GUID = new byte[] {
        (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
        (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
        (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b,
        (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f
    };
    private static final String NAME = "MYHOST";

    public byte[] acceptedClientId;
    public String name;
    public byte[] cameraId;


    public ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public List<PTPIPContainer> packets = new ArrayList<>();

    private Thread worker;
    private ServerSocket server;

    public boolean isOn() {
        return worker.isAlive() && (server != null);
    }

    @Override
    public void run() {
        System.out.println("starting worker " + this + " " + Thread.currentThread());
        try {
            server = new ServerSocket(15740);
            Socket s = server.accept();

            PacketInputStream in = new PacketInputStream(new TeeInputStream(s.getInputStream(), buffer));

            PTPIPContainer packet = new PTPIPContainer();

            // ---
            // Introduction
            //

            //
            // INIT COMMAND REQUEST
            //

            //
            // length of the packet
            //
            int size = in.readLEInt();
            System.out.println("packet length: " + size);

            //
            // type of the packet
            //
            packet.type = in.readLEInt();
            System.out.println("packet type: " + packet.type);

            //
            // read introduction payload
            // TODO: to move in PacketInputStream
            //
            packet.payload = readIntroduction(in);

            packets.add(packet);

            //
            // INIT_COMMAND ACNOWLEDGE
            //
            new PayloadWriter().write(
                s.getOutputStream(),
                new PTPIPContainer(new InitCommandAcknowledge(0x01020304, cameraId, name, "1.0"))
            );

            s.close(); server.close();
        } catch (IOException x) {
            x.printStackTrace();
        }
        System.out.println("worker done " + this + " " + Thread.currentThread());
    }

    public void start() {
        worker = new Thread(this);
        worker.start();
    }

    // --------------------------------------------------------- private methods

    private InitCommandRequest readIntroduction(InputStream is) throws IOException {

        //
        // read guid
        //
        byte[] guid = new byte[16];
        is.read(guid);
        System.out.println("introduction - guid: " + Hex.encodeHexString(guid));

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
        int minor = is.read() | (is.read() << 8);
        int major = is.read() | (is.read() << 8);
        System.out.println("introduction - version: " + major + "." + minor);

        return new InitCommandRequest(guid, hostname.toString(), major + "." + minor);
    }
}
