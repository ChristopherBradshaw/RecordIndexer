package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.database.Database;
import server.database.DatabaseException;
import server.facade.ServerFacade;
import shared.communication.DownloadBatchOutput;
import shared.communication.SubmitBatchInput;
import shared.model.Batch;
import shared.model.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("restriction")
public class SubmitBatchHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());

	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		SubmitBatchInput params = (SubmitBatchInput)xmlStream.fromXML(exchange.getRequestBody());
		User tmpUser = null;
		boolean result = false;
		try 
		{
			tmpUser = ServerFacade.isValidUser(params.getValidateUser().getUserName(), params.getValidateUser().getPassword());
			if(tmpUser != null && tmpUser.getCurrentBatchID() != -1 && tmpUser.getCurrentBatchID() == params.getBatch())	//If valid login, has assigned batch, and is only submitting their own batch
			{
				result = ServerFacade.submitBatch(params.getValues(),params.getBatch());
				tmpUser.setCurrentBatchID(-1);
				tmpUser.setCompletedBatches(tmpUser.getCompletedBatches()+(params.getValues().size()));
				ServerFacade.updateUser(tmpUser);
			}
		}
		catch (ServerException e) 
		{
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}

		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		
		xmlStream.toXML(result,exchange.getResponseBody());
		exchange.getResponseBody().close();
	}
}
