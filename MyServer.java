// Set up the webserver, install the servlet, and start the server.

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.thread.*;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.handler.*;




public class MyServer {

    public static void main (String[] argv ) 
    {
	try {
	    Server server = new Server (40109);   // Port# 

	    //NOTE: change the above port number to your assigned port #

	    // CAN SKIP READING FROM HERE -----------------
	    ResourceHandler rHandler = new ResourceHandler();
	    rHandler.setResourceBase(".");                     // Current dir.
	    ContextHandler cHandler = new ContextHandler("/"); // Generic URL
	    cHandler.setHandler(rHandler);
	    // Now the servlets: in this case just one.
	    ServletContextHandler sHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            // ----------------TO HERE (It's all boilerplate).

	    // We want the form URL /passenger to go to PassengerServlet
	    sHandler.addServlet(new ServletHolder(new HomePageServlet()),"/shopping");

	    // An example of adding another servlet for another URL:
	    // sHandler.addServlet(new ServletHolder(new AdminServlet()),"/admin");

	    // AND SKIP READING FROM HERE ----------------------------
	    // Put all of these "handlers" into the server.
	    ContextHandlerCollection contexts = new ContextHandlerCollection();
	    contexts.setHandlers(new Handler[] { cHandler, sHandler });
	    server.setHandler(contexts);
	    // -------------------------------------------------TO HERE.
	    
	    // Start the server.
	    server.start();
	    System.out.println ("Webserver started, ready for browser connections");
	    server.join();
	}
	catch (Exception e) {
	    // We really don't want to reach here.
	    e.printStackTrace();
	}
    }

}
