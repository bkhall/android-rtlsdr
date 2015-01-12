package com.hoho.android.usbserial.driver;

/**
 * Generic unchecked exception for the usbserial package.
 * 
 * @author mike wakerly (opensource@hoho.com)
 */
@SuppressWarnings("serial")
public class UsbSerialRuntimeException extends RuntimeException {

	public UsbSerialRuntimeException() {
		super();
	}

	public UsbSerialRuntimeException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public UsbSerialRuntimeException(String detailMessage) {
		super(detailMessage);
	}

	public UsbSerialRuntimeException(Throwable throwable) {
		super(throwable);
	}

}
