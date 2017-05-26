package com.nestorrente.jitl.util;

import java.util.StringJoiner;
import java.util.regex.Pattern;

public class StringUtils {

	// Busca un grupo de mayúsculas juntas, y captura dicho grupo sin incluir las letras de los extremos. Ejemplo: en "SQLException" encuentra "SQLE" y captura "QL".
	private static final Pattern UPPERCASE_LETTERS_GROUP_BETWEEN_UPERCASE_LETTERS_PATTERN = Pattern.compile("(?<=[A-Z])[A-Z]+(?=[A-Z])");

	// Captura las regiones de "separación" de una cadena en formato CamelCase. Es decir, el "hueco" que hay entre una minúscula y una mayúscula.
	private static final Pattern CAMMEL_CASE_SEPARATOR_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");

	public static String camelToLowerUnderscore(String camel) {

		// MySQLQuery -> MySqlQuery
		// FIXME for some unknown reason, passing String::toLowerCase to PatternUtils.replace(...) is "ambiguous" for Maven compiler
		// String result = PatternUtils.replace(camel, UPPERCASE_LETTERS_GROUP_BETWEEN_UPERCASE_LETTERS_PATTERN, String::toLowerCase);
		String result = PatternUtils.replace(camel, UPPERCASE_LETTERS_GROUP_BETWEEN_UPERCASE_LETTERS_PATTERN, str -> str.toLowerCase());

		// MySqlQuery -> My_Sql_Query
		result = CAMMEL_CASE_SEPARATOR_PATTERN.matcher(result).replaceAll("_");

		// My_Sql_Query -> my_sql_query
		return result.toLowerCase();

	}

	public static String joinRepeating(String element, String delimiter, int times) {

		StringJoiner joiner = new StringJoiner(delimiter);

		while(times-- > 0) {
			joiner.add(element);
		}

		return joiner.toString();

	}

}
