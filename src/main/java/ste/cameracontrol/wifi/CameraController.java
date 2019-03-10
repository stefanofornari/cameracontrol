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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.OpenSessionOperation;
import ste.ptp.PTPException;
import ste.ptp.ip.Constants;
import ste.ptp.ip.InitCommandAcknowledge;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.InitError;
import ste.ptp.ip.InitEventRequest;
import ste.ptp.ip.OperationRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;
import ste.ptp.ip.PacketOutputStream;

/**
 * See http://www.gphoto.org/doc/ptpip.php for some information on PTPIP protocol
 */
public class CameraController {

    public static final int TIMEOUT_CONNECT = 2000;
    public static final int PORT = 15740;

    public static final String CLIENT_NAME = "Wi-4000D-Fi";
    // MD5(CLIENT_NAME);
    public static final byte[] CLIENT_ID = new byte[] {
        (byte)0xF7, (byte)0xA8, (byte)0xAD, (byte)0xCB,
        (byte)0x95, (byte)0x64, (byte)0x92, (byte)0x53,
        (byte)0x62, (byte)0xFA, (byte)0x2E, (byte)0x5B,
        (byte)0x0B, (byte)0xC4, (byte)0xA1, (byte)0xA0
    };
    public static final String CLIENT_VERSION = "1.0";

    private Socket socket;

    private byte[] cameraId;
    private int    sessionId;
    private String cameraName;
    private String cameraSwVersion;

    private int reqCounter = 0, resCounter = 0;

    private String host;

    public void connect(String host)
    throws CameraNotAvailableException, PTPException {
        final String[] errors = new String[] {
            "command request error", "event request error"
        };

        reset();

        int step = 0;
        try {
            if (InetAddress.getByName(host).isReachable(TIMEOUT_CONNECT)) {
                this.host = host;

                request(
                    new PTPIPContainer(new InitCommandRequest(CLIENT_ID, CLIENT_NAME, CLIENT_VERSION))
                );

                PTPIPContainer response = response();
                if (response.payload.getType() == Constants.INIT_COMMAND_ACK) {
                    InitCommandAcknowledge payload = (InitCommandAcknowledge)response.payload;
                    cameraId = payload.guid;
                    cameraName = payload.hostname;
                    sessionId = payload.sessionId;
                    cameraSwVersion = payload.version;

                    try {socket.close();} catch (IOException x) {};  // we need to reopen a connection after InitCommandRequest

                    ++step;
                    request(
                        new PTPIPContainer(new InitEventRequest(sessionId))
                    );

                    response = response();
                }

                return;
            }
        } catch (PTPException x) {
            x.printStackTrace();
            throw new PTPException(errors[step], x.getErrorCode());
        } catch (ConnectException x) {
            //
            // nothing to do, a CameranNotAvailableException will be thrown
            //
        } catch (IOException x) {
            x.printStackTrace();
        }

        try {socket.close();} catch (IOException x) {};

        throw new CameraNotAvailableException();
    }

    public void startRemoteSesssion() throws PTPException {
        try {
            request(
                new PTPIPContainer(new OperationRequest(new OpenSessionOperation()))
            );
            PTPIPContainer response = response();
            System.out.println("response.type: " + response.type);
        } catch (IOException x) {
            throw new PTPException("io error", x);
        }
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
        return cameraSwVersion;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getTransactionId() {
        return 1;
    }

    // --------------------------------------------------------- private methods

    private void request(PTPIPContainer packet) throws IOException {
        socket = new Socket();
        socket.setSoTimeout(0);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.connect(new InetSocketAddress(host, PORT));

        PacketOutputStream out = new PacketOutputStream(
            new TeeOutputStream(
                socket.getOutputStream(),
                new FileOutputStream(new File("cc-req-" + (++reqCounter) + ".dump"))
            )
        );

        out.write(packet); out.flush();
    }

    private PTPIPContainer response() throws IOException, PTPException {
        PacketInputStream in = new PacketInputStream(
            new TeeInputStream(
                socket.getInputStream(),
                new FileOutputStream(new File("cc-res-" + (++resCounter) + ".dump"))
            )
        );

        PTPIPContainer packet = in.readPTPContainer();

        switch (packet.type) {
            case Constants.INIT_COMMAND_ACK:
                cameraId = ((InitCommandAcknowledge)packet.payload).guid;
                return packet;

            case Constants.INIT_EVENT_ACK:
            case Constants.OPERATION_RESPONSE:
                return packet;

            case Constants.INIT_COMMAND_FAIL:
                InitError error = (InitError)packet.payload;
                throw new PTPException(error.error);

        }

        throw new IOException("protocol error (type:  " + packet.type + ")");
    }

    private void reset() {
        host = null;
        socket = null;
        cameraName = null;
        cameraId = null;
        cameraSwVersion = null;
        sessionId = 0;
        reqCounter = 0;
        resCounter = 0;
    }
}
