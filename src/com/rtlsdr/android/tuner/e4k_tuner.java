package com.rtlsdr.android.tuner;

import java.io.IOException;

import com.rtlsdr.android.SdrUSBDriver;

public class e4k_tuner implements RtlSdr_tuner_iface {
	class e4k_if_filter {
		int E4K_IF_FILTER_MIX;
		int E4K_IF_FILTER_CHAN;
		int E4K_IF_FILTER_RC;
	};

	class e4k_pll_params {
		int fosc;
		int intended_flo;
		int flo;
		char x;
		byte z;
		byte r;
		byte r_idx;
		byte threephase;
	};

	class e4k_band {
		int E4K_BAND_VHF2 = 0;
		int E4K_BAND_VHF3 = 1;
		int E4K_BAND_UHF = 2;
		int E4K_BAND_L = 3;
	};

	class e4k_state {

		byte i2c_addr;
		e4k_band band;
		e4k_pll_params vco;

	};

	class reg_field {
		byte reg;
		byte shift;
		byte width;
	};

	private final int E4K_I2C_ADDR = 0xC8;
	private final int E4K_REG_MASTER1 = 0x00,
			E4K_REG_MASTER2 = 0x01,
			E4K_REG_MASTER3 = 0x02,
			E4K_REG_MASTER4 = 0x03,
			E4K_REG_MASTER5 = 0x04,
			E4K_REG_CLK_INP = 0x05,
			E4K_REG_REF_CLK = 0x06,
			E4K_REG_SYNTH1 = 0x07,
			E4K_REG_SYNTH2 = 0x08,
			E4K_REG_SYNTH3 = 0x09,
			E4K_REG_SYNTH4 = 0x0a,
			E4K_REG_SYNTH5 = 0x0b,
			E4K_REG_SYNTH6 = 0x0c,
			E4K_REG_SYNTH7 = 0x0d,
			E4K_REG_SYNTH8 = 0x0e,
			E4K_REG_SYNTH9 = 0x0f,
			E4K_REG_FILT1 = 0x10,
			E4K_REG_FILT2 = 0x11,
			E4K_REG_FILT3 = 0x12,
			// gap
			E4K_REG_GAIN1 = 0x14,
			E4K_REG_GAIN2 = 0x15,
			E4K_REG_GAIN3 = 0x16,
			E4K_REG_GAIN4 = 0x17,
			// gap
			E4K_REG_AGC1 = 0x1a,
			E4K_REG_AGC2 = 0x1b,
			E4K_REG_AGC3 = 0x1c,
			E4K_REG_AGC4 = 0x1d,
			E4K_REG_AGC5 = 0x1e,
			E4K_REG_AGC6 = 0x1f,
			E4K_REG_AGC7 = 0x20,
			E4K_REG_AGC8 = 0x21,
			// gap
			E4K_REG_AGC11 = 0x24,
			E4K_REG_AGC12 = 0x25,
			// gap
			E4K_REG_DC1 = 0x29,
			E4K_REG_DC2 = 0x2a,
			E4K_REG_DC3 = 0x2b,
			E4K_REG_DC4 = 0x2c,
			E4K_REG_DC5 = 0x2d,
			E4K_REG_DC6 = 0x2e,
			E4K_REG_DC7 = 0x2f,
			E4K_REG_DC8 = 0x30,
			// gap
			E4K_REG_QLUT0 = 0x50,
			E4K_REG_QLUT1 = 0x51,
			E4K_REG_QLUT2 = 0x52,
			E4K_REG_QLUT3 = 0x53,
			// gap
			E4K_REG_ILUT0 = 0x60,
			E4K_REG_ILUT1 = 0x61,
			E4K_REG_ILUT2 = 0x62,
			E4K_REG_ILUT3 = 0x63,
			// gap
			E4K_REG_DCTIME1 = 0x70, E4K_REG_DCTIME2 = 0x71,
			E4K_REG_DCTIME3 = 0x72, E4K_REG_DCTIME4 = 0x73,
			E4K_REG_PWM1 = 0x74, E4K_REG_PWM2 = 0x75, E4K_REG_PWM3 = 0x76,
			E4K_REG_PWM4 = 0x77, E4K_REG_BIAS = 0x78,
			E4K_REG_CLKOUT_PWDN = 0x7a, E4K_REG_CHFILT_CALIB = 0x7b,
			E4K_REG_I2C_REG_ADDR = 0x7d, E4K_AGC_MOD_SERIAL = 0x0,
			E4K_AGC_MOD_IF_PWM_LNA_SERIAL = 0x1,
			E4K_AGC_MOD_IF_PWM_LNA_AUTONL = 0x2,
			E4K_AGC_MOD_IF_PWM_LNA_SUPERV = 0x3,
			E4K_AGC_MOD_IF_SERIAL_LNA_PWM = 0x4,
			E4K_AGC_MOD_IF_PWM_LNA_PWM = 0x5,
			E4K_AGC_MOD_IF_DIG_LNA_SERIAL = 0x6,
			E4K_AGC_MOD_IF_DIG_LNA_AUTON = 0x7,
			E4K_AGC_MOD_IF_DIG_LNA_SUPERV = 0x8,
			E4K_AGC_MOD_IF_SERIAL_LNA_AUTON = 0x9,
			E4K_AGC_MOD_IF_SERIAL_LNA_SUPERV = 0xa, E4K_BAND_VHF2 = 0,
			E4K_BAND_VHF3 = 1, E4K_BAND_UHF = 2, E4K_BAND_L = 3,
			E4K_F_MIX_BW_27M = 0, E4K_F_MIX_BW_4M6 = 8, E4K_F_MIX_BW_4M2 = 9,
			E4K_F_MIX_BW_3M8 = 10, E4K_F_MIX_BW_3M4 = 11, E4K_F_MIX_BW_3M = 12,
			E4K_F_MIX_BW_2M7 = 13, E4K_F_MIX_BW_2M3 = 14,
			E4K_F_MIX_BW_1M9 = 15, E4K_IF_FILTER_MIX = 0,
			E4K_IF_FILTER_CHAN = 1, E4K_IF_FILTER_RC = 3;

