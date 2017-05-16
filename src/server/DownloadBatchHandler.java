package server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import server.database.Database;
import server.database.DatabaseException;
import server.facade.ServerFacade;
import shared.communication.DownloadBatchOutput;
import shared.communication.GetBatchInput;
import shared.model.Batch;
import shared.model.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("restriction")
public class DownloadBatchHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer"); 
	
	private XStream xmlStream = new XStream(new DomDriver());

	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		GetBatchInput params = (GetBatchInput)xmlStream.fromXML(exchange.getRequestBody());
		User tmpUser = null;
		DownloadBatchOutput result = null;
		 
		try {
			tmpUser = ServerFacade.isValidUser(params.getValidateUser().getUserName(), params.getValidateUser().getPassword());
		
			if(tmpUser != null && tmpUser.getCurrentBatchID() == -1)
			{
				int projectId = params.getProjectId();
				Batch tmpBatch = ServerFacade.downloadBatch(projectId);
				if(tmpBatch == null)
				{
					result = null;
				}
				else
				{
					tmpUser.setCurrentBatchID(tmpBatch.getId());
					ServerFacade.updateUser(tmpUser);
					Database.initialize();
					Database tmpDB = new Database();
					tmpDB.startTransaction();
					result = new DownloadBatchOutput(tmpBatch, tmpDB.getProjectDAO().getProject(projectId), tmpDB.getFieldDAO().getFieldsInProject(projectId));
					tmpDB.endTransaction(false);
				}
			}
			
			
		}
		catch (ServerException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
			exchange.sendResponseHeaders(HttpURLConnection.HTTP_INTERNAL_ERROR, -1);
			return;
		}
		catch(DatabaseException e)
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
