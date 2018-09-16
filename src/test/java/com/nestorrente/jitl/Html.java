package com.nestorrente.jitl;

import com.nestorrente.jitl.annotation.BaseClasspath;
import com.nestorrente.jitl.annotation.ClasspathTemplate;
import com.nestorrente.jitl.annotation.Encoding;
import com.nestorrente.jitl.annotation.param.ParamName;
import com.nestorrente.jitl.annotation.param.ParamNames;

@BaseClasspath("com/nestorrente/jitl/html_views/")
@Encoding("UTF-8")
public interface Html {

	@ClasspathTemplate("index")
	@ParamNames({ "title", "place" })
	String welcome(String arg0, String arg1);

	String header(@ParamName("text") String arg);

	@ClasspathTemplate
	String footer();

	String notFound();

}