	private final int E4K_MASTER1_RESET = (1 << 0);
	private final int E4K_MASTER1_NORM_STBY = (1 << 1);
	private final int E4K_MASTER1_POR_DET = (1 << 2);
	private final int E4K_SYNTH1_PLL_LOCK = (1 << 0);
	private final int E4K_SYNTH1_BAND_SHIF = 1;
	private final int E4K_SYNTH7_3PHASE_EN = (1 << 3);
	private final int E4K_SYNTH8_VCOCAL_UPD = (1 << 2);
	private final int E4K_FILT3_DISABLE = (1 << 5);
	private final int E4K_AGC1_LIN_MODE = (1 << 4);
	private final int E4K_AGC1_LNA_UPDATE = (1 << 5);
	private final int E4K_AGC1_LNA_G_LOW = (1 << 6);
	private final int E4K_AGC1_LNA_G_HIGH = (1 << 7);
	private final int E4K_AGC6_LNA_CAL_REQ = (1 << 4);
	private final int E4K_AGC7_MIX_GAIN_AUTO = (1 << 0);
	private final int E4K_AGC7_GAIN_STEP_5dB = (1 << 5);
	private final int E4K_AGC8_SENS_LIN_AUTO = (1 << 0);
	private final int E4K_AGC11_LNA_GAIN_ENH = (1 << 0);
	private final int E4K_DC1_CAL_REQ = (1 << 0);
	private final int E4K_DC5_I_LUT_EN = (1 << 0);
	private final int E4K_DC5_Q_LUT_EN = (1 << 1);
	private final int E4K_DC5_RANGE_DET_EN = (1 << 2);
	private final int E4K_DC5_RANGE_EN = (1 << 3);
	private final int E4K_DC5_TIMEVAR_EN = (1 << 4);
	private final int E4K_CLKOUT_DISABLE = 0x96;
	private final int E4K_CHFCALIB_CMD = (1 << 0);
	private final int E4K_AGC1_MOD_MASK = 0xF;

	private final byte[] if_stage1_gain = { -3, 6 };

	private final byte[] if_stage23_gain = { 0, 3, 6, 9 };

	private final byte[] if_stage4_gain = { 0, 1, 2, 2 };

	private final byte[] if_stage56_gain = { 3, 6, 9, 12, 15, 15, 15, 15 };

	private final byte[] if_stage_gain[] = { null, if_stage1_gain,
			if_stage23_gain, if_stage23_gain, if_stage4_gain, if_stage56_gain,
			if_stage56_gain };

	private final int[] if_stage_gain_len = { 0, if_stage1_gain.length,
			if_stage23_gain.length, if_stage23_gain.length,
			if_stage4_gain.length, if_stage56_gain.length,
			if_stage56_gain.length };

	@Override
	public int init(int param) throws IOException {
		// TODO Auto-generated method stub

		return e4k_init();
	}

	@Override
	public int exit(int param) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_freq(int param, long freq) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_bw(int param, int bw) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_gain(int param, int gain) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_if_gain(int param, int stage, int gain) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int set_gain_mode(int param, boolean manual) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	private int MHZ(int x) {
		return ((x) * 1000 * 1000);
	}

	private int KHZ(int x) {
		return ((x) * 1000);
	}

	byte e4k_reg_read(int reg) {
		return SdrUSBDriver.rtlsdr_i2c_read_reg((char) E4K_I2C_ADDR, reg);
	}

	int e4k_reg_write(int reg, int val) {
		return SdrUSBDriver.rtlsdr_i2c_write_reg((byte) E4K_I2C_ADDR,
				(char) reg, (char) val);
	}

	/*
	 * ! \brief Set or clear some (masked) bits inside a register \param[in] e4k
	 * reference to the tuner \param[in] reg number of the register \param[in]
	 * mask bit-mask of the value \param[in] val data value to be written to
	 * register \returns 0 on success, negative in case of error
	 */
	int e4k_reg_set_mask(byte reg, char mask, char val) {
		byte tmp = e4k_reg_read(reg);

		if ((tmp & mask) == val)
			return 0;

		return e4k_reg_write(reg, (tmp & ~mask) | (val & mask));
	}

	/*
	 * ! \brief Enables / Disables the channel filter \param[in] e4k reference
	 * to the tuner chip \param[in] on 1=filter enabled, 0=filter disabled
	 * \returns 0 success, negative errors
	 */
	int e4k_if_filter_chan_enable(boolean on) {
		return e4k_reg_set_mask((byte) E4K_REG_FILT3, (char) E4K_FILT3_DISABLE,
				(char) (on ? 0 : E4K_FILT3_DISABLE));
	}

	/*
	 * ! \brief Initialize the E4K tuner
	 */
	int e4k_init() {
		/* make a dummy i2c read or write command, will not be ACKed! */
		e4k_reg_read((byte) 0);

		/* Make sure we reset everything and clear POR indicator */
		e4k_reg_write((byte) E4K_REG_MASTER1, (byte) (E4K_MASTER1_RESET
				| E4K_MASTER1_NORM_STBY | E4K_MASTER1_POR_DET));

		/* Configure clock input */
		e4k_reg_write((byte) E4K_REG_CLK_INP, (byte) 0x00);

		/* Disable clock output */
		e4k_reg_write((byte) E4K_REG_REF_CLK, (byte) 0x00);
		e4k_reg_write((byte) E4K_REG_CLKOUT_PWDN, (byte) 0x96);

		/* Write some magic values into registers */
		magic_init();
		/*
		 * #if 0 /* Set common mode voltage a bit higher for more margin 850 mv
		 * * / e4k_commonmode_set(e4k, 4);
		 * 
		 * /* Initialize DC offset lookup tables * /
		 * e4k_dc_offset_gen_table(e4k);
		 * 
		 * / * Enable time variant DC correction * / e4k_reg_write(e4k,
		 * E4K_REG_DCTIME1, 0x01); e4k_reg_write(e4k, E4K_REG_DCTIME2, 0x01);
		 * #endif
		 */

		/* Set LNA mode to manual */
		e4k_reg_write((byte) E4K_REG_AGC4, 0x10); /* High threshold */
		e4k_reg_write((byte) E4K_REG_AGC5, 0x04); /* Low threshold */
		e4k_reg_write((byte) E4K_REG_AGC6, 0x1a); /* LNA calib + loop rate */

		e4k_reg_set_mask((byte) E4K_REG_AGC1, (char) E4K_AGC1_MOD_MASK,
				(char) E4K_AGC_MOD_SERIAL);

		/* Set Mixer Gain Control to manual */
		e4k_reg_set_mask((byte) E4K_REG_AGC7, (char) E4K_AGC7_MIX_GAIN_AUTO,
				(char) 0);

		/*
		 * #if 0/* Enable LNA Gain enhancement * / e4k_reg_set_mask(e4k,
		 * E4K_REG_AGC11, 0x7, E4K_AGC11_LNA_GAIN_ENH | (2 << 1));
		 * 
		 * /* Enable automatic IF gain mode switching * / e4k_reg_set_mask(e4k,
		 * E4K_REG_AGC8, 0x1, E4K_AGC8_SENS_LIN_AUTO); $infif
		 */

		/* Use auto-gain as default */
		e4k_enable_manual_gain(false);

		/* Select moderate gain levels */
		e4k_if_gain_set((byte) 1, (byte) 6);
		e4k_if_gain_set(2, 0);
		e4k_if_gain_set(3, 0);
		e4k_if_gain_set(4, 0);
		e4k_if_gain_set(5, 9);
		e4k_if_gain_set(6, 9);

		/* Set the most narrow filter we can possibly use */
		// e4k_if_filter_bw_set(E4K_IF_FILTER_MIX, KHZ(1900));
		// e4k_if_filter_bw_set( E4K_IF_FILTER_RC, KHZ(1000));
		// e4k_if_filter_bw_set( E4K_IF_FILTER_CHAN, KHZ(2150));
		// e4k_if_filter_chan_enable(true);

		/* Disable time variant DC correction and LUT */
		e4k_reg_set_mask((byte) E4K_REG_DC5, (char) 0x03, (char) 0);
		e4k_reg_set_mask((byte) E4K_REG_DCTIME1, (char) 0x03, (char) 0);
		e4k_reg_set_mask((byte) E4K_REG_DCTIME2, (char) 0x03, (char) 0);

		return 0;
	}

	int find_stage_gain(byte stage, byte val) {
		byte[] arr;
		int i;

		if (stage >= (if_stage_gain.length))
			return -1;

		arr = if_stage_gain[stage];

		for (i = 0; i < if_stage_gain_len[stage]; i++) {
			if (arr[i] == val)
				return i;
		}
		return -1;
	}

	/*
	 * ! \brief Set the gain of one of the IF gain stages \param [e4k] handle to
	 * the tuner chip \param [stage] number of the stage (1..6) \param [value]
	 * gain value in dB \returns 0 on success, negative in case of error
	 */
	int e4k_if_gain_set(int stage, int value) {
		int rc;
		byte mask;
		// const struct reg_field *field;

		rc = find_stage_gain((byte) stage, (byte) value);
		if (rc < 0)
			return rc;

		/* compute the bit-mask for the given gain field */
		// field = if_stage_gain_regs[stage];
		// mask = width2mask[field.width] << field->shift;

		return 0;// e4k_reg_set_mask( field.reg, mask, rc << field.shift);
	}

	private int e4k_enable_manual_gain(boolean manual) {
		if (manual) {
			/* Set LNA mode to manual */
			e4k_reg_set_mask((byte) E4K_REG_AGC1, (char) E4K_AGC1_MOD_MASK,
					(char) E4K_AGC_MOD_SERIAL);

			/* Set Mixer Gain Control to manual */
			e4k_reg_set_mask((byte) E4K_REG_AGC7,
					(char) E4K_AGC7_MIX_GAIN_AUTO, (char) 0);
		} else {
			/* Set LNA mode to auto */
			e4k_reg_set_mask((byte) E4K_REG_AGC1, (char) E4K_AGC1_MOD_MASK,
					(char) E4K_AGC_MOD_IF_SERIAL_LNA_AUTON);
			/* Set Mixer Gain Control to auto */
			e4k_reg_set_mask((byte) E4K_REG_AGC7,
					(char) E4K_AGC7_MIX_GAIN_AUTO, (char) 1);

			e4k_reg_set_mask((byte) E4K_REG_AGC11, (char) 0x7, (char) 0);
		}

		return 0;
	}

	int unsigned_delta(int a, int b) {
		if (a > b)
			return a - b;
		else
			return b - a;
	}

	int closest_arr_idx(int arr[], int arr_size, int freq) {
		int i, bi = 0;
		int best_delta = 0xffffffff;

		/*
		 * iterate over the array containing a list of the center frequencies,
		 * selecting the closest one
		 */
		for (i = 0; i < arr_size; i++) {
			int delta = unsigned_delta(freq, arr[i]);
			if (delta < best_delta) {
				best_delta = delta;
				bi = i;
			}
		}

		return bi;
	}

	int magic_init() {
		e4k_reg_write((byte) 0x7e, (byte) 0x01);
		e4k_reg_write((byte) 0x7f, (byte) 0xfe);
		e4k_reg_write((byte) 0x82, (byte) 0x00);
		e4k_reg_write((byte) 0x86, (byte) 0x50); /* polarity A */
		e4k_reg_write((byte) 0x87, (byte) 0x20);
		e4k_reg_write((byte) 0x88, (byte) 0x01);
		e4k_reg_write((byte) 0x9f, (byte) 0x7f);
		e4k_reg_write((byte) 0xa0, (byte) 0x07);

		return 0;
	}
}
