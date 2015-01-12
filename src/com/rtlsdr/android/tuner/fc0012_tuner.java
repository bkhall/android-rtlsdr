package com.rtlsdr.android.tuner;

import java.io.IOException;

public class fc0012_tuner implements RtlSdr_tuner_iface {
	private static final String TAG = fc0012_tuner.class.getSimpleName();
	@Override
	public int init(int param) throws IOException {
		// TODO Auto-generated method stub
		return 0;
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

}
