package org.amneris.petstore.mixins;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.MixinAfter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;

import java.util.regex.Pattern;

@MixinAfter
public class Nav
{
	@Inject
	private ComponentClassResolver resolver;

	@Inject
	private Request request;

	@Inject
	private RequestGlobals requestGlobals;

	/**
	 * An string or a class representing the page.
	 */
	@Parameter(required = false, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	private Object page;

	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
	private String expression;

	@Parameter(required = false, defaultPrefix = BindingConstants.LITERAL, value = "false")
	private boolean skipPageMatch;

	@Parameter(name = "class", required = true, allowNull = false, defaultPrefix = BindingConstants.LITERAL)
	private String className;

	private Pattern pattern;

	private boolean hasExpression;

	@SetupRender
	void setup()
	{
		if (StringUtils.isNotBlank(expression))
		{
			pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			hasExpression = true;
		} else
		{
			hasExpression = false;
		}
	}

	@BeginRender
	void render(MarkupWriter writer)
	{
		if (isSelected())
		{
			writer.attributes("class", className);
		}
	}

	private boolean isSelected()
	{

		boolean selected = false;

		if (page != null && !skipPageMatch)
		{
			String canonicalized = resolver.canonicalizePageName(getPageName());
			selected = canonicalized.equals(requestGlobals.getActivePageName());
		}

		if (!selected && hasExpression)
		{
			final String path = request.getPath();
			selected = pattern.matcher(path).matches();
		}

		return selected;
	}

	private String getPageName()
	{
		if (page instanceof String)
		{
			return (String) page;
		} else if (page instanceof Class)
		{
			return resolver.resolvePageClassNameToPageName(((Class) page).getName());
		}
		return "";
	}
}
