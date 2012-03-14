package org.amneris.petstore.pages;

import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.tynamo.security.pages.Login;
import org.tynamo.security.services.PageService;
import org.tynamo.security.services.SecurityService;

import java.io.IOException;
import org.slf4j.Logger;

public class Signin extends Login
{
	@Inject
	private Logger logger;

	@Property
	private String jsecLogin;

	@Property
	private String jsecPassword;

	@Property
	private boolean jsecRememberMe;

	@Persist(PersistenceConstants.FLASH)
	private String loginMessage;

	@Inject
	private Response response;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private SecurityService securityService;

	@Inject
	private PageService pageService;

	public Object onActionFromJsecLoginForm()
	{

		Subject currentUser = securityService.getSubject();

		if (currentUser == null)
		{
			throw new IllegalStateException("Subject can`t be null");
		}

		UsernamePasswordToken token = new UsernamePasswordToken(jsecLogin, jsecPassword);
		token.setRememberMe(jsecRememberMe);

		try
		{
			currentUser.login(token);
		} catch (UnknownAccountException e)
		{
			loginMessage = "Account not exists";
			return null;
		} catch (IncorrectCredentialsException e)
		{
			loginMessage = "Wrong password";
			return null;
		} catch (LockedAccountException e)
		{
			loginMessage = "Account locked";
			return null;
		} catch (AuthenticationException e)
		{
			loginMessage = "Authentication Error";
			return null;
		}

		SavedRequest savedRequest = WebUtils.getAndClearSavedRequest(requestGlobals.getHTTPServletRequest());

		// TODO: try using shiro's own WebUtils.redirectToSavedRequest
		if (savedRequest != null && savedRequest.getMethod().equalsIgnoreCase("GET"))
		{
			try
			{
				response.sendRedirect(savedRequest.getRequestUrl());
				return null;
			} catch (IOException e)
			{
				logger.warn("Can't redirect to saved request.");
				return pageService.getSuccessPage();
			}
		} else
		{
			return pageService.getSuccessPage();
		}

	}

	public String getLoginMessage()
	{
		if (hasLoginMessage())
		{
			return loginMessage;
		} else
		{
			return " ";
		}
	}

	public boolean hasLoginMessage()
	{
		return StringUtils.hasText(loginMessage);
	}
}
