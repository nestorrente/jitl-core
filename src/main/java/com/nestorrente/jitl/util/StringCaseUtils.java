package com.nestorrente.jitl.util;

import java.util.function.Function;
import java.util.regex.Pattern;

public class StringCaseUtils {

	// Busca un grupo de mayúsculas juntas, y captura dicho grupo sin incluir las letras de los extremos. Ejemplo: en "SQLException" encuentra "SQLE" y captura "QL".
	private static final Pattern UPPERCASE_LETTERS_GROUP_BETWEEN_UPERCASE_LETTERS_PATTERN = Pattern.compile("(?<=[A-Z])[A-Z]+(?=[A-Z])");

	// Captura las regiones de "separación" de una cadena en formato CamelCase. Es decir, el "hueco" que hay entre una minúscula y una mayúscula.
	private static final Pattern CAMMEL_CASE_SEPARATOR_PATTERN = Pattern.compile("(?<=[a-z])(?=[A-Z])");

	public static String camelToLowerUnderscore(String camel) {

		// MySQLQuery -> MySqlQuery
		String capitalizedCamel = PatternUtils.replace(camel, UPPERCASE_LETTERS_GROUP_BETWEEN_UPERCASE_LETTERS_PATTERN, (Function<String, String>) String::toLowerCase);

		// MySqlQuery -> My_Sql_Query
		String capitalizedUnderscore = CAMMEL_CASE_SEPARATOR_PATTERN.matcher(capitalizedCamel).replaceAll("_");

		// My_Sql_Query -> my_sql_query
		return capitalizedUnderscore.toLowerCase();

	}

}
