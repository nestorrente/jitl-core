package com.nestorrente.jitl.util;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

	/**
	 * @param input
	 * @param regex
	 * @param converter
	 *            A {@code match -> replacement} function.
	 * @return
	 */
	public static String replace(String input, Pattern regex, Function<String, String> converter) {

		Matcher matcher = regex.matcher(input);

		StringBuilder sb = new StringBuilder();

		int lastPosition = 0;

		while(matcher.find()) {

			sb.append(input.substring(lastPosition, matcher.start()));
			sb.append(converter.apply(matcher.group()));

			lastPosition = matcher.end();

		}

		sb.append(input.substring(lastPosition));

		return sb.toString();

	}

	/**
	 * @param input
	 * @param regex
	 * @param converter
	 *            A {@code (match, backreferences) -> replacement} function. {@code backreferences} array contains {@code match} in its 0-index.
	 * @return
	 */
	public static String replace(String input, Pattern regex, BiFunction<String, String[], String> converter) {

		Matcher matcher = regex.matcher(input);

		StringBuilder sb = new StringBuilder();

		int lastPosition = 0;

		while(matcher.find()) {

			sb.append(input.substring(lastPosition, matcher.start()));
			sb.append(converter.apply(matcher.group(), getMatchBackreferences(matcher)));

			lastPosition = matcher.end();

		}

		sb.append(input.substring(lastPosition));

		return sb.toString();

	}

	private static String[] getMatchBackreferences(Matcher matcher) {

		String[] backreferences = new String[matcher.groupCount() + 1];

		for(int i = 0; i < backreferences.length; ++i) {
			backreferences[i] = matcher.group(i);
		}

		return backreferences;

	}

}
