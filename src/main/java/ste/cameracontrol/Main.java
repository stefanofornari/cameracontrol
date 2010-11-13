// Copyright 2000 by David Brownell <dbrownell@users.sourceforge.net>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
package ste.cameracontrol;

import ch.ntb.usb.Device;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.devinf.CanonEOS1000D;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import ste.ptp.DeviceInfo;
import ste.ptp.DevicePropDesc;
import ste.ptp.Initiator;
import ste.ptp.Response;

/**
 * This is a command line tool, which currently supports
 * access only to PTP cameras.
 *
 * @version $Id: Main.java,v 1.20 2001/05/30 19:35:13 dbrownell Exp $
 * @author David Brownell
 */
public class Main {

    /** No instances permitted */
    private Main() {
    }
    
    // options
    static private File directory;
    static private String device;
    static private boolean overwrite;
    static private int storageId;
        
    private static void usage(int status) {
        System.err.println("Usage: jphoto command [options]");

        System.err.println("Key commands include:");
        System.err.println("  cameras ... lists devices by portid");
        System.err.println("  capture ... starts image/object capture");
        System.err.println("  devinfo ... shows device info");
        System.err.println("  devprops ... shows all device properties");
        System.err.println("  format ... reformat a storage unit");
        System.err.println("  images ... download images/videos to directory (default 'images')");
        System.err.println("  reset ... reset request");
        System.err.println("  storage ... shows storage info");
        System.err.println("  tree ... lists storage contents");

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
                + "http://code.google.com/p/cameracontrol/");
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

            for (String arg: line.getArgs()) {

                /*
                if ("cameras".equals (argv [c]) || "devices".equals (argv [c]))
                cameras (argv, c);
                else if ("capture".equals (argv [c]))
                capture (argv, c);
                else
                 */
                if ("devinfo".equals(arg)) {
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

        } catch (IOException e) {
            System.err.println("I/O exception: " + e.getMessage());
            // e.printStackTrace ();
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
            throws USBException {
        Initiator dev = null;

        try {
            dev = startCamera(false);
        } catch (USBException e) {
            System.err.println("ERROR: " + e.getMessage());
            return;
        }
        DeviceInfo info = dev.getDeviceInfo();

        info.dump(System.out);
        // no session to close!
    }

    private static void getprop(String argv[], int index)
            throws USBException {
        if (index != (argv.length - 2)) {
            usage(-1);
        }

        int propcode;
        Initiator dev;
        DeviceInfo info;

        propcode = DevicePropDesc.getPropertyCode(argv[index + 1]);
        if (propcode < 0) {
            System.err.println("unrecognized property name: "
                    + argv[index + 1]);
            System.err.println("'jphoto devinfo' lists device properties");
            System.exit(1);
        }
        dev = startCamera();

        try {
            info = dev.getDeviceInfo();

            if (!info.supportsProperty(propcode)) {
                System.err.println("device does not support property: "
                        + dev.getPropertyName(propcode));
            } else {
                DevicePropDesc desc;
                int status;

                desc = new DevicePropDesc(dev);
                status = dev.getDevicePropDesc(propcode, desc);
                if (status == Response.OK) {
                    desc.dump(System.out);
                } else {
                    System.out.print("... can't read ");
                    System.out.print(dev.getPropertyName(propcode));
                    System.out.print(", ");
                    System.out.println(dev.getResponseString(status));
                }
            }
        } finally {
            closeSession(dev);
        }
    }

    private static Initiator startCamera()
            throws USBException {
        return startCamera(true);
    }

    private static Initiator startCamera(boolean session)
            throws USBException {
        Device dev = null;
        Initiator retval;

        //
        // For now, let's look at the Canond EOS 1000D...
        //
        dev = USB.getDevice(CanonEOS1000D.VENDOR, CanonEOS1000D.PRODUCT);

        if (dev == null) {
            System.err.println("Camera not available");
            System.exit(1);
        }
        retval = new Initiator(dev);

        if (session) {
            retval.openSession();
        }

        System.out.print("PTP device at ");
        System.out.println(device);

        return retval;
    }

    static void closeSession(Initiator dev) {
        try {
            dev.closeSession();
        } catch (Exception e) {
            // ignore everything!
        }
    }
}
