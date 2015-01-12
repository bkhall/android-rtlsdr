package com.rtlsdr.android;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

//import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.hoho.android.usbserial.driver.CommonUsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbId;
import com.rtlsdr.android.tuner.RtlSdr_tuner_iface;
import com.rtlsdr.android.tuner.e4k_tuner;
import com.rtlsdr.android.tuner.fc0012_tuner;
import com.rtlsdr.android.tuner.fc0013_tuner;
import com.rtlsdr.android.tuner.fc2580_tuner;
import com.rtlsdr.android.tuner.r820T_tuner;

public class SdrUSBDriver extends CommonUsbSerialDriver {
	private static final String TAG = SdrUSBDriver.class.getSimpleName();
	static RtlSdr_tuner_iface myTuner = null;
	boolean set_gain_mode = true;
	/*
	 * private static final int DEFAULT_BAUD_RATE = 9600;
	 * 
	 * private static final int DEFAULT_BUF_LENGTH = 512;//(16 * 16384); private
	 * static final char MESSAGEGO = 253; private static final char OVERWRITE =
	 * 254; private static final char BADSAMPLE = 255;
	 */
	private final int MODES_DEFAULT_RATE = 2000000;
	private final int MODES_DEFAULT_FREQ = 1090000000;
	private final int MODES_AUTO_GAIN = -100; /* Use automatic gain. */
	private final int MODES_MAX_GAIN = 999999; /* Use max available gain. */
	// /private final long ADSB_FREQ = 1090000000;
	// private final int AUTO_GAIN = -100;
	// private final long ADSB_RATE = 2000000;
	private int gain = MODES_MAX_GAIN;// AUTO_GAIN;
	private boolean enable_agc = false;
	private long freq = MODES_DEFAULT_FREQ;
	// Buffersizes
	// private static final int MODES_PREAMBLE_US = 8;
	// private static final int MODES_LONG_MSG_BITS =112;
	// private static final int MODES_FULL_LEN
	// =(MODES_PREAMBLE_US+MODES_LONG_MSG_BITS);
	// private static final int MODES_DATA_LEN = (16*16384); /* 256k */
	// private static final int BUFSIZ = MODES_DATA_LEN + (MODES_FULL_LEN-1)*4;

	// private byte [] input = new byte[BUFSIZ];
	// private static char[] mag = new char[BUFSIZ *2];

	// private int [] adsb_frame = new int[14];
	// private final int preamble_len = 16;
	// private final int long_frame = 112;
	// private final int char_frame = 56;
	// private double quality = 1.0;
	// private int allowed_errors = 5;

	private static final int USB_WRITE_TIMEOUT_MILLIS = 5000;// was 5000

	/*
	 * Configuration Request Types
	 */
	private static final int REQTYPE_HOST_TO_DEVICE = 0x41;
	// private static final int REQTYPE_DEVICE_TO_HOST = 0x80;

	/** In: device-to-host */
	private static final int LIBUSB_ENDPOINT_IN = 0x80;

	/** Out: host-to-device */
	private static final int LIBUSB_ENDPOINT_OUT = 0x00;

	/** Standard */
	// private static final int LIBUSB_REQUEST_TYPE_STANDARD = (0x00 << 5);

	/** Class */
	// private static final int LIBUSB_REQUEST_TYPE_CLASS = (0x01 << 5);

	/** Vendor */
	private static final int LIBUSB_REQUEST_TYPE_VENDOR = (0x02 << 5);

	/** Reserved */
	// private static final int LIBUSB_REQUEST_TYPE_RESERVED = (0x03 << 5);

	private static final byte CTRL_IN = (byte) (LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_IN);
	private static final byte CTRL_OUT = (byte) (LIBUSB_REQUEST_TYPE_VENDOR | LIBUSB_ENDPOINT_OUT);

	// private static final int DEFAULT_SAMPLE_RATE = 2048000;
	// private static final int DEFAULT_ASYNC_BUF_NUMBER = 32;
	// private static final int DEFAULT_BUF_LENGTH = (16 * 16384);
	// private static final int MINIMAL_BUF_LENGTH = 512;
	// static final int MAXIMAL_BUF_LENGTH = (256 * 16384);

	// private static final int DEFAULT_BUF_NUMBER = 32;
	// private static final int DEFAULT_BUF_LENGTH = (16 * 32 * 512);

	private static final int DEF_RTL_XTAL_FREQ = 28800000;
	private static final int MIN_RTL_XTAL_FREQ = (DEF_RTL_XTAL_FREQ - 1000);
	private static final int MAX_RTL_XTAL_FREQ = (DEF_RTL_XTAL_FREQ + 1000);

	private static final int MAX_SAMP_RATE = 3200000;

	// private static final int CTRL_TIMEOUT = 300;
	private static final int BULK_TIMEOUT = 0;
	private static final char EEPROM_ADDR = 0xa0;

	private static final char USB_SYSCTL = 0x2000;
	// private static final char USB_CTRL = 0x2010;
	// private static final char USB_STAT = 0x2014;
	// private static final char USB_EPA_CFG = 0x2144;
	private static final char USB_EPA_CTL = 0x2148;
	private static final char USB_EPA_MAXPKT = 0x2158;
	// private static final char USB_EPA_MAXPKT_2 = 0x215a;
	// private static final char USB_EPA_FIFO_CFG = 0x2160;

	private static final char DEMOD_CTL = 0x3000;
	private static final char GPO = 0x3001;
	// private static final char GPI = 0x3002;
	private static final char GPOE = 0x3003;
	private static final char GPD = 0x3004;
	// private static final char SYSINTE = 0x3005;
	// private static final char SYSINTS = 0x3006;
	// private static final char GP_CFG0 = 0x3007;
	// private static final char GP_CFG1 = 0x3008;
	// private static final char SYSINTE_1 = 0x3009;
	// private static final char SYSINTS_1 = 0x300a;
	private static final char DEMOD_CTL_1 = 0x300b;
	// private static final char IR_SUSPEND = 0x300c;

	// private static final byte DEMODB = 0;
	private static final byte USBB = 1;
	private static final byte SYSB = 2;
	// private static final byte TUNB = 3;
	// private static final byte ROMB = 4;
	// private static final byte IRB = 5;
	private static final byte IICB = 6;

	/*
	 * Configuration Request Codes
	 */
	// private static final int SILABSER_IFC_ENABLE_REQUEST_CODE = 0x00;
	// private static final int SILABSER_SET_BAUDDIV_REQUEST_CODE = 0x01;
	// private static final int SILABSER_SET_LINE_CTL_REQUEST_CODE = 0x03;
	// private static final int SILABSER_SET_MHS_REQUEST_CODE = 0x07;
	// private static final int SILABSER_SET_BAUDRATE = 0x1E;

	/*
	 * SILABSER_IFC_ENABLE_REQUEST_CODE
	 */
	// private static final int UART_ENABLE = 0x0001;
	// private static final int UART_DISABLE = 0x0000;

	/*
	 * SILABSER_SET_BAUDDIV_REQUEST_CODE
	 */
	// private static final int BAUD_RATE_GEN_FREQ = 0x384000;

	private static final int E4K_I2C_ADDR = 0xc8;
	private static final int E4K_CHECK_ADDR = 0x02;
	private static final int E4K_CHECK_VAL = 0x40;

	private static final int FC0013_I2C_ADDR = 0xc6;
	private static final int FC0013_CHECK_ADDR = 0x00;
	private static final int FC0013_CHECK_VAL = 0xa3;

	private static final int R820T_I2C_ADDR = 0x34;
	private static final int R828D_I2C_ADDR = 0x74;
	private static final int R820T_CHECK_ADDR = 0x00;
	private static final int R820T_CHECK_VAL = 0x69;

	private static final int FC2580_I2C_ADDR = 0xac;
	private static final int FC2580_CHECK_ADDR = 0x01;
	private static final int FC2580_CHECK_VAL = 0x56;

	private static final int FC0012_I2C_ADDR = 0xc6;
	private static final int FC0012_CHECK_ADDR = 0x00;
	private static final int FC0012_CHECK_VAL = 0xa1;

	/*
	 * SILABSER_SET_MHS_REQUEST_CODE
	 */
	// private static final int MCR_DTR = 0x0001;
	// private static final int MCR_RTS = 0x0002;
	// private static final int MCR_ALL = 0x0003;

	// private static final int CONTROL_WRITE_DTR = 0x0100;
	// private static final int CONTROL_WRITE_RTS = 0x0200;

	// private static final int TIMEOUT = 500;
	// private static final byte i2c_addr = 0; //FIXME
	// private static final long[] null = null;
	private static final int RTLSDR_TUNER_UNKNOWN = 0;
	private static final int RTLSDR_TUNER_E4000 = 1;
	private static final int RTLSDR_TUNER_FC0012 = 2;
	private static final int RTLSDR_TUNER_FC0013 = 3;
	private static final int RTLSDR_TUNER_FC2580 = 4;
	private static final int RTLSDR_TUNER_R820T = 5;
	private static final int RTLSDR_TUNER_R828D = 6;
	private static final long R820T_IF_FREQ = 3570000;
	// private static final String RTLSDR_INACTIVE = null;

	private UsbEndpoint mReadEndpoint;
	private boolean tuner = false;
	private boolean tuner_exit = false;
	private static long rtl_xtal = DEF_RTL_XTAL_FREQ;
	private long rate = MAX_SAMP_RATE;
	private static long tun_xtal = DEF_RTL_XTAL_FREQ;
	// private long freq=DEF_RTL_XTAL_FREQ;
	private static int corr = 0;
	// private Object e4k_s;
	private boolean direct_sampling = false;
	private long offs_freq = 0;
	private int tuner_type = 0;

	private int ppm_error = 0;
	private boolean tuner_set_gain = true;
	// private boolean tuner_set_if_gain=false;
	// private boolean tuner_set_gain_mode=false;
	private boolean tuner_set_bw = true;
	private boolean tuner_init = false;
	int erro_count = 0;

	// AdsbParse adsb;
	public SdrUSBDriver(UsbDevice device, UsbDeviceConnection connection) {
		super(device, connection);
	}

	private int setConfigSingle(int request, int value) {
		return mConnection.controlTransfer(REQTYPE_HOST_TO_DEVICE, request,
				value, 0, null, 0, USB_WRITE_TIMEOUT_MILLIS);
	}

