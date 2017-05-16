package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.facade.ServerFacade;
import shared.communication.GetProjectsOutput;
import shared.communication.ValidateUserInput;
import shared.model.Project;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetProjectsHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());	

	@SuppressWarnings("restriction")
	@Override
	public void handle(HttpExchange exchange) throws IOException 
	{

		ValidateUserInput params = (ValidateUserInput)xmlStream.fromXML(exchange.getRequestBody());
		GetProjectsOutput result = null;
		
		try 
		{
			if(ServerFacade.isValidUser(params.getUserName(), params.getPassword()) != null) 
			{	
				result = new GetProjectsOutput(ServerFacade.getProjects());		//Valid login
			}
		}
		catch (ServerException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		
		
		xmlStream.toXML(result,exchange.getResponseBody());
		exchange.getResponseBody().close();
		
	}
}
