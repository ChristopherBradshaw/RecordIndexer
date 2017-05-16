package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.facade.ServerFacade;
import shared.communication.ValidateUserInput;
import shared.communication.ValidateUserOutput;
import shared.model.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ValidateUserHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());	

	@SuppressWarnings("restriction")
	@Override
	public void handle(HttpExchange exchange) throws IOException 
	{
		ValidateUserInput params = (ValidateUserInput)xmlStream.fromXML(exchange.getRequestBody());
		User tmpUser = null;
		
		try
		{
			tmpUser = ServerFacade.isValidUser(params.getUserName(), params.getPassword());
		}
		catch (ServerException e) 
		{
            logger.log(Level.SEVERE, e.getMessage(), e);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		
		ValidateUserOutput result = new ValidateUserOutput((tmpUser != null),tmpUser);
		
		if(result.isValid())
		{
			logger.info("Successfully validated: " + result.getUser().getUsername() + "(" + result.getUser().getFirstName() + " "  + result.getUser().getLastName() + ")");
		}
		else
		{
			logger.info("Failed to validate user");
		}
		
		xmlStream.toXML(result,exchange.getResponseBody());
		exchange.getResponseBody().close();
		
	}
}
