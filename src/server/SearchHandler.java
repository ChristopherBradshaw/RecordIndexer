package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.facade.ServerFacade;
import shared.communication.GetFieldsOutput;
import shared.communication.SearchInput;
import shared.communication.SearchOutput;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("restriction")
public class SearchHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		SearchInput params = (SearchInput)xmlStream.fromXML(exchange.getRequestBody());
		SearchOutput result = null;
		
		try 
		{
			if(ServerFacade.isValidUser(params.getValidateUser().getUserName(), params.getValidateUser().getPassword()) == null) 
			{
				result = null;	//Invalid login
			}
			else
			{
				result = new SearchOutput(ServerFacade.search(params.getFieldIds(), params.getKeywords()));		//Valid login
			}
		}
		catch (ServerException e) 
		{
            logger.log(Level.SEVERE, e.getMessage(), e);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		
		xmlStream.toXML(result,exchange.getResponseBody());
		exchange.getResponseBody().close();
	}

}
