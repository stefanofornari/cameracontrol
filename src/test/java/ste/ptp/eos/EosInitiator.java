/*
 * Camera Control
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

import ch.ntb.usb.Device;
import java.util.ArrayList;
import ste.ptp.Response;

/**
 * This is a Mock of EosInitiator
 * 
 * @author ste
 */
public class EosInitiator {
    public static Device device = null;
    public static boolean inSession = false;
    public static boolean shootError = false;

    public static ArrayList<String> invoked = new ArrayList<String>();

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

    public int initiateCapture(int i1, int i2) {
        invoked.add("initiateCapture");
        
        return (shootError) ? Response.GeneralError : Response.OK;
    }

    public String getResponseString(int i) {
        return "error";
    }

}
