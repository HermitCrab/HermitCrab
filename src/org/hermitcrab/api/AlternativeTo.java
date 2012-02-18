package org.hermitcrab.api;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.hermitcrab.entity.Software;

import com.google.gson.Gson;

import android.util.Log;

public class AlternativeTo {
	public static final String PLATFORM_ANDROID = "android";
	public static final String PLATFORM_IPAD = "ipad";
	public static final String PLATFORM_WIN = "windows";
	public static final String PLATFORM_MAC = "mac";
	public static final String PLATFORM_IPHONE = "iphone";
	public static final String PLATFORM_LINUX = "linux";
	public static final String PLATFORM_WINPHONE = "windows-phone";
	public static final String PLATFORM_ONLINE = "online";

	private static final String API_PREFIX = "http://api.alternativeto.net/";
	private static final String LOG_TAG = "AlternativeToAPI";
	private static final String API_SEARCH = "search/software/";
	private static final String API_ALTERNATIVE = "software/";

	private static Gson mGson = new Gson();

	private static String join(String[] strings, String delimit) {
		StringBuilder builder = new StringBuilder();

		if (strings != null && strings.length > 0) {
			for (String p : strings) {
				builder.append(p);
				builder.append(delimit);
			}
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.toString();
	}

	public static Software alternative(String name, int count,
			String[] platforms) {
		StringBuilder builder = new StringBuilder();
		builder.append(API_PREFIX);
		builder.append(API_ALTERNATIVE);
		builder.append(name);

		if (count > 0 || (platforms != null && platforms.length > 0)) {
			builder.append("?");

			if (count > 0) {
				builder.append("count=");
				builder.append(count);
			}

			if (platforms != null && platforms.length > 0) {
				builder.append("&platform=");
				builder.append(join(platforms, "|"));
			}
		}

		try {
			URL url = new URL(builder.toString());
			builder = new StringBuilder();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			conn.disconnect();
			Software sw = mGson.fromJson(builder.toString(), Software.class);
			return sw;
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return null;
	}

	public static Software[] search(String keyword, String[] platforms) {
		StringBuilder builder = new StringBuilder();
		builder.append(API_PREFIX);
		builder.append(API_SEARCH);
		builder.append(keyword);

		if (platforms != null && platforms.length > 0) {
			builder.append("?platform=");
			builder.append(join(platforms, "|"));
		}

		try {
			URL url = new URL(builder.toString());
			builder = new StringBuilder();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream in = new BufferedInputStream(conn.getInputStream());
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			conn.disconnect();
			Software[] apps = mGson.fromJson(builder.toString(),
					Software[].class);
			return apps;
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(LOG_TAG, e.getMessage());
		}
		return new Software[0];
	}
}
