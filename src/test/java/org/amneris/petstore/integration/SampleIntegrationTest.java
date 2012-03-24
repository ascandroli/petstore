package org.amneris.petstore.integration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
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
	
	@BeforeClass 
	public void setUp() throws Exception {
		PetstoreServer.startServer();
	}

	@AfterClass 
	public void setDown() throws Exception {
		PetstoreServer.stopServer();
	}
		 
	@Test
	public void assertCorrectLogin() throws Exception
	{
		//Set the correctly used port
		final HtmlPage loginPage = webClient.getPage(url);
		
	    final HtmlForm form = loginPage.getForms().get(0);

	    final HtmlTextInput userTextField = form.getInputByName("jsecLogin");
	    final HtmlPasswordInput passwordTextField = form.getInputByName("jsecPassword");
	    
	    final HtmlSubmitInput button = form.getInputByName("login");

		// Change the value of the text field
		userTextField.setValueAttribute("admin");
		passwordTextField.setValueAttribute("admin");
		    
		HtmlPage homePage=null;
	    try {
	    	 homePage = button.click();
	    } catch (Exception e){
	    	System.out.println("Error submitting: " + e.toString());
	    }

	    Assert.assertEquals(homePage.getTitleText(), "Tynamo!");
	    webClient.closeAllWindows();
	}
	
	@Test
	public void assertCorrectTitleHomePage() throws Exception
	{		
		final HtmlPage homePage = webClient.getPage(url);
		Assert.assertEquals(homePage.getTitleText(), "Tynamo!");
		webClient.closeAllWindows();
	}

}