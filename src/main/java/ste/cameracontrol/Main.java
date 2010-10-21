package ste.cameracontrol;

import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.UsbBus;
import ch.ntb.usb.UsbDevice;
import ch.ntb.usb.UsbDeviceDescriptor;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        LibusbJava.usb_init();
        LibusbJava.usb_find_busses();
        LibusbJava.usb_find_devices();

        UsbBus bus = LibusbJava.usb_get_busses();

        while (bus != null) {
            UsbDevice device = bus.getDevices();

            while (device != null) {
                UsbDeviceDescriptor desc = device.getDescriptor();

                System.out.println(bus.getDirname() + "/" + device.getFilename());
                System.out.println("class: " + desc.getDeviceClass());
                System.out.println("subclass: " + desc.getDeviceSubClass());
                System.out.println("protocol: " + desc.getDeviceProtocol());

                device = device.getNext();
            }

            bus = bus.getNext();
        }
    }
}
