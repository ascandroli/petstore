package org.amneris.petstore.integration;

import com.gargoylesoftware.htmlunit.html.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.tynamo.test.AbstractContainerTest;

public class SampleIntegrationTest extends AbstractContainerTest {

	private static final Logger logger = LoggerFactory.getLogger(SampleIntegrationTest.class);

	@Test
	public void login() throws Exception {
		//Set the correctly used port
		final HtmlPage loginPage = webClient.getPage(BASEURI);

		final HtmlForm form = loginPage.getForms().get(0);

		final HtmlTextInput userTextField = form.getInputByName("jsecLogin");
		final HtmlPasswordInput passwordTextField = form.getInputByName("jsecPassword");

		final HtmlSubmitInput button = form.getInputByName("login");

		// Change the value of the text field
		userTextField.setValueAttribute("admin");
		passwordTextField.setValueAttribute("admin");

		HtmlPage homePage = null;

		try {
			homePage = button.click();
		} catch (Exception e) {
			logger.error("error submitting", e);
		}

		Assert.assertEquals(homePage.getTitleText(), "Tynamo!");
		webClient.closeAllWindows();
	}

	@Test(groups = {"loggedIn"}, dependsOnMethods = {"login"})
	public void check_home_page_title() throws Exception {
		final HtmlPage homePage = webClient.getPage(BASEURI);
		Assert.assertEquals(homePage.getTitleText(), "Tynamo!");
		webClient.closeAllWindows();
	}

}