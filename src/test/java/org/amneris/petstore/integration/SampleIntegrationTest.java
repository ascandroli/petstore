package org.amneris.petstore.integration;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

public class SampleIntegrationTest extends AbstractContainerTest
{
	@Test
	public void assertCorrectTitle() throws Exception
	{
		final HtmlPage homePage = webClient.getPage(BASEURI);
		Assert.assertEquals(homePage.getTitleText(), "Tynamo!");
	}
}