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


package ste.cameracontrol;

import java.util.ArrayList;
import java.util.List;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.UsbDevice;

import ste.ptp.DeviceInfo;
import ste.ptp.PTPException;
import ste.ptp.eos.EosEvent;
import ste.ptp.eos.EosEventFormat;
import ste.ptp.eos.EosInitiator;

/**
 * This class is the centralized controlled of the camera. It controls all
 * aspects of the interaction with the camera, from connectivity to picture
 * download and other controlling functions.
 *
 * @author ste
 */
public class CameraController implements Runnable {

    private final long POLLING_PERIOD = 50;

    private CameraConnection connection;

    private ArrayList<CameraListener> listeners;

    private boolean cameraMonitorActive;
    private Device camera;
    private boolean cameraConnected;
    //
    // I need to find a better name for this...
    //
    private EosInitiator device;

    /**
     * Creates a new CameraController
     */
    public CameraController() {
        connection = new CameraConnection();
        listeners = new ArrayList<CameraListener>();
        cameraMonitorActive = false;
        cameraConnected = false;
        camera = null;
        checkCamera();
    }

    /**
     * Checks the connection with the camera.
     * 
     * @return true if the camera is connected, false otherwise
     */
    public boolean isConnected() {
        return cameraConnected;
    }

    /**
     * Adds a CameraControllerListener to the list of listeners to be notified
     * of camera events.
     *
     * @param listener the listener to add - NOT NULL
     *
     * @trhows  IllegalArgumentException if listener is null
     *
     */
    public synchronized void addCameraListener(CameraListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }

        listeners.add(listener);
    }

    /**
     * Starts the camera detecting monitor in a new thread (if not already
     * started).
     */
    public synchronized void startCameraMonitor() {
        if (!cameraMonitorActive) {
            new Thread(this).start();
        }
    }

    /**
     * Stops the camera detecting monitor.
     */
    public synchronized void stopCameraMonior() {
        cameraMonitorActive = false;
    }

    /**
     * Retrieves and prints on the standard output the device capabilities of
     * the camera.
     * 
     * @throws PTPException in case of errors
     */
    public void devinfo() throws PTPException {
        sanityCheck();
        //
        // If the camera is not found we should not be here (an exception is
        // thrown).
        //
        DeviceInfo info = device.getDeviceInfo();

        info.dump(System.out);
        //
        // no session to close!
        //
    }

    /**
     * Retrieves camera's events and dispatches them to the listeners.
     *
     * @throws PTPException in case of not recoverable errors
     */
    public void getEvents()
    throws PTPException {
        sanityCheck();
        List<EosEvent> events = device.checkEvents();

        System.out.println("Events:");
        if (events.isEmpty()) {
             System.out.println("no events found");
        }

        for (EosEvent event: events) {
            System.out.println(EosEventFormat.format(event));
        }
    }

    /**
     * Command the camera to take a picture. If an error occurs, a PTPException
     * is thrown.
     *
     * @throws PTPException in case of errors
     */
    public void shoot() throws PTPException {
        sanityCheck();
        device.initiateCapture (0, 0);
        getEvents();
    }

    /**
     * Runs the camera detecting monitor (required by Runnable)
     */
    @Override
    public void run() {
        cameraMonitorActive = true;
        setConnected();
        while (cameraMonitorActive) {
            boolean cameraConnectedOld = cameraConnected;

            checkCamera(); 
            if (cameraConnectedOld != cameraConnected) {
                setConnected();
            }
            
            try {
                Thread.sleep(POLLING_PERIOD);
            } catch (Exception e) {
                break;
            }
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Used to set the connection status. Setting the connection status is
     * considered a status change, therefore invokes the registered
     * CameraListeners (if any).
     *
     * @param status the status of the connection: true if the camera is
     *        connected false otherwise.
     */
    private synchronized void setConnected() {
        if (listeners == null) {
            return;
        }

        for(CameraListener listener: listeners) {
            if (cameraConnected) {
                listener.cameraConnected(camera);
            } else {
                listener.cameraDisconnected(camera);
            }
        }
    }

    private synchronized void checkCamera() {
        UsbDevice dev = connection.findCamera();
        
        cameraConnected = (dev != null);

        if (dev == null) {
            camera = null;
            cameraConnected = false;
        } else {
            camera = USB.getDevice(
                         dev.getDescriptor().getVendorId(),
                         dev.getDescriptor().getProductId()
                     );
            cameraConnected = true;
        }
    }

    public void startCamera()
    throws PTPException {
        startCamera(true);
    }

    /**
     * Starts the connection with the camera. If session is true, a new session
     * is established. If session is true, but a session has been already
     * established, a BusyException is thrown.
     * 
     * @param session true if a new session should be established
     *
     * @return the communication endpoint
     *
     * @throws PTPException in case of PTP errors
     * @throws cameraBusyException in case a session is still active
     * @throws CameraNotAvailableException in case no cameras are connected
     */
    public void startCamera(boolean session)
    throws PTPException {
        checkCamera();
        if (camera == null) {
            //
            // No cameras found
            //
            throw new PTPException("Camera not available");
        }
        
        device = new EosInitiator(camera);

        if (session) {
            device.openSession();
        }

        System.out.print("PTP device at ");
        System.out.println(camera);
    }

    public void releaseCamera() {
        try {
            device.closeSession();
        } catch (Exception e) {
            // ignore everything!
        }
    }

    // --------------------------------------------------------- private methods

    private void sanityCheck() throws PTPException {
        if (device == null) {
            throw new CameraNotAvailableException();
        }
    }
}
