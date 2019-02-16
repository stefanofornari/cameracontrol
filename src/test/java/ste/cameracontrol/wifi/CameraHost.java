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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import org.apache.commons.codec.binary.Hex;

/**
 *
 */
public class CameraHost extends SocketImpl {

    private final ByteArrayOutputStream OUT = new ByteArrayOutputStream();

    private static final byte[] GUID = new byte[] {
        (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03,
        (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07,
        (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b,
        (byte)0x0c, (byte)0x0d, (byte)0x0e, (byte)0x0f
    };

    public IOException error;

    public byte[] response;

    public byte[] getWrittenBytes() {
        return OUT.toByteArray();
    }

    // -------------------------------------------------------------- SocketImpl

    @Override
    protected void create(boolean stream) throws IOException {}

    @Override
    protected void connect(String host, int port) throws IOException {
        connect();
    }

    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        connect();
    }

    @Override
    protected void connect(SocketAddress address, int timeout) throws IOException {
        connect();
    }

    @Override
    protected void bind(InetAddress host, int port) throws IOException {}

    @Override
    protected void listen(int backlog) throws IOException {}

    @Override
    protected void accept(SocketImpl s) throws IOException {}

    @Override
    protected InputStream getInputStream() throws IOException {
        System.out.println(this + " returning " + Hex.encodeHexString(response));
        return new ByteArrayInputStream(response);
    }

    @Override
    protected OutputStream getOutputStream() throws IOException {
        return OUT;
    }

    @Override
    protected int available() throws IOException {
        return 0;
    }

    @Override
    protected void close() throws IOException {}

    @Override
    protected void sendUrgentData(int data) throws IOException {
        throw new UnsupportedOperationException("sendUrgentData not supported yet.");
    }

    @Override
    public void setOption(int optID, Object value) throws SocketException {}

    @Override
    public Object getOption(int optID) throws SocketException {
        return null;
    }

    // --------------------------------------------------------- private methods

    private void connect() throws IOException {
        if (error != null) {
            throw error;
        }
    }
}
