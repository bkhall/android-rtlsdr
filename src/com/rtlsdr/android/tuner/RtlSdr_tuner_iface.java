package com.rtlsdr.android.tuner;

import java.io.IOException;

public interface RtlSdr_tuner_iface {
    int init(int param )throws IOException;
    int exit(int param )throws IOException;
    int set_freq(int param, long freq /* Hz */)throws IOException;
    int set_bw (int param, int bw /* Hz */)throws IOException;
    int set_gain(int param, int gain /* tenth dB */)throws IOException;
    int set_if_gain(int param, int stage, int gain /* tenth dB */)throws IOException;
    int set_gain_mode(int param, boolean manual) throws IOException;
}

