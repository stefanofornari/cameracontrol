/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol.event;

import javax.usb.UsbDevice;
import ste.cameracontrol.BaseCameraListener;

/**
 *
 * @author ste
 */
public class ConnectedEventListener extends BaseCameraListener {

    public UsbDevice device = null;

    @Override
    public void cameraConnected(UsbDevice device) {
        this.device = device;
    }

    @Override
    public void cameraDisconnected(UsbDevice device) {
        this.device = null;
    }
}
