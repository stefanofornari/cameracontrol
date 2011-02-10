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
package ste.ptp.eos;

import java.util.ArrayList;
import ch.ntb.usb.Device;
import java.io.IOException;
import java.util.List;
import ste.ptp.Data;
import ste.ptp.NameFactory;
import ste.ptp.OutputStreamData;
import ste.ptp.PTPException;
import ste.ptp.Response;

/**
 * This is a Mock of EosInitiator
 * 
 * @author ste
 */
public class EosInitiator extends NameFactory {
    public static Device device = null;
    public static boolean inSession = false;
    public static boolean shootError = false;

    public static ArrayList<String> invoked = new ArrayList<String>();
    public static ArrayList<EosEvent> events = new ArrayList<EosEvent>();

    public EosInitiator(Device device) {
        this.device = device;
        inSession = false;
    }

    public void openSession() {
        invoked.add("openSession");
        inSession = true;
    }

    public void closeSession() {
        invoked.add("closeSession");
        inSession = false;
    }

    public void initiateCapture(int i1, int i2) throws PTPException {
        invoked.add("initiateCapture");

        if (shootError) {
            throw new PTPException("Error!", Response.GeneralError);
        }
    }

    public List<EosEvent> checkEvents() {
        //
        // we need to simulate that the events are consumed
        //
        List<EosEvent> ret = new ArrayList<EosEvent>(events.size());
        ret.addAll(events);
        events.clear();

        return ret;
    }

    public String getResponseString(int i) {
        return "error";
    }

    public void getPartialObject(int oid, int offset, int size, Data data)
    throws PTPException {
        invoked.add("getPartialObject");
        OutputStreamData out = (OutputStreamData) data;
        try {
            out.write(new byte[] {64}, 0, 1);
        } catch (IOException e) {
            throw new PTPException(e.getMessage());
        }
    }

    public void transferComplete(int oid) {
        invoked.add("transferComplete");
    }

    public void close() {
        invoked.add("close");
    }

}
