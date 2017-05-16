package server;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

@SuppressWarnings("restriction")
public class DownloadFileHandler implements HttpHandler
{
	private Logger logger = Logger.getLogger("recordindexer");
	private XStream xmlStream = new XStream(new DomDriver());
	
	@Override
	public void handle(HttpExchange exchange) throws IOException
	{
		byte[] returnBytes = Files.readAllBytes(Paths.get("data/" + exchange.getRequestURI()));
		
		exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
		
		OutputStream outputStream = exchange.getResponseBody();
		outputStream.write(returnBytes);
		outputStream.close();
		
		exchange.getResponseBody().close();
	}
}
