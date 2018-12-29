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
package ste.cameracontrol.ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import ste.cameracontrol.CameraController;
import ste.cameracontrol.Configuration;
import ste.cameracontrol.Photo;
import ste.ptp.PTPBusyException;
import ste.ptp.PTPException;

/**
 * This is a command line tool to access EOS PTP cameras.
 *
 * @author Stefano Fornari
 */
public class CameraControlCLI {

    /** No instances permitted */
    private CameraControlCLI() {
    }

    // options
    static private File directory;
    static private String device;
    static private boolean overwrite;
    static private int storageId;

    static private CameraController camera = null;

    private static void usage(int status) {
        System.err.println("Usage: jphoto command [options]");

        System.err.println("Key commands include:");
        System.err.println("  cameras ... lists devices by portid");
        System.err.println("  get-events ... checks and list camera events");
        System.err.println("  capture ... starts image/object capture");
        System.err.println("  devinfo ... shows device info");
        System.err.println("  devprops ... shows all device properties");
        System.err.println("  format ... reformat a storage unit");
        System.err.println("  images ... download images/videos to directory (default 'images')");
        System.err.println("  reset ... reset request");
        System.err.println("  storage ... shows storage info");
        System.err.println("  tree ... lists storage contents");
        System.err.println("  shoot ... take a picture without transferring the impage");

        System.err.println("Other commands include:");
        // System.err.println ("  delete filename ... deletes one object");
        System.err.println("  devices ... (same as 'cameras')");
        System.err.println("  getprop propname ... shows one device property");
        System.err.println("  help ... shows this message");
        System.err.println("  powerdown ... powers down device");
        // System.err.println ("  print ... prints according to DPOF order");
        System.err.println("  put fileOrURL [...] ... copy object(s) to device");
        System.err.println("  selftest ... runs basic selftest");
        // System.err.println ("  settime ... sets clock on device");
        System.err.println("  status ... status summary");
        System.err.println("  thumbs ... download thumbs to directory (default 'thumbs')");

        System.err.println("Options include:");
        System.err.println("  --camera value (or, -c value)");
        System.err.println("  --directory value (or, -d value)");
        System.err.println("  --help (or, -h)");
        System.err.println("  --overwrite (or, -w)");
        System.err.println("  --storage  value (or, -s value)");

        System.err.println("Documentation and Copyright at:  "
                + "http://code.google.com/p/cameracontrol");
        System.err.println("Licensed under the Affero GNU General Public License 3.0.");
        System.err.println("No Warranty.");
        System.err.println();

        if (status != 0) {
            System.exit(status);
        }
    }

    /**
     * Parameters are a command and any option parameters
     * such as <em><b>--camera</b> port-id</em>
     * specifying the the port id for a camera.
     * (See <em>usb.core.PortIdentifier</em> for information
     * about those identifiers.)
     * Such port ids may be omitted when there is only one
     * camera currently connected; list them
     * using the <em>cameras</em> commands.
     * PTP devices may support only some of these commands.
     *
     * <table border=1 cellpadding=3 cellspacing=0 width="80%">
     * <tr bgcolor="#ccccff" class="TableHeadingColor">
     *  <th>Command and Arguments</th>
     *  <th>Description</th>
     *  <th>Options</th>
     *  </tr>
     *
     * <tr valign=top>
     *	<td> <code>cameras</code> </td>
     *	<td> (same as "devices") </td>
     *	<td> <em>none</em> </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>capture</code> </td>
     *	<td> starts capturing images or other objects, according
     *		to the current device properties. </td>
     *	<td> <em>--port-id</em> id <br />
     *	     <em>--storage</em> id
     *		</td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>devices</code> </td>
     *	<td> Lists PTP devices with their port identifiers </td>
     *	<td> <em>none</em> </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>devinfo</code> </td>
     *	<td> Displays the DeviceInfo for a camera, including
     *		all the operations, events, device properties,
     *		and object formats supported. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>devprops</code> </td>
     *	<td> shows all device properties, with types and values. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>format</code> </td>
     *	<td> Reformats the specified storage unit (zero based).  </td>
     *	<td> <em>--port-id</em> id
     *		<br> <em>--storage</em> number
     *		</td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>getprop</code> <em>propname</em></td>
     *	<td> shows named device property, with type and value. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>help</code> </td>
     *	<td> shows command summary</td>
     *	<td> <em>none</em> </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>images</code> </td>
     *	<td> Downloads image files to directory </td>
     *	<td> <em>--port-id</em> id
     *		<br> <em>--overwrite</em>
     *		<br> <em>--directory</em> directory (default "images") </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>powerdown</code> </td>
     *	<td> Causes the device to power down. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>put</code> <em>file-or-URL [...]</em> </td>
     *	<td> Copies images or other objects to device.  </td>
     *	<td> <em>--port-id</em> id <br />
     *	     <em>--storage</em> id
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>reset</code> </td>
     *	<td> Issues a PTP level reset. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>selftest</code> </td>
     *	<td> Runs a basic device self test. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>status</code> </td>
     *	<td> Shows status summary for the device </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>storage</code> </td>
     *	<td> Displays the StorageInfo for the device's
     *		storage units, all or just the specified (zero base) store </td>
     *	<td> <em>--port-id</em> id
     *		<br> <em>--storage</em> number
     *		</td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>thumbs</code> </td>
     *	<td> Downloads image thumbnails to directory </td>
     *	<td> <em>--port-id</em> id
     *		<br> <em>--overwrite</em>
     *		<br> <em>--directory</em> directory (default "thumbs") </td>
     * </tr>
     *
     * <tr valign=top>
     *	<td> <code>tree</code> </td>
     *	<td> Lists contents of camera storage. </td>
     *	<td> <em>--port-id</em> id </td>
     * </tr>
     *
     * </table>
     */
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            usage(-1);
        }

        Options options = new Options();
        options.addOption(
            OptionBuilder.withLongOpt( "camera" )
                         .withArgName("c")
                         .hasArg()
                         .withArgName("value")
                         .create()
        );
        options.addOption("d", "directory", true , "");
        options.addOption("h", "help"     , false, "");
        options.addOption("w", "overwrite", false, "");
        options.addOption("s", "storage"  , true , "");

        CommandLineParser parser = new PosixParser();
        CommandLine line = parser.parse(options, args);

        try {
            if (line.hasOption('c')) {
                device = line.getOptionValue('c');
            }
            if (line.hasOption('d')) {
                directory = new File(line.getOptionValue('d'));

            } else {
                directory = new File("images");
            }

            if (line.hasOption('h')) {
                usage(0);
                        System.exit(0);
            }
            if (line.hasOption('s')) {
                storageId = Integer.parseInt(line.getOptionValue('s'));
                if (storageId < 0) {
                    System.err.println("--storage N ... "
                            + "parameter must be an integer");
                    usage(-1);
                }
            }
            if (line.hasOption('w')) {
                overwrite = true;
            }

            if (!directory.exists()) {
                directory.mkdirs();
            }

            Configuration c = new Configuration();
            c.setImageDir(directory.getAbsolutePath());
            camera = new CameraController(c);
            camera.startCamera();

            for (String arg: line.getArgs()) {

                /*
                if ("cameras".equals (argv [c]) || "devices".equals (argv [c]))
                cameras (argv, c);
                else
                */
                if ("get-events".equals(arg)) {
                    getEvents();
                } else if ("shoot".equals(arg)) {
                    shoot();
                } else if ("devinfo".equals(arg)) {
                    devinfo();
                } /*else if ("devprops".equals (argv [c]))
                devprops (argv, c);
                else if ("format".equals (argv [c]))
                format (argv, c);
                else if ("getprop".equals (argv [c]))
                getprop (argv, c);
                 */ else if ("help".equals(arg)) {
                    usage(0);
                } /*
                else if ("images".equals (argv [c]))
                images (argv, c);
                else if ("put".equals (argv [c]))
                put (argv, c);
                else if ("powerdown".equals (argv [c]))
                powerdown (argv, c);
                else if ("reset".equals (argv [c]))
                reset (argv, c);
                else if ("selftest".equals (argv [c]))
                selftest (argv, c);
                else if ("status".equals (argv [c]))
                status (argv, c);
                else if ("storage".equals (argv [c]))
                storage (argv, c);
                else if ("thumbs".equals (argv [c]))
                thumbs (argv, c);
                else if ("tree".equals (argv [c]))
                tree (argv, c);
                 */ else {
                    usage(-1);
                }
            }

        } catch (UnsupportedOperationException e) {
            System.err.println("Device does not support " + e.getMessage());
            System.exit(1);

        } catch (PTPBusyException x) {
            System.err.println("The camera is busy. Please make sure not any other program is using and locking the device.");
            System.exit(1);
        } catch (PTPException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (SecurityException e) {
            System.err.println(e.getMessage());
            System.exit(1);

        }
    }

    /*--------------------------------------------------------------------*/
    private static void indent(PrintStream out, int depth) {
        while (depth >= 8) {
            out.print("\t");
            depth -= 8;
        }
        while (depth != 0) {
            out.print(" ");
            depth--;
        }
    }

    private static void devinfo()
    throws PTPException {
        camera.devinfo();
    }

    private static void getEvents()
    throws PTPException {
        camera.dumpEvents();
    }


    private static void shoot()
    throws IOException, PTPException {
        Photo[] photos = camera.shootAndDownload();

        for (Photo photo: photos) {
            camera.savePhoto(photo);
        }
    }
}
