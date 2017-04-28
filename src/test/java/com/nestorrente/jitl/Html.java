package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;
import com.nestorrente.jitl.annotation.Param;
import com.nestorrente.jitl.annotation.Params;

@BaseClasspath("com/nestorrente/jitl/html_views/")
@Encoding("UTF-8")
public interface Html {

	@ClasspathTemplate("index")
	@Params({ "title", "place" })
	String welcome(String arg0, String arg1);

	String header(@Param("text") String arg);

	@ClasspathTemplate
	String footer();

	String notFound();

}
