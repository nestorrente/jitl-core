package com.nestorrente.jitl.util;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {

	/**
	 * Finds and replaces matches from the {@code input} string using the {@code regex} regular expression and the {@code converter} replacement function.
	 *
	 * @param input     input string.
	 * @param regex     regular expression that must be used in order to find matches.
	 * @param converter A {@code match -> replacement} function.
	 * @return the resultant string
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
	 * Finds and replaces matches from the {@code input} string using the {@code regex} regular expression and the {@code converter} replacement function.
	 *
	 * @param input     input string.
	 * @param regex     regular expression that must be used in order to find matches.
	 * @param converter A {@code (match, backreferences) -> replacement} function. {@code backreferences} array contains {@code match} in its 0-index.
	 * @return the resultant string
	 */
	public static String replace(String input, Pattern regex, BiFunction<String, String[], String> converter) {

		Matcher matcher = regex.matcher(input);

		StringBuilder sb = new StringBuilder();

		int lastPosition = 0;

		while(matcher.find()) {

			sb.append(input.substring(lastPosition, matcher.start()));
			sb.append(converter.apply(matcher.group(), getMatchBackReferences(matcher)));

			lastPosition = matcher.end();

		}

		sb.append(input.substring(lastPosition));

		return sb.toString();

	}

	private static String[] getMatchBackReferences(Matcher matcher) {

		String[] backreferences = new String[matcher.groupCount() + 1];

		for(int i = 0; i < backreferences.length; ++i) {
			backreferences[i] = matcher.group(i);
		}

		return backreferences;

	}

}
