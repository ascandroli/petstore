package org.amneris.petstore.services;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.TapestryConstants;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Advise;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Primary;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.plastic.MethodAdvice;
import org.apache.tapestry5.plastic.MethodInvocation;
import org.apache.tapestry5.services.*;

import java.io.IOException;

public class RoutingHacksModule {

	private static final EventContext EMPTY_CONTEXT = new EmptyEventContext();

	@Advise(serviceInterface = ComponentEventLinkEncoder.class)
	public static void noIndexPages(MethodAdviceReceiver receiver) throws NoSuchMethodException {
		receiver.adviseMethod(ComponentEventLinkEncoder.class.getMethod("decodePageRenderRequest", Request.class), new MethodAdvice() {
			@Override
			public void advise(MethodInvocation methodInvocation) {
				methodInvocation.proceed();
				PageRenderRequestParameters parameters = (PageRenderRequestParameters) methodInvocation.getReturnValue();
				if (parameters != null && parameters.getLogicalPageName().toLowerCase().endsWith("index"))
					methodInvocation.setReturnValue(null);
			}
		});
	}

	@Contribute(Dispatcher.class)
	@Primary
	public static void setupCustomDispatcher(OrderedConfiguration<Dispatcher> configuration,
	                                         final ComponentRequestHandler componentRequestHandler,
	                                         final ComponentClassResolver componentClassResolver,
	                                         final @Symbol(SymbolConstants.APPLICATION_FOLDER) String applicationFolder) {
		configuration.add("MyRootDispatcher", new Dispatcher() {
			@Override
			public boolean dispatch(Request request, Response response) throws IOException {

				// The extended name may include a page activation context. The trick is
				// to figure out where the logical page name stops and where the
				// activation context begins. Further, strip out the leading slash.

				boolean hasAppFolder = applicationFolder.equals("");
				String applicationFolderPrefix = hasAppFolder ? null : "/" + applicationFolder;

				String path = request.getPath();

				if (applicationFolderPrefix != null) {
					int prefixLength = applicationFolderPrefix.length();

					assert path.substring(0, prefixLength).equalsIgnoreCase(applicationFolderPrefix);

					// This checks that the character after the prefix is a slash ... the extra complexity
					// only seems to occur in Selenium. There's some ambiguity about what to do with a request for
					// the application folder that doesn't end with a slash. Manuyal with Chrome and IE 8 shows that such
					// requests are passed through with a training slash,  automated testing with Selenium and FireFox
					// can include requests for the folder without the trailing slash.

					assert path.length() <= prefixLength || path.charAt(prefixLength) == '/';

					// Strip off the folder prefix (i.e., "/foldername"), leaving the rest of the path (i.e., "/en/pagename").

					path = path.substring(prefixLength);
				}


				// TAPESTRY-1343: Sometimes path is the empty string (it should always be at least a slash,
				// but Tomcat may return the empty string for a root context request).

				String extendedName = path.length() == 0 ? path : path.substring(1);

				// Ignore trailing slashes in the path.
				while (extendedName.endsWith("/")) {
					extendedName = extendedName.substring(0, extendedName.length() - 1);
				}

				if (componentClassResolver.isPageName(extendedName)) {
					String canonicalized = componentClassResolver.canonicalizePageName(extendedName);

					boolean loopback = request.getParameter(TapestryConstants.PAGE_LOOPBACK_PARAMETER_NAME) != null;

					PageRenderRequestParameters parameters = new PageRenderRequestParameters(canonicalized, EMPTY_CONTEXT, loopback);

					componentRequestHandler.handlePageRender(parameters);
					return true;
				}

				return false;
			}
		}, "after:*");
	}

}
