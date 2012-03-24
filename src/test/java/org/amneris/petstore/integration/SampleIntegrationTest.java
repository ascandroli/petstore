package org.amneris.petstore.integration;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;
import com.gargoylesoftware.htmlunit.util.WebClientUtils;

import org.amneris.petstore.util.PetstoreServer;
import org.apache.tapestry5.internal.services.RequestGlobalsImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.h2.server.web.WebApp;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

public class SampleIntegrationTest extends AbstractContainerTest
{
	private String url=PetstoreServer.BASEURL+":"+PetstoreServer.PORT;
	
	@BeforeMethod 
	public void setUp() throws Exception {
		PetstoreServer.startServer();
	}

	@AfterMethod 
	public void setDown() throws Exception {
		PetstoreServer.stopServer();
	}

		 
	@Test
	public void assertCorrectTitleSigninPage() throws Exception
	{		
		final HtmlPage homePage = webClient.getPage(url);
		Assert.assertEquals(homePage.getTitleText(), "signin");	
	}
	
	@Test
	public void assertCorrectLogin() throws Exception
	{
		//Set the correctly used port
		final HtmlPage homePage = webClient.getPage(url);
		
	    homePage.getElementById("jsecLogin").setNodeValue("admin");
	    homePage.getElementById("jsecPassword").setNodeValue("admin");

	    try {
	    	homePage.getElementByName("login").click();
	    } catch (Exception e){
	    	System.out.println("Error submitting: " + e.toString());
	    }
	    wait(45);
	    Assert.assertEquals(homePage.getTitleText(), "Tynamo!");	
	}

}