package com.rtlsdr.android.tuner;

import java.io.IOException;

import android.util.Log;

import com.rtlsdr.android.SdrUSBDriver;

public class r820T_tuner implements RtlSdr_tuner_iface {
	private static final String TAG = r820T_tuner.class.getSimpleName();
	public static final byte R820T_I2C_ADDR = 0x34;
	public static final int R820T_CHECK_ADDR = 0x00;
	public static final int R820T_CHECK_VAL = 0x69;

	public static final int R820T_IF_FREQ = 3570000;

	public static final String VERSION = "R820T_v1.49_ASTRO";
	public static final int VER_NUM = 49;

	public static final boolean USE_16M_XTAL = false;
	public static final int R828_Xtal = 28800;

	public static final boolean USE_DIPLEXER = false;
	public static final boolean TUNER_CLK_OUT = true;

	// #ifndef _UINT_X_
	public static final int _UINT_X_ = 1;

	public static final int FUNCTION_SUCCESS = 0;
	public static final int FUNCTION_ERROR = -1;
	public static final int DIP_FREQ = 320000;
	public static final int IMR_TRIAL = 9;
	public static final int VCO_pwr_ref = 0x02;
	R828_I2C_TYPE R828_I2C = new R828_I2C_TYPE();
	R828_I2C_LEN_TYPE R828_I2C_Len = new R828_I2C_LEN_TYPE();

	// enum Rafael_Chip_Type //Don't modify chip list
	// {
	public static final int R828 = 0;
	public static final int R828D = 1;
	public static final int R828S = 2;
	public static final int R820T = 3;
	public static final int R820C = 4;
	public static final int R620D = 5;
	public static final int R620S = 6;
	// };
	// enum R828_Standard_Type //Don't remove standard list!!
	// {
	public static final int NTSC_MN = 0;
	public static final int PAL_I = 1;
	public static final int PAL_DK = 2;
	public static final int PAL_B_7M = 3; // no use
	public static final int PAL_BGH_8M = 4; // for PAL B/G, PAL G/H
	public static final int SECAM_L = 5;
	public static final int SECAM_L1_INV = 6; // for SECAM L'
	public static final int SECAM_L1 = 7; // no use
	public static final int ATV_SIZE = 8;
	public static final int DVB_T_6M = 8;
	public static final int DVB_T_7M = 9;
	public static final int DVB_T_7M_2 = 10;
	public static final int DVB_T_8M = 11;
	public static final int DVB_T2_6M = 12;
	public static final int DVB_T2_7M = 13;
	public static final int DVB_T2_7M_2 = 14;
	public static final int DVB_T2_8M = 15;
	public static final int DVB_T2_1_7M = 16;
	public static final int DVB_T2_10M = 17;
	public static final int DVB_C_8M = 18;
	public static final int DVB_C_6M = 19;
	public static final int ISDB_T = 20;
	public static final int DTMB = 21;
	public static final int R828_ATSC = 22;
	public static final int FM = 23;
	public static final int STD_SIZE = 23;
	// };
	// class R828_SetFreq_Type
	// {
	public static final boolean FAST_MODE = true;
	public static final boolean NORMAL_MODE = false;
	private static final int LOOP_THROUGH = 0;

	// private static final int true = 0;
	// private static final int false = 1;
	// private static final int TRUE = 1;
	// private static final int FALSE = 0;
	// };

	class R828_LoopThrough_Type {
		public static final boolean LOOP_THROUGH1 = true;
		public static final boolean SIGLE_IN = false;
	};

	class R828_InputMode_Type {
		public static final int AIR_IN = 0;
		public static final int CABLE_IN_1 = 1;
		public static final int CABLE_IN_2 = 2;
	};

	class R828_IfAgc_Type {
		public static final int IF_AGC1 = 0;
		public static final int IF_AGC2 = 1;
	};

	class R828_GPIO_Type {
		public static final boolean HI_SIG = true;
		public static final boolean LO_SIG = false;
	};

	class R828_Set_Info {
		long RF_Hz;
		long RF_KHz;
		int R828_Standard;
		R828_LoopThrough_Type RT_Input;
		R828_InputMode_Type RT_InputMode;
		R828_IfAgc_Type R828_IfAgc_Select;
	};

	class R828_RF_Gain_Info {
		byte RF_gain1;
		byte RF_gain2;
		byte RF_gain_comb;
	};

	class R828_RF_Gain_TYPE {
		int RF_AUTO = 0;
		int RF_MANUAL;
	};

	class R828_I2C_LEN_TYPE {
		byte RegAddr;
		byte[] Data = new byte[50];
		byte Len;
	};

	class R828_I2C_TYPE {
		byte RegAddr;
		byte Data;
	};

	byte[] R828_iniArry = { (byte) 0x83, 0x32, 0x75, (byte) 0xC0, 0x40,
			(byte) 0xD6, 0x6C, (byte) 0xF5, 0x63,
			/* 0x05 0x06 0x07 0x08 0x09 0x0A 0x0B 0x0C 0x0D */

			0x75, 0x68, 0x6C, (byte) 0x83, (byte) 0x80, 0x00, 0x0F,
			0x00,
			(byte) 0xC0,// xtal_check
			/* 0x0E 0x0F 0x10 0x11 0x12 0x13 0x14 0x15 0x16 */

			0x30, 0x48, (byte) 0xCC, 0x60, 0x00, 0x54, (byte) 0xAE, 0x4A,
			(byte) 0xC0 };
	/*
	 * 0x17 0x18 0x19 0x1A 0x1B 0x1C 0x1D 0x1E 0x1F
	 */
	int R828_ADDRESS = 0x34;
	int Rafael_Chip = R820T;
	byte[] R828_Arry = new byte[27];

	// ----------------------------------------------------------//
	// Internal Structs //
	// ----------------------------------------------------------//
	class R828_SectType {
		int Phase_Y;
		int Gain_X;
		int Value;
	};

	// class BW_Type
	// {
	byte BW_6M = 0;
	byte BW_7M = 1;
	byte BW_8M = 2;
	byte BW_1_7M = 3;
	byte BW_10M = 4;
	byte BW_200K = 5;

	// };

	class Sys_Info_Type {
		char IF_KHz;
		int BW;
		int FILT_CAL_LO;
		byte FILT_GAIN;
		byte IMG_R;
		byte FILT_Q;
		byte HP_COR;
		byte EXT_ENABLE;
		byte LOOP_THROUGH1;
		byte LT_ATT;
		byte FLT_EXT_WIDEST;
		byte POLYFIL_CUR;
	};

	class Freq_Info_Type {
		byte OPEN_D;
		byte RF_MUX_PLOY;
		byte TF_C;
		byte XTAL_CAP20P;
		byte XTAL_CAP10P;
		byte XTAL_CAP0P;
		byte IMR_MEM;
	};

	class SysFreq_Info_Type {
		byte LNA_TOP;
		byte LNA_VTH_L;
		byte MIXER_TOP;
		byte MIXER_VTH_L;
		byte AIR_CABLE1_IN;
		byte CABLE2_IN;
		byte PRE_DECT;
		byte LNA_DISbyteGE;
		byte CP_CUR;
		byte DIV_BUF_CUR;
		byte FILTER_CUR;
	};

	// ----------------------------------------------------------//
	// Internal Parameters //
	// ----------------------------------------------------------//
	// class XTAL_CAP_VALUE
	// {
	public static final int XTAL_LOW_CAP_30P = 0;
	public static final int XTAL_LOW_CAP_20P = 1;
	public static final int XTAL_LOW_CAP_10P = 2;
	public static final int XTAL_LOW_CAP_0P = 3;
	public static final int XTAL_HIGH_CAP_0P = 4;
	// };

	// byte [] R828_Arry = new byte[27];
	/*
	 * R828_SectType [] IMR_Data_ = { {0, 0, 0}, {0, 0, 0}, {0, 0, 0}, {0, 0,
	 * 0}, {0, 0, 0} };//Please keep this array data for standby mode.
	 */
	// R828_I2C_TYPE R828_I2C;
	// R828_I2C_LEN_TYPE R828_I2C_Len;
	R828_SectType[] IMR_Data = new R828_SectType[5];

	int R828_IF_khz;
	int R828_CAL_LO_khz;
	byte R828_IMR_point_num;
	boolean R828_IMR_done_flag = false;
	byte[] R828_Fil_Cal_flag = new byte[STD_SIZE];
	byte[] R828_Fil_Cal_code = new byte[STD_SIZE];

	int Xtal_cap_sel = XTAL_LOW_CAP_0P;
	int Xtal_cap_sel_tmp = XTAL_LOW_CAP_0P;
	// ----------------------------------------------------------//
	// Internal static struct //
	// ----------------------------------------------------------//
	static SysFreq_Info_Type SysFreq_Info1;
	static Sys_Info_Type Sys_Info1;
	// static Freq_Info_Type R828_Freq_Info;
	static Freq_Info_Type Freq_Info1;

	@Override
	public int init(int param) throws IOException {
		boolean r = R828_Init(param);
		r820t_SetStandardMode(param, DVB_T_6M);
		return 0;
	}

	@Override
	public int exit(int param) throws IOException {
		return r820t_SetStandby(param, 0);

	}

	@Override
	public int set_freq(int param, long freq) throws IOException {
		return r820t_SetRfFreqHz(param, freq);
	}

	@Override
	public int set_bw(int param, int bw) throws IOException {

		return 0;
	}

	@Override
	public int set_gain(int param, int gain) throws IOException {

		if (R828_SetRfGain(param, gain))
			return 0;
		else
			return -1;
	}

	@Override
	public int set_if_gain(int param, int stage, int gain) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_gain_mode(int param, boolean manual) throws IOException {
		if (R828_RfGainMode(param, manual))
			return 0;
		else
			return -1;

	}

	int r820t_SetRfFreqHz(int pTuner, long RfFreqHz) {
		R828_Set_Info R828Info = new R828_Set_Info();

		// if(pExtra->IsStandardModeSet==NO)
		// goto error_status_set_tuner_rf_frequency;

		// R828Info.R828_Standard = (R828_Standard_Type)pExtra->StandardMode;
		R828Info.R828_Standard = 8;// R828_Standard_Type.DVB_T_6M;

		R828Info.RF_Hz = (RfFreqHz);
		R828Info.RF_KHz = (RfFreqHz / 1000);

		if (R828_SetFrequency(pTuner, R828Info, NORMAL_MODE) != true)
			return FUNCTION_ERROR;

		return FUNCTION_SUCCESS;
	}

	int r820t_SetStandardMode(int pTuner, int StandardMode) {
		if (R828_SetStandard(pTuner, StandardMode) != true)
			return FUNCTION_ERROR;

		return FUNCTION_SUCCESS;
	}

	int r820t_SetStandby(int pTuner, int LoopThroughType) {

		if (R828_Standby(pTuner, LoopThroughType) != true)
			return FUNCTION_ERROR;

		return FUNCTION_SUCCESS;
	}

	/* just reverses the bits of a byte */
	int r820t_Convert(int InvertNum) {
		int ReturnNum;
		int AddNum;
		int BitNum;
		int CountNum;

		ReturnNum = 0;
		AddNum = 0x80;
		BitNum = 0x01;

		for (CountNum = 0; CountNum < 8; CountNum++) {
			if ((BitNum & InvertNum) != 0)
				ReturnNum += AddNum;

			AddNum /= 2;
			BitNum *= 2;
		}

		return ReturnNum;
	}

	boolean I2C_Write_Len(int pTuner, R828_I2C_LEN_TYPE I2C_Info) {
		// byte DeviceAddr;

		int i = 0, j = 0;

		byte RegStartAddr;
		byte[] pWritingBytes = new byte[I2C_Info.Data.length];
		long ByteNum;
		// int count=0;
		byte[] WritingBuffer = new byte[2];
		long WritingByteNum, WritingByteNumMax, WritingByteNumRem;
		byte RegWritingAddr;

		// Get regiser start address, writing bytes, and byte number.
		RegStartAddr = I2C_Info.RegAddr;
		pWritingBytes = I2C_Info.Data;
		ByteNum = I2C_Info.Len;

		// Calculate maximum writing byte number.
		// WritingByteNumMax = pBaseInterface->I2cWritingByteNumMax -
		// LEN_1_BYTE;
		WritingByteNumMax = 2 - 1; // 9 orig

		// Set tuner register bytes with writing bytes.
		// Note: Set tuner register bytes considering maximum writing byte
		// number.
		for (i = 0; i < ByteNum; i += WritingByteNumMax) {
			// Set register writing address.
			RegWritingAddr = (byte) (RegStartAddr + i);

			// Calculate remainder writing byte number.
			WritingByteNumRem = (char) (ByteNum - i);

			// Determine writing byte number.
			WritingByteNum = (WritingByteNumRem > WritingByteNumMax) ? WritingByteNumMax
					: WritingByteNumRem;

			// Set writing buffer.
			// Note: The I2C format of tuner register byte setting is as
			// follows:
			// start_bit + (DeviceAddr | writing_bit) + RegWritingAddr +
			// writing_bytes (WritingByteNum bytes) +
			// stop_bit
			WritingBuffer[0] = RegWritingAddr;

			for (j = 0; j < WritingByteNum; j++)
				// WritingBuffer[j+1] = pWritingBytes[i + j];
				WritingBuffer[j + 1] = I2C_Info.Data[i + j];
			// Set tuner register bytes with writing buffer.
			// if(pI2cBridge->ForwardI2cWritingCmd(pI2cBridge, DeviceAddr,
			// WritingBuffer, WritingByteNum + LEN_1_BYTE) !=
			// FUNCTION_SUCCESS)
			// goto error_status_set_tuner_registers;
			Log.d(TAG, "WritingBuffer[0] " + WritingBuffer[0] + ","
					+ " WritingBuffer[1] " + WritingBuffer[1] + " i= " + i);
			while (SdrUSBDriver.rtlsdr_i2c_write_fn(R820T_I2C_ADDR,
					WritingBuffer,  (byte) (WritingByteNum + 1)) < 0) {
				Log.d(TAG, "WritingBuffer[0] " + WritingBuffer[0] + ","
						+ " WritingBuffer[1] " + WritingBuffer[1] + " i= " + i
						+ " FAILED!!!");
				int count = 0;
				count++;
				if (count > 5)
					return false;
			}
		}
		return true;
	}

	boolean I2C_Read_Len(int pTuner, R828_I2C_LEN_TYPE I2C_Info) {
		// byte DeviceAddr;

		int i;

		byte[] RegStartAddr = { 0x00 };
		byte[] ReadingBytes = new byte[128];
		byte ByteNum;

		// Get regiser start address, writing bytes, and byte number.
		// RegStartAddr = 0x00;
		ByteNum =  I2C_Info.Len;

		// Set tuner register reading address.
		// Note: The I2C format of tuner register reading address setting is as
		// follows:
		// start_bit + (DeviceAddr | writing_bit) + RegReadingAddr + stop_bit
		// if(pI2cBridge->ForwardI2cWritingCmd(pI2cBridge, DeviceAddr,
		// &RegStartAddr, LEN_1_BYTE) != FUNCTION_SUCCESS)
		// goto error_status_set_tuner_register_reading_address;

		if (SdrUSBDriver.rtlsdr_i2c_write_fn(R820T_I2C_ADDR, RegStartAddr, (byte) 1) < 0)
			return false;

		// Get tuner register bytes.
		// Note: The I2C format of tuner register byte getting is as follows:
		// start_bit + (DeviceAddr | reading_bit) + reading_bytes
		// (ReadingByteNum bytes) + stop_bit
		// if(pI2cBridge->ForwardI2cReadingCmd(pI2cBridge, DeviceAddr,
		// ReadingBytes, ByteNum) != FUNCTION_SUCCESS)
		// goto error_status_get_tuner_registers;

		if (SdrUSBDriver.rtlsdr_i2c_read_fn(R820T_I2C_ADDR, ReadingBytes,
				ByteNum) < 0)
			return false;

		for (i = 0; i < ByteNum; i++) {
			I2C_Info.Data[i] = (byte) r820t_Convert(ReadingBytes[i]);
		}

		return true;

		// error_status_get_tuner_registers:
		// error_status_set_tuner_register_reading_address:

		// return false;
	}

	boolean I2C_Write(int pTuner, R828_I2C_TYPE I2C_Info) {
		byte[] WritingBuffer = new byte[2];

		// Set writing bytes.
		// Note: The I2C format of tuner register byte setting is as follows:
		// start_bit + (DeviceAddr | writing_bit) + addr + data + stop_bit
		WritingBuffer[0] = I2C_Info.RegAddr;
		WritingBuffer[1] = I2C_Info.Data;

		// Set tuner register bytes with writing buffer.
		// if(pI2cBridge->ForwardI2cWritingCmd(pI2cBridge, DeviceAddr,
		// WritingBuffer, LEN_2_BYTE) != FUNCTION_SUCCESS)
		// goto error_status_set_tuner_registers;

		// printf("called %s: %02x -> %02x\n", __FUNCTION__, WritingBuffer[0],
		// WritingBuffer[1]);

		if (SdrUSBDriver.rtlsdr_i2c_write_fn(R820T_I2C_ADDR, WritingBuffer,	 (byte) 2) < 0)
			return false;

		return true;
	}

	void R828_Delay_MS(int pTuner, long WaitTimeMs) {
		/* simply don't wait for now */
		return;
	}

	/*
	 * int R828_RfGainMode(int pTuner, boolean manual) { int MixerGain; int
	 * LnaGain;
	 * 
	 * MixerGain = 0; LnaGain = 0;
	 * 
	 * if (manual) { //LNA auto off R828_I2C.RegAddr = 0x05; R828_Arry[0] =
	 * (byte) (R828_Arry[0] | 0x10); R828_I2C.Data = R828_Arry[0];
	 * if(I2C_Write(pTuner, R828_I2C) != true) return false;
	 * 
	 * //Mixer auto off R828_I2C.RegAddr = 0x07; R828_Arry[2] = (byte)
	 * (R828_Arry[2] & 0xEF); R828_I2C.Data = R828_Arry[2]; if(I2C_Write(pTuner,
	 * R828_I2C) != true) return false;
	 * 
	 * R828_I2C_Len.RegAddr = 0x00; R828_I2C_Len.Len = 4;
	 * if(I2C_Read_Len(pTuner, R828_I2C_Len) != true) return false;
	 * 
	 * set fixed VGA gain for now (16.3 dB) R828_I2C.RegAddr = 0x0C;
	 * R828_Arry[7] = (byte) ((R828_Arry[7] & 0x60) | 0x08); R828_I2C.Data =
	 * R828_Arry[7]; if(I2C_Write(pTuner, R828_I2C) != true) return false;
	 * 
	 * 
	 * } else { //LNA R828_I2C.RegAddr = 0x05; R828_Arry[0] = (byte)
	 * (R828_Arry[0] & 0xEF); R828_I2C.Data = R828_Arry[0]; if(I2C_Write(pTuner,
	 * R828_I2C) != true) return false;
	 * 
	 * //Mixer R828_I2C.RegAddr = 0x07; R828_Arry[2] = (byte) (R828_Arry[2] |
	 * 0x10); R828_I2C.Data = R828_Arry[2]; if(I2C_Write(pTuner, R828_I2C) !=
	 * true) return false;
	 * 
	 * set fixed VGA gain for now (26.5 dB) R828_I2C.RegAddr = 0x0C;
	 * R828_Arry[7] = (byte) ((R828_Arry[7] & 0x60) | 0x0B); R828_I2C.Data =
	 * R828_Arry[7]; if(I2C_Write(pTuner, R828_I2C) != true) return false; }
	 * 
	 * return true; }
	 */
	Sys_Info_Type R828_Sys_Sel(int R828_Standard) {
		Sys_Info_Type R828_Sys_Info = new Sys_Info_Type();

		switch (R828_Standard) {

		case DVB_T_6M:
		case DVB_T2_6M:
			R828_Sys_Info.IF_KHz = 3570;
			R828_Sys_Info.BW = BW_6M;
			R828_Sys_Info.FILT_CAL_LO = 56000; // 52000->56000
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x6B; // 1.7M disable, +2cap, 1.0MHz
			R828_Sys_Info.EXT_ENABLE = 0x60; // R30[6]=1 ext enable; R30[5]:1
												// ext at LNA max-1
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		case DVB_T_7M:
		case DVB_T2_7M:
			R828_Sys_Info.IF_KHz = 4070;
			R828_Sys_Info.BW = BW_7M;
			R828_Sys_Info.FILT_CAL_LO = 60000;
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x2B; // 1.7M disable, +1cap, 1.0MHz
			R828_Sys_Info.EXT_ENABLE = 0x60; // R30[6]=1 ext enable; R30[5]:1
												// ext at LNA max-1
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		case DVB_T_7M_2:
		case DVB_T2_7M_2:
			R828_Sys_Info.IF_KHz = 4570;
			R828_Sys_Info.BW = BW_7M;
			R828_Sys_Info.FILT_CAL_LO = 63000;
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x2A; // 1.7M disable, +1cap, 1.25MHz
			R828_Sys_Info.EXT_ENABLE = 0x60; // R30[6]=1 ext enable; R30[5]:1
												// ext at LNA max-1
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		case DVB_T_8M:
		case DVB_T2_8M:
			R828_Sys_Info.IF_KHz = 4570;
			R828_Sys_Info.BW = BW_8M;
			R828_Sys_Info.FILT_CAL_LO = 68500;
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x0B; // 1.7M disable, +0cap, 1.0MHz
			R828_Sys_Info.EXT_ENABLE = 0x60; // R30[6]=1 ext enable; R30[5]:1
												// ext at LNA max-1
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		case ISDB_T:
			R828_Sys_Info.IF_KHz = 4063;
			R828_Sys_Info.BW = BW_6M;
			R828_Sys_Info.FILT_CAL_LO = 59000;
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x6A; // 1.7M disable, +2cap, 1.25MHz
			R828_Sys_Info.EXT_ENABLE = 0x40; // R30[6], ext enable; R30[5]:0 ext
												// at LNA max
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		default: // DVB_T_8M
			R828_Sys_Info.IF_KHz = 4570;
			R828_Sys_Info.BW = BW_8M;
			R828_Sys_Info.FILT_CAL_LO = 68500;
			R828_Sys_Info.FILT_GAIN = 0x10; // +3dB, 6MHz on
			R828_Sys_Info.IMG_R = 0x00; // image negative
			R828_Sys_Info.FILT_Q = 0x10; // R10[4]:low Q(1'b1)
			R828_Sys_Info.HP_COR = 0x0D; // 1.7M disable, +0cap, 0.7MHz
			R828_Sys_Info.EXT_ENABLE = 0x60; // R30[6]=1 ext enable; R30[5]:1
												// ext at LNA max-1
			R828_Sys_Info.LOOP_THROUGH1 = 0x00; // R5[7], LT ON
			R828_Sys_Info.LT_ATT = 0x00; // R31[7], LT ATT enable
			R828_Sys_Info.FLT_EXT_WIDEST = 0x00;// R15[7]: FLT_EXT_WIDE OFF
			R828_Sys_Info.POLYFIL_CUR = 0x60; // R25[6:5]:Min
			break;

		}

		return R828_Sys_Info;
	}

	Freq_Info_Type R828_Freq_Sel(int LO_freq) {
		Freq_Info_Type R828_Freq_Info = new Freq_Info_Type();

		if (LO_freq < 50000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = (byte) 0xDF; // R27[7:0] band2,band0
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		}

		else if (LO_freq >= 50000 && LO_freq < 55000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = (byte) 0xBE; // R27[7:0] band4,band1
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 55000 && LO_freq < 60000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = (byte) 0x8B; // R27[7:0] band7,band4
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 60000 && LO_freq < 65000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x7B; // R27[7:0] band8,band4
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 65000 && LO_freq < 70000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x69; // R27[7:0] band9,band6
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 70000 && LO_freq < 75000) {
			R828_Freq_Info.OPEN_D = 0x08; // low
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x58; // R27[7:0] band10,band7
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 75000 && LO_freq < 80000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x44; // R27[7:0] band11,band11
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 80000 && LO_freq < 90000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x44; // R27[7:0] band11,band11
			R828_Freq_Info.XTAL_CAP20P = 0x02; // R16[1:0] 20pF (10)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 90000 && LO_freq < 100000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x34; // R27[7:0] band12,band11
			R828_Freq_Info.XTAL_CAP20P = 0x01; // R16[1:0] 10pF (01)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 100000 && LO_freq < 110000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x34; // R27[7:0] band12,band11
			R828_Freq_Info.XTAL_CAP20P = 0x01; // R16[1:0] 10pF (01)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 0;
		} else if (LO_freq >= 110000 && LO_freq < 120000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x24; // R27[7:0] band13,band11
			R828_Freq_Info.XTAL_CAP20P = 0x01; // R16[1:0] 10pF (01)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 1;
		} else if (LO_freq >= 120000 && LO_freq < 140000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x24; // R27[7:0] band13,band11
			R828_Freq_Info.XTAL_CAP20P = 0x01; // R16[1:0] 10pF (01)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 1;
		} else if (LO_freq >= 140000 && LO_freq < 180000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x14; // R27[7:0] band14,band11
			R828_Freq_Info.XTAL_CAP20P = 0x01; // R16[1:0] 10pF (01)
			R828_Freq_Info.XTAL_CAP10P = 0x01;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 1;
		} else if (LO_freq >= 180000 && LO_freq < 220000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x13; // R27[7:0] band14,band12
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 1;
		} else if (LO_freq >= 220000 && LO_freq < 250000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x13; // R27[7:0] band14,band12
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 2;
		} else if (LO_freq >= 250000 && LO_freq < 280000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x11; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 2;
		} else if (LO_freq >= 280000 && LO_freq < 310000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x02; // R26[7:6]=0 (LPF) R26[1:0]=2
												// (low)
			R828_Freq_Info.TF_C = 0x00; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 2;
		} else if (LO_freq >= 310000 && LO_freq < 450000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x41; // R26[7:6]=1 (bypass) R26[1:0]=1
												// (middle)
			R828_Freq_Info.TF_C = 0x00; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 2;
		} else if (LO_freq >= 450000 && LO_freq < 588000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x41; // R26[7:6]=1 (bypass) R26[1:0]=1
												// (middle)
			R828_Freq_Info.TF_C = 0x00; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 3;
		} else if (LO_freq >= 588000 && LO_freq < 650000) {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x40; // R26[7:6]=1 (bypass) R26[1:0]=0
												// (highest)
			R828_Freq_Info.TF_C = 0x00; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 3;
		} else {
			R828_Freq_Info.OPEN_D = 0x00; // high
			R828_Freq_Info.RF_MUX_PLOY = 0x40; // R26[7:6]=1 (bypass) R26[1:0]=0
												// (highest)
			R828_Freq_Info.TF_C = 0x00; // R27[7:0] highest,highest
			R828_Freq_Info.XTAL_CAP20P = 0x00; // R16[1:0] 0pF (00)
			R828_Freq_Info.XTAL_CAP10P = 0x00;
			R828_Freq_Info.XTAL_CAP0P = 0x00;
			R828_Freq_Info.IMR_MEM = 4;
		}

		return R828_Freq_Info;
	}

	SysFreq_Info_Type R828_SysFreq_Sel(
			/* R828_Standard_Type */int R828_Standard, int RF_freq) {
		SysFreq_Info_Type R828_SysFreq_Info = new SysFreq_Info_Type();

		switch (R828_Standard) {

		case DVB_T_6M:
		case DVB_T_7M:
		case DVB_T_7M_2:
		case DVB_T_8M:
			if ((RF_freq == 506000) || (RF_freq == 666000)
					|| (RF_freq == 818000)) {
				R828_SysFreq_Info.MIXER_TOP = 0x14; // MIXER TOP:14 , TOP-1,
													// low-disbytege
				R828_SysFreq_Info.LNA_TOP = (byte) 0xE5; // Detect BW 3, LNA
															// TOP:4, PreDet
															// Top:2
				R828_SysFreq_Info.CP_CUR = 0x28; // 101, 0.2
				R828_SysFreq_Info.DIV_BUF_CUR = 0x20; // 10, 200u
			} else {
				R828_SysFreq_Info.MIXER_TOP = 0x24; // MIXER TOP:13 , TOP-1,
													// low-disbytege
				R828_SysFreq_Info.LNA_TOP = (byte) 0xE5; // Detect BW 3, LNA
															// TOP:4, PreDet
															// Top:2
				R828_SysFreq_Info.CP_CUR = 0x38; // 111, auto
				R828_SysFreq_Info.DIV_BUF_CUR = 0x30; // 11, 150u
			}
			R828_SysFreq_Info.LNA_VTH_L = 0x53; // LNA VTH 0.84 , VTL 0.64
			R828_SysFreq_Info.MIXER_VTH_L = 0x75; // MIXER VTH 1.04, VTL 0.84
			R828_SysFreq_Info.AIR_CABLE1_IN = 0x00;
			R828_SysFreq_Info.CABLE2_IN = 0x00;
			R828_SysFreq_Info.PRE_DECT = 0x40;
			R828_SysFreq_Info.LNA_DISbyteGE = 14;
			R828_SysFreq_Info.FILTER_CUR = 0x40; // 10, low
			break;

		case DVB_T2_6M:
		case DVB_T2_7M:
		case DVB_T2_7M_2:
		case DVB_T2_8M:
			R828_SysFreq_Info.MIXER_TOP = 0x24; // MIXER TOP:13 , TOP-1,
												// low-disbytege
			R828_SysFreq_Info.LNA_TOP = (byte) 0xE5; // Detect BW 3, LNA TOP:4,
														// PreDet Top:2
			R828_SysFreq_Info.LNA_VTH_L = 0x53; // LNA VTH 0.84 , VTL 0.64
			R828_SysFreq_Info.MIXER_VTH_L = 0x75; // MIXER VTH 1.04, VTL 0.84
			R828_SysFreq_Info.AIR_CABLE1_IN = 0x00;
			R828_SysFreq_Info.CABLE2_IN = 0x00;
			R828_SysFreq_Info.PRE_DECT = 0x40;
			R828_SysFreq_Info.LNA_DISbyteGE = 14;
			R828_SysFreq_Info.CP_CUR = 0x38; // 111, auto
			R828_SysFreq_Info.DIV_BUF_CUR = 0x30; // 11, 150u
			R828_SysFreq_Info.FILTER_CUR = 0x40; // 10, low
			break;

		case ISDB_T:
			R828_SysFreq_Info.MIXER_TOP = 0x24; // MIXER TOP:13 , TOP-1,
												// low-disbytege
			R828_SysFreq_Info.LNA_TOP = (byte) 0xE5; // Detect BW 3, LNA TOP:4,
														// PreDet Top:2
			R828_SysFreq_Info.LNA_VTH_L = 0x75; // LNA VTH 1.04 , VTL 0.84
			R828_SysFreq_Info.MIXER_VTH_L = 0x75; // MIXER VTH 1.04, VTL 0.84
			R828_SysFreq_Info.AIR_CABLE1_IN = 0x00;
			R828_SysFreq_Info.CABLE2_IN = 0x00;
			R828_SysFreq_Info.PRE_DECT = 0x40;
			R828_SysFreq_Info.LNA_DISbyteGE = 14;
			R828_SysFreq_Info.CP_CUR = 0x38; // 111, auto
			R828_SysFreq_Info.DIV_BUF_CUR = 0x30; // 11, 150u
			R828_SysFreq_Info.FILTER_CUR = 0x40; // 10, low
			break;

		default: // DVB-T 8M
			R828_SysFreq_Info.MIXER_TOP = 0x24; // MIXER TOP:13 , TOP-1,
												// low-disbytege
			R828_SysFreq_Info.LNA_TOP = (byte) 0xE5; // Detect BW 3, LNA TOP:4,
														// PreDet Top:2
			R828_SysFreq_Info.LNA_VTH_L = 0x53; // LNA VTH 0.84 , VTL 0.64
			R828_SysFreq_Info.MIXER_VTH_L = 0x75; // MIXER VTH 1.04, VTL 0.84
			R828_SysFreq_Info.AIR_CABLE1_IN = 0x00;
			R828_SysFreq_Info.CABLE2_IN = 0x00;
			R828_SysFreq_Info.PRE_DECT = 0x40;
			R828_SysFreq_Info.LNA_DISbyteGE = 14;
			R828_SysFreq_Info.CP_CUR = 0x38; // 111, auto
			R828_SysFreq_Info.DIV_BUF_CUR = 0x30; // 11, 150u
			R828_SysFreq_Info.FILTER_CUR = 0x40; // 10, low
			break;

		} // end switch

		// DTV use Diplexer
		/*
		 * #if(USE_DIPLEXER==TRUE) if ((Rafael_Chip==R820C) ||
		 * (Rafael_Chip==R820T) || (Rafael_Chip==R828S)) { // Air-in
		 * (>=DIP_FREQ) & cable-1(<DIP_FREQ) if(RF_freq >= DIP_FREQ) {
		 * R828_SysFreq_Info.AIR_CABLE1_IN = 0x00; //air in, cable-1 off
		 * R828_SysFreq_Info.CABLE2_IN = 0x00; //cable-2 off } else {
		 * R828_SysFreq_Info.AIR_CABLE1_IN = 0x60; //cable-1 in, air off
		 * R828_SysFreq_Info.CABLE2_IN = 0x00; //cable-2 off } } #endif
		 */
		return R828_SysFreq_Info;

	}

	boolean R828_Xtal_Check(int pTuner) {
		int ArrayNum;

		ArrayNum = 27;
		for (ArrayNum = 0; ArrayNum < 27; ArrayNum++) {
			R828_Arry[ArrayNum] = R828_iniArry[ArrayNum];
		}

		// cap 30pF & Drive Low
		R828_I2C.RegAddr = 0x10;
		R828_Arry[11] = (byte) ((R828_Arry[11] & 0xF4) | 0x0B);
		R828_I2C.Data = R828_Arry[11];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set pll autotune = 128kHz
		R828_I2C.RegAddr = 0x1A;
		R828_Arry[21] = (byte) (R828_Arry[21] & 0xF3);
		R828_I2C.Data = R828_Arry[21];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set manual initial reg = 111111;
		R828_I2C.RegAddr = 0x13;
		R828_Arry[14] = (byte) ((R828_Arry[14] & 0x80) | 0x7F);
		R828_I2C.Data = R828_Arry[14];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set auto
		R828_I2C.RegAddr = 0x13;
		R828_Arry[14] = (byte) (R828_Arry[14] & 0xBF);
		R828_I2C.Data = R828_Arry[14];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// R828_Delay_MS(pTuner, 5);

		R828_I2C_Len.RegAddr = 0x00;
		R828_I2C_Len.Len = 3;
		if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
			return false;

		// if 30pF unlock, set to cap 20pF
		/*
		 * #if (USE_16M_XTAL==TRUE) //VCO=2360MHz for 16M Xtal. VCO band 26
		 * if(((R828_I2C_Len.Data[2] & 0x40) == 0x00) || ((R828_I2C_Len.Data[2]
		 * & 0x3F) > 29) || ((R828_I2C_Len.Data[2] & 0x3F) < 23)) #else
		 */
		if (((R828_I2C_Len.Data[2] & 0x40) == 0x00)
				|| ((R828_I2C_Len.Data[2] & 0x3F) == 0x3F))
		// #endif
		{
			// cap 20pF
			R828_I2C.RegAddr = 0x10;
			R828_Arry[11] = (byte) ((R828_Arry[11] & 0xFC) | 0x02);
			R828_I2C.Data = R828_Arry[11];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_Delay_MS(pTuner, 5);

			R828_I2C_Len.RegAddr = 0x00;
			R828_I2C_Len.Len = 3;
			if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
				return false;

			// if 20pF unlock, set to cap 10pF
			/*
			 * #if (USE_16M_XTAL==TRUE) if(((R828_I2C_Len.Data[2] & 0x40) ==
			 * 0x00) || ((R828_I2C_Len.Data[2] & 0x3F) > 29) ||
			 * ((R828_I2C_Len.Data[2] & 0x3F) < 23)) #else
			 */
			if (((R828_I2C_Len.Data[2] & 0x40) == 0x00)
					|| ((R828_I2C_Len.Data[2] & 0x3F) == 0x3F))
			// #endif
			{
				// cap 10pF
				R828_I2C.RegAddr = 0x10;
				R828_Arry[11] = (byte) ((R828_Arry[11] & 0xFC) | 0x01);
				R828_I2C.Data = R828_Arry[11];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Delay_MS(pTuner, 5);

				R828_I2C_Len.RegAddr = 0x00;
				R828_I2C_Len.Len = 3;
				if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
					return false;

				// if 10pF unlock, set to cap 0pF
				/*
				 * #if (USE_16M_XTAL==TRUE) if(((R828_I2C_Len.Data[2] & 0x40) ==
				 * 0x00) || ((R828_I2C_Len.Data[2] & 0x3F) > 29) ||
				 * ((R828_I2C_Len.Data[2] & 0x3F) < 23)) #else
				 */
				if (((R828_I2C_Len.Data[2] & 0x40) == 0x00)
						|| ((R828_I2C_Len.Data[2] & 0x3F) == 0x3F))
				// #endif
				{
					// cap 0pF
					R828_I2C.RegAddr = 0x10;
					R828_Arry[11] = (byte) ((R828_Arry[11] & 0xFC) | 0x00);
					R828_I2C.Data = R828_Arry[11];
					if (I2C_Write(pTuner, R828_I2C) != true)
						return false;

					R828_Delay_MS(pTuner, 5);

					R828_I2C_Len.RegAddr = 0x00;
					R828_I2C_Len.Len = 3;
					if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
						return false;

					// if unlock, set to high drive
					/*
					 * #if (USE_16M_XTAL==TRUE) if(((R828_I2C_Len.Data[2] &
					 * 0x40) == 0x00) || ((R828_I2C_Len.Data[2] & 0x3F) > 29) ||
					 * ((R828_I2C_Len.Data[2] & 0x3F) < 23)) #else
					 */
					if (((R828_I2C_Len.Data[2] & 0x40) == 0x00)
							|| ((R828_I2C_Len.Data[2] & 0x3F) == 0x3F))
					// #endif
					{
						// X'tal drive high
						R828_I2C.RegAddr = 0x10;
						R828_Arry[11] = (byte) (R828_Arry[11] & 0xF7);
						R828_I2C.Data = R828_Arry[11];
						if (I2C_Write(pTuner, R828_I2C) != true)
							return false;

						// R828_Delay_MS(15);
						R828_Delay_MS(pTuner, 20);

						R828_I2C_Len.RegAddr = 0x00;
						R828_I2C_Len.Len = 3;
						if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
							return false;

						/*
						 * #if (USE_16M_XTAL==TRUE) if(((R828_I2C_Len.Data[2] &
						 * 0x40) == 0x00) || ((R828_I2C_Len.Data[2] & 0x3F) >
						 * 29) || ((R828_I2C_Len.Data[2] & 0x3F) < 23)) #else
						 */
						if (((R828_I2C_Len.Data[2] & 0x40) == 0x00)
								|| ((R828_I2C_Len.Data[2] & 0x3F) == 0x3F))
						// #endif
						{
							return false;
						} else // 0p+high drive lock
						{
							Xtal_cap_sel_tmp = XTAL_HIGH_CAP_0P;
						}
					} else // 0p lock
					{
						Xtal_cap_sel_tmp = XTAL_LOW_CAP_0P;
					}
				} else // 10p lock
				{
					Xtal_cap_sel_tmp = XTAL_LOW_CAP_10P;
				}
			} else // 20p lock
			{
				Xtal_cap_sel_tmp = XTAL_LOW_CAP_20P;
			}
		} else // 30p lock
		{
			Xtal_cap_sel_tmp = XTAL_LOW_CAP_30P;
		}

		return true;
	}

