package com.nestorrente.jitl.param.provider;

import com.nestorrente.jitl.param.ParamProvider;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CurrentUTCTimeParamProvider implements ParamProvider {

	private static final String UTC_TIMEZONE_ID = "UTC";

	@Override
	public Map<String, Object> params() {

		Map<String, Object> params = new HashMap<>();

		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(UTC_TIMEZONE_ID));

		params.put("year", calendar.get(Calendar.YEAR));
		params.put("month", calendar.get(Calendar.MONTH));
		params.put("day", calendar.get(Calendar.DAY_OF_MONTH));
		params.put("hours", calendar.get(Calendar.HOUR_OF_DAY));
		params.put("minutes", calendar.get(Calendar.MINUTE));
		params.put("seconds", calendar.get(Calendar.SECOND));
		params.put("millis", calendar.get(Calendar.MILLISECOND));

		return params;

	}

}
