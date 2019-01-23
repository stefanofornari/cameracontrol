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
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.ip.InitCommandAcknowledge;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;
import ste.ptp.ip.PayloadWriter;

/**
 * See http://www.gphoto.org/doc/ptpip.php for some information on PTPIP protocol
 */
public class CameraController {

    public static final int TIMEOUT_CONNECT = 2000;
    public static final int PORT = 15740;

    public static final String CLIENT_NAME = "Camera Control";
    // MD5(CLIENT_NAME);
    public static final byte[] CLIENT_ID = new byte[] {
            (byte)0x69, (byte)0x61, (byte)0x75, (byte)0x16,
            (byte)0x6c, (byte)0x22, (byte)0x82, (byte)0xe8,
            (byte)0x95, (byte)0xf3, (byte)0x60, (byte)0x30,
            (byte)0xbd, (byte)0x25, (byte)0x56, (byte)0x8f
    };
    public static final String CLIENT_VERSION = "1.0";

    private Socket socket;

    private byte[] cameraId;
    private String cameraName;

    public void connect(String host) throws CameraNotAvailableException {
        try {
            if (InetAddress.getByName(host).isReachable(TIMEOUT_CONNECT)) {
                socket = new Socket(host, PORT);

                request(
                    new PTPIPContainer(new InitCommandRequest(CLIENT_ID, CLIENT_NAME, CLIENT_VERSION))
                );

                PTPIPContainer response = response();

                InitCommandAcknowledge payload = (InitCommandAcknowledge)response.payload;
                cameraId = payload.guid;
                cameraName = payload.hostname;

                return;
            }
        } catch (ConnectException x) {
            //
            // nothing to do, a CameranNotAvailableException will be thrown
            //
        } catch (IOException x) {
            x.printStackTrace();
        } finally {
            if (socket != null) {
                try {socket.close();} catch (IOException x) {};
            }
        }

        throw new CameraNotAvailableException();
    }

    public boolean isConnected() {
        return ((socket != null) && socket.isConnected());
    }

    public String getCameraName() {
        return cameraName;
    }

    public byte[] getCameraGUID() {
        return cameraId;
    }

    public String getCameraSwVersion() {
        return "1.0";
    }

    public int getConnectionNumber() {
        return 0x00000001;
    }

    // --------------------------------------------------------- private methods

    private void request(PTPIPContainer packet) throws IOException {
        new PayloadWriter().write(socket.getOutputStream(), packet);
    }

    private PTPIPContainer response() throws IOException {
        PacketInputStream in = new PacketInputStream(socket.getInputStream());

        PTPIPContainer ret = new PTPIPContainer();
        int size = in.readLEInt();
        int type = in.readLEInt();

        System.out.println("size: " + size);
        System.out.println("type: " + type);

        InitCommandAcknowledge ack = in.readInitCommandAcknowledge();
        cameraId = ack.guid;

        return new PTPIPContainer(ack);
    }
}
