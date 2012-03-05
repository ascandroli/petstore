package org.amneris.petstore.integration;

import java.io.IOException;
import java.net.MalformedURLException;

import org.testng.annotations.Test;
import static org.testng.Assert.*;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.tynamo.test.AbstractContainerTest;

public class SampleIntegrationTest extends AbstractContainerTest
{
	@Test
	public void assertCorrectTitle() throws Exception
	{
    final HtmlPage homePage = webClient.getPage(BASEURI);
		assertEquals("Tynamo!", homePage.getTitleText());


	}
}