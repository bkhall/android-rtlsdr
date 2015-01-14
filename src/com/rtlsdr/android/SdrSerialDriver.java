package com.rtlsdr.android;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

import com.hoho.android.usbserial.driver.CommonUsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

public class SdrSerialDriver implements UsbSerialDriver {

	private class SdrSerialPort extends CommonUsbSerialPort {

		public SdrSerialPort(UsbDevice device, int portNumber) {
			super(device, portNumber);
		}

		@Override
		public void close() throws IOException {
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
		public UsbSerialDriver getDriver() {
			return null;
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
		public boolean getRI() throws IOException {
			return false;
		}

		@Override
		public boolean getRTS() throws IOException {
			return false;
		}

		@Override
		public void open(UsbDeviceConnection connection) throws IOException {
		}

		@Override
		public int read(byte[] dest, int timeoutMillis) throws IOException {
			return 0;
		}

		@Override
		public void setDTR(boolean value) throws IOException {
		}

		@Override
		public void setParameters(int baudRate, int dataBits, int stopBits,
				int parity) throws IOException {
		}

		@Override
		public void setRTS(boolean value) throws IOException {
		}

		@Override
		public int write(byte[] src, int timeoutMillis) throws IOException {
			return 0;
		}
	}

	public static Map<Integer, int[]> getSupportedDevices() {
		final Map<Integer, int[]> supportedDevices = new LinkedHashMap<Integer, int[]>();

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_KYE),
				new int[] { SdrUsbId.KYE_RTL2832_707F });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_RTL), new int[] {
				SdrUsbId.RTL_RTL2832, SdrUsbId.RTL_RTL2838 });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_TERRATEC),
				new int[] { SdrUsbId.TERRATEC_RTL2838_A9,
						SdrUsbId.TERRATEC_RTL2838_B3,
						SdrUsbId.TERRATEC_RTL2838_B4,
						SdrUsbId.TERRATEC_RTL2838_B7,
						SdrUsbId.TERRATEC_RTL2838_C6,
						SdrUsbId.TERRATEC_RTL2838_D3,
						SdrUsbId.TERRATEC_RTL2838_D7,
						SdrUsbId.TERRATEC_RTL2838_E0 });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_COMPRO),
				new int[] { SdrUsbId.COMPRO_620, SdrUsbId.COMPRO_650,
						SdrUsbId.COMPRO_680 });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_AFATECH),
				new int[] { SdrUsbId.AFATECH_RTL2838_D393,
						SdrUsbId.AFATECH_RTL2838_D394,
						SdrUsbId.AFATECH_RTL2838_D395,
						SdrUsbId.AFATECH_RTL2838_D395,
						SdrUsbId.AFATECH_RTL2838_D396,
						SdrUsbId.AFATECH_RTL2838_D397,
						SdrUsbId.AFATECH_RTL2838_D398,
						SdrUsbId.AFATECH_RTL2838_D39D,
						SdrUsbId.AFATECH_RTL2838_D3A4 });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_DEXATEC),
				new int[] { SdrUsbId.DEXATEC_1101, SdrUsbId.DEXATEC_1102,
						SdrUsbId.DEXATEC_1103 });

		supportedDevices.put(Integer.valueOf(SdrUsbId.VENDOR_GTEK), new int[] {
				SdrUsbId.GTEK_RTL2838_B803, SdrUsbId.GTEK_RTL2838_C803,
				SdrUsbId.GTEK_RTL2838_D286, SdrUsbId.GTEK_RTL2838_D803 });

		return supportedDevices;
	}

	private final UsbDevice mDevice;

	private final UsbSerialPort mPort;

	public SdrSerialDriver(UsbDevice device) {
		mDevice = device;
		mPort = new SdrSerialPort(mDevice, 0);
	}

	@Override
	public UsbDevice getDevice() {
		return mDevice;
	}

	@Override
	public List<UsbSerialPort> getPorts() {
		return Collections.singletonList(mPort);
	}
}