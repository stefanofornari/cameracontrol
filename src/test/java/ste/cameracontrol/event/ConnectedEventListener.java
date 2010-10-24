/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol.event;

import ch.ntb.usb.UsbDevice;
import ste.cameracontrol.BaseCameraListener;

/**
 *
 * @author ste
 */
public class ConnectedEventListener extends BaseCameraListener {

    public boolean fired = false;

    @Override
    public void cameraConnected(UsbDevice device) {
        fired = true;
    }

}
