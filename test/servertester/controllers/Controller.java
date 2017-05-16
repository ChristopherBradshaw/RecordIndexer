package servertester.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import servertester.views.IView;
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
import client.communication.ClientCommunicator;

public class Controller implements IController
{

	private IView _view;

	private ClientCommunicator comm;
	
	public Controller()
	{
		return;
	}

	public IView getView()
	{
		return _view;
	}

	public void setView(IView value)
	{
		_view = value;
	}

	// IController methods
	//

	@Override
	public void initialize()
	{
		getView().setHost("localhost");
		getView().setPort("39640");
		comm = new ClientCommunicator(Integer.parseInt(getView().getPort()) , getView().getHost());

		operationSelected();

	}

	@Override
	public void operationSelected()
	{
		ArrayList<String> paramNames = new ArrayList<String>();
		paramNames.add("User");
		paramNames.add("Password");

		switch (getView().getOperation())
		{
		case VALIDATE_USER:
			break;
		case GET_PROJECTS:
			break;
		case GET_SAMPLE_IMAGE:
			paramNames.add("Project");
			break;
		case DOWNLOAD_BATCH:
			paramNames.add("Project");
			break;
		case GET_FIELDS:
			paramNames.add("Project");
			break;
		case SUBMIT_BATCH:
			paramNames.add("Batch");
			paramNames.add("Record Values");
			break;
		case SEARCH:
			paramNames.add("Fields");
			paramNames.add("Search Values");
			break;
		default:
			assert false;
			break;
		}

		getView().setRequest("");
		getView().setResponse("");
		getView().setParameterNames(
				paramNames.toArray(new String[paramNames.size()]));
	}

	@Override
	public void executeOperation()
	{
//		System.out.println(comm.getHost() + " " + comm.getPort());
		switch (getView().getOperation())
		{
		case VALIDATE_USER:
			validateUser();
			break;
		case GET_PROJECTS:
			getProjects();
			break;
		case GET_SAMPLE_IMAGE:
			getSampleImage();
			break;
		case DOWNLOAD_BATCH:
			downloadBatch();
			break;
		case GET_FIELDS:
			getFields();
			break;
		case SUBMIT_BATCH:
			submitBatch();
			break;
		case SEARCH:
			search();
			break;
		default:
			assert false;
			break;
		}
	}

	private void refreshPortAndHost()
	{
		comm.setPort(Integer.parseInt(getView().getPort()));
		comm.setHost(getView().getHost());
	}
	
	private void validateUser()	
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();
			
			ValidateUserOutput out = comm.validateUser(new ValidateUserInput(strs[0],strs[1]));
			
			getView().setResponse(out.toString());
		} 
		catch (Exception e)
		{
			getView().setResponse("FAILED\n");
		}
	}

	private void getProjects()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();

			GetProjectsOutput out = comm.getProjects(new ValidateUserInput(strs[0],strs[1]));
			getView().setResponse(out.toString());
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}
	}

	private void getSampleImage()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();
			String relativePath = comm.getSampleBatch(new GetBatchInput(new ValidateUserInput(strs[0],strs[1]),Integer.parseInt(strs[2])));
			String out = ClientCommunicator.getURL() + File.separator + relativePath;
			if(relativePath == null || relativePath.equals(""))
			{
				getView().setResponse("FAILED\n");
			}
			else
				getView().setResponse(out);
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}
	}

	private void downloadBatch()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();

			DownloadBatchOutput out = comm.downloadBatch(new GetBatchInput(new ValidateUserInput(strs[0],strs[1]),Integer.parseInt(strs[2])));
			getView().setResponse(out.toString());
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}		
	}

	private void getFields()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();

			if(strs[2].length() == 0)
			{
				GetFieldsOutput out = comm.getFields(new GetFieldsInput(new GetBatchInput(new ValidateUserInput(strs[0],strs[1]),-1)));
				getView().setResponse(out.toString());
			}
			else if(Integer.parseInt(strs[2]) <= 0)
			{
				getView().setResponse("FAILED\n");
			}
			else
			{
				GetFieldsOutput out = comm.getFields(new GetFieldsInput(new GetBatchInput(new ValidateUserInput(strs[0],strs[1]),Integer.parseInt(strs[2]))));
				if(out.toString().trim().length() == 0)
				{
					getView().setResponse("FAILED\n");
				}
				else
				{
					getView().setResponse(out.toString());
				}
			}
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}		
	}

	private void submitBatch()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();

			ValidateUserInput validate = new ValidateUserInput(strs[0],strs[1]);
			
			StringTokenizer tok = new StringTokenizer(strs[3],";",false);
			ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
			int index = -1;
			while(tok.hasMoreTokens())
			{
				++index;
				dataList.add(new ArrayList<String>());
				String currentRow = tok.nextToken();
			
				String[] lines = currentRow.split(",",-1);
				for(String s : lines)
				{
					dataList.get(index).add(s);
				}
			}
			
			boolean out = comm.submitBatch(new SubmitBatchInput(validate,Integer.parseInt(strs[2]),dataList));
			
			if(out)
				getView().setResponse("TRUE\n");
			else
				getView().setResponse("FAILED\n");
			
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}		
	}

	private void search()
	{
		try
		{
			refreshPortAndHost();
			String[] strs = getView().getParameterValues();

			ValidateUserInput validate = new ValidateUserInput(strs[0],strs[1]);
			List<Integer> fields = new ArrayList<Integer>();
			List<String> keywords = new ArrayList<String>();
			
			StringTokenizer fieldTokenizer = new StringTokenizer(strs[2],",");
			while(fieldTokenizer.hasMoreTokens())
			{
				fields.add(Integer.parseInt(fieldTokenizer.nextToken()));
			}
			
			StringTokenizer keywordsTokenizer = new StringTokenizer(strs[3],",");
			while(keywordsTokenizer.hasMoreTokens())
			{
				keywords.add(keywordsTokenizer.nextToken());
			}
			
			SearchOutput out = comm.search(new SearchInput(validate,fields,keywords));
			getView().setResponse(out.toString());
		}
		catch(Exception e)
		{
			getView().setResponse("FAILED\n");
		}		
	}
}
