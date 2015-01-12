package com.rtlsdr.android.tuner;

import java.io.IOException;

import android.util.Log;

import com.rtlsdr.android.SdrUSBDriver;

public class fc0013_tuner implements RtlSdr_tuner_iface {
	private static final String TAG = fc0013_tuner.class.getSimpleName();
	final int  FC0013_I2C_ADDR		= 0xc6;
	final int  FC0013_CHECK_ADDR	= 0x00;
	final int  FC0013_CHECK_VAL		= 0xa3;
	int []  fc0013_lna_gains = {
			-99,	0x02,
			-73,	0x03,
			-65,	0x05,
			-63,	0x04,
			-63,	0x00,
			-60,	0x07,
			-58,	0x01,
			-54,	0x06,
			58,	0x0f,
			61,	0x0e,
			63,	0x0d,
			65,	0x0c,
			67,	0x0b,
			68,	0x0a,
			70,	0x09,
			71,	0x08,
			179,	0x17,
			181,	0x16,
			182,	0x15,
			184,	0x14,
			186,	0x13,
			188,	0x12,
			191,	0x11,
			197,	0x10
		};
	
	@Override
	public int init(int param) throws IOException {
		fc0013_init();
		return 0;
	}

	@Override
	public int exit(int param) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_freq(int param, long freq) throws IOException {
		return fc0013_set_params((int) freq, 6000000);
	}

	@Override
	public int set_bw(int param, int bw) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_gain(int param, int gain) throws IOException {
		return fc0013_set_lna_gain( gain);
	}

	@Override
	public int set_if_gain(int param, int stage, int gain) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_gain_mode(int param, boolean manual) throws IOException {
		 return fc0013_set_gain_mode(manual);
		
	}
	private int fc0013_writereg(byte reg, byte val)
	{
		byte[]  data  = new byte[2];
		data[0] = reg;
		data[1] = val;

		if (SdrUSBDriver.rtlsdr_i2c_write_fn((byte)FC0013_I2C_ADDR, data,(byte) 2) < 0)
			return -1;

		return 0;
	}

	private int fc0013_readreg( byte reg, byte[] val)
	{
		byte[]  data  = new byte[2];
		data[0] = reg;

		if (SdrUSBDriver.rtlsdr_i2c_write_fn((byte)FC0013_I2C_ADDR, data, (byte)1) < 0)
			return -1;

		if (SdrUSBDriver.rtlsdr_i2c_read_fn((byte)FC0013_I2C_ADDR, data, (byte)1) < 0)
			return -1;

		
		val[0] = data[0];

		return 0;
	}
	int fc0013_set_gain_mode(boolean manual)
	{
		int ret = 0;
		byte [] tmp = new byte [2];

		ret |= fc0013_readreg((byte) 0x0d, tmp);

		if (manual)
			tmp[0] |= (1 << 3);
		else
			tmp[0] &= ~(1 << 3);

		ret |= fc0013_writereg((byte)0x0d, tmp[0]);

		/* set a fixed IF-gain for now */
		ret |= fc0013_writereg((byte)0x13,(byte) 0x0a);

		return ret;
	}

