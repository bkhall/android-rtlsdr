package com.hoho.android.usbserial.driver;

/**
 * Registry of USB vendor/product ID constants.
 * 
 * Culled from various sources; see <a
 * href="http://www.linux-usb.org/usb.ids">usb.ids</a> for one listing.
 * 
 * @author mike wakerly (opensource@hoho.com)
 */
public final class UsbId {

	public static final int VENDOR_FTDI = 0x0403;
	public static final int FTDI_FT232R = 0x6001;

	public static final int FTDI_FT4232H = 0x6011;
	public static final int FTDI_FT232H = 0x6014;
	public static final int FTDI_FT232C = 0x6010;// ??

	// ///////////////////////////////////////// add vendor and devices ID here
	public static final int VENDOR_TERRACTEC = 0x0CCD;
	public static final int TER_00A9 = 0x00a9; // Terratec Cinergy T Stick Black
												// (rev 1)"
	public static final int TER_00B3 = 0x00B3; // Terratec NOXON DAB/DAB+ USB
												// dongle (rev 1)"
	public static final int TER_00B4 = 0x00B4; // Terratec Deutschlandradio DAB
												// Stick""
	public static final int TER_00B5 = 0x00B5; // "Terratec NOXON DAB Stick - Radio Energy"
	public static final int TER_00B7 = 0x00B7;
	public static final int TER_00B8 = 0x00B8;
	public static final int TER_00B9 = 0x00B9;
	public static final int TER_00C0 = 0x00C0;
	public static final int TER_00C6 = 0x00C6;
	public static final int TER_00D3 = 0x00d3;
	public static final int TER_00D7 = 0x00d7;
	public static final int TER_00E0 = 0x00e0;

	// /////////////////////////////////////////////////////////
	public static final int VENDOR_RTL = 0x0bda;
	public static final int RTL_RTL2838 = 0x2838;
	public static final int RTL_RTL2832 = 0x2832;

	public static final int VENDOR_ATMEL = 0x03EB;
	public static final int ATMEL_LUFA_CDC_DEMO_APP = 0x2044;

	public static final int VENDOR_ARDUINO = 0x2341;
	public static final int ARDUINO_UNO = 0x0001;
	public static final int ARDUINO_MEGA_2560 = 0x0010;
	public static final int ARDUINO_SERIAL_ADAPTER = 0x003b;
	public static final int ARDUINO_MEGA_ADK = 0x003f;
	public static final int ARDUINO_MEGA_2560_R3 = 0x0042;
	public static final int ARDUINO_UNO_R3 = 0x0043;
	public static final int ARDUINO_MEGA_ADK_R3 = 0x0044;
	public static final int ARDUINO_SERIAL_ADAPTER_R3 = 0x0044;
	public static final int ARDUINO_LEONARDO = 0x8036;

	public static final int VENDOR_VAN_OOIJEN_TECH = 0x16c0;
	public static final int VAN_OOIJEN_TECH_TEENSYDUINO_SERIAL = 0x0483;

	public static final int VENDOR_LEAFLABS = 0x1eaf;
	public static final int LEAFLABS_MAPLE = 0x0004;

	public static final int VENDOR_SILAB = 0x10c4;
	public static final int SILAB_CP2102 = 0xea60;

	private UsbId() {
		throw new IllegalAccessError("Non-instantiable class.");
	}

}
