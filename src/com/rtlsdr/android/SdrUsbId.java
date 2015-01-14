package com.rtlsdr.android;

public class SdrUsbId {

	public static final int VENDOR_KYE = 0x0458;
	public static final int KYE_RTL2832_707F = 0x707f;

	public static final int VENDOR_RTL = 0x0bda;
	public static final int RTL_RTL2832 = 0x2832;
	public static final int RTL_RTL2838 = 0x2838;

	public static final int VENDOR_TERRATEC = 0x0ccd;
	public static final int TERRATEC_RTL2838_A9 = 0x00a9;
	public static final int TERRATEC_RTL2838_B3 = 0x00b3;
	public static final int TERRATEC_RTL2838_B4 = 0x00b4;
	public static final int TERRATEC_RTL2838_B7 = 0x00b7;
	public static final int TERRATEC_RTL2838_C6 = 0x00c6;
	public static final int TERRATEC_RTL2838_D3 = 0x00d3;
	public static final int TERRATEC_RTL2838_D7 = 0x00d7;
	public static final int TERRATEC_RTL2838_E0 = 0x00e0;

	public static final int VENDOR_COMPRO = 0x185b;
	public static final int COMPRO_620 = 0x0620;;
	public static final int COMPRO_650 = 0x0650;
	public static final int COMPRO_680 = 0x0680;

	public static final int VENDOR_AFATECH = 0x01b80;
	public static final int AFATECH_RTL2838_D393 = 0xd393;
	public static final int AFATECH_RTL2838_D394 = 0xd394;
	public static final int AFATECH_RTL2838_D395 = 0xd395;
	public static final int AFATECH_RTL2838_D396 = 0xd396;
	public static final int AFATECH_RTL2838_D397 = 0xd397;
	public static final int AFATECH_RTL2838_D398 = 0xd398;
	public static final int AFATECH_RTL2838_D39D = 0xd39d;
	public static final int AFATECH_RTL2838_D3A4 = 0xd3a4;

	public static final int VENDOR_DEXATEC = 0x01d19;
	public static final int DEXATEC_1101 = 0x1101;
	public static final int DEXATEC_1102 = 0x1102;
	public static final int DEXATEC_1103 = 0x1103;

	public static final int VENDOR_GTEK = 0x1f4d;
	public static final int GTEK_RTL2838_B803 = 0xb803;
	public static final int GTEK_RTL2838_C803 = 0xC803;
	public static final int GTEK_RTL2838_D286 = 0xd286;
	public static final int GTEK_RTL2838_D803 = 0xD803;

	private SdrUsbId() {
		throw new IllegalAccessError("Non-instantiable class.");
	}
}
