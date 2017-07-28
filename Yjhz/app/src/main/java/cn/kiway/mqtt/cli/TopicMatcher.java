package cn.kiway.mqtt.cli;

import java.io.UnsupportedEncodingException;

public class TopicMatcher {
	private static final String SEPARATOR = "/";
	private static final String MULTI_LEVEL_WILDCARD = "#";
	private static final String SINGLE_LEVEL_WILDCARD = "+";

	private static final int MIN_LENGTH = 1;
	private static final int MAX_LENGTH = 65535;
	private static final char NULL = '\u0000';

	private static final int INDEX_NOT_FOUND = -1;

	public static final String MULTI_LEVEL_WILDCARD_PATTERN = SEPARATOR + MULTI_LEVEL_WILDCARD;
	public static final String TOPIC_WILDCARDS = MULTI_LEVEL_WILDCARD + SINGLE_LEVEL_WILDCARD;

	public static boolean isValid(String topicFilter, boolean allowWildcard) {
		if (topicFilter == null) { return false; }

		int length = 0;
		try {
			length = topicFilter.getBytes("UTF-8").length;
		}
		catch (UnsupportedEncodingException e) {
			
			return false;
		}

		if (length < MIN_LENGTH || length > MAX_LENGTH) {
			
			return false;
		}

		if (allowWildcard) {
			if (topicFilter.equals(MULTI_LEVEL_WILDCARD) || topicFilter.equals(SINGLE_LEVEL_WILDCARD)) { return true; }

			if (countMatches(topicFilter, MULTI_LEVEL_WILDCARD) > 1 || (topicFilter.contains(MULTI_LEVEL_WILDCARD)
					&& !topicFilter.endsWith(MULTI_LEVEL_WILDCARD_PATTERN))) {
				
				return false;
			}
		}

		return isValidSingleLevelWildcard(topicFilter);
	}

	private static int countMatches(String str, String sub) {
		if (str==null ||"".equals(str) || sub==null || "".equals(sub)) { return 0; }

		int count = 0;
		int idx = 0;
		while ((idx = str.indexOf(sub, idx)) != INDEX_NOT_FOUND) {
			count++;
			idx += sub.length();
		}

		return count;
	}

	private static boolean isValidSingleLevelWildcard(String topicString) {
		char singleLevelWildcardChar = SINGLE_LEVEL_WILDCARD.charAt(0);
		char topicLevelSeparatorChar = SEPARATOR.charAt(0);

		char[] chars = topicString.toCharArray();
		int length = chars.length;
		char prev = NULL;
		char next = NULL;

		for (int i = 0; i < length; i++) {
			prev = (i - 1 >= 0) ? chars[i - 1] : NULL;
			next = (i + 1 < length) ? chars[i + 1] : NULL;

			if ((chars[i] == singleLevelWildcardChar) && (prev != topicLevelSeparatorChar && prev != NULL
					|| next != topicLevelSeparatorChar && next != NULL)) {
				
				return false;
			}
		}

		return true;
	}

	public static boolean match(String topicFilter, String topicName) {
		if (!TopicMatcher.isValid(topicFilter, true) || !TopicMatcher.isValid(topicName, false)) { return false; }

		if ((topicFilter.startsWith(MULTI_LEVEL_WILDCARD) || topicFilter.startsWith(SINGLE_LEVEL_WILDCARD))
				&& topicName.startsWith("$")) { return false; } // [MQTT-4.7.2-1]

		String[] tfs = topicFilter.split("/");
		String[] tns = topicName.split("/");

		for (int i = 0; i < tns.length+1; ++i) {
			if(i==tfs.length && i== tns.length){
				return true;
			}
			if (i >= tfs.length) {
				return false;
			}
			else if (tfs[i].equals(MULTI_LEVEL_WILDCARD)) {
				return true;
			}
			else if (tfs[i].equals(SINGLE_LEVEL_WILDCARD)) {
				continue;
			}
			if (i >= tns.length) {
				return false;
			}
			if (tfs[i].equals(tns[i])) {
				continue;
			}
			else {
				return false;
			}
		}

		return true;
	}
}