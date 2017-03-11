package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Param;
import com.nestorrente.jitl.annotation.Params;

@BaseClasspath("com/nestorrente/jitl/html_views/")
public interface Html {

	@ClasspathTemplate("index")
	@Params({ "title", "place" })
	String welcome(String arg0, String arg1);

	String header(@Param("text") String arg);

	String notFound();

}
