package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.facade.ServerFacade;
import shared.communication.GetFieldsInput;
import shared.communication.GetFieldsOutput;
import shared.communication.GetProjectsOutput;
import shared.model.Field;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("restriction")
public class GetFieldsHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		GetFieldsInput params = (GetFieldsInput)xmlStream.fromXML(exchange.getRequestBody());
		GetFieldsOutput result = null;

		try {
			
			if(params.getBatchInput() == null)	//No specific project
			{
				if(ServerFacade.isValidUser(params.getValidateUser().getUserName(), params.getValidateUser().getPassword()) == null)	
				{
					result = null;	//Invalid login
				}
				else
				{
					result = new GetFieldsOutput(ServerFacade.getFields(-1));	//Valid login
				}
			}
			else		//Specific project
			{
				if(ServerFacade.isValidUser(params.getBatchInput().getValidateUser().getUserName(), params.getBatchInput().getValidateUser().getPassword()) == null)
				{
					result = null;	//Invalid login
				}
				else
				{
					result = new GetFieldsOutput(ServerFacade.getFields(params.getBatchInput().getProjectId()));	//Valid login
				}
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
