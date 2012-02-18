package org.hermitcrab.util;

import java.io.Closeable;
import java.util.UUID;

public class FileUtils {

	public static void closeSilently(Closeable c) {
		if (c == null) {
			return;
		}
		try {
			c.close();
		} catch (Throwable t) {
			// do nothing
		}
	}

	public static String makeUUID(String input) {
		UUID uuid = new UUID(input.hashCode(), input.hashCode());
		return uuid.toString();
	}

}
