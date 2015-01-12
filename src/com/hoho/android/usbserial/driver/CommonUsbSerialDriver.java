package com.hoho.android.usbserial.driver;

import java.io.IOException;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;

/**
 * A base class shared by several driver implementations.
 * 
 * @author mike wakerly (opensource@hoho.com)
 */
public abstract class CommonUsbSerialDriver implements UsbSerialDriver {

	public static final int DEFAULT_READ_BUFFER_SIZE = (16 * 16384);// was
																	// 16*1024
	public static final int DEFAULT_WRITE_BUFFER_SIZE = 16 * 1024;

	protected final UsbDevice mDevice;
	protected static UsbDeviceConnection mConnection = null;

	protected final Object mReadBufferLock = new Object();
	protected final Object mWriteBufferLock = new Object();

	/** Internal read buffer. Guarded by {@link #mReadBufferLock}. */
	protected byte[] mReadBuffer;

	/** Internal write buffer. Guarded by {@link #mWriteBufferLock}. */
	protected byte[] mWriteBuffer;

	public CommonUsbSerialDriver(UsbDevice device,
			UsbDeviceConnection connection) {
		mDevice = device;
		mConnection = connection;

		mReadBuffer = new byte[DEFAULT_READ_BUFFER_SIZE];
		mWriteBuffer = new byte[DEFAULT_WRITE_BUFFER_SIZE];
	}

	/**
	 * Returns the currently-bound USB device.
	 * 
	 * @return the device
	 */
	public final UsbDevice getDevice() {
		return mDevice;
	}

	/**
	 * Sets the size of the internal buffer used to exchange data with the USB
	 * stack for read operations. Most users should not need to change this.
	 * 
	 * @param bufferSize
	 *            the size in bytes
	 */
	public final void setReadBufferSize(int bufferSize) {
		synchronized (mReadBufferLock) {
			if (bufferSize == mReadBuffer.length) {
				return;
			}
			mReadBuffer = new byte[bufferSize];
		}
	}

	/**
	 * Sets the size of the internal buffer used to exchange data with the USB
	 * stack for write operations. Most users should not need to change this.
	 * 
	 * @param bufferSize
	 *            the size in bytes
	 */
	public final void setWriteBufferSize(int bufferSize) {
		synchronized (mWriteBufferLock) {
			if (bufferSize == mWriteBuffer.length) {
				return;
			}
			mWriteBuffer = new byte[bufferSize];
		}
	}

	@Override
	public abstract void open() throws IOException;

	@Override
	public abstract void close() throws IOException;

	@Override
	public abstract int read(final byte[] dest, final int timeoutMillis)
			throws IOException;

	@Override
	public abstract int write(final byte[] src, final int timeoutMillis)
			throws IOException;

	@Override
	public abstract void setParameters(int baudRate, int dataBits,
			int stopBits, int parity) throws IOException;

	@Override
	public abstract boolean getCD() throws IOException;

	@Override
	public abstract boolean getCTS() throws IOException;

	@Override
	public abstract boolean getDSR() throws IOException;

	@Override
	public abstract boolean getDTR() throws IOException;

	@Override
	public abstract void setDTR(boolean value) throws IOException;

	@Override
	public abstract boolean getRI() throws IOException;

	@Override
	public abstract boolean getRTS() throws IOException;

	@Override
	public abstract void setRTS(boolean value) throws IOException;

}
