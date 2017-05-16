package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.facade.ServerFacade;
import shared.communication.GetBatchInput;
import shared.communication.ValidateUserOutput;
import shared.model.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class GetSampleImageHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());	

	@SuppressWarnings("restriction")
	@Override
	public void handle(HttpExchange exchange) throws IOException 
	{

		GetBatchInput params = (GetBatchInput)xmlStream.fromXML(exchange.getRequestBody());
		User tmpUser = null;
		String result = null;
		try {
			tmpUser = ServerFacade.isValidUser(params.getValidateUser().getUserName(), params.getValidateUser().getPassword());
			if(tmpUser != null)
			{
				result = ServerFacade.getSampleBatch(params.getProjectId());	//Valid login
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
