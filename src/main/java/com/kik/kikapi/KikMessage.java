package com.kik.kikapi;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by costa on 2014-08-29.
 */
public abstract class KikMessage
{
    public static enum KikMessagePlatform {
        KIK_MESSAGE_PLATFORM_GENERIC(""),
        KIK_MESSAGE_PLATFORM_IPHONE("iphone"),
        KIK_MESSAGE_PLATFORM_ANDROID("android"),
        KIK_MESSAGE_PLATFORM_CARDS("cards");

        private String _value;

        private KikMessagePlatform(String value)
        {
            this._value = value;
        }
    }

    private static final String KIK_MESSENGER_API_SEND_URL = "kik-share://kik.com/send/";
    private static final int ICON_MAX_SIZE = 48;
    protected String _appName; // Required
    protected String _appPkg; // Required
    protected String _title;
    protected String _text;
    protected boolean _forwardable = true;
    protected ArrayList<String> _Urls = new ArrayList<String>();
    protected String _imageUrl;
    protected String _previewUrl;
    protected String _iconUrl;
    protected boolean _disallowSave = false;

    /**
     * Set whether the KikMessage is forwardable. Default value is true.
     *
     * @param isForwardable <code>false</code> to disable the ability to forward the content message
     * @return The current instance of KikMessage with the forwardable property set
     */
    public KikMessage setForwardable(boolean isForwardable)
    {
        _forwardable = isForwardable;
        return this;
    }

    /**
     * Set whether the user should be disallowed from saving the content. Default value is false.
     * @param disallowSave <code>true</code> to disable the ability to save the content
     * @return the current instance of KikMessage with the disallowSave property set
     */
    public KikMessage setDisallowSave(boolean disallowSave)
    {
        _disallowSave = disallowSave;
        return this;
    }

    protected KikMessage(Context context) throws IllegalArgumentException
    {
        if (context == null) {
            throw new IllegalArgumentException("Context can't be null when creating a KikMessage");
        }

        _appPkg = context.getPackageName();

        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(_appPkg, 0);
        }
        catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        _appName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
        Drawable icon = pm.getApplicationIcon(ai);
        _iconUrl = KikImageUtil.generateEncodedUrlForImage(KikImageUtil.generateScaledBitmap(icon, ICON_MAX_SIZE, ICON_MAX_SIZE));
    }

    /**
     * Add a fallback URL for a specified platform that will be displayed to the user upon opening the KikMessage
     *
     * @param fallbackUrl a valid URL to be displayed to the user
     * @param platform the platform which this fallbackUrl should target
     * @return The current instance of KikMessage with the provided fallbackUrl attached
     */
    public KikMessage addFallbackUrl(String fallbackUrl, KikMessagePlatform platform)
    {
        try {
            new URL(fallbackUrl);
        }
        catch (MalformedURLException e) {
            // do nothing?
        }
        // Only insert the platform if it is defined
        String fallback = (platform != null && !"".equals(platform._value)) ? platform._value + "," : "";
        fallback += fallbackUrl;
        _Urls.add(fallback);
        return this;
    }

    /**
     * @return A valid link representation of the KikMessage
     */
    protected String linkRepresentation()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(KIK_MESSENGER_API_SEND_URL);
        builder.append(getMessageType()).append("?");

        List<NameValuePair> nameValuePairs = getNameValuePairs();

        String httpParams = URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
        builder.append(httpParams);
        return builder.toString();
    }

    /**
     * Returns a list of all name-value pairs, used in link representation
     */
    protected List<NameValuePair> getNameValuePairs()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        BasicNameValuePair appNamePair = new BasicNameValuePair("app_name", _appName);
        nameValuePairs.add(appNamePair);
        BasicNameValuePair appPkgPair = new BasicNameValuePair("app_pkg", _appPkg);
        nameValuePairs.add(appPkgPair);
        if (isValueSet(_title)) {
            BasicNameValuePair titlePair = new BasicNameValuePair("title", _title);
            nameValuePairs.add(titlePair);
        }

        if (isValueSet(_text)) {
            BasicNameValuePair textPair = new BasicNameValuePair("text", _text);
            nameValuePairs.add(textPair);
        }

        BasicNameValuePair forwardablePair = new BasicNameValuePair("forwardable", booleanToString(_forwardable));
        nameValuePairs.add(forwardablePair);

        for (String fallbackUrl : _Urls) {
            BasicNameValuePair urlPair = new BasicNameValuePair("url", fallbackUrl);
            nameValuePairs.add(urlPair);
        }

        if (isValueSet(_imageUrl)) {
            BasicNameValuePair imagePair = new BasicNameValuePair("image_url", _imageUrl);
            nameValuePairs.add(imagePair);
        }

        if (isValueSet(_previewUrl)) {
            BasicNameValuePair previewPair = new BasicNameValuePair("preview_url", _previewUrl);
            nameValuePairs.add(previewPair);
        }

        BasicNameValuePair iconPair = new BasicNameValuePair("icon_url", _iconUrl);
        nameValuePairs.add(iconPair);

        BasicNameValuePair nativePair = new BasicNameValuePair("isNative", "1");
        nameValuePairs.add(nativePair);

        BasicNameValuePair referrerPair = new BasicNameValuePair("referer", _appPkg);
        nameValuePairs.add(referrerPair);

        BasicNameValuePair disallowSavePair = new BasicNameValuePair("disallow_save", booleanToString(_disallowSave));
        nameValuePairs.add(disallowSavePair);

        return nameValuePairs;
    }

    protected String booleanToString(boolean value)
    {
        return value ? "1" : "0";
    }

    protected boolean isValueSet(String value)
    {
        return value != null && !"".equals(value);
    }

    protected abstract String getMessageType();
}
