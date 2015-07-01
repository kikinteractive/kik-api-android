package com.kik.kikapi;

import android.content.Context;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * Created by gabrielgrant on 15-07-01.
 */
public class KikVideoMessage extends KikMessage
{
    protected String _videoUrl;
    protected boolean _shouldAutoplay = false;
    protected boolean _shouldBeMuted = false;
    protected boolean _shouldLoop = false;

    /**
     * Set whether the KikVideoMessage should autoplay. Default value is false.
     *
     * @param shouldAutoplay <code>true</code> to enable the video autoplaying
     * @return The current instance of KikMessage with the autoplay property set
     */
    public KikMessage setShouldAutoplay(boolean shouldAutoplay)
    {
        _shouldAutoplay = shouldAutoplay;
        return this;
    }

    /**
     * Set whether the KikVideoMessage should be muted. Default value is false.
     *
     * @param shouldMute <code>true</code> to disable the video's audio during playback
     * @return The current instance of KikMessage with the mute property set
     */
    public KikMessage setShouldBeMuted(boolean shouldMute)
    {
        _shouldBeMuted = shouldMute;
        return this;
    }

    /**
     * Set whether the KikVideoMessage should loop. Default value is false.
     *
     * @param shouldLoop <code>true</code> to enable the video looping
     * @return The current instance of KikMessage with the looping property set
     */
    public KikMessage setShouldLoop(boolean shouldLoop)
    {
        _shouldLoop = shouldLoop;
        return this;
    }

    /**
     * A KikVideoMessage requires a video URL and preview URL. Use KikImageUtil.createImageURL to
     * ensure a preview URL is provided.
     *
     * @param context non-null Android context
     * @param videoUrl a valid URL to a video
     * @param previewUrl a valid URL to an image or a data URI representation of an image to provide a thumbnail preview that will be displayed in your message
     *            This should be the first frame of the video.
     */
    public KikVideoMessage(Context context, String videoUrl, String previewUrl) throws IllegalArgumentException
    {
        super(context);
        if (videoUrl == null || previewUrl == null) {
            throw new IllegalArgumentException("The videoUrl and previewUrl must be non-null values.");
        }
        _videoUrl = videoUrl;
        _previewUrl = previewUrl;
    }

    @Override
    protected List<NameValuePair> getNameValuePairs()
    {
        List<NameValuePair> nameValuePairs = super.getNameValuePairs();

        if (isValueSet(_videoUrl)) {
            BasicNameValuePair videoPair = new BasicNameValuePair("video_url", _videoUrl);
            nameValuePairs.add(videoPair);
        }

        BasicNameValuePair autoplayPair = new BasicNameValuePair("video_should_autoplay", booleanToString(_shouldAutoplay));
        nameValuePairs.add(autoplayPair);

        BasicNameValuePair mutePair = new BasicNameValuePair("video_should_be_muted", booleanToString(_shouldBeMuted));
        nameValuePairs.add(mutePair);

        BasicNameValuePair loopPair = new BasicNameValuePair("video_should_loop", booleanToString(_shouldLoop));
        nameValuePairs.add(loopPair);

        return nameValuePairs;
    }

    @Override
    protected String getMessageType()
    {
        return "video";
    }

}
