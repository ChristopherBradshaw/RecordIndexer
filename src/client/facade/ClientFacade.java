package client.facade;

import java.util.List;

import shared.communication.DownloadBatchOutput;
import shared.communication.GetBatchInput;
import shared.communication.GetProjectsOutput;
import shared.communication.SubmitBatchInput;
import shared.communication.ValidateUserInput;
import shared.communication.ValidateUserOutput;
import shared.model.Project;
import shared.model.User;
import client.ClientException;
import client.communication.ClientCommunicator;

public class ClientFacade
{
	private static ClientCommunicator cc = new ClientCommunicator();

	
	public static User validateUser(String username, String password) throws ClientException
	{
		ValidateUserOutput output = null;
	
		output = cc.validateUser(new ValidateUserInput(username,password));
		
		User userResult = null;
		
		if(output.getUser() != null)
		{
			userResult = output.getUser();
		}
		
		return userResult;
	
	}
	
	public static DownloadBatchOutput getBatch(GetBatchInput params) throws ClientException
	{
		return cc.downloadBatch(params);
	}
	
	public static DownloadBatchOutput getBatchById(int batchId) throws ClientException
	{
		return cc.getBatchById(batchId);
	}
	
	public static String getSample(GetBatchInput params) throws ClientException
	{
		return cc.getSampleBatch(params);
	}
	
	public static byte[] getFile(String path) throws ClientException
	{
		return cc.getFile(path);
	}
	
	public static List<Project> getProjects(ValidateUserInput params) throws ClientException
	{
		GetProjectsOutput out = cc.getProjects(params);
		
		if(out != null)
			return out.getProjects();
		else
			return null;
	}
	
	public static boolean submitBatch(SubmitBatchInput params) throws ClientException
	{
		return cc.submitBatch(params);
	}
}
