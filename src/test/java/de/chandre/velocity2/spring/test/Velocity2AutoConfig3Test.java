package de.chandre.velocity2.spring.test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import de.chandre.velocity2.spring.app.TestApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=TestApplication.class)
@TestPropertySource(properties = { 
		"velocity.enabled=true",
		"velocity.toolbox.enabled=true"
		})
public class Velocity2AutoConfig3Test {
	
	private static final Log LOGGER = LogFactory.getLog(Velocity2AutoConfig3Test.class);
	
	@Autowired
	private VelocityEngine velocityEngine;
	
	@Autowired
	private ToolManager toolManager;
	
	@Test
	public void startEnvironment_test() throws IOException {
		assertNotNull(velocityEngine);
		assertNotNull(toolManager);
	
		ToolContext context = toolManager.createContext();
		context.put("foo", "World");
		StringWriter sw = new StringWriter();
		boolean eval = velocityEngine.evaluate(context, sw, "unitTest1", "Hello $foo. $esc.html('This > was escaped &')");
		
		assertTrue("String not evaluated", eval);
		String result = sw.toString();
		LOGGER.info("evaluated: " + result);
		assertThat(result, is(equalTo("Hello World. This &gt; was escaped &amp;")));
		
		sw.close();
	}

}
