package client.communication;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import shared.communication.DownloadBatchOutput;
import shared.communication.GetBatchInput;
import shared.communication.GetFieldsInput;
import shared.communication.GetFieldsOutput;
import shared.communication.GetProjectsOutput;
import shared.communication.SearchInput;
import shared.communication.SearchOutput;
import shared.communication.SubmitBatchInput;
import shared.communication.ValidateUserInput;
import shared.communication.ValidateUserOutput;
import client.ClientException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ClientCommunicator
{
	private static String SERVER_HOST = "localhost";
	private static int SERVER_PORT = 39640;
	private static String URL_PREFIX;
	
	private XStream xmlStream;
	
	/**
	 * Create a new instance of the ClientCommunicator class
	 */
	public ClientCommunicator()
	{
		xmlStream = new XStream(new DomDriver());
		URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
	}
	
	/**
	 * Create a new instance of the ClientCommunicator class
	 * @param port The port
	 */
	public ClientCommunicator(int port, String host)
	{
		xmlStream = new XStream(new DomDriver());
		SERVER_PORT = port;
		SERVER_HOST = host;
		URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
	}
	
	
	/**
	 * Get url
	 * @return The URL
	 */
	public static String getURL()
	{
		return URL_PREFIX;
	}
	/**
	 * Set a new port
	 * @param port New port
	 */
	public void setPort(int port)
	{
		SERVER_PORT = port;
		URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
	}
	
	public int getPort()
	{
		return SERVER_PORT;
	}
	
	/**
	 * Set a new host
	 * @param host New host
	 */
	public void setHost(String host)
	{
		SERVER_HOST = host;
		URL_PREFIX = "http://" + SERVER_HOST + ":" + SERVER_PORT;
	}
	
	public String getHost()
	{
		return SERVER_HOST;
	}
	
	/**
	 * Connect to the server and check for a valid username and password pair
	 * @param params Wrapper class for username and password inputs
	 * @return Wrapper class holding <code>true</code> if valid pair, <code>false</code> if invalid pair
	 * @throws ClientException 
	 */
	public ValidateUserOutput validateUser(ValidateUserInput params) throws ClientException
	{
		return (ValidateUserOutput)doServerRequest("/ValidateUser", params);
	}
	
	/**
	 * Connect to the server and get a list of all projects
	 * @return Wrapper class holding a <code>List</code> containing all <code>Projects</code>
	 * @throws ClientException 
	 */
	public GetProjectsOutput getProjects(ValidateUserInput params) throws ClientException
	{
		return (GetProjectsOutput)doServerRequest("/GetProjects", params);
	}
	
	/**
	 * Connect to the server and get a sample <code>Batch</code> associated with the <code>Project</code> that has the id <code>projectId</code>
	 * @param params Wrapper class containing username/password pair and project id
	 * @return Path to sample batch if succeeds, "FAILED" otherwise
	 * @throws ClientException
	 */
	public String getSampleBatch(GetBatchInput params) throws ClientException
	{
		return (String)doServerRequest("/GetSampleImage", params);
	}
	
	/**
	 * Connect to the server and get a <code>Batch</code> associated with the <code>Project</code> that has the id <code>projectId</code>
     * @param params Wrapper class containing username/password pair and project id
 	 * @return Wrapper class holding a <code>Batch</code>
 	 * @throws ClientException
	 */ 
	public DownloadBatchOutput downloadBatch(GetBatchInput params) throws ClientException
	{
		return (DownloadBatchOutput)doServerRequest("/DownloadBatch", params);
	}
	
	
	
	public DownloadBatchOutput getBatchById(int params) throws ClientException
	{
		return (DownloadBatchOutput)doServerRequest("/GetBatch", params);
	}
	
	/**
	 * Connect to the server and submit a <code>Batch</code>
	 * @param params Wrapper class holding username/password info, batch info and indexed data info
	 * @throws ClientException
	 */
	public boolean submitBatch(SubmitBatchInput params) throws ClientException
	{
		return (boolean)doServerRequest("/SubmitBatch", params);
	}
	
	
	
	/**
	 * Connect to the server and get a <code>List</code> of <code>Fields</code> in a specific project or every project
	 * @param params Wrapper class containing username/password and optionally a project ID
	 * @return A <code>List</code> of <code>Fields</code>
	 * @throws ClientException
	 */
	public GetFieldsOutput getFields(GetFieldsInput params) throws ClientException
	{	
		return (GetFieldsOutput)doServerRequest("/GetFields", params);
	}
	
	/**
	 * Connect to the server and get a <code>List</code> of <code>IndexedData</code> which match elements in keywords
	 * @param params Wrapper class holding fieldId and search keywords
	 * @return A <code>List</code> of <code>IndexedData</code> which match any of the keywords
	 * @throws ClientException
	 */
	public SearchOutput search(SearchInput params) throws ClientException
	{
		return (SearchOutput)doServerRequest("/Search", params);
	}
	
	
	public byte[] getFile(String path) throws ClientException
	{
		return (byte[])doGet(File.separator + path);
	}
	
	private Object doServerRequest(String urlPath, Object postData) throws ClientException 
	{		
		try 
		{
			URL url = new URL(URL_PREFIX + urlPath);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("POST");

			connection.setDoOutput(true);

			connection.connect();

			xmlStream.toXML(postData, connection.getOutputStream());

			connection.getOutputStream().close();


			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) 
			{
				throw new ClientException(String.format("doServerRequest failed: %s (http code %d)",
						urlPath, connection.getResponseCode()));
			}
			else
			{
				Object resultObject = xmlStream.fromXML(connection.getInputStream());
				return resultObject;
			}
		}
		catch (IOException e) 
		{
			throw new ClientException(String.format("doServerRequest failed: %s", e.getMessage()), e);
		}
	}
	
	private Object doGet(String urlPath) throws ClientException {
		try {
			URL url = new URL(URL_PREFIX + urlPath);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				Object result = xmlStream.fromXML(connection.getInputStream());
				return result;
			}
			else {
				throw new ClientException(String.format("doGet failed: %s (http code %d)",
											urlPath, connection.getResponseCode()));
			}
		}
		catch (IOException e) {
			throw new ClientException(String.format("doGet failed: %s", e.getMessage()), e);
		}
	}
}
