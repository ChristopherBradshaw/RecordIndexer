package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.sun.net.httpserver.HttpHandler;

import server.facade.ServerFacade;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class Server
{
	private static int SERVER_PORT_NUMBER;
	private static final int MAX_WAITING_CONNECTIONS = 10;
	
	private static Logger logger;
	
	static 
	{
		try 
		{
			initLog();
		}
		catch (IOException e) 
		{
			System.out.println("Could not initialize log: " + e.getMessage());
		}
	}

	private static void initLog() throws IOException 
	{
		
		Level logLevel = Level.FINE;
		
		logger = Logger.getLogger("recordindexer"); 
		logger.setLevel(logLevel);
		logger.setUseParentHandlers(false);
		
		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(logLevel);
		consoleHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(consoleHandler);

		FileHandler fileHandler = new FileHandler("log.txt", false);
		fileHandler.setLevel(logLevel);
		fileHandler.setFormatter(new SimpleFormatter());
		logger.addHandler(fileHandler);
	}
	
	private HttpServer server;
	
	private void run()
	{
		logger.info("Initializing Model");
		
		try 
		{
			ServerFacade.initialize();		
		}
		catch (ServerException e) 
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			return;
		}
		
		logger.info("Initializing HTTP Server");
		
		try 
		{
			server = HttpServer.create(new InetSocketAddress(SERVER_PORT_NUMBER),
											MAX_WAITING_CONNECTIONS);
		} 
		catch (IOException e) 
		{
			logger.log(Level.SEVERE, e.getMessage(), e);			
			return;
		}

		server.setExecutor(null); // use the default executor
		
		server.createContext("/ValidateUser", validateHandler);
		server.createContext("/GetProjects", getProjectsHandler);
		server.createContext("/GetSampleImage", sampleHandler);
		server.createContext("/DownloadBatch", downloadBatchHandler);
		server.createContext("/SubmitBatch", submitBatchHandler);
		server.createContext("/GetFields", getFieldsHandler);
		server.createContext("/Search", searchHandler);	
		server.createContext("/",downloadFileHandler);
		logger.info("Starting HTTP Server");

		server.start();
	}
	
	private HttpHandler downloadFileHandler = new DownloadFileHandler();
	private HttpHandler downloadBatchHandler = new DownloadBatchHandler();
	private HttpHandler getFieldsHandler = new GetFieldsHandler();
	private HttpHandler getProjectsHandler = new GetProjectsHandler();
	private HttpHandler sampleHandler = new GetSampleImageHandler();
	private HttpHandler searchHandler = new SearchHandler();
	private HttpHandler submitBatchHandler = new SubmitBatchHandler();
	private HttpHandler validateHandler = new ValidateUserHandler();
	
	public static void main(String[] args) 
	{
		
		if(args.length != 1)
			return;
		
		Server.SERVER_PORT_NUMBER = Integer.parseInt(args[0]);
		new Server().run();
	}
}