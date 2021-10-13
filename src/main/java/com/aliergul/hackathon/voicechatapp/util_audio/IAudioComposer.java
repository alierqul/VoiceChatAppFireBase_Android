package com.aliergul.hackathon.voicechatapp.util_audio;

public interface IAudioComposer {
    void setup();

    boolean stepPipeline();

    long getWrittenPresentationTimeUs();

    boolean isFinished();

    void release();
}