	@Override
	public void open() throws IOException {
		// inteface 0 has 1 bulktransfer read endpoint
		boolean opened = false;
		try {
			for (int i = 0; i < mDevice.getInterfaceCount(); i++) {
				UsbInterface usbIface = mDevice.getInterface(i);
				if (mConnection.claimInterface(usbIface, true)) {
					Log.e(TAG, "claimInterface " + i + " SUCCESS");
				} else {
					Log.e(TAG, "claimInterface " + i + " FAIL");
				}
			}
			int count = mDevice.getInterfaceCount();
			for (int i = 0; i < count; i++) {
				UsbInterface intf = mDevice.getInterface(i);
				if (intf != null) {
					Log.e(TAG,
							"intf.getInterfaceClass()=" + i + " "
									+ intf.getInterfaceClass()
									+ " intf.getInterfaceSubclass()=" + i + " "
									+ intf.getInterfaceSubclass()
									+ " intf.getInterfaceProtocol()=" + i + " "
									+ intf.getInterfaceProtocol());
					Log.e(TAG,
							"dataIface.getEndpointCount " + i + " "
									+ intf.getEndpointCount());
				}
				int getEndpointCount = intf.getEndpointCount();
				for (int j = 0; j < getEndpointCount; j++) {
					UsbEndpoint ep = intf.getEndpoint(j);
					Log.e(TAG,
							"found endpoint getEndpointNumber= "
									+ ep.getEndpointNumber());
					Log.e(TAG, "found endpoint type= " + ep.getType());
					Log.e(TAG,
							"found endpoint getDirection= " + ep.getDirection());
					Log.e(TAG,
							"found endpoint getMaxPacketSize= "
									+ ep.getMaxPacketSize());

					if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
						if (ep.getDirection() == UsbConstants.USB_DIR_IN) {
							mReadEndpoint = ep;
							Log.e(TAG, "found READ USB_ENDPOINT_XFER_BULK");
						} else {
							Log.e(TAG,
									"found WRITE USB_DIR_OUT->USB_ENDPOINT_XFER_BULK");
						}
					}
				}
				opened = true;

			}
			// if(myTuner == null)
			myTuner = rtlsdr_detect();
			if (myTuner != null) {

				StartDevice();

			}
		} finally {
			if (!opened) {
				close();
			}
		}
	}

	@Override
	public void close() throws IOException {
		rtlsdr_deinit_baseband();

		if (myTuner != null)
			myTuner.exit(0);
		mConnection.close();
	}

	@Override
	public int read(byte[] dest, int timeoutMillis) throws IOException {
		final int totalBytesRead;

		synchronized (mReadBufferLock) {
			int readAmt = Math.min(dest.length, mReadBuffer.length);
			totalBytesRead = mConnection.bulkTransfer(mReadEndpoint,
					mReadBuffer, readAmt, timeoutMillis * 1000);
			// if (totalBytesRead < MODEM_STATUS_HEADER_LENGTH) {
			// throw new IOException("Expected at least " +
			// MODEM_STATUS_HEADER_LENGTH + " bytes");
			// }
			if (totalBytesRead < 0) {
				Log.e(TAG, " mConnection.bulkTransfer ERROR totalBytesRead="
						+ totalBytesRead);

			} else {
				System.arraycopy(mReadBuffer, 0, dest, 0, totalBytesRead);
			}
			return totalBytesRead;
		}

	}

	@Override
	public int write(byte[] src, int timeoutMillis) throws IOException {

		Log.e(TAG, "Writes not support yet writeLength= " + src.length);

		return src.length;
	}

	private void StartDevice() {
		/* Set gain, frequency, sample rate, and reset the device. */

		rtlsdr_set_tuner_gain_mode((gain == MODES_AUTO_GAIN) ? false : true);
		if (gain != MODES_AUTO_GAIN) {
			if (gain == MODES_MAX_GAIN) {
				/* Find the maximum gain available. */
				int numgains;
				int[] gains = new int[100];

				numgains = rtlsdr_get_tuner_gains(gains);
				gain = gains[numgains - 1];
				Log.e(TAG,
						"Max available gain is: "
								+ String.format("%.2f", gain / 10.0));
			}
			rtlsdr_set_tuner_gain(gain);
			Log.e(TAG, "Setting gain to: " + String.format("%.2f", gain / 10.0));
		} else {
			Log.e(TAG, "Using automatic gain control.\n");
		}
		rtlsdr_set_freq_correction(ppm_error);
		if (enable_agc)
			rtlsdr_set_agc_mode(true);
		rtlsdr_set_center_freq(this.freq);
		// rtlsdr_set_tuner_gain_mode(true);//true == AGC
		rtlsdr_set_sample_rate(MODES_DEFAULT_RATE);
		rtlsdr_reset_buffer();
		Log.e(TAG,
				"Gain reported by device: "
						+ String.format("%.2f", rtlsdr_get_tuner_gain() / 10.0));
		return;
	}

	public static Map<Integer, int[]> getSupportedDevices() {
		final Map<Integer, int[]> supportedDevices = new LinkedHashMap<Integer, int[]>();
		supportedDevices.put(Integer.valueOf(UsbId.VENDOR_RTL), new int[] {
				UsbId.RTL_RTL2838, UsbId.RTL_RTL2832, });
		supportedDevices.put(Integer.valueOf(UsbId.VENDOR_TERRACTEC),
				new int[] { UsbId.TER_00A9, UsbId.TER_00B3, UsbId.TER_00B4,
						UsbId.TER_00B5, UsbId.TER_00B7, UsbId.TER_00B8,
						UsbId.TER_00B9, UsbId.TER_00C0, UsbId.TER_00C6,
						UsbId.TER_00D3, UsbId.TER_00D7, UsbId.TER_00E0, });
		return supportedDevices;
	}

	private static int libusb_control_transfer(byte requestType, byte request,
			char value, char index, byte[] buffer, char length, int timeout) {
		return mConnection.controlTransfer(requestType, request, value, index,
				buffer, length, timeout);
	}

	private static int rtlsdr_read_array(byte iicb2, char addr, byte[] data,
			byte len) {
		int r;
		char index = (char) (iicb2 << 8);

		r = libusb_control_transfer(CTRL_IN, (byte) 0, addr, index, data,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0) {
			Log.e("rtlsdr_read_array", " error" + len);
			Log.e("rtlsdr_read_array",
					"addr 0x" + Integer.toHexString((int) addr) + " index 0x"
							+ Integer.toHexString((int) index) + " array "
							+ data[0] + " len " + len);
		}
		return r;
	}

	private static int rtlsdr_write_array(byte iicb2, char addr, byte[] array,
			byte len) {
		int r;
		char index = (char) ((iicb2 << 8) | 0x10);

		r = libusb_control_transfer(CTRL_OUT, (byte) 0, addr, index, array,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0) {
			Log.e("rtlsdr_write_array",
					"addr 0x" + Integer.toHexString((int) addr) + " index 0x"
							+ Integer.toHexString((int) index) + " array "
							+ array[0] + " len " + len);
			Log.e("rtlsdr_write_array", " error byte count " + len);
		}
		return r;
	}

	public static int rtlsdr_i2c_write_reg(byte i2c_addr, char reg, char val) {
		char addr = (char) i2c_addr;
		byte[] data = new byte[2];

		data[0] = (byte) reg;
		data[1] = (byte) val;
		return rtlsdr_write_array(IICB, addr, data, (byte) 2);
	}

	public static byte rtlsdr_i2c_read_reg(int e4kI2cAddr, int e4kCheckAddr) {
		char addr = (char) e4kI2cAddr;
		byte[] data = new byte[2];
		byte[] out = new byte[2];
		out[0] = (byte) e4kCheckAddr;
		rtlsdr_write_array(IICB, addr, out, (byte) 1);
		rtlsdr_read_array(IICB, addr, data, (byte) 1);

		return data[0];
	}

	public static int rtlsdr_get_tuner_clock() {
		long[] tuner_freq = new long[1];
		long[] xtal_freq = new long[1];
		/* read corrected clock value */
		if (rtlsdr_get_xtal_freq(xtal_freq, tuner_freq) < 0)
			return 0;

		return (int) tuner_freq[0];
	}

	private static int rtlsdr_i2c_write(byte i2c_addr, byte[] buffer, byte len) {
		byte addr = i2c_addr;

		return rtlsdr_write_array(IICB, (char) addr, buffer, len);
	}

	private static int rtlsdr_i2c_read(byte i2c_addr, byte[] buffer, byte len) {
		byte addr = i2c_addr;

		return rtlsdr_read_array(IICB, (char) addr, buffer, (byte) len);
	}

	private char rtlsdr_read_reg(byte block, char addr, byte len) {
		int r = 0;
		byte[] data = new byte[2];
		char index = (char) (block << 8);
		char reg;

		r = libusb_control_transfer(CTRL_IN, (byte) 0, addr, index, data,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0)
			Log.e(TAG, "rtlsdr_read_reg failed " + r);

		reg = (char) ((data[1] << 8) | data[0]);

		return reg;
	}

	private int rtlsdr_write_reg(byte block, char addr, char val, byte len) {
		int r = 0;
		byte[] data = new byte[2];

		char index = (char) ((block << 8) | 0x10);

		if (len == 1)
			data[0] = (byte) (val & 0xff);
		else
			data[0] = (byte) (val >> 8);

		data[1] = (byte) (val & 0xff);

		r = libusb_control_transfer(CTRL_OUT, (byte) 0, addr, index, data,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0) {
			Log.e("rtlsdr_write_reg", "block " + block + " addr " + " val "
					+ " len " + len);
			Log.e("rtlsdr_write_reg", "Line "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		}
		return r;
	}

	private char rtlsdr_demod_read_reg(byte page, char addr, byte len) {
		int r;
		byte[] data = new byte[2];

		char index = (char) page;
		char reg;
		addr = (char) ((addr << 8) | 0x20);

		r = libusb_control_transfer(CTRL_IN, (byte) 0, addr, index, data,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0) {
			Log.e(TAG, "rtlsdr_demod_read_reg error " + r);
			Log.e(TAG,
					" addr " + Integer.toHexString((int) addr) + " index "
							+ Integer.toHexString((int) index) + " data[0] "
							+ Integer.toHexString((int) data[0]) + " data[1] "
							+ Integer.toHexString((int) data[1]) + " len "
							+ Integer.toHexString((int) len));
		}
		reg = (char) ((data[1] << 8) | data[0]);

		return ((char) r);
	}

	private int rtlsdr_demod_write_reg(byte page, char addr, char val, byte len) {
		int r;
		byte[] data = new byte[2];
		char index = (char) (0x10 | page);
		addr = (char) ((addr << 8) | 0x20);

		if (len == 1)
			data[0] = (byte) (val & 0xff);
		else
			data[0] = (byte) (val >> 8);

		data[1] = (byte) (val & 0xff);

		r = libusb_control_transfer(CTRL_OUT, (byte) 0, addr, index, data,
				(char) len, USB_WRITE_TIMEOUT_MILLIS);

		if (r < 0) {
			Log.e(TAG, "rtlsdr_demod_write_reg failed " + r);
			Log.e(TAG,
					" addr " + Integer.toHexString((int) addr) + " index "
							+ Integer.toHexString((int) index) + " data[0] "
							+ Integer.toHexString((int) data[0]) + " data[1] "
							+ Integer.toHexString((int) data[1]) + " len "
							+ Integer.toHexString((int) len));
		}
		rtlsdr_demod_read_reg((byte) 0x0a, (char) 0x01, (byte) 1);

		return (r == len) ? len : -1;
	}

	private void rtlsdr_set_gpio_bit(byte gpio, boolean val) {
		int r;

		gpio = (byte) (1 << gpio);
		r = rtlsdr_read_reg(SYSB, GPO, (byte) 1);
		r = (val ? (r | gpio) : (r & ~gpio));
		rtlsdr_write_reg(SYSB, GPO, (char) r, (byte) 1);
	}

	private void rtlsdr_set_gpio_output(byte gpio) {
		int r;
		gpio = (byte) (1 << gpio);

		r = rtlsdr_read_reg(SYSB, GPD, (byte) 1);
		rtlsdr_write_reg(SYSB, GPO, (char) (r & ~gpio), (byte) 1);
		r = rtlsdr_read_reg(SYSB, GPOE, (byte) 1);
		rtlsdr_write_reg(SYSB, GPOE, (char) (r | gpio), (byte) 1);
	}

	private void rtlsdr_set_i2c_repeater(boolean on) {
		rtlsdr_demod_write_reg((byte) 1, (char) 0x01,
				(char) (on ? 0x18 : 0x10), (byte) 1);
	}

	private void rtlsdr_init_baseband() {
		int i = 0;
		int ret = 0;
		/*
		 * default FIR coefficients used for DAB/FM by the Windows driver, the
		 * DVB driver uses different ones
		 */
		char[] fir_coeff = { 0xca, 0xdc, 0xd7, 0xd8, 0xe0, 0xf2, 0x0e, 0x35,
				0x06, 0x50, 0x9c, 0x0d, 0x71, 0x11, 0x14, 0x71, 0x74, 0x19,
				0x41, 0xa5, };

		/* initialize USB */
		ret = rtlsdr_write_reg(USBB, USB_SYSCTL, (char) 0x09, (byte) 1);
		ret = rtlsdr_write_reg(USBB, USB_EPA_MAXPKT, (char) 0x0002, (byte) 2);
		ret = rtlsdr_write_reg(USBB, USB_EPA_CTL, (char) 0x1002, (byte) 2);

		/* poweron demod */
		ret = rtlsdr_write_reg(SYSB, DEMOD_CTL_1, (char) 0x22, (byte) 1);
		ret = rtlsdr_write_reg(SYSB, DEMOD_CTL, (char) 0xe8, (byte) 1);

		/* reset demod (bit 3, soft_rst) */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x01, (char) 0x14,
				(byte) 1);
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x01, (char) 0x10,
				(byte) 1);

		/* disable spectrum inversion and adjacent channel rejection */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x15, (char) 0x00,
				(byte) 1);
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x16, (char) 0x0000,
				(byte) 2);

		/* clear both DDC shift and IF frequency registers */
		for (i = 0; i < 6; i++) {
			ret = rtlsdr_demod_write_reg((byte) 1, (char) (0x16 + i),
					(char) 0x00, (byte) 1);
		}

		/* set FIR coefficients */
		for (i = 0; i < fir_coeff.length; i++) {
			ret = rtlsdr_demod_write_reg((byte) 1, (char) (0x1c + i),
					fir_coeff[i], (byte) 1);
		}
		/* enable SDR mode, disable DAGC (bit 5) */
		ret = rtlsdr_demod_write_reg((byte) 0, (char) 0x19, (char) 0x05,
				(byte) 1);

		/* init FSM state-holding register */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x93, (char) 0xf0,
				(byte) 1);

		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x94, (char) 0x0f,
				(byte) 1);

		/* disable AGC (en_dagc, bit 0) (this seems to have no effect) */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x11, (char) 0x00,
				(byte) 1);

		/* disable RF and IF AGC loop */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0x04, (char) 0x00,
				(byte) 1);

		/* disable PID filter (enable_PID = 0) */
		ret = rtlsdr_demod_write_reg((byte) 0, (char) 0x61, (char) 0x60,
				(byte) 1);

		/* opt_adc_iq = 0, default ADC_I/ADC_Q datapath */
		ret = rtlsdr_demod_write_reg((byte) 0, (char) 0x06, (char) 0x80,
				(byte) 1);

		/*
		 * Enable Zero-IF mode (en_bbin bit), DC cancellation (en_dc_est), IQ
		 * estimation/compensation (en_iq_comp, en_iq_est)
		 */
		ret = rtlsdr_demod_write_reg((byte) 1, (char) 0xb1, (char) 0x1b,
				(byte) 1);

		/* disable 4.096 MHz clock output on pin TP_CK0 */
		ret = rtlsdr_demod_write_reg((byte) 0, (char) 0x0d, (char) 0x83,
				(byte) 1);

	}

	private int rtlsdr_deinit_baseband() {
		int r = 0;
		int ret = 0;

		if ((tuner) && (!this.tuner_exit)) {
			rtlsdr_set_i2c_repeater(true);

			rtlsdr_set_i2c_repeater(false);
		}

		/* poweroff demodulator and ADCs */
		ret = rtlsdr_write_reg(SYSB, DEMOD_CTL, (char) 0x20, (byte) 1);
		if (ret != 0)
			Log.e(TAG, " ret " + ret + "Line "
					+ Thread.currentThread().getStackTrace()[2].getLineNumber());
		tuner_exit = true;
		tuner = false;
		return r;
	}

	private int rtlsdr_set_if_freq(long freq) {
		// long [] rtl_xtal = new long[1] ;
		// long [] rtl_dummy = new long[1] ;
		long if_freq = 0;
		char tmp;
		int r;

		/* read corrected clock value */
		// if (rtlsdr_get_xtal_freq(rtl_xtal, rtl_dummy) < 0) return -2;

		if_freq = (long) (((freq * TWO_POW(22)) / rtl_xtal) * (-1));
		// if(rtl_xtal !=0 ) if_freq = (freq/rtl_xtal) & 0x003fffff;
		tmp = (char) ((if_freq >> 16) & 0x3f);
		r = rtlsdr_demod_write_reg((byte) 1, (char) 0x19, tmp, (byte) 1);
		tmp = (char) ((if_freq >> 8) & 0xff);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x1a, tmp, (byte) 1);
		tmp = (char) (if_freq & 0xff);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x1b, tmp, (byte) 1);

		return r;
	}

	private double TWO_POW(int i) {
		return (1 << (i));

	}

	private int rtlsdr_set_sample_freq_correction(int ppm) {
		int r = 0;
		char tmp;
		char offs = (char) (ppm * (-1) * TWO_POW(24) / 1000000);

		tmp = (char) (offs & 0xff);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x3f, tmp, (byte) 1);
		tmp = (char) ((offs >> 8) & 0x3f);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x3e, tmp, (byte) 1);

		return r;
	}

	private int rtlsdr_set_xtal_freq(long rtl_freq, long tuner_freq) {
		int r = 0;

		if (rtl_freq > 0
				&& (rtl_freq < MIN_RTL_XTAL_FREQ || rtl_freq > MAX_RTL_XTAL_FREQ))
			return -2;

		if (rtl_freq > 0 && rtl_xtal != rtl_freq) {
			rtl_xtal = rtl_freq;

			/* update xtal-dependent settings */
			if (this.rate != 0)
				r = rtlsdr_set_sample_rate(this.rate);
		}

		if (tun_xtal != tuner_freq) {
			if (0 == tuner_freq)
				tun_xtal = rtl_xtal;
			else
				tun_xtal = tuner_freq;

			/* read corrected clock value into e4k structure */
			// if (rtlsdr_get_xtal_freq(null, /*this.e4k_s.vco.fosc
			// fixxme*/null) < 0) return -3;

			/* update xtal-dependent settings */
			if (this.freq != 0)
				r = rtlsdr_set_center_freq(this.freq);
		}

		return r;
	}

	private static double APPLY_PPM_CORR(long val, int ppm) {
		return (((val) * (1.0 + (ppm) / 1e6)));
	}

	private static int rtlsdr_get_xtal_freq(long[] rtl_xtal2, long[] tuner_freq) {

		// #define APPLY_PPM_CORR(val,ppm) (((val) * (1.0 + (ppm) / 1e6)))

		rtl_xtal2[0] = (long) APPLY_PPM_CORR(rtl_xtal, corr);

		tuner_freq[0] = (long) APPLY_PPM_CORR(tun_xtal, corr);

		return 0;
	}

	private int rtlsdr_write_eeprom(byte[] data, char offset, char len)
			throws InterruptedException {
		int r = 0;
		int i;
		byte[] cmd = new byte[2];

		// if (!dev) return -1;

		if ((len + offset) > 256)
			return -2;

		for (i = 0; i < len; i++) {
			cmd[0] = (byte) (i + offset);
			r = rtlsdr_write_array(IICB, EEPROM_ADDR, cmd, (byte) 1);
			r = rtlsdr_read_array(IICB, EEPROM_ADDR, cmd, (byte) 1);

			/* only write the byte if it differs */
			if (cmd[1] == data[i])
				continue;

			cmd[1] = data[i];
			r = rtlsdr_write_array(IICB, EEPROM_ADDR, cmd, (byte) 2);
			if (r != cmd.length)
				return -3;

			/*
			 * for some EEPROMs (e.g. ATC 240LC02) we need a delay between write
			 * operations, otherwise they will fail
			 */

			Thread.sleep(5000);

			// usleep(5000);

		}

		return 0;
	}

	private int rtlsdr_read_eeprom(byte[] data, byte[] offset, char len) {
		int r = 0;
		int i;
		byte[] output = new byte[2];

		if ((len + offset[0]) > 256) {
			Log.e(TAG, " rtlsdr_read_eeprom offset + len > 256");
			return -2;
		}

		r = rtlsdr_write_array(IICB, EEPROM_ADDR, offset, (byte) 1);
		if (r < 0)
			return -3;

		for (i = 0; i < len; i++) {
			output[0] = data[i];
			r = rtlsdr_read_array(IICB, EEPROM_ADDR, output, (byte) 1);

			if (r < 0) {
				Log.e(TAG, " rtlsdr_read_eeprom FAILED");
				return -3;
			}

		}

		return r;
	}

	private int rtlsdr_set_center_freq(long freq) {
		int r = -1;

		// if (this.tuner ==0) return -1;

		if (this.direct_sampling) {
			r = rtlsdr_set_if_freq(freq);
		} else if (myTuner != null) {

			rtlsdr_set_i2c_repeater(true);
			try {
				r = myTuner.set_freq(0, freq);
			} catch (IOException e) {

				e.printStackTrace();

				Log.e(TAG, e.toString());
				try {
					myTuner.set_freq(0, freq - this.offs_freq);
				} catch (IOException e1) {

					e1.printStackTrace();
					Log.e(TAG, e.toString());
				}
			}

			rtlsdr_set_i2c_repeater(false);
		}

		if (r <= 0)
			this.freq = freq;
		else
			this.freq = 0;
		return r;
	}

	private long rtlsdr_get_center_freq() {

		return this.freq;
	}

	private int rtlsdr_set_freq_correction(int ppm) {
		int r = 0;
		long[] xtal_freq = new long[1];
		long[] tuner_freq2 = new long[1];

		if (corr == ppm)
			return -2;

		corr = ppm;

		r |= rtlsdr_set_sample_freq_correction(ppm);

		/* read corrected clock value into e4k structure */
		if (rtlsdr_get_xtal_freq(xtal_freq, tuner_freq2) < 0)
			return -3;

		if (this.freq != 0) /* retune to apply new correction value */
			r |= rtlsdr_set_center_freq(this.freq);

		return r;
	}

	private int rtlsdr_get_freq_correction() {

		return corr;
	}

	// int rtlsdr_tuner
	private int rtlsdr_get_tuner_type() {
		// return RTLSDR_TUNER_UNKNOWN;

		return (this.tuner_type);
	}

	private int rtlsdr_get_tuner_gains(int[] dst) {
		int len;
		/* all gain values are expressed in tenths of a dB */
		int[] e4k_gains = { -10, 15, 40, 65, 90, 115, 140, 165, 190, 215, 240,
				290, 340, 420 };
		int[] fc0012_gains = { -99, -40, 71, 179, 192 };
		int[] fc0013_gains = { -99, -73, -65, -63, -60, -58, -54, 58, 61, 63,
				65, 67, 68, 70, 71, 179, 181, 182, 184, 186, 188, 191, 197 };
		int[] fc2580_gains = { 0 /* no gain values */};
		int[] r820t_gains = { 0, 9, 14, 27, 37, 77, 87, 125, 144, 157, 166,
				197, 207, 229, 254, 280, 297, 328, 338, 364, 372, 386, 402,
				421, 434, 439, 445, 480, 496 };
		int[] unknown_gains = { 0 /* no gain values */};

		switch (this.tuner_type) {
		case RTLSDR_TUNER_E4000:
			len = e4k_gains.length;
			System.arraycopy(e4k_gains, 0, dst, 0, e4k_gains.length);
			break;
		case RTLSDR_TUNER_FC0012:
			len = fc0012_gains.length;
			System.arraycopy(fc0012_gains, 0, dst, 0, fc0012_gains.length);
			break;
		case RTLSDR_TUNER_FC0013:
			len = fc0013_gains.length;
			System.arraycopy(fc0013_gains, 0, dst, 0, fc0013_gains.length);
			break;
		case RTLSDR_TUNER_FC2580:
			len = fc2580_gains.length;
			System.arraycopy(fc2580_gains, 0, dst, 0, fc2580_gains.length);
			break;
		case RTLSDR_TUNER_R820T:
			len = r820t_gains.length;
			System.arraycopy(r820t_gains, 0, dst, 0, r820t_gains.length);
			break;
		default:

			len = unknown_gains.length;
			System.arraycopy(unknown_gains, 0, dst, 0, unknown_gains.length);
			break;
		}

		return (len);
	}

	private int rtlsdr_set_tuner_gain(int inputGain) {
		int r = 0;
		if (this.tuner_set_gain) {
			rtlsdr_set_i2c_repeater(true);
			try {
				myTuner.set_gain(0, inputGain);
			} catch (IOException e) {
				Log.e(TAG, "set_gain error " + e.toString());
				e.printStackTrace();
			}
			rtlsdr_set_i2c_repeater(false);
		}
		gain = inputGain;
		return r;
	}

	private long rtlsdr_get_tuner_gain() {
		return this.gain;
	}

	private int rtlsdr_set_tuner_if_gain(int stage, int gain) {
		int r = 0;
		if (myTuner != null)
			return -1;
		if (myTuner != null) {
			rtlsdr_set_i2c_repeater(true);
			try {
				myTuner.set_if_gain(0, stage, gain);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
			rtlsdr_set_i2c_repeater(false);
		}

		return r;
	}

	private int rtlsdr_set_tuner_gain_mode(boolean mode) {
		int r = 0;
		// if(myTuner == null) return -1;

		if (this.set_gain_mode) {
			rtlsdr_set_i2c_repeater(true);
			try {
				myTuner.set_gain_mode(0, mode);
			} catch (IOException e) {
				Log.e(TAG, e.toString());
				e.printStackTrace();
			}
			rtlsdr_set_i2c_repeater(false);
		}

		return r;
	}

	private int rtlsdr_set_sample_rate(long samp_rate) {
		int r = 0;
		char tmp;
		long rsamp_ratio;
		double real_rate;

		// if (!dev) return -1;

		/* check for the maximum rate the resampler supports */
		if (samp_rate > MAX_SAMP_RATE)
			samp_rate = MAX_SAMP_RATE;

		rsamp_ratio = (long) ((rtl_xtal * TWO_POW(22)) / samp_rate);
		rsamp_ratio &= ~3;

		real_rate = (rtl_xtal * TWO_POW(22)) / rsamp_ratio;

		if (samp_rate != real_rate)
			Log.e(TAG, "Exact sample rate is: " + real_rate + " Hz");

		if ((myTuner != null) && this.tuner_set_bw) {
			rtlsdr_set_i2c_repeater(true);
			try {
				myTuner.set_bw(0, (int) real_rate);
			} catch (IOException e1) {
				Log.e(TAG, e1.toString());
				e1.printStackTrace();
			}
			if (myTuner != null)
				try {
					myTuner.set_bw(0, (int) real_rate);
				} catch (IOException e) {
					Log.e(TAG, "error set_bw " + real_rate);
					e.printStackTrace();
				}
			rtlsdr_set_i2c_repeater(false);
		}

		this.rate = (long) real_rate;

		tmp = (char) ((char) (rsamp_ratio >> 16) & 0x0000ffff);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x9f, tmp, (byte) 2);
		tmp = (char) (rsamp_ratio & 0x0ffff);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0xa1, tmp, (byte) 2);

		r |= rtlsdr_set_sample_freq_correction(corr);

		/* reset demod (bit 3, soft_rst) */
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x01, (char) 0x14,
				(byte) 1);
		r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x01, (char) 0x10,
				(byte) 1);

		/* recalculate offset frequency if offset tuning is enabled */
		if (this.offs_freq != 0)
			rtlsdr_set_offset_tuning(true);

		return r;
	}

	private long rtlsdr_get_sample_rate() {

		return this.rate;
	}

	private int rtlsdr_set_testmode(boolean on) {

		return rtlsdr_demod_write_reg((byte) 0, (char) 0x19, (char) (on ? 0x03
				: 0x05), (byte) 1);
	}

	private int rtlsdr_set_agc_mode(boolean on) {

		return rtlsdr_demod_write_reg((byte) 0, (char) 0x19, (char) (on ? 0x25
				: 0x05), (byte) 1);
	}

	private int rtlsdr_set_direct_sampling(boolean on) {
		int r = 0;

		// if (!dev) return -1;

		if (on) {
			if ((this.tuner) && this.tuner_exit) {
				rtlsdr_set_i2c_repeater(true);
				// r = this.tuner->exit();
				if (myTuner != null)
					try {
						myTuner.exit(0);
					} catch (IOException e) {
						Log.e(TAG, "myTuner.exit(0) FAILED " + e.toString());
						e.printStackTrace();
					}
				rtlsdr_set_i2c_repeater(false);
			}

			/* disable Zero-IF mode */
			r |= rtlsdr_demod_write_reg((byte) 1, (char) 0xb1, (char) 0x1a,
					(byte) 1);

			/* disable spectrum inversion */
			r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x15, (char) 0x00,
					(byte) 1);

			/* only enable In-phase ADC input */
			r |= rtlsdr_demod_write_reg((byte) 0, (char) 0x08, (char) 0x4d,
					(byte) 1);

			/* swap I and Q ADC, this allows to select between two inputs */
			r |= rtlsdr_demod_write_reg((byte) 0, (char) 0x06,
					(char) (on ? 0x90 : 0x80), (byte) 1);

			Log.e(TAG,
					"rtlsdr_set_direct_sampling Enabled direct sampling mode, input "
							+ on);
			this.direct_sampling = on;
		} else {
			if ((this.myTuner != null) && this.tuner_init) {
				rtlsdr_set_i2c_repeater(true);
				try {
					r |= myTuner.init(0);
				} catch (IOException e) {

					e.printStackTrace();
					Log.e(TAG, e.toString());
				}
				rtlsdr_set_i2c_repeater(true);
			}

			if (this.tuner_type == RTLSDR_TUNER_R820T) {
				r |= rtlsdr_set_if_freq(R820T_IF_FREQ);

				/* enable spectrum inversion */
				r |= rtlsdr_demod_write_reg((byte) 1, (char) 0x15, (char) 0x01,
						(byte) 1);
			} else {
				r |= rtlsdr_set_if_freq(0);

				/* enable In-phase + Quadrature ADC input */
				r |= rtlsdr_demod_write_reg((byte) 0, (char) 0x08, (char) 0xcd,
						(byte) 1);

				/* Enable Zero-IF mode */
				r |= rtlsdr_demod_write_reg((byte) 1, (char) 0xb1, (char) 0x1b,
						(byte) 1);
			}

			/* opt_adc_iq = 0, default ADC_I/ADC_Q datapath */
			r |= rtlsdr_demod_write_reg((byte) 0, (char) 0x06, (char) 0x80,
					(byte) 1);

			Log.e(TAG, "Disabled direct sampling mode\n");
			this.direct_sampling = false;
		}

		r |= rtlsdr_set_center_freq(this.freq);

		return r;
	}

	private boolean rtlsdr_get_direct_sampling() {
		return this.direct_sampling;
	}

	private int rtlsdr_set_offset_tuning(boolean on) {
		int r = 0;

		if (this.tuner_type == RTLSDR_TUNER_R820T)
			return -2;

		if (this.direct_sampling)
			return -3;

		/* based on keenerds 1/f noise measurements */
		this.offs_freq = on ? ((this.rate / 2) * 170 / 100) : 0;
		// r |= rtlsdr_set_if_freq(this.offs_freq);

		if ((this.tuner) && /* this.tuner.set_bw */true) {
			rtlsdr_set_i2c_repeater(true);
			// this.tuner.set_bw(on ? (2 * this.offs_freq) : this. rate);
			rtlsdr_set_i2c_repeater(false);
		}

		if (this.freq > this.offs_freq)
			r |= rtlsdr_set_center_freq(this.freq);

		return r;
	}

	private int rtlsdr_get_offset_tuning() {
		return (this.offs_freq < 0) ? 1 : 0;
	}

	private RtlSdr_tuner_iface rtlsdr_detect() throws IOException {
		int reg = 0;
		int ret = 0;

		if (myTuner != null) {
			Log.e(TAG, "Error Already called rtlsdr_open()");
			// return (myTuner);
		}

		Log.e(TAG, "rtlsdr_open()");

		/* perform a dummy write, if it fails, reset the device */
		if ((ret = rtlsdr_write_reg(USBB, USB_SYSCTL, (char) 0x09, (byte) 1)) < 0) {
			if (ret != 0)
				Log.e(TAG,
						"ERROR ret "
								+ ret
								+ " Line "
								+ Thread.currentThread().getStackTrace()[2]
										.getLineNumber());
			Log.e(TAG, "Resetting device...");

		}

		rtlsdr_init_baseband();
		/* Probe tuners */
		rtlsdr_set_i2c_repeater(true);

		reg = rtlsdr_i2c_read_reg(R820T_I2C_ADDR, R820T_CHECK_ADDR) & 0x0000FF;
		if (reg == R820T_CHECK_VAL) {
			Log.e(TAG, "Found Rafael Micro R820T tuner\n");
			this.tuner_type = RTLSDR_TUNER_R820T;

			/* disable Zero-IF mode */
			rtlsdr_demod_write_reg((byte) 1, (char) 0xb1, (char) 0x1a, (byte) 1);

			/* only enable In-phase ADC input */
			rtlsdr_demod_write_reg((byte) 0, (char) 0x08, (char) 0x4d, (byte) 1);

			/*
			 * the R820T uses 3.57 MHz IF for the DVB-T 6 MHz mode, and 4.57 MHz
			 * for the 8 MHz mode
			 */
			rtlsdr_set_if_freq(R820T_IF_FREQ);

			/* enable spectrum inversion */
			rtlsdr_demod_write_reg((byte) 1, (char) 0x15, (char) 0x01, (byte) 1);

			myTuner = new r820T_tuner();
			if (myTuner != null)
				Log.e(TAG, "Error on tuner init");
			myTuner.init(0);

			rtlsdr_set_i2c_repeater(false);

			tun_xtal = rtl_xtal; /* use the rtl clock value by default */

			return myTuner;
		}

		reg = rtlsdr_i2c_read_reg(R828D_I2C_ADDR, R820T_CHECK_ADDR);
		if (reg == R820T_CHECK_VAL) {
			Log.e(TAG, "Found Rafael Micro R828D tuner\n");
			this.tuner_type = RTLSDR_TUNER_R828D;
			myTuner = new r820T_tuner();// ??

			myTuner.init(0);
			rtlsdr_set_i2c_repeater(false);
		}

		/* initialise GPIOs */
		reg = rtlsdr_i2c_read_reg(E4K_I2C_ADDR, E4K_CHECK_ADDR) & 0x000000FF;
		if (reg == E4K_CHECK_VAL) {
			Log.e(TAG, "Found Elonics E4000 tuner");
			this.tuner_type = RTLSDR_TUNER_E4000;
			myTuner = new e4k_tuner();

			myTuner.init(0);

			rtlsdr_set_i2c_repeater(false);

			tun_xtal = rtl_xtal; /* use the rtl clock value by default */

			return myTuner;
		}

		reg = rtlsdr_i2c_read_reg(FC0013_I2C_ADDR, FC0013_CHECK_ADDR) & 0x000000FF;
		if (reg == FC0013_CHECK_VAL) {
			Log.e(TAG, "Found Fitipower FC0013 tuner");

			this.tuner_type = RTLSDR_TUNER_FC0013;

			tun_xtal = rtl_xtal; /* use the rtl clock value by default */

			myTuner = new fc0013_tuner();

			myTuner.init(0);

			rtlsdr_set_i2c_repeater(false);

			return myTuner;
		}

		rtlsdr_set_gpio_output((byte) 5);

		/* reset tuner before probing */
		rtlsdr_tuner_reset();

		reg = rtlsdr_i2c_read_reg(FC2580_I2C_ADDR, FC2580_CHECK_ADDR) & 0x000000FF;
		if ((reg & 0x7f) == FC2580_CHECK_VAL) {
			Log.e(TAG, "Found FCI 2580 tuner");
			this.tuner_type = RTLSDR_TUNER_FC2580;
			myTuner = new fc2580_tuner();

			myTuner.init(0);

			rtlsdr_set_i2c_repeater(false);

			tun_xtal = rtl_xtal; /* use the rtl clock value by default */

			return myTuner;
		}

		reg = rtlsdr_i2c_read_reg(FC0012_I2C_ADDR, FC0012_CHECK_ADDR) & 0x000000FF;
		if (reg == FC0012_CHECK_VAL) {
			Log.e(TAG, "Found Fitipower FC0012 tuner");
			rtlsdr_set_gpio_output((byte) 6);
			this.tuner_type = RTLSDR_TUNER_FC0012;

			myTuner = new fc0012_tuner();

			myTuner.init(0);

			rtlsdr_set_i2c_repeater(false);

			tun_xtal = rtl_xtal; /* use the rtl clock value by default */

			return myTuner;
		}

		if (this.tuner_type == RTLSDR_TUNER_UNKNOWN) {
			Log.e(TAG, "No supported tuner found\n");
			rtlsdr_set_direct_sampling(true);
			return null;
		}

		tun_xtal = rtl_xtal; /* use the rtl clock value by default */

		rtlsdr_set_i2c_repeater(false);

		return myTuner;
	}

	private void rtlsdr_tuner_led_on() {
		/* reset tuner before probing */
		rtlsdr_set_gpio_bit((byte) 0, true);
		// rtlsdr_set_gpio_bit((byte) 1, false);
	}

	private void rtlsdr_tuner_reset() {
		/* reset tuner before probing */
		rtlsdr_set_gpio_bit((byte) 5, true);
		rtlsdr_set_gpio_bit((byte) 5, false);
	}

	private int rtlsdr_reset_buffer() {

		rtlsdr_write_reg(USBB, USB_EPA_CTL, (char) 0x1002, (byte) 2);
		rtlsdr_write_reg(USBB, USB_EPA_CTL, (char) 0x0000, (byte) 2);

		return 0;
	}

	private int rtlsdr_read_sync(byte[] buf, int len, int n_read)
			throws IOException {
		return (read(buf, BULK_TIMEOUT));
		// return libusb_bulk_transfer(0x81, buf, len, n_read, BULK_TIMEOUT);
	}

	/*
	 * @Override public int setBaudRate(int baudRate) throws IOException { //
	 * TODO Auto-generated method stub return 0; }
	 */

	@Override
	public void settings(boolean agc, int mygain, int ppm) {
		enable_agc = agc;
		ppm_error = ppm;
		gain = mygain;
	}

	@Override
	public void setParameters(int baudRate, int dataBits, int stopBits,
			int parity) throws IOException {

	}

	@Override
	public boolean getCD() throws IOException {
		return false;
	}

	@Override
	public boolean getCTS() throws IOException {
		return false;
	}

	@Override
	public boolean getDSR() throws IOException {
		return false;
	}

	@Override
	public boolean getDTR() throws IOException {
		return false;
	}

	@Override
	public void setDTR(boolean value) throws IOException {
	}

	@Override
	public boolean getRI() throws IOException {
		return false;
	}

	@Override
	public boolean getRTS() throws IOException {
		return false;
	}

	@Override
	public void setRTS(boolean value) throws IOException {
	}

	public static int rtlsdr_i2c_write_fn(byte addr, byte[] buf, byte len) {
		return rtlsdr_i2c_write(addr, buf, len);

	}

	public static int rtlsdr_i2c_read_fn(byte addr, byte[] buf, byte len) {
		return rtlsdr_i2c_read(addr, buf, len);

	}

}