	int fc0013_init()
	{
		int ret = 0;
		int i;
		byte [] reg = {
			0x00,	/* reg. 0x00: dummy */
			0x09,	/* reg. 0x01 */
			0x16,	/* reg. 0x02 */
			0x00,	/* reg. 0x03 */
			0x00,	/* reg. 0x04 */
			0x17,	/* reg. 0x05 */
			0x02,	/* reg. 0x06: LPF bandwidth */
			0x0a,	/* reg. 0x07: CHECK */
	(byte)	0xff,	/* reg. 0x08: AGC Clock divide by 256, AGC gain 1/256,
				   Loop Bw 1/8 */
			0x6e,	/* reg. 0x09: Disable LoopThrough, Enable LoopThrough: 0x6f */
	(byte)		0xb8,	/* reg. 0x0a: Disable LO Test Buffer */
	(byte)	0x82,	/* reg. 0x0b: CHECK */
	(byte)	0xfc,	/* reg. 0x0c: depending on AGC Up-Down mode, may need 0xf8 */
			0x01,	/* reg. 0x0d: AGC Not Forcing & LNA Forcing, may need 0x02 */
			0x00,	/* reg. 0x0e */
			0x00,	/* reg. 0x0f */
			0x00,	/* reg. 0x10 */
			0x00,	/* reg. 0x11 */
			0x00,	/* reg. 0x12 */
			0x00,	/* reg. 0x13 */
			0x50,	/* reg. 0x14: DVB-t High Gain, UHF.
				   Middle Gain: 0x48, Low Gain: 0x40 */
			0x01,	/* reg. 0x15 */
		};
/*	#if 0
		switch (rtlsdr_get_tuner_clock(dev)) {
		case FC_XTAL_27_MHZ:
		case FC_XTAL_28_8_MHZ:
			reg[0x07] |= 0x20;
			break;
		case FC_XTAL_36_MHZ:
		default:
			break;
		}
	#endif*/
		reg[0x07] |= 0x20;

//		if (dev->dual_master)
		reg[0x0c] |= 0x02;

		for (i = 1; i < reg.length; i++) {
			ret = fc0013_writereg((byte) i, reg[i]);
			if (ret < 0)
				break;
		}

		return ret;
	}

	int fc0013_set_lna_gain(int gain)
	{
		int ret = 0;
		int i;
		byte [] tmp = new byte[1];

		ret |= fc0013_readreg((byte) 0x14, tmp);

		/* mask bits off */
		tmp[0] &= 0xe0;

		for (i = 0; i < fc0013_lna_gains.length/2; i++) {
			if ((fc0013_lna_gains[i*2] >= gain) || (i+1 == (fc0013_lna_gains.length/2))) {
				tmp[0] |= fc0013_lna_gains[i*2 + 1];
				break;
			}
		}

		/* set gain */
		ret |= fc0013_writereg((byte) 0x14, tmp[0]);

		return ret;
	}
	int fc0013_set_params(int freq, int bandwidth)
	{
		int i, ret = 0;
		char []  reg = new char[7];
		byte am, pm, multi;
		long f_vco;
		int xtal_freq_div_2;
		char xin, xdiv;
		boolean vco_select = false;
		byte [] tmp = new byte[1];
		
		xtal_freq_div_2 = SdrUSBDriver.rtlsdr_get_tuner_clock() / 2;

		/* set VHF track */
		ret = fc0013_set_vhf_track(freq);
		if (ret<0)
			return -1;

		if (freq < 300000000) {
			/* enable VHF filter */
			ret = fc0013_readreg((byte) 0x07, tmp);
			if (ret<0)
				return -1;
			ret = fc0013_writereg(  (byte) 0x07, (byte)(tmp[0] | 0x10));
			if (ret<0)
				return -1;

			/* disable UHF & disable GPS */
			ret = fc0013_readreg(  (byte) 0x14, tmp);
			if (ret<0)
				return -1;
			ret = fc0013_writereg( (byte) 0x14, (byte) (tmp[0] & 0x1f));
			if (ret<0)
				return -1;
		} else if (freq <= 862000000) {
			/* disable VHF filter */
			ret = fc0013_readreg( (byte) 0x07, tmp);
			if (ret<0)
				return -1;
			ret = fc0013_writereg( (byte) 0x07,(byte) (tmp[0] & 0xef));
			if (ret<0)
				return -1;;

			/* enable UHF & disable GPS */
			ret = fc0013_readreg(  (byte) 0x14, tmp);
			if (ret<0)
				return -1;;
			ret = fc0013_writereg( (byte) 0x14,(byte)( (tmp[0] & 0x1f) | 0x40));
			if (ret<0)
				return -1;;
		} else {
			/* disable VHF filter */
			ret = fc0013_readreg((byte) 0x07, tmp);
			if (ret<0)
				return -1;
			ret = fc0013_writereg((byte) 0x07, (byte) (tmp[0] & 0xef));
			if (ret<0)
				return -1;

			/* disable UHF & enable GPS */
			ret = fc0013_readreg((byte)  0x14, tmp);
			if (ret<0)
				return -1;;
			ret = fc0013_writereg((byte)  0x14,(byte) ((tmp[0] & 0x1f) | 0x20));
			if (ret<0)
				return -1;;
		}

		/* select frequency divider and the frequency of VCO */
		if (freq < 37084000) {		/* freq * 96 < 3560000000 */
			multi = 96;
			reg[5] = (char) 0x82;
			reg[6] = 0x00;
		} else if (freq < 55625000) {	/* freq * 64 < 3560000000 */
			multi = 64;
			reg[5] = 0x02;
			reg[6] = 0x02;
		} else if (freq < 74167000) {	/* freq * 48 < 3560000000 */
			multi = 48;
			reg[5] = 0x42;
			reg[6] = 0x00;
		} else if (freq < 111250000) {	/* freq * 32 < 3560000000 */
			multi = 32;
			reg[5] = (char) 0x82;
			reg[6] = 0x02;
		} else if (freq < 148334000) {	/* freq * 24 < 3560000000 */
			multi = 24;
			reg[5] = 0x22;
			reg[6] = 0x00;
		} else if (freq < 222500000) {	/* freq * 16 < 3560000000 */
			multi = 16;
			reg[5] = 0x42;
			reg[6] = 0x02;
		} else if (freq < 296667000) {	/* freq * 12 < 3560000000 */
			multi = 12;
			reg[5] = 0x12;
			reg[6] = 0x00;
		} else if (freq < 445000000) {	/* freq * 8 < 3560000000 */
			multi = 8;
			reg[5] = 0x22;
			reg[6] = 0x02;
		} else if (freq < 593334000) {	/* freq * 6 < 3560000000 */
			multi = 6;
			reg[5] = 0x0a;
			reg[6] = 0x00;
		} else if (freq < 950000000) {	/* freq * 4 < 3800000000 */
			multi = 4;
			reg[5] = 0x12;
			reg[6] = 0x02;
		} else {
			multi = 2;
			reg[5] = 0x0a;
			reg[6] = 0x02;
		}

		f_vco = freq * multi;

		if (f_vco >= 3060000000L) {
			reg[6] |= 0x08;
			vco_select = true;
		}

		/* From divided value (XDIV) determined the FA and FP value */
		xdiv = (char)(f_vco / xtal_freq_div_2);
		if ((f_vco - xdiv * xtal_freq_div_2) >= (xtal_freq_div_2 / 2))
			xdiv++;

		pm = (byte)(xdiv / 8);
		am = (byte)(xdiv - (8 * pm));

		if (am < 2) {
			am += 8;
			pm--;
		}

		if (pm > 31) {
			reg[1] = (char) (am + (8 * (pm - 31)));
			reg[2] = 31;
		} else {
			reg[1] = (char) am;
			reg[2] = (char)pm;
		}

		if ((reg[1] > 15) || (reg[2] < 0x0b)) {
			Log.e("TAG", "[FC0013] no valid PLL combination found for "+ freq+" HZ!");
			return -1;
		}

		/* fix clock out */
		reg[6] |= 0x20;

		/* From VCO frequency determines the XIN ( fractional part of Delta
		   Sigma PLL) and divided value (XDIV) */
		xin = (char)((f_vco - (f_vco / xtal_freq_div_2) * xtal_freq_div_2) / 1000);
		xin = (char) ((xin << 15) / (xtal_freq_div_2 / 1000));
		if (xin >= 16384)
			xin += 32768;

		reg[3] = (char) (xin >> 8);
		reg[4] = (char) (xin & 0xff);

		reg[6] &= 0x3f; /* bits 6 and 7 describe the bandwidth */
		switch (bandwidth) {
		case 6000000:
			reg[6] |= 0x80;
			break;
		case 7000000:
			reg[6] |= 0x40;
			break;
		case 8000000:
		default:
			break;
		}

		/* modified for Realtek demod */
		reg[5] |= 0x07;

		for (i = 1; i <= 6; i++) {
			ret = fc0013_writereg( (byte) i, (byte)reg[i]);
			if (ret<0)
				return -1;
		}

		ret = fc0013_readreg( (byte) 0x11, tmp);
		if (ret<0)
			return -1;
		if (multi == 64)
			ret = fc0013_writereg((byte)0x11,(byte)( tmp[0] | 0x04));
		else
			ret = fc0013_writereg((byte)0x11, (byte)(tmp[0] & 0xfb));
		if (ret<0)
			return -1;

		/* VCO Calibration */
		ret = fc0013_writereg((byte)0x0e,(byte) 0x80);
		if (ret != 0)
			ret = fc0013_writereg((byte) 0x0e, (byte)0x00);

		/* VCO Re-Calibration if needed */
		if (ret !=0)
			ret = fc0013_writereg((byte) 0x0e, (byte) 0x00);

		if (ret != 0) {
//			msleep(10);
			ret = fc0013_readreg((byte) 0x0e, tmp);
		}
		if (ret<0)
			return -1;

		/* vco selection */
		tmp[0] &= 0x3f;

		if (vco_select ) {
			if (tmp[0] > 0x3c) {
				reg[6] &= ~0x08;
				ret = fc0013_writereg( (byte) 0x06, (byte)reg[6]);
				if (ret != 0)
					ret = fc0013_writereg( (byte) 0x0e, (byte)0x80);
				if (ret !=0)
					ret = fc0013_writereg((byte)  0x0e,(byte) 0x00);
			}
		} else {
			if (tmp[0] < 0x02) {
				reg[6] |= 0x08;
				ret = fc0013_writereg((byte)  0x06, (byte)reg[6]);
				if (ret != 0)
					ret = fc0013_writereg((byte)  0x0e,(byte) 0x80);
				if (ret != 0)
					ret = fc0013_writereg( (byte) 0x0e,(byte) 0x00);
			}
		}

	//exit:
		return ret;
	}
	private int fc0013_set_vhf_track(int freq)
	{
		int ret;
		
		byte [] tmp = new byte[1];
		
		ret = fc0013_readreg((byte) 0x1d, tmp);
		if (ret != 0)
			return -1;
		tmp[0] &= 0xe3;
		if (freq <= 177500000) {		/* VHF Track: 7 */
			ret = fc0013_writereg((byte)0x1d,(byte)( tmp[0] | 0x1c));
		} else if (freq <= 184500000) {	/* VHF Track: 6 */
			ret = fc0013_writereg((byte) 0x1d, (byte)(tmp[0] | 0x18));
		} else if (freq <= 191500000) {	/* VHF Track: 5 */
			ret = fc0013_writereg((byte) 0x1d,(byte)( tmp[0] | 0x14));
		} else if (freq <= 198500000) {	/* VHF Track: 4 */
			ret = fc0013_writereg((byte) 0x1d, (byte)(tmp[0] | 0x10));
		} else if (freq <= 205500000) {	/* VHF Track: 3 */
			ret = fc0013_writereg( (byte) 0x1d,(byte)( tmp[0] | 0x0c));
		} else if (freq <= 219500000) {	/* VHF Track: 2 */
			ret = fc0013_writereg( (byte)0x1d, (byte)(tmp[0] | 0x08));
		} else if (freq < 300000000) {		/* VHF Track: 1 */
			ret = fc0013_writereg((byte) 0x1d, (byte)(tmp[0] | 0x04));
		} else {				/* UHF and GPS */
			ret = fc0013_writereg((byte) 0x1d, (byte)(tmp[0] | 0x1c));
		}

	//error_out:
		return ret;
	}
}
