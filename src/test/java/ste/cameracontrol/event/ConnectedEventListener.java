/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ste.cameracontrol.event;

import ch.ntb.usb.Device;
import ste.cameracontrol.BaseCameraListener;

/**
 *
 * @author ste
 */
public class ConnectedEventListener extends BaseCameraListener {

    public Device device = null;

    @Override
    public void cameraConnected(Device device) {
        this.device = device;
    }

    @Override
    public void cameraDisconnected(Device device) {
        this.device = null;
    }
}
