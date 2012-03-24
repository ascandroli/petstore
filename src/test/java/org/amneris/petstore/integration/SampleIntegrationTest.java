package org.amneris.petstore.integration;

import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.WebClientUtils;

import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.server.web.WebApp;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

public class SampleIntegrationTest extends AbstractContainerTest
{
	@Test
	public void assertCorrectTitle() throws Exception
	{
		//Set the correctly used port
		String url=BASEURI.replaceAll(":([0-9]){4}/*", ":8080/").concat("petstore");
		final HtmlPage homePage = webClient.getPage(url);
		Assert.assertEquals(homePage.getTitleText(), "Tinamo!");
		
	}
	
	
}