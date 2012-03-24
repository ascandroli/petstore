package org.amneris.petstore.util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class PetstoreServer {
	
	private static Server server = new Server();;
	
	public static String BASEURL="http://localhost";
	public static int PORT=8091;
	
	public static Server startServer() throws Exception {
		server = new Server(PORT);
	    
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("src/main/webapp");

		server.setHandler(webapp);
		server.start();
		
		return server;
	}
	
	public static void stopServer() throws Exception {
		server.stop();
	}
}