	boolean R828_Init(int pTuner) {
		// R820T_EXTRA_MODULE *pExtra;
		int i;

		// Get tuner extra module.
		// pExtra = &(pTuner->Extra.R820t);

		// write initial reg
		if (R828_InitReg(pTuner) != true)
			return false;

		if (R828_IMR_done_flag == false) {

			// write initial reg
			// if(R828_InitReg(pTuner) != true)
			// return false;

			// Do Xtal check
			if ((Rafael_Chip == R820T) || (Rafael_Chip == R828S)
					|| (Rafael_Chip == R820C)) {
				Xtal_cap_sel = XTAL_HIGH_CAP_0P;
			} else {
				if (R828_Xtal_Check(pTuner) != true) // 1st
					return false;

				Xtal_cap_sel = Xtal_cap_sel_tmp;

				if (R828_Xtal_Check(pTuner) != true) // 2nd
					return false;

				if (Xtal_cap_sel_tmp > Xtal_cap_sel) {
					Xtal_cap_sel = Xtal_cap_sel_tmp;
				}

				if (R828_Xtal_Check(pTuner) != true) // 3rd
					return false;

				if (Xtal_cap_sel_tmp > Xtal_cap_sel) {
					Xtal_cap_sel = Xtal_cap_sel_tmp;
				}

			}

			// reset filter cal.
			for (i = 0; i < STD_SIZE; i++) {
				R828_Fil_Cal_flag[i] = 0;// FALSE=0
				R828_Fil_Cal_code[i] = 0;
			}

			/*
			 * #if 0 //start imr cal. if(R828_InitReg(pTuner) != true) //write
			 * initial reg before doing cal return false;
			 * 
			 * if(R828_IMR_Prepare(pTuner) != true) return false;
			 * 
			 * if(R828_IMR(pTuner, 3, TRUE) != true) //Full K node 3 return
			 * false;
			 * 
			 * if(R828_IMR(pTuner, 1, FALSE) != true) return false;
			 * 
			 * if(R828_IMR(pTuner, 0, FALSE) != true) return false;
			 * 
			 * if(R828_IMR(pTuner, 2, FALSE) != true) return false;
			 * 
			 * if(R828_IMR(pTuner, 4, FALSE) != true) return false;
			 * 
			 * R828_IMR_done_flag = TRUE; #endif
			 */
		}

		// write initial reg
		if (R828_InitReg(pTuner) != true)
			return false;

		return true;
	}

	boolean R828_InitReg(int pTuner) {
		int InitArryCount;
		int InitArryNum;

		InitArryCount = 0;
		InitArryNum = 27;

		// int LO_KHz = 0;

		// Write Full Table
		R828_I2C_Len.RegAddr = 0x05;
		R828_I2C_Len.Len = (byte) InitArryNum;
		for (InitArryCount = 0; InitArryCount < InitArryNum; InitArryCount++) {
			R828_I2C_Len.Data[InitArryCount] = R828_iniArry[InitArryCount];
		}
		if (I2C_Write_Len(pTuner, R828_I2C_Len) != true)
			return false;

		return true;
	}

	boolean R828_IMR_Prepare(int pTuner)

	{
		int ArrayNum;

		ArrayNum = 27;

		for (ArrayNum = 0; ArrayNum < 27; ArrayNum++) {
			R828_Arry[ArrayNum] = R828_iniArry[ArrayNum];
		}
		// IMR Preparation
		// lna off (air-in off)
		R828_I2C.RegAddr = 0x05;
		R828_Arry[0] = (byte) (R828_Arry[0] | 0x20);
		R828_I2C.Data = R828_Arry[0];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// mixer gain mode = manual
		R828_I2C.RegAddr = 0x07;
		R828_Arry[2] = (byte) (R828_Arry[2] & 0xEF);
		R828_I2C.Data = R828_Arry[2];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// filter corner = lowest
		R828_I2C.RegAddr = 0x0A;
		R828_Arry[5] = (byte) (R828_Arry[5] | 0x0F);
		R828_I2C.Data = R828_Arry[5];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// filter bw=+2cap, hp=5M
		R828_I2C.RegAddr = 0x0B;
		R828_Arry[6] = (byte) ((R828_Arry[6] & 0x90) | 0x60);
		R828_I2C.Data = R828_Arry[6];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// adc=on, vga code mode, gain = 26.5dB
		R828_I2C.RegAddr = 0x0C;
		R828_Arry[7] = (byte) ((R828_Arry[7] & 0x60) | 0x0B);
		R828_I2C.Data = R828_Arry[7];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// ring clk = on
		R828_I2C.RegAddr = 0x0F;
		R828_Arry[10] &= 0xF7;
		R828_I2C.Data = R828_Arry[10];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// ring power = on
		R828_I2C.RegAddr = 0x18;
		R828_Arry[19] = (byte) (R828_Arry[19] | 0x10);
		R828_I2C.Data = R828_Arry[19];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// from ring = ring pll in
		R828_I2C.RegAddr = 0x1C;
		R828_Arry[23] = (byte) (R828_Arry[23] | 0x02);
		R828_I2C.Data = R828_Arry[23];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// sw_pdect = det3
		R828_I2C.RegAddr = 0x1E;
		R828_Arry[25] = (byte) (R828_Arry[25] | 0x80);
		R828_I2C.Data = R828_Arry[25];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// Set filt_3dB
		R828_Arry[1] = (byte) (R828_Arry[1] | 0x20);
		R828_I2C.RegAddr = 0x06;
		R828_I2C.Data = R828_Arry[1];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_IMR(int pTuner, int IMR_MEM, boolean IM_Flag) {

		int RingVCO;
		int RingFreq;
		int RingRef;
		int n_ring;
		int n;

		R828_SectType[] IMR_POINT = new R828_SectType[1];

		RingVCO = 0;
		RingFreq = 0;
		RingRef = 0;
		n_ring = 0;

		if (R828_Xtal > 24000)
			RingRef = R828_Xtal / 2;
		// else
		// RingRef = R828_Xtal;

		for (n = 0; n < 16; n++) {
			if ((16 + n) * 8 * RingRef >= 3100000) {
				n_ring = n;
				break;
			}

			if (n == 15) // n_ring not found
			{
				// return false;
				n_ring = n;
			}

		}

		R828_Arry[19] &= 0xF0; // set ring[3:0]
		R828_Arry[19] |= n_ring;
		RingVCO = (16 + n_ring) * 8 * RingRef;
		R828_Arry[19] &= 0xDF; // clear ring_se23
		R828_Arry[20] &= 0xFC; // clear ring_seldiv
		R828_Arry[26] &= 0xFC; // clear ring_att

		switch (IMR_MEM) {
		case 0:
			RingFreq = RingVCO / 48;
			R828_Arry[19] |= 0x20; // ring_se23 = 1
			R828_Arry[20] |= 0x03; // ring_seldiv = 3
			R828_Arry[26] |= 0x02; // ring_att 10
			break;
		case 1:
			RingFreq = RingVCO / 16;
			R828_Arry[19] |= 0x00; // ring_se23 = 0
			R828_Arry[20] |= 0x02; // ring_seldiv = 2
			R828_Arry[26] |= 0x00; // pw_ring 00
			break;
		case 2:
			RingFreq = RingVCO / 8;
			R828_Arry[19] |= 0x00; // ring_se23 = 0
			R828_Arry[20] |= 0x01; // ring_seldiv = 1
			R828_Arry[26] |= 0x03; // pw_ring 11
			break;
		case 3:
			RingFreq = RingVCO / 6;
			R828_Arry[19] |= 0x20; // ring_se23 = 1
			R828_Arry[20] |= 0x00; // ring_seldiv = 0
			R828_Arry[26] |= 0x03; // pw_ring 11
			break;
		case 4:
			RingFreq = RingVCO / 4;
			R828_Arry[19] |= 0x00; // ring_se23 = 0
			R828_Arry[20] |= 0x00; // ring_seldiv = 0
			R828_Arry[26] |= 0x01; // pw_ring 01
			break;
		default:
			RingFreq = RingVCO / 4;
			R828_Arry[19] |= 0x00; // ring_se23 = 0
			R828_Arry[20] |= 0x00; // ring_seldiv = 0
			R828_Arry[26] |= 0x01; // pw_ring 01
			break;
		}

		// write pw_ring,n_ring,ringdiv2 to I2C

		// ------------n_ring,ring_se23----------//
		R828_I2C.RegAddr = 0x18;
		R828_I2C.Data = R828_Arry[19];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// ------------ring_sediv----------------//
		R828_I2C.RegAddr = 0x19;
		R828_I2C.Data = R828_Arry[20];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		// ------------pw_ring-------------------//
		R828_I2C.RegAddr = 0x1f;
		R828_I2C.Data = R828_Arry[26];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Must do before PLL()
		if (R828_MUX(pTuner, RingFreq - 5300) != true) // MUX input freq ~ RF_in
														// Freq
			return false;

		if (R828_PLL(pTuner, (RingFreq - 5300) * 1000, STD_SIZE) != true) // set
																			// pll
																			// freq
																			// =
																			// ring
																			// freq
																			// -
																			// 6M
			return false;

		if (IM_Flag == true) {
			if (R828_IQ(pTuner, IMR_POINT) != true)
				return false;
		} else {
			IMR_POINT[0].Gain_X = IMR_Data[3].Gain_X;
			IMR_POINT[0].Phase_Y = IMR_Data[3].Phase_Y;
			IMR_POINT[0].Value = IMR_Data[3].Value;

			if (R828_F_IMR(pTuner, IMR_POINT) != true)
				return false;
		}

		// Save IMR Value
		switch (IMR_MEM) {
		case 0:
			IMR_Data[0].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[0].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[0].Value = IMR_POINT[0].Value;
			break;
		case 1:
			IMR_Data[1].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[1].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[1].Value = IMR_POINT[0].Value;
			break;
		case 2:
			IMR_Data[2].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[2].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[2].Value = IMR_POINT[0].Value;
			break;
		case 3:
			IMR_Data[3].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[3].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[3].Value = IMR_POINT[0].Value;
			break;
		case 4:
			IMR_Data[4].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[4].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[4].Value = IMR_POINT[0].Value;
			break;
		default:
			IMR_Data[4].Gain_X = IMR_POINT[0].Gain_X;
			IMR_Data[4].Phase_Y = IMR_POINT[0].Phase_Y;
			IMR_Data[4].Value = IMR_POINT[0].Value;
			break;
		}
		return true;
	}

	boolean R828_PLL(int pTuner, int LO_Freq, /* R828_Standard_Type */
			int R828_Standard) {

		// R820T_EXTRA_MODULE *pExtra;

		int MixDiv;
		int DivBuf;
		int Ni;
		int Si;
		int DivNum;
		int Nint;
		int VCO_Min_kHz;
		int VCO_Max_kHz;
		long VCO_Freq; // was 64 bit aew
		int PLL_Ref; // Max 24000 (kHz)
		int VCO_Fra; // VCO contribution by SDM (kHz)
		char Nsdm;
		char SDM;
		char SDM16to9;
		char SDM8to1;
		// int Judge = 0;
		int VCO_fine_tune;

		MixDiv = 2;
		DivBuf = 0;
		Ni = 0;
		Si = 0;
		DivNum = 0;
		Nint = 0;
		VCO_Min_kHz = 1770000;
		VCO_Max_kHz = VCO_Min_kHz * 2;
		VCO_Freq = 0;
		PLL_Ref = 0; // Max 24000 (kHz)
		VCO_Fra = 0; // VCO contribution by SDM (kHz)
		Nsdm = 2;
		SDM = 0;
		SDM16to9 = 0;
		SDM8to1 = 0;
		// int Judge = 0;
		VCO_fine_tune = 0;
		/*
		 * #if 0 if ((Rafael_Chip==R620D) || (Rafael_Chip==R828D) ||
		 * (Rafael_Chip==R828)) / /X'tal can't not exceed 20MHz for ATV {
		 * if(R828_Standard <= SECAM_L1) / /ref set refdiv2, reffreq = Xtal/2 on
		 * ATV application { R828_Arry[11] |= 0x10; //b4=1 PLL_Ref = R828_Xtal
		 * /2; } else //DTV, FilCal, IMR { R828_Arry[11] &= 0xEF; PLL_Ref =
		 * R828_Xtal; } } else { if(R828_Xtal > 24000) { R828_Arry[11] |= 0x10;
		 * //b4=1 PLL_Ref = R828_Xtal /2; } else { R828_Arry[11] &= 0xEF;
		 * PLL_Ref = R828_Xtal; } } #endif
		 */
		// FIXME hack
		R828_Arry[11] &= 0xEF;
		PLL_Ref = SdrUSBDriver.rtlsdr_get_tuner_clock();

		R828_I2C.RegAddr = 0x10;
		R828_I2C.Data = R828_Arry[11];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set pll autotune = 128kHz
		R828_I2C.RegAddr = 0x1A;
		R828_Arry[21] = (byte) (R828_Arry[21] & 0xF3);
		R828_I2C.Data = R828_Arry[21];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set VCO current = 100
		R828_I2C.RegAddr = 0x12;
		R828_Arry[13] = (byte) ((R828_Arry[13] & 0x1F) | 0x80);
		R828_I2C.Data = R828_Arry[13];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Divider
		while (MixDiv <= 64) {
			if ((((LO_Freq / 1000) * MixDiv) >= VCO_Min_kHz)
					&& (((LO_Freq / 1000) * MixDiv) < VCO_Max_kHz)) {
				DivBuf = MixDiv;
				while (DivBuf > 2) {
					DivBuf = DivBuf >> 1;
					DivNum++;
				}
				break;
			}
			MixDiv = MixDiv << 1;
		}

		R828_I2C_Len.RegAddr = 0x00;
		R828_I2C_Len.Len = 5;
		if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
			return false;

		VCO_fine_tune = (R828_I2C_Len.Data[4] & 0x30) >> 4;

		if (VCO_fine_tune > VCO_pwr_ref)
			DivNum = DivNum - 1;
		else if (VCO_fine_tune < VCO_pwr_ref)
			DivNum = DivNum + 1;

		R828_I2C.RegAddr = 0x10;
		R828_Arry[11] &= 0x1F;
		R828_Arry[11] |= (DivNum << 5);
		R828_I2C.Data = R828_Arry[11];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		VCO_Freq = (LO_Freq * (long) MixDiv);
		Nint = (int) (VCO_Freq / 2 / PLL_Ref);
		VCO_Fra = (char) ((VCO_Freq - 2 * PLL_Ref * Nint) / 1000);

		// FIXME hack
		PLL_Ref /= 1000;

		Log.d(TAG,
				"VCO_Freq " + Long.toString(VCO_Freq) + " Nint "
						+ Long.toString(Nint) + " VCO_Fra "
						+ Long.toString(VCO_Fra) + " LO_Freq "
						+ Long.toString(LO_Freq) + " MixDiv "
						+ Long.toString(MixDiv));

		// boundary spur prevention
		if (VCO_Fra < PLL_Ref / 64) // 2*PLL_Ref/128
			VCO_Fra = 0;
		else if (VCO_Fra > PLL_Ref * 127 / 64) // 2*PLL_Ref*127/128
		{
			VCO_Fra = 0;
			Nint++;
		} else if ((VCO_Fra > PLL_Ref * 127 / 128) && (VCO_Fra < PLL_Ref)) // >
																			// 2*PLL_Ref*127/256,
																			// <
																			// 2*PLL_Ref*128/256
			VCO_Fra = PLL_Ref * 127 / 128; // VCO_Fra = 2*PLL_Ref*127/256
		else if ((VCO_Fra > PLL_Ref) && (VCO_Fra < PLL_Ref * 129 / 128)) // >
																			// 2*PLL_Ref*128/256,
																			// <
																			// 2*PLL_Ref*129/256
			VCO_Fra = PLL_Ref * 129 / 128; // VCO_Fra = 2*PLL_Ref*129/256
		else
			VCO_Fra = VCO_Fra + 0;

		if (Nint > 63) {
			Log.d(TAG, String.format("[R820T] No valid PLL values for %u Hz! ",
					LO_Freq));
			return false;
		}

		// N & S
		Ni = (Nint - 13) / 4;
		Si = Nint - 4 * Ni - 13;
		R828_I2C.RegAddr = 0x14;
		R828_Arry[15] = 0x00;
		R828_Arry[15] |= (Ni + (Si << 6));
		R828_I2C.Data = R828_Arry[15];

		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// pw_sdm
		R828_I2C.RegAddr = 0x12;
		R828_Arry[13] &= 0xF7;
		if (VCO_Fra == 0)
			R828_Arry[13] |= 0x08;
		R828_I2C.Data = R828_Arry[13];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// SDM calculator
		while (VCO_Fra > 1) {
			if (VCO_Fra > (2 * PLL_Ref / Nsdm)) {
				SDM = (char) (SDM + 32768 / (Nsdm / 2));
				VCO_Fra = VCO_Fra - 2 * PLL_Ref / Nsdm;
				if (Nsdm >= 0x8000)
					break;
			}
			Nsdm = (char) (Nsdm << 1);
		}

		SDM16to9 = (char) (SDM >> 8);
		SDM8to1 = (char) (SDM - (SDM16to9 << 8));

		R828_I2C.RegAddr = 0x16;
		R828_Arry[17] = (byte) SDM16to9;
		R828_I2C.Data = R828_Arry[17];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;
		R828_I2C.RegAddr = 0x15;
		R828_Arry[16] = (byte) SDM8to1;
		R828_I2C.Data = R828_Arry[16];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// R828_Delay_MS(10);

		if ((Rafael_Chip == R620D) || (Rafael_Chip == R828D)
				|| (Rafael_Chip == R828)) {
			if (R828_Standard <= SECAM_L1)
				R828_Delay_MS(pTuner, 20);
			else
				R828_Delay_MS(pTuner, 10);
		} else {
			R828_Delay_MS(pTuner, 10);
		}

		// check PLL lock status
		R828_I2C_Len.RegAddr = 0x00;
		R828_I2C_Len.Len = 3;
		if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
			return false;

		if ((R828_I2C_Len.Data[2] & 0x40) == 0x00) {
			Log.d(TAG, "[R820T] PLL not locked for %u Hz! " + LO_Freq);
			R828_I2C.RegAddr = 0x12;
			R828_Arry[13] = (byte) ((R828_Arry[13] & 0x1F) | 0x60); // increase
																	// VCO
																	// current
			R828_I2C.Data = R828_Arry[13];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			return false;
		}

		// set pll autotune = 8kHz
		R828_I2C.RegAddr = 0x1A;
		R828_Arry[21] = (byte) (R828_Arry[21] | 0x08);
		R828_I2C.Data = R828_Arry[21];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_MUX(int pTuner, int RF_KHz) {
		int RT_Reg08;
		int RT_Reg09;

		RT_Reg08 = 0;
		RT_Reg09 = 0;

		// Freq_Info_Type Freq_Info1;
		Freq_Info1 = R828_Freq_Sel(RF_KHz);

		// Open Drain
		R828_I2C.RegAddr = 0x17;
		R828_Arry[18] = (byte) ((R828_Arry[18] & 0xF7) | Freq_Info1.OPEN_D);
		R828_I2C.Data = R828_Arry[18];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// RF_MUX,Polymux
		R828_I2C.RegAddr = 0x1A;
		R828_Arry[21] = (byte) ((R828_Arry[21] & 0x3C) | Freq_Info1.RF_MUX_PLOY);
		R828_I2C.Data = R828_Arry[21];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// TF BAND
		R828_I2C.RegAddr = 0x1B;
		R828_Arry[22] &= 0x00;
		R828_Arry[22] |= Freq_Info1.TF_C;
		R828_I2C.Data = R828_Arry[22];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// XTAL CAP & Drive
		R828_I2C.RegAddr = 0x10;
		R828_Arry[11] &= 0xF4;
		switch (Xtal_cap_sel) {
		case XTAL_LOW_CAP_30P:
		case XTAL_LOW_CAP_20P:
			R828_Arry[11] = (byte) (R828_Arry[11] | Freq_Info1.XTAL_CAP20P | 0x08);
			break;

		case XTAL_LOW_CAP_10P:
			R828_Arry[11] = (byte) (R828_Arry[11] | Freq_Info1.XTAL_CAP10P | 0x08);
			break;

		case XTAL_LOW_CAP_0P:
			R828_Arry[11] = (byte) (R828_Arry[11] | Freq_Info1.XTAL_CAP0P | 0x08);
			break;

		case XTAL_HIGH_CAP_0P:
			R828_Arry[11] = (byte) (R828_Arry[11] | Freq_Info1.XTAL_CAP0P | 0x00);
			break;

		default:
			R828_Arry[11] = (byte) (R828_Arry[11] | Freq_Info1.XTAL_CAP0P | 0x08);
			break;
		}
		R828_I2C.Data = R828_Arry[11];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set_IMR
		if (R828_IMR_done_flag == true) {
			RT_Reg08 = IMR_Data[Freq_Info1.IMR_MEM].Gain_X & 0x3F;
			RT_Reg09 = IMR_Data[Freq_Info1.IMR_MEM].Phase_Y & 0x3F;
		} else {
			RT_Reg08 = 0;
			RT_Reg09 = 0;
		}

		R828_I2C.RegAddr = 0x08;
		R828_Arry[3] = (byte) (R828_iniArry[3] & 0xC0);
		R828_Arry[3] = (byte) (R828_Arry[3] | RT_Reg08);
		R828_I2C.Data = R828_Arry[3];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x09;
		R828_Arry[4] = (byte) (R828_iniArry[4] & 0xC0);
		R828_Arry[4] = (byte) (R828_Arry[4] | RT_Reg09);
		R828_I2C.Data = R828_Arry[4];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_IQ(int pTuner, R828_SectType[] IQ_Pont) {
		R828_SectType[] Compare_IQ = new R828_SectType[3];
		// R828_SectType CompareTemp;
		// byte IQ_Count = 0;
		byte VGA_Count;
		char[] VGA_Read = new char[1];
		byte[] X_Direction = new byte[1]; // 1:X, 0:Y

		VGA_Count = 0;
		VGA_Read[0] = 0;

		// increase VGA power to let image significant
		for (VGA_Count = 12; VGA_Count < 16; VGA_Count++) {
			R828_I2C.RegAddr = 0x0C;
			R828_I2C.Data = (byte) ((R828_Arry[7] & 0xF0) + VGA_Count);
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_Delay_MS(pTuner, 10); //

			if (R828_Muti_Read(pTuner, (byte) 0x01, VGA_Read) != true)
				return false;

			if (VGA_Read[0] > 40 * 4)
				break;
		}

		// initial 0x08, 0x09
		// Compare_IQ[0].Gain_X = 0x40; //should be 0xC0 in R828, Jason
		// Compare_IQ[0].Phase_Y = 0x40; //should be 0x40 in R828
		Compare_IQ[0].Gain_X = R828_iniArry[3] & 0xC0; // Jason modified, clear
														// b[5], b[4:0]
		Compare_IQ[0].Phase_Y = R828_iniArry[4] & 0xC0; //

		// while(IQ_Count < 3)
		// {
		// Determine X or Y
		if (R828_IMR_Cross(pTuner, Compare_IQ, X_Direction) != true)
			return false;

		// if(X_Direction==1)
		// {
		// if(R828_IQ_Tree(Compare_IQ[0].Phase_Y, Compare_IQ[0].Gain_X, 0x09,
		// &Compare_IQ[0]) != true) //X
		// return false;
		// }
		// else
		// {
		// if(R828_IQ_Tree(Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y, 0x08,
		// &Compare_IQ[0]) != true) //Y
		// return false;
		// }

		/*
		 * //--- X direction ---// //X: 3 points
		 * if(R828_IQ_Tree(Compare_IQ[0].Phase_Y, Compare_IQ[0].Gain_X, 0x09,
		 * &Compare_IQ[0]) != true) // return false;
		 * 
		 * //compare and find min of 3 points. determine I/Q direction
		 * if(R828_CompreCor(&Compare_IQ[0]) != true) return false;
		 * 
		 * //increase step to find min value of this direction
		 * if(R828_CompreStep(&Compare_IQ[0], 0x08) != true) return false;
		 */

		if (X_Direction[0] == 1) {
			// compare and find min of 3 points. determine I/Q direction
			if (R828_CompreCor(Compare_IQ) != true)
				return false;

			// increase step to find min value of this direction
			if (R828_CompreStep(pTuner, Compare_IQ, (byte) 0x08) != true) // X
				return false;
		} else {
			// compare and find min of 3 points. determine I/Q direction
			if (R828_CompreCor(Compare_IQ) != true)
				return false;

			// increase step to find min value of this direction
			if (R828_CompreStep(pTuner, Compare_IQ, (byte) 0x09) != true) // Y
				return false;
		}
		/*
		 * //--- Y direction ---// //Y: 3 points
		 * if(R828_IQ_Tree(Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y, 0x08,
		 * &Compare_IQ[0]) != true) // return false;
		 * 
		 * //compare and find min of 3 points. determine I/Q direction
		 * if(R828_CompreCor(&Compare_IQ[0]) != true) return false;
		 * 
		 * //increase step to find min value of this direction
		 * if(R828_CompreStep(&Compare_IQ[0], 0x09) != true) return false;
		 */

		// Another direction
		if (X_Direction[0] == 1) {
			if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X,
					Compare_IQ[0].Phase_Y, 0x08, Compare_IQ) != true) // Y
				return false;

			// compare and find min of 3 points. determine I/Q direction
			if (R828_CompreCor(Compare_IQ) != true)
				return false;

			// increase step to find min value of this direction
			if (R828_CompreStep(pTuner, Compare_IQ, (byte) 0x09) != true) // Y
				return false;
		} else {
			if (R828_IQ_Tree(pTuner, Compare_IQ[0].Phase_Y,
					Compare_IQ[0].Gain_X, 0x09, Compare_IQ) != true) // X
				return false;

			// compare and find min of 3 points. determine I/Q direction
			if (R828_CompreCor(Compare_IQ) != true)
				return false;

			// increase step to find min value of this direction
			if (R828_CompreStep(pTuner, Compare_IQ, (byte) 0x08) != true) // X
				return false;
		}
		// CompareTemp = Compare_IQ[0];

		// --- Check 3 points again---//
		if (X_Direction[0] == 1) {
			if (R828_IQ_Tree(pTuner, Compare_IQ[0].Phase_Y,
					Compare_IQ[0].Gain_X, 0x09, Compare_IQ) != true) // X
				return false;
		} else {
			if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X,
					Compare_IQ[0].Phase_Y, 0x08, Compare_IQ) != true) // Y
				return false;
		}

		// if(R828_IQ_Tree(Compare_IQ[0].Phase_Y, Compare_IQ[0].Gain_X, 0x09,
		// &Compare_IQ[0]) != true) //
		// return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		// if((CompareTemp.Gain_X == Compare_IQ[0].Gain_X) &&
		// (CompareTemp.Phase_Y == Compare_IQ[0].Phase_Y))//Ben Check
		// break;

		// IQ_Count ++;
		// }
		// if(IQ_Count == 3)
		// return false;

		// Section-4 Check
		/*
		 * CompareTemp = Compare_IQ[0]; for(IQ_Count = 0;IQ_Count < 5;IQ_Count
		 * ++) { if(R828_Section(&Compare_IQ[0]) != true) return false;
		 * 
		 * if((CompareTemp.Gain_X == Compare_IQ[0].Gain_X) &&
		 * (CompareTemp.Phase_Y == Compare_IQ[0].Phase_Y)) break; }
		 */

		// Section-9 check
		// if(R828_F_IMR(&Compare_IQ[0]) != true)
		if (R828_Section(pTuner, Compare_IQ) != true)
			return false;

		IQ_Pont[0] = Compare_IQ[0];

		// reset gain/phase control setting
		R828_I2C.RegAddr = 0x08;
		R828_I2C.Data = (byte) (R828_iniArry[3] & 0xC0); // Jason
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x09;
		R828_I2C.Data = (byte) (R828_iniArry[4] & 0xC0);
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	// --------------------------------------------------------------------------------------------
	// Purpose: record IMC results by input gain/phase location
	// then adjust gain or phase positive 1 step and negtive 1 step, both record
	// results
	// input: FixPot: phase or gain
	// FlucPot phase or gain
	// PotReg: 0x08 or 0x09
	// CompareTree: 3 IMR trace and results
	// output: TREU or FALSE
	// --------------------------------------------------------------------------------------------
	boolean R828_IQ_Tree(int pTuner, int FixPot, int FlucPot, int PotReg,
			R828_SectType[] CompareTree) {
		byte TreeCount;
		byte TreeTimes;
		byte TempPot;
		byte PntReg;

		TreeCount = 0;
		TreeTimes = 3;
		TempPot = 0;
		PntReg = 0;

		if (PotReg == 0x08)
			PntReg = 0x09; // phase control
		else
			PntReg = 0x08; // gain control

		for (TreeCount = 0; TreeCount < TreeTimes; TreeCount++) {
			R828_I2C.RegAddr = (byte) PotReg;
			R828_I2C.Data = (byte) FixPot;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_I2C.RegAddr = PntReg;
			R828_I2C.Data = (byte) FlucPot;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
			char[] value = new char[1];

			if (R828_Muti_Read(pTuner, (byte) 0x01, /*
													 * CompareTree[TreeCount].Value
													 */value) != true)
				return false;
			CompareTree[TreeCount].Value = value[0];
			if (PotReg == 0x08) {
				CompareTree[TreeCount].Gain_X = FixPot;
				CompareTree[TreeCount].Phase_Y = FlucPot;
			} else {
				CompareTree[TreeCount].Phase_Y = FixPot;
				CompareTree[TreeCount].Gain_X = FlucPot;
			}

			if (TreeCount == 0) // try right-side point
				FlucPot++;
			else if (TreeCount == 1) // try left-side point
			{
				if ((FlucPot & 0x1F) < 0x02) // if absolute location is 1,
												// change I/Q direction
				{
					TempPot = (byte) (2 - (FlucPot & 0x1F));
					if ((FlucPot & 0x20) != 0) // b[5]:I/Q selection. 0:Q-path,
												// 1:I-path
					{
						FlucPot &= 0xC0;
						FlucPot |= TempPot;
					} else {
						FlucPot |= (0x20 | TempPot);
					}
				} else
					FlucPot -= 2;
			}
		}

		return true;
	}

	// -----------------------------------------------------------------------------------/
	// Purpose: compare IMC result aray [0][1][2], find min value and store to
	// CorArry[0]
	// input: CorArry: three IMR data array
	// output: TRUE or FALSE
	// -----------------------------------------------------------------------------------/
	boolean R828_CompreCor(R828_SectType[] CorArry) {
		byte CompCount;
		R828_SectType CorTemp;

		CompCount = 0;

		for (CompCount = 3; CompCount > 0; CompCount--) {
			if (CorArry[0].Value > CorArry[CompCount - 1].Value) // compare IMC
																	// result
																	// [0][1][2],
																	// find min
																	// value
			{
				CorTemp = CorArry[0];
				CorArry[0] = CorArry[CompCount - 1];
				CorArry[CompCount - 1] = CorTemp;
			}
		}

		return true;
	}

	// -------------------------------------------------------------------------------------//
	// Purpose: if (Gain<9 or Phase<9), Gain+1 or Phase+1 and compare with min
	// value
	// new < min => update to min and continue
	// new > min => Exit
	// input: StepArry: three IMR data array
	// Pace: gain or phase register
	// output: TRUE or FALSE
	// -------------------------------------------------------------------------------------//
	boolean R828_CompreStep(int pTuner, R828_SectType[] StepArry, byte Pace) {
		// byte StepCount = 0;
		R828_SectType StepTemp = new R828_SectType();

		// min value already saved in StepArry[0]
		StepTemp.Phase_Y = StepArry[0].Phase_Y;
		StepTemp.Gain_X = StepArry[0].Gain_X;

		while (((StepTemp.Gain_X & 0x1F) < IMR_TRIAL)
				&& ((StepTemp.Phase_Y & 0x1F) < IMR_TRIAL)) // 5->10
		{
			if (Pace == 0x08)
				StepTemp.Gain_X++;
			else
				StepTemp.Phase_Y++;

			R828_I2C.RegAddr = 0x08;
			R828_I2C.Data = (byte) StepTemp.Gain_X;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_I2C.RegAddr = 0x09;
			R828_I2C.Data = (byte) StepTemp.Phase_Y;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
			char[] value = new char[1];
			if (R828_Muti_Read(pTuner, (byte) 0x01, /* StepTemp.Value */value) != true)
				return false;

			StepTemp.Value = value[0];

			if (StepTemp.Value <= StepArry[0].Value) {
				StepArry[0].Gain_X = StepTemp.Gain_X;
				StepArry[0].Phase_Y = StepTemp.Phase_Y;
				StepArry[0].Value = StepTemp.Value;
			} else {
				break;
			}

		} // end of while()

		return true;
	}

	// -----------------------------------------------------------------------------------/
	// Purpose: read multiple IMC results for stability
	// input: IMR_Reg: IMC result address
	// IMR_Result_Data: result
	// output: TRUE or FALSE
	// -----------------------------------------------------------------------------------/
	boolean R828_Muti_Read(int pTuner, byte IMR_Reg, char[] IMR_Result_Data) // jason
																				// modified
	{
		byte ReadCount;
		char ReadAmount;
		byte ReadMax;
		byte ReadMin;
		byte ReadData;

		ReadCount = 0;
		ReadAmount = 0;
		ReadMax = 0;
		ReadMin = (byte) 255;
		ReadData = 0;

		R828_Delay_MS(pTuner, 5);

		for (ReadCount = 0; ReadCount < 6; ReadCount++) {
			R828_I2C_Len.RegAddr = 0x00;
			R828_I2C_Len.Len = (byte) (IMR_Reg + 1); // IMR_Reg = 0x01
			if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
				return false;

			ReadData = R828_I2C_Len.Data[1];

			ReadAmount = (char) (ReadAmount + ReadData);

			if (ReadData < ReadMin)
				ReadMin = ReadData;

			if (ReadData > ReadMax)
				ReadMax = ReadData;
		}
		IMR_Result_Data[0] = (char) (ReadAmount - ReadMax - ReadMin);

		return true;
	}

	boolean R828_Section(int pTuner, R828_SectType[] IQ_Pont) {
		R828_SectType[] Compare_IQ = new R828_SectType[3];
		R828_SectType[] Compare_Bet = new R828_SectType[3];

		// Try X-1 column and save min result to Compare_Bet[0]
		if ((IQ_Pont[0].Gain_X & 0x1F) == 0x00) {
			/*
			 * if((IQ_Pont->Gain_X & 0xE0) == 0x40) //bug => only compare b[5],
			 * Compare_IQ[0].Gain_X = 0x61; // Gain=1, I-path //Jason else
			 * Compare_IQ[0].Gain_X = 0x41; // Gain=1, Q-path
			 */
			Compare_IQ[0].Gain_X = ((IQ_Pont[0].Gain_X) & 0xDF) + 1; // Q-path,
																		// Gain=1
		} else
			Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X - 1; // left point
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true) // y-direction
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[0].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[0].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[0].Value = Compare_IQ[0].Value;

		// Try X column and save min result to Compare_Bet[1]
		Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X;
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true)
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[1].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[1].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[1].Value = Compare_IQ[0].Value;

		// Try X+1 column and save min result to Compare_Bet[2]
		if ((IQ_Pont[0].Gain_X & 0x1F) == 0x00)
			Compare_IQ[0].Gain_X = ((IQ_Pont[0].Gain_X) | 0x20) + 1; // I-path,
																		// Gain=1
		else
			Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X + 1;
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true)
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[2].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[2].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[2].Value = Compare_IQ[0].Value;

		if (R828_CompreCor(Compare_Bet) != true)
			return false;

		IQ_Pont[0] = Compare_Bet[0];

		return true;
	}

	boolean R828_IMR_Cross(int pTuner, R828_SectType[] IQ_Pont, byte[] X_Direct) {

		R828_SectType[] Compare_Cross = new R828_SectType[5]; // (0,0)(0,Q-1)(0,I-1)(Q-1,0)(I-1,0)
		R828_SectType Compare_Temp = new R828_SectType();
		byte CrossCount;
		byte Reg08;
		byte Reg09;

		CrossCount = 0;
		Reg08 = (byte) (R828_iniArry[3] & 0xC0);
		Reg09 = (byte) (R828_iniArry[4] & 0xC0);

		// memset(&Compare_Temp,0, sizeof(R828_SectType));
		Compare_Temp.Gain_X = 0;
		Compare_Temp.Phase_Y = 0;
		Compare_Temp.Value = 0;

		Compare_Temp.Value = 255;

		for (CrossCount = 0; CrossCount < 5; CrossCount++) {

			if (CrossCount == 0) {
				Compare_Cross[CrossCount].Gain_X = Reg08;
				Compare_Cross[CrossCount].Phase_Y = Reg09;
			} else if (CrossCount == 1) {
				Compare_Cross[CrossCount].Gain_X = Reg08; // 0
				Compare_Cross[CrossCount].Phase_Y = Reg09 + 1; // Q-1
			} else if (CrossCount == 2) {
				Compare_Cross[CrossCount].Gain_X = Reg08; // 0
				Compare_Cross[CrossCount].Phase_Y = (Reg09 | 0x20) + 1; // I-1
			} else if (CrossCount == 3) {
				Compare_Cross[CrossCount].Gain_X = Reg08 + 1; // Q-1
				Compare_Cross[CrossCount].Phase_Y = Reg09;
			} else {
				Compare_Cross[CrossCount].Gain_X = (Reg08 | 0x20) + 1; // I-1
				Compare_Cross[CrossCount].Phase_Y = Reg09;
			}

			R828_I2C.RegAddr = 0x08;
			R828_I2C.Data = (byte) Compare_Cross[CrossCount].Gain_X;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_I2C.RegAddr = 0x09;
			R828_I2C.Data = (byte) Compare_Cross[CrossCount].Phase_Y;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			char[] value = new char[1];

			if (R828_Muti_Read(pTuner, (byte) 0x01, /*
													 * Compare_Cross[CrossCount].
													 * Value
													 */value) != true)
				return false;

			Compare_Cross[CrossCount].Value = value[0];

			if (Compare_Cross[CrossCount].Value < Compare_Temp.Value) {
				Compare_Temp.Value = Compare_Cross[CrossCount].Value;
				Compare_Temp.Gain_X = Compare_Cross[CrossCount].Gain_X;
				Compare_Temp.Phase_Y = Compare_Cross[CrossCount].Phase_Y;
			}
		} // end for loop

		if ((Compare_Temp.Phase_Y & 0x1F) == 1) // y-direction
		{
			X_Direct[0] = (byte) 0;
			IQ_Pont[0].Gain_X = Compare_Cross[0].Gain_X;
			IQ_Pont[0].Phase_Y = Compare_Cross[0].Phase_Y;
			IQ_Pont[0].Value = Compare_Cross[0].Value;

			IQ_Pont[1].Gain_X = Compare_Cross[1].Gain_X;
			IQ_Pont[1].Phase_Y = Compare_Cross[1].Phase_Y;
			IQ_Pont[1].Value = Compare_Cross[1].Value;

			IQ_Pont[2].Gain_X = Compare_Cross[2].Gain_X;
			IQ_Pont[2].Phase_Y = Compare_Cross[2].Phase_Y;
			IQ_Pont[2].Value = Compare_Cross[2].Value;
		} else // (0,0) or x-direction
		{
			X_Direct[0] = (byte) 1;
			IQ_Pont[0].Gain_X = Compare_Cross[0].Gain_X;
			IQ_Pont[0].Phase_Y = Compare_Cross[0].Phase_Y;
			IQ_Pont[0].Value = Compare_Cross[0].Value;

			IQ_Pont[1].Gain_X = Compare_Cross[3].Gain_X;
			IQ_Pont[1].Phase_Y = Compare_Cross[3].Phase_Y;
			IQ_Pont[1].Value = Compare_Cross[3].Value;

			IQ_Pont[2].Gain_X = Compare_Cross[4].Gain_X;
			IQ_Pont[2].Phase_Y = Compare_Cross[4].Phase_Y;
			IQ_Pont[2].Value = Compare_Cross[4].Value;
		}
		return true;
	}

	// ----------------------------------------------------------------------------------------//
	// purpose: search surrounding points from previous point
	// try (x-1), (x), (x+1) columns, and find min IMR result point
	// input: IQ_Pont: previous point data(IMR Gain, Phase, ADC Result, RefRreq)
	// will be updated to final best point
	// output: TRUE or FALSE
	// ----------------------------------------------------------------------------------------//
	boolean R828_F_IMR(int pTuner, R828_SectType[] IQ_Pont) {
		R828_SectType[] Compare_IQ = new R828_SectType[3];
		R828_SectType[] Compare_Bet = new R828_SectType[3];
		byte VGA_Count;
		char VGA_Read;

		VGA_Count = 0;
		VGA_Read = 0;

		// VGA
		for (VGA_Count = 12; VGA_Count < 16; VGA_Count++) {
			R828_I2C.RegAddr = 0x0C;
			R828_I2C.Data = (byte) ((R828_Arry[7] & 0xF0) + VGA_Count);
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_Delay_MS(pTuner, 10);
			char[] value = new char[1];
			if (R828_Muti_Read(pTuner, (byte) 0x01, /* VGA_Read */value) != true)
				return false;
			VGA_Read = value[0];
			if (VGA_Read > 40 * 4)
				break;
		}

		// Try X-1 column and save min result to Compare_Bet[0]
		if ((IQ_Pont[0].Gain_X & 0x1F) == 0x00) {
			Compare_IQ[0].Gain_X = ((IQ_Pont[0].Gain_X) & 0xDF) + 1; // Q-path,
																		// Gain=1
		} else
			Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X - 1; // left point
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true) // y-direction
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[0].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[0].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[0].Value = Compare_IQ[0].Value;

		// Try X column and save min result to Compare_Bet[1]
		Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X;
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true)
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[1].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[1].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[1].Value = Compare_IQ[0].Value;

		// Try X+1 column and save min result to Compare_Bet[2]
		if ((IQ_Pont[0].Gain_X & 0x1F) == 0x00)
			Compare_IQ[0].Gain_X = ((IQ_Pont[0].Gain_X) | 0x20) + 1; // I-path,
																		// Gain=1
		else
			Compare_IQ[0].Gain_X = IQ_Pont[0].Gain_X + 1;
		Compare_IQ[0].Phase_Y = IQ_Pont[0].Phase_Y;

		if (R828_IQ_Tree(pTuner, Compare_IQ[0].Gain_X, Compare_IQ[0].Phase_Y,
				0x08, Compare_IQ) != true)
			return false;

		if (R828_CompreCor(Compare_IQ) != true)
			return false;

		Compare_Bet[2].Gain_X = Compare_IQ[0].Gain_X;
		Compare_Bet[2].Phase_Y = Compare_IQ[0].Phase_Y;
		Compare_Bet[2].Value = Compare_IQ[0].Value;

		if (R828_CompreCor(Compare_Bet) != true)
			return false;

		IQ_Pont[0] = Compare_Bet[0];

		return true;
	}

	boolean R828_GPIO(int pTuner, /* R828_GPIO_Type */boolean R828_GPIO_Conrl) {
		if (R828_GPIO_Conrl == /* HI_SIG */true)
			R828_Arry[10] |= 0x01;
		else
			R828_Arry[10] &= 0xFE;

		R828_I2C.RegAddr = 0x0F;
		R828_I2C.Data = R828_Arry[10];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_SetStandard(int pTuner, /* R828_Standard_Type */int RT_Standard) {

		// Used Normal Arry to Modify
		byte ArrayNum;

		ArrayNum = 27;
		for (ArrayNum = 0; ArrayNum < 27; ArrayNum++) {
			R828_Arry[ArrayNum] = R828_iniArry[ArrayNum];
		}

		// Record Init Flag & Xtal_check Result
		if (R828_IMR_done_flag == true)
			R828_Arry[7] = (byte) ((R828_Arry[7] & 0xF0) | 0x01 | (Xtal_cap_sel << 1));
		else
			R828_Arry[7] = (byte) ((R828_Arry[7] & 0xF0) | 0x00);

		R828_I2C.RegAddr = 0x0C;
		R828_I2C.Data = R828_Arry[7];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Record version
		R828_I2C.RegAddr = 0x13;
		R828_Arry[14] = (byte) ((R828_Arry[14] & 0xC0) | VER_NUM);
		R828_I2C.Data = R828_Arry[14];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// for LT Gain test
		if (RT_Standard > SECAM_L1) {
			R828_I2C.RegAddr = 0x1D; // [5:3] LNA TOP
			R828_I2C.Data = (byte) ((R828_Arry[24] & 0xC7) | 0x00);
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			// R828_Delay_MS(1);
		}

		// Look Up System Dependent Table
		Sys_Info1 = R828_Sys_Sel(RT_Standard);
		R828_IF_khz = Sys_Info1.IF_KHz;
		R828_CAL_LO_khz = Sys_Info1.FILT_CAL_LO;

		// Filter Calibration
		if (R828_Fil_Cal_flag[RT_Standard] == 0) {
			// do filter calibration
			if (R828_Filt_Cal(pTuner, Sys_Info1.FILT_CAL_LO, Sys_Info1.BW) != true)
				return false;

			// read and set filter code
			R828_I2C_Len.RegAddr = 0x00;
			R828_I2C_Len.Len = 5;
			if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
				return false;

			R828_Fil_Cal_code[RT_Standard] = (byte) (R828_I2C_Len.Data[4] & 0x0F);

			// Filter Cali. Protection
			if (R828_Fil_Cal_code[RT_Standard] == 0
					|| R828_Fil_Cal_code[RT_Standard] == 15) {
				if (R828_Filt_Cal(pTuner, Sys_Info1.FILT_CAL_LO, Sys_Info1.BW) != true)
					return false;

				R828_I2C_Len.RegAddr = 0x00;
				R828_I2C_Len.Len = 5;
				if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
					return false;

				R828_Fil_Cal_code[RT_Standard] = (byte) (R828_I2C_Len.Data[4] & 0x0F);

				if (R828_Fil_Cal_code[RT_Standard] == 15) // narrowest
					R828_Fil_Cal_code[RT_Standard] = 0;

			}
			R828_Fil_Cal_flag[RT_Standard] = 1;
		}

		// Set Filter Q
		R828_Arry[5] = (byte) ((R828_Arry[5] & 0xE0) | Sys_Info1.FILT_Q | R828_Fil_Cal_code[RT_Standard]);
		R828_I2C.RegAddr = 0x0A;
		R828_I2C.Data = R828_Arry[5];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set BW, Filter_gain, & HP corner
		R828_Arry[6] = (byte) ((R828_Arry[6] & 0x10) | Sys_Info1.HP_COR);
		R828_I2C.RegAddr = 0x0B;
		R828_I2C.Data = R828_Arry[6];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set Img_R
		R828_Arry[2] = (byte) ((R828_Arry[2] & 0x7F) | Sys_Info1.IMG_R);
		R828_I2C.RegAddr = 0x07;
		R828_I2C.Data = R828_Arry[2];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set filt_3dB, V6MHz
		R828_Arry[1] = (byte) ((R828_Arry[1] & 0xCF) | Sys_Info1.FILT_GAIN);
		R828_I2C.RegAddr = 0x06;
		R828_I2C.Data = R828_Arry[1];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// channel filter extension
		R828_Arry[25] = (byte) ((R828_Arry[25] & 0x9F) | Sys_Info1.EXT_ENABLE);
		R828_I2C.RegAddr = 0x1E;
		R828_I2C.Data = R828_Arry[25];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Loop through
		R828_Arry[0] = (byte) ((R828_Arry[0] & 0x7F) | Sys_Info1.LOOP_THROUGH1);
		R828_I2C.RegAddr = 0x05;
		R828_I2C.Data = R828_Arry[0];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Loop through attenuation
		R828_Arry[26] = (byte) ((R828_Arry[26] & 0x7F) | Sys_Info1.LT_ATT);
		R828_I2C.RegAddr = 0x1F;
		R828_I2C.Data = R828_Arry[26];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// filter extention widest
		R828_Arry[10] = (byte) ((R828_Arry[10] & 0x7F) | Sys_Info1.FLT_EXT_WIDEST);
		R828_I2C.RegAddr = 0x0F;
		R828_I2C.Data = R828_Arry[10];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// RF poly filter current
		R828_Arry[20] = (byte) ((R828_Arry[20] & 0x9F) | Sys_Info1.POLYFIL_CUR);
		R828_I2C.RegAddr = 0x19;
		R828_I2C.Data = R828_Arry[20];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_Filt_Cal(int pTuner, int Cal_Freq, int R828_BW) {
		// set in Sys_sel()
		/*
		 * if(R828_BW == BW_8M) { //set filt_cap = no cap R828_I2C.RegAddr =
		 * 0x0B; //reg11 R828_Arry[6] &= 0x9F; //filt_cap = no cap R828_I2C.Data
		 * = R828_Arry[6]; } else if(R828_BW == BW_7M) { //set filt_cap = +1 cap
		 * R828_I2C.RegAddr = 0x0B; //reg11 R828_Arry[6] &= 0x9F; //filt_cap =
		 * no cap R828_Arry[6] |= 0x20; //filt_cap = +1 cap R828_I2C.Data =
		 * R828_Arry[6]; } else if(R828_BW == BW_6M) { //set filt_cap = +2 cap
		 * R828_I2C.RegAddr = 0x0B; //reg11 R828_Arry[6] &= 0x9F; //filt_cap =
		 * no cap R828_Arry[6] |= 0x60; //filt_cap = +2 cap R828_I2C.Data =
		 * R828_Arry[6]; }
		 * 
		 * 
		 * if(I2C_Write(pTuner, R828_I2C) != true) return false;
		 */

		// Set filt_cap
		R828_I2C.RegAddr = 0x0B;
		R828_Arry[6] = (byte) ((R828_Arry[6] & 0x9F) | (Sys_Info1.HP_COR & 0x60));
		R828_I2C.Data = R828_Arry[6];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set cali clk =on
		R828_I2C.RegAddr = 0x0F; // reg15
		R828_Arry[10] |= 0x04; // calibration clk=on
		R828_I2C.Data = R828_Arry[10];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// X'tal cap 0pF for PLL
		R828_I2C.RegAddr = 0x10;
		R828_Arry[11] = (byte) ((R828_Arry[11] & 0xFC) | 0x00);
		R828_I2C.Data = R828_Arry[11];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set PLL Freq = Filter Cali Freq
		if (R828_PLL(pTuner, Cal_Freq * 1000, STD_SIZE) != true)
			return false;

		// Start Trigger
		R828_I2C.RegAddr = 0x0B; // reg11
		R828_Arry[6] |= 0x10; // vstart=1
		R828_I2C.Data = R828_Arry[6];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// delay 0.5ms
		R828_Delay_MS(pTuner, 1);

		// Stop Trigger
		R828_I2C.RegAddr = 0x0B;
		R828_Arry[6] &= 0xEF; // vstart=0
		R828_I2C.Data = R828_Arry[6];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// set cali clk =off
		R828_I2C.RegAddr = 0x0F; // reg15
		R828_Arry[10] &= 0xFB; // calibration clk=off
		R828_I2C.Data = R828_Arry[10];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;

	}

	boolean R828_SetFrequency(int pTuner, R828_Set_Info R828_INFO, /* R828_SetFreq_Type */
			boolean R828_SetFreqMode) {
		int LO_Hz;
		/*
		 * #if 0 // Check Input Frequency Range if((R828_INFO.RF_KHz<40000) ||
		 * (R828_INFO.RF_KHz>900000)) { return false; } #endif
		 */

		Sys_Info1 = R828_Sys_Sel(DVB_T_6M); // 8=DVB_T_6M

		if (R828_INFO.R828_Standard == SECAM_L1)
			LO_Hz = (int) (R828_INFO.RF_Hz - (Sys_Info1.IF_KHz * 1000));
		else
			LO_Hz = (int) (R828_INFO.RF_Hz + (Sys_Info1.IF_KHz * 1000));

		// Set MUX dependent var. Must do before PLL( )
		if (R828_MUX(pTuner, LO_Hz / 1000) != true)
			return false;

		// Set PLL
		if (R828_PLL(pTuner, LO_Hz, R828_INFO.R828_Standard) != true)
			return false;

		R828_IMR_point_num = Freq_Info1.IMR_MEM;

		// Set TOP,VTH,VTL
		SysFreq_Info1 = R828_SysFreq_Sel(R828_INFO.R828_Standard,
				(int) R828_INFO.RF_KHz);

		// write DectBW, pre_dect_TOP
		R828_Arry[24] = (byte) ((R828_Arry[24] & 0x38) | (SysFreq_Info1.LNA_TOP & 0xC7));
		R828_I2C.RegAddr = 0x1D;
		R828_I2C.Data = R828_Arry[24];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// write MIXER TOP, TOP+-1
		R828_Arry[23] = (byte) ((R828_Arry[23] & 0x07) | (SysFreq_Info1.MIXER_TOP & 0xF8));
		R828_I2C.RegAddr = 0x1C;
		R828_I2C.Data = R828_Arry[23];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// write LNA VTHL
		R828_Arry[8] = (byte) ((R828_Arry[8] & 0x00) | SysFreq_Info1.LNA_VTH_L);
		R828_I2C.RegAddr = 0x0D;
		R828_I2C.Data = R828_Arry[8];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// write MIXER VTHL
		R828_Arry[9] = (byte) ((R828_Arry[9] & 0x00) | SysFreq_Info1.MIXER_VTH_L);
		R828_I2C.RegAddr = 0x0E;
		R828_I2C.Data = R828_Arry[9];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Cable-1/Air in
		R828_I2C.RegAddr = 0x05;
		R828_Arry[0] &= 0x9F;
		R828_Arry[0] |= SysFreq_Info1.AIR_CABLE1_IN;
		R828_I2C.Data = R828_Arry[0];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Cable-2 in
		R828_I2C.RegAddr = 0x06;
		R828_Arry[1] &= 0xF7;
		R828_Arry[1] |= SysFreq_Info1.CABLE2_IN;
		R828_I2C.Data = R828_Arry[1];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// CP current
		R828_I2C.RegAddr = 0x11;
		R828_Arry[12] &= 0xC7;
		R828_Arry[12] |= SysFreq_Info1.CP_CUR;
		R828_I2C.Data = R828_Arry[12];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// div buffer current
		R828_I2C.RegAddr = 0x17;
		R828_Arry[18] &= 0xCF;
		R828_Arry[18] |= SysFreq_Info1.DIV_BUF_CUR;
		R828_I2C.Data = R828_Arry[18];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set channel filter current
		R828_I2C.RegAddr = 0x0A;
		R828_Arry[5] = (byte) ((R828_Arry[5] & 0x9F) | SysFreq_Info1.FILTER_CUR);
		R828_I2C.Data = R828_Arry[5];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Air-In only for Astrometa
		R828_Arry[0] = (byte) ((R828_Arry[0] & 0x9F) | 0x00);
		R828_Arry[1] = (byte) ((R828_Arry[1] & 0xF7) | 0x00);

		R828_I2C.RegAddr = 0x05;
		R828_I2C.Data = R828_Arry[0];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x06;
		R828_I2C.Data = R828_Arry[1];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		// Set LNA
		if (R828_INFO.R828_Standard > SECAM_L1) {

			if (R828_SetFreqMode == FAST_MODE) // FAST mode
			{
				// R828_Arry[24] = (R828_Arry[24] & 0xC7) | 0x20; //LNA TOP:4
				R828_Arry[24] = (byte) ((R828_Arry[24] & 0xC7) | 0x00); // LNA
																		// TOP:lowest
				R828_I2C.RegAddr = 0x1D;
				R828_I2C.Data = R828_Arry[24];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Arry[23] = (byte) (R828_Arry[23] & 0xFB); // 0: normal mode
				R828_I2C.RegAddr = 0x1C;
				R828_I2C.Data = R828_Arry[23];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Arry[1] = (byte) (R828_Arry[1] & 0xBF); // 0: PRE_DECT off
				R828_I2C.RegAddr = 0x06;
				R828_I2C.Data = R828_Arry[1];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// agc clk 250hz
				R828_Arry[21] = (byte) ((R828_Arry[21] & 0xCF) | 0x30);
				R828_I2C.RegAddr = 0x1A;
				R828_I2C.Data = R828_Arry[21];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;
			} else // NORMAL mode
			{

				R828_Arry[24] = (byte) ((R828_Arry[24] & 0xC7) | 0x00); // LNA
																		// TOP:lowest
				R828_I2C.RegAddr = 0x1D;
				R828_I2C.Data = R828_Arry[24];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Arry[23] = (byte) (R828_Arry[23] & 0xFB); // 0: normal mode
				R828_I2C.RegAddr = 0x1C;
				R828_I2C.Data = R828_Arry[23];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Arry[1] = (byte) (R828_Arry[1] & 0xBF); // 0: PRE_DECT off
				R828_I2C.RegAddr = 0x06;
				R828_I2C.Data = R828_Arry[1];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// agc clk 250hz
				R828_Arry[21] = (byte) ((R828_Arry[21] & 0xCF) | 0x30); // 250hz
				R828_I2C.RegAddr = 0x1A;
				R828_I2C.Data = R828_Arry[21];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Delay_MS(pTuner, 250);

				// PRE_DECT on
				/*
				 * R828_Arry[1] = (R828_Arry[1] & 0xBF) |
				 * SysFreq_Info1.PRE_DECT; R828_I2C.RegAddr = 0x06;
				 * R828_I2C.Data = R828_Arry[1]; if(I2C_Write(pTuner, R828_I2C)
				 * != true) return false;
				 */
				// write LNA TOP = 3
				// R828_Arry[24] = (R828_Arry[24] & 0xC7) |
				// (SysFreq_Info1.LNA_TOP & 0x38);
				R828_Arry[24] = (byte) ((R828_Arry[24] & 0xC7) | 0x18); // TOP=3
				R828_I2C.RegAddr = 0x1D;
				R828_I2C.Data = R828_Arry[24];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// write disbytege mode
				R828_Arry[23] = (byte) ((R828_Arry[23] & 0xFB) | (SysFreq_Info1.MIXER_TOP & 0x04));
				R828_I2C.RegAddr = 0x1C;
				R828_I2C.Data = R828_Arry[23];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// LNA disbytege current
				R828_Arry[25] = (byte) ((R828_Arry[25] & 0xE0) | SysFreq_Info1.LNA_DISbyteGE);
				R828_I2C.RegAddr = 0x1E;
				R828_I2C.Data = R828_Arry[25];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// agc clk 60hz
				R828_Arry[21] = (byte) ((R828_Arry[21] & 0xCF) | 0x20);
				R828_I2C.RegAddr = 0x1A;
				R828_I2C.Data = R828_Arry[21];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;
			}
		} else {
			if (R828_SetFreqMode == NORMAL_MODE
					|| R828_SetFreqMode == FAST_MODE) {
				/*
				 * // PRE_DECT on R828_Arry[1] = (R828_Arry[1] & 0xBF) |
				 * SysFreq_Info1.PRE_DECT; R828_I2C.RegAddr = 0x06;
				 * R828_I2C.Data = R828_Arry[1]; if(I2C_Write(pTuner, R828_I2C)
				 * != true) return false;
				 */
				// PRE_DECT off
				R828_Arry[1] = (byte) (R828_Arry[1] & 0xBF); // 0: PRE_DECT off
				R828_I2C.RegAddr = 0x06;
				R828_I2C.Data = R828_Arry[1];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// write LNA TOP
				R828_Arry[24] = (byte) ((R828_Arry[24] & 0xC7) | (SysFreq_Info1.LNA_TOP & 0x38));
				R828_I2C.RegAddr = 0x1D;
				R828_I2C.Data = R828_Arry[24];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// write disbytege mode
				R828_Arry[23] = (byte) ((R828_Arry[23] & 0xFB) | (SysFreq_Info1.MIXER_TOP & 0x04));
				R828_I2C.RegAddr = 0x1C;
				R828_I2C.Data = R828_Arry[23];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// LNA disbytege current
				R828_Arry[25] = (byte) ((R828_Arry[25] & 0xE0) | SysFreq_Info1.LNA_DISbyteGE);
				R828_I2C.RegAddr = 0x1E;
				R828_I2C.Data = R828_Arry[25];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				// agc clk 1Khz, external det1 cap 1u
				R828_Arry[21] = (byte) ((R828_Arry[21] & 0xCF) | 0x00);
				R828_I2C.RegAddr = 0x1A;
				R828_I2C.Data = R828_Arry[21];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;

				R828_Arry[11] = (byte) ((R828_Arry[11] & 0xFB) | 0x00);
				R828_I2C.RegAddr = 0x10;
				R828_I2C.Data = R828_Arry[11];
				if (I2C_Write(pTuner, R828_I2C) != true)
					return false;
			}
		}

		return true;

	}

	boolean R828_Standby(int pTuner, int R828_LoopSwitch) {
		if (R828_LoopSwitch == LOOP_THROUGH) {
			R828_I2C.RegAddr = 0x06;
			R828_I2C.Data = (byte) 0xB1;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
			R828_I2C.RegAddr = 0x05;
			R828_I2C.Data = 0x03;

			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
		} else {
			R828_I2C.RegAddr = 0x05;
			R828_I2C.Data = (byte) 0xA3;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_I2C.RegAddr = 0x06;
			R828_I2C.Data = (byte) 0xB1;
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
		}

		R828_I2C.RegAddr = 0x07;
		R828_I2C.Data = 0x3A;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x08;
		R828_I2C.Data = 0x40;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x09;
		R828_I2C.Data = (byte) 0xC0; // polyfilter off
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x0A;
		R828_I2C.Data = 0x36;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x0C;
		R828_I2C.Data = 0x35;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x0F;
		R828_I2C.Data = 0x68; /* was 0x78, which turns off CLK_Out */
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x11;
		R828_I2C.Data = 0x03;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x17;
		R828_I2C.Data = (byte) 0xF4;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		R828_I2C.RegAddr = 0x19;
		R828_I2C.Data = 0x0C;
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_GetRfGain(int pTuner, R828_RF_Gain_Info[] pR828_rf_gain) {

		R828_I2C_Len.RegAddr = 0x00;
		R828_I2C_Len.Len = 4;
		if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
			return false;

		pR828_rf_gain[0].RF_gain1 = (byte) (R828_I2C_Len.Data[3] & 0x0F);
		pR828_rf_gain[0].RF_gain2 = (byte) ((R828_I2C_Len.Data[3] & 0xF0) >> 4);
		pR828_rf_gain[0].RF_gain_comb = (byte) (pR828_rf_gain[0].RF_gain1 * 2 + pR828_rf_gain[0].RF_gain2);

		return true;
	}

	/*
	 * measured with a Racal 6103E GSM test set at 928 MHz with -60 dBm input
	 * power, for raw results see:
	 * http://steve-m.de/projects/rtl-sdr/gain_measurement/r820t/
	 */

	static final int VGA_BASE_GAIN = -47;
	static int[] r820t_vga_gain_steps = { 0, 26, 26, 30, 42, 35, 24, 13, 14,
			32, 36, 34, 35, 37, 35, 36 };

	static int[] r820t_lna_gain_steps = { 0, 9, 13, 40, 38, 13, 31, 22, 26, 31,
			26, 14, 19, 5, 35, 13 };

	static int[] r820t_mixer_gain_steps = { 0, 5, 10, 10, 19, 9, 10, 25, 17,
			10, 8, 16, 13, 6, 3, -8 };

	boolean R828_SetRfGain(int pTuner, int gain) {
		int i, total_gain = 0;
		byte mix_index = 0, lna_index = 0;

		for (i = 0; i < 15; i++) {
			if (total_gain >= gain)
				break;

			total_gain += r820t_lna_gain_steps[++lna_index];

			if (total_gain >= gain)
				break;

			total_gain += r820t_mixer_gain_steps[++mix_index];
		}

		/* set LNA gain */
		R828_I2C.RegAddr = 0x05;
		R828_Arry[0] = (byte) ((R828_Arry[0] & 0xF0) | lna_index);
		R828_I2C.Data = R828_Arry[0];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		/* set Mixer gain */
		R828_I2C.RegAddr = 0x07;
		R828_Arry[2] = (byte) ((R828_Arry[2] & 0xF0) | mix_index);
		R828_I2C.Data = R828_Arry[2];
		if (I2C_Write(pTuner, R828_I2C) != true)
			return false;

		return true;
	}

	boolean R828_RfGainMode(int pTuner, boolean manual) {
		byte MixerGain;
		byte LnaGain;

		MixerGain = 0;
		LnaGain = 0;

		if (manual) {
			// LNA auto off
			R828_I2C.RegAddr = 0x05;
			R828_Arry[0] = (byte) (R828_Arry[0] | 0x10);
			R828_I2C.Data = R828_Arry[0];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			// Mixer auto off
			R828_I2C.RegAddr = 0x07;
			R828_Arry[2] = (byte) (R828_Arry[2] & 0xEF);
			R828_I2C.Data = R828_Arry[2];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			R828_I2C_Len.RegAddr = 0x00;
			R828_I2C_Len.Len = 4;
			if (I2C_Read_Len(pTuner, R828_I2C_Len) != true)
				return false;

			/* set fixed VGA gain for now (16.3 dB) */
			R828_I2C.RegAddr = 0x0C;
			R828_Arry[7] = (byte) ((R828_Arry[7] & 0x60) | 0x08);
			R828_I2C.Data = R828_Arry[7];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			Log.d(TAG, "set manual gain mode");
		} else {
			// LNA
			R828_I2C.RegAddr = 0x05;
			R828_Arry[0] = (byte) (R828_Arry[0] & 0xEF);
			R828_I2C.Data = R828_Arry[0];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			// Mixer
			R828_I2C.RegAddr = 0x07;
			R828_Arry[2] = (byte) (R828_Arry[2] | 0x10);
			R828_I2C.Data = R828_Arry[2];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;

			/* set fixed VGA gain for now (26.5 dB) */
			R828_I2C.RegAddr = 0x0C;
			R828_Arry[7] = (byte) ((R828_Arry[7] & 0x60) | 0x0B);
			R828_I2C.Data = R828_Arry[7];
			if (I2C_Write(pTuner, R828_I2C) != true)
				return false;
			Log.d(TAG, "set auto gain mode");
		}

		return true;
	}

}
