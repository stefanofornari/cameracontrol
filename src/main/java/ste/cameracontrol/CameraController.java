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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.usb.UsbDevice;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import ste.ptp.DeviceInfo;
import ste.ptp.OutputStreamData;
import ste.ptp.PTPException;
import ste.ptp.eos.EosEvent;
import ste.ptp.eos.EosEventConstants;
import ste.ptp.eos.EosEventFormat;
import ste.ptp.eos.EosInitiator;

/**
 * This class is the centralized controller of the camera. It controls all
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
    private UsbDevice camera;
    private boolean cameraConnected;

    private Configuration configuration;
    //
    // I need to find a better name for this...
    //
    private EosInitiator device;

    /**
     * Creates a new CameraController
     */
    public CameraController() {
        listeners = new ArrayList<CameraListener>();
        cameraMonitorActive = false;
    }

    /**
     * Creates a new CameraController with the given configuration
     *
     * @param configuration the configuration object that contains the configuration
     *
     * @throws  IllegalArgumentException if configuration is null
     */
    public CameraController(Configuration configuration) {
        this();
        initialize(configuration);
    }

    /**
     * Initialize the controller with the given configuration
     *
     * @param configuration
     */
    public void initialize(Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration cannot be null");
        }

        this.configuration = configuration;

        cameraCleanup();
        checkCamera();
    }

    /**
     * initialize the controller with default configuration
     */
    public void initialize() {
        initialize(new Configuration());
    }


    /**
     * Returns the configuration object used by this CameraController
     *
     * @return the configuration object used by this CameraController
     */
    public Configuration getConfiguration() {
        return configuration;
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
        setConnected();
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
    public void dumpEvents()
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

        device.checkEvents();
    }

    /**
     * Command the camera to take a picture and download the foto(s) from the
     * camera. For now fotos are are saved with the name given by the camera
     * under a configured directory. It may change in the future.
     * If an error occurs, a PTPException is thrown.
     *
     * @return the Photo made available after the shot
     *
     * @throws PTPException in case of errors
     */
    public Photo[] shootAndDownload() throws PTPException {
        sanityCheck();
        device.initiateCapture (0, 0);

        ArrayList<EosEvent> objects = new ArrayList<EosEvent>();
        for (int i=0; i<5; ++i) {
            List<EosEvent> events = device.checkEvents();
            for (EosEvent e: events) {
                if (e.getCode() == EosEventConstants.EosEventObjectAddedEx) {
                    objects.add(e);
                }
            }

            //
            // We wait for a while to give give some break to the camera while
            // preparing the images
            //
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                //
                // What to do???
                //
            }
        }

        Set<Photo> photos = new HashSet<Photo>();
        Photo photo = null;
        for (EosEvent e: objects) {
            String name = FilenameUtils.getBaseName(e.getStringParam(6));
            for (Photo p: photos) {
                if (p.getName().equals(name)) {
                    photo  = p;
                    break;
                }
            }

            if (photo == null) {
                photo = new Photo(name);
            }

            downloadPhoto(
                e.getIntParam(1),
                e.getIntParam(5),
                photo,
                !StringUtils.endsWithIgnoreCase(e.getStringParam(6), ".jpg")
            );
            photos.add(photo);
        }

        return photos.toArray(new Photo[photos.size()]);
    }

    /**
     * Downloads a photo from the camera given its id, size and Photo object.
     * The photo object is loaded on the jpeg or raw data buffer based on the
     * value of the raw parameter.
     *
     * @param id object id on the camera
     * @param size size of the object
     * @param photo Photo object where to load the item
     * @param raw is the object the raw image (true) or jpeg(false)?
     *
     * @return an Photo object with the downloaded photo
     *
     * @throws PTPException in case of communication or protocol errors
     */
    public void downloadPhoto(int id, int size, Photo photo, boolean raw)
    throws PTPException {
        sanityCheck();

        FileOutputStream file = null;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream(size);
            OutputStreamData data =
                new OutputStreamData(buf, device);
            device.getPartialObject(id, 0, size, data);
            device.transferComplete(id);

            if (raw) {
                photo.setRawData(buf.toByteArray());
            } else {
                photo.setJpegData(buf.toByteArray());
            }
        } catch (Exception e) {
            throw new PTPException("Unable to store the object: " + e.getMessage(), e);
        } finally {
            if (file != null) {
                try { file.close(); } catch (Exception e) {}
            }
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

        if (device == null) {
            device = new EosInitiator(camera);
        }

        if (session) {
            device.openSession();
        }
    }

    public void releaseCamera() {
        try {
            device.closeSession();
        } catch (Exception e) {
            // ignore everything!
        }
        cameraCleanup();
    }

    /**
     * Save the given Photo in the controller's configured directory
     *
     * @param photo - NULL
     *
     * @throws IOException in case of IO errors
     */
    public void savePhoto(Photo photo) throws IOException {
        if (photo.getJpegData() != null) {
            File f = new File(configuration.getImageDir(), photo.getName() + ".JPG");
            FileUtils.writeByteArrayToFile(f, photo.getJpegData());
        }

        if (photo.getRawData() != null) {
            File f = new File(configuration.getImageDir(), photo.getName() + ".CR2");
            FileUtils.writeByteArrayToFile(f, photo.getRawData());
        }
    }

    /**
     * Runs the camera detecting monitor (required by Runnable)
     */
    @Override
    public void run() {
        cameraMonitorActive = true;

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

    //
    // SPIKE
    //
    public void shutdown() {
        cameraCleanup();
    }

    // --------------------------------------------------------- private methods

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

        if (dev == null) {
            cameraCleanup();
        } else {
            camera = dev;
            cameraConnected = true;
        }
    }

    private void sanityCheck() throws PTPException {
        if (device == null) {
            throw new CameraNotAvailableException();
        }
    }

    private void cameraCleanup() {
        connection = new CameraConnection();
        camera = null;
        cameraConnected = false;
        if (device != null) {
            try {
                device.close();
            } catch (Exception ignore) {
            } finally {
                device = null;
            }
        }
    }

}
