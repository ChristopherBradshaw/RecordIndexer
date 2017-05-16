package server;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.database.DataImporter;
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
import shared.model.Project;
import client.ClientException;
import client.communication.ClientCommunicator;


public class ClientCommunicatorTests
{
	private ClientCommunicator comm;
	
	@Before
	public void init()
	{
		new DataImporter("test/Records.xml").importNewData();
		Server.main(new String[]{"10255"});
		comm = new ClientCommunicator(10255,"localhost");
	}
	
	@Test
	public void test() throws ClientException
	{
		testValidate();
		testGetProjects();
		testGetSampleBatch();
		testDownloadSubmitBatch();
		testGetFields();
		testSearch();
	}
	
	public void testValidate() throws ClientException
	{
		ValidateUserInput in = new ValidateUserInput("test1","test1");
		ValidateUserOutput out = comm.validateUser(in);
		assert(out.getUser().getUsername().equals("test1"));
		assert(out.getUser().getFirstName().equals("Test"));
		assert(out.getUser().getLastName().equals("One"));
		assert(out.getUser().getEmail().equals("test1@gmail.com"));
		assert(out.getUser().getCompletedBatches() == 0);
		
		in = new ValidateUserInput("test2","test2");
		out = comm.validateUser(in);
		assert(out.getUser().getUsername().equals("test2"));
		assert(out.getUser().getFirstName().equals("Test"));
		assert(out.getUser().getLastName().equals("Two"));
		assert(out.getUser().getEmail().equals("test2@gmail.com"));
		assert(out.getUser().getCompletedBatches() == 0);

		in = new ValidateUserInput("test2","test1");
		out = comm.validateUser(in);
		assert(out.getUser() == null);
	}
	
	public void testGetProjects() throws ClientException
	{
		ValidateUserInput in = new ValidateUserInput("test1","test1");
		GetProjectsOutput out = comm.getProjects(in);
		
		List<Project> projects = out.getProjects();
		assert(projects.size() == 3);
		
		assert(projects.get(0).getTitle().equals("1890 Census"));
		assert(projects.get(0).getRecordsPerImage() == 8);
		assert(projects.get(0).getFirstYCoord() == 199);
		assert(projects.get(0).getRecordHeight() == 60);
		
		assert(projects.get(2).getTitle().equals("Draft Records"));
		assert(projects.get(2).getRecordsPerImage() == 7);
		assert(projects.get(2).getFirstYCoord() == 195);
		assert(projects.get(2).getRecordHeight() == 65);
		
		
		in = new ValidateUserInput("test23","test1");
		out = comm.getProjects(in);
		
		assert(out == null);
		
	}
	
	public void testGetSampleBatch() throws ClientException
	{
		GetBatchInput in = new GetBatchInput(new ValidateUserInput("test1","test1"), 1);
		String out = comm.getSampleBatch(in);
		assert(out.equals("images/1890_image0.png"));

		in = new GetBatchInput(new ValidateUserInput("test1","test1"), 2);
		out = comm.getSampleBatch(in);
		assert(out.equals("images/1900_image0.png"));
		
		in = new GetBatchInput(new ValidateUserInput("test123","test1"), 2);
		out = comm.getSampleBatch(in);
		assert(out == null);
	}

	public void testDownloadSubmitBatch() throws ClientException
	{
		GetBatchInput in = new GetBatchInput(new ValidateUserInput("test1","test1"), 1);
		DownloadBatchOutput out = comm.downloadBatch(in);
		
		int id = out.getBatch().getId();
		out = comm.downloadBatch(in);
		assert(out == null);
		
		in = new GetBatchInput(new ValidateUserInput("test2","test2"), 1);
		out = comm.downloadBatch(in);
		int id2 = out.getBatch().getId();
		assert(id != out.getBatch().getId());
		
		in = new GetBatchInput(new ValidateUserInput("sheila","parker"), 1);
		out = comm.downloadBatch(in);
		assert(id != out.getBatch().getId() && id2 != out.getBatch().getId());		
		
		id = out.getBatch().getId();
		
		List<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < 8; ++i)
		{
			data.add(new ArrayList<String>());
		}
		
		data.get(0).add("Bradshaw");
		data.get(0).add("Chris");
		data.get(0).add("Male");
		data.get(0).add("17");
		
		data.get(1).add("adam");
		data.get(1).add("john");
		data.get(1).add("Male");
		data.get(1).add("45");
		
		data.get(2).add("bvxwe");
		data.get(2).add("Chrnbdssis");
		data.get(2).add("Male");
		data.get(2).add("33");
		
		data.get(3).add("tyer");
		data.get(3).add("bvcx");
		data.get(3).add("Male");
		data.get(3).add("12");
		
		data.get(4).add("vczxer");
		data.get(4).add("ewrqw");
		data.get(4).add("Male");
		data.get(4).add("19");
		
		data.get(5).add("vczxsl");
		data.get(5).add("etrtcv");
		data.get(5).add("Male");
		data.get(5).add("43634");
		
		data.get(6).add("fbcxvlpepe");
		data.get(6).add("povcx");
		data.get(6).add("Male");
		data.get(6).add("19");
		
		data.get(7).add("bvcxt");
		data.get(7).add("fgper");
		data.get(7).add("Male");
		data.get(7).add("12");
		
		SubmitBatchInput submitIn = new SubmitBatchInput(new ValidateUserInput("sheila","parker"),out.getBatch().getId(),data);
		boolean submitOut = comm.submitBatch(submitIn);
		assert(submitOut);
		
		in = new GetBatchInput(new ValidateUserInput("sheila","parker"), 1);
		out = comm.downloadBatch(in);
		assert(out.getBatch().getId() != id);	//make sure not assigned batch just submitted
		
		
		in = new GetBatchInput(new ValidateUserInput("sheilaaaa","parker"), 14);
		out = comm.downloadBatch(in);
		assert(out == null);
	}
	
	public void testGetFields() throws ClientException
	{
		GetFieldsInput params = new GetFieldsInput(new GetBatchInput(new ValidateUserInput("test1","test1"),3));
		GetFieldsOutput out = comm.getFields(params);
		
		assert(out.getFields().size() == 4);
		assert(out.getFields().get(3).getTitle().equals("Ethnicity"));
		assert(out.getFields().get(3).getxCoord() == 845);
		
		
		params = new GetFieldsInput(new GetBatchInput(new ValidateUserInput("test165","test1"),3));
		out = comm.getFields(params);
		assert(out == null);
	}
	
	public void testSearch() throws ClientException
	{
		List<Integer> fieldIds = new ArrayList<Integer>();
		fieldIds.add(10);
	
		List<String> keywords = new ArrayList<String>();
		keywords.add("potter");
		keywords.add("gray");

		SearchInput in = new SearchInput(new ValidateUserInput("test1","test1"),fieldIds,keywords);
		SearchOutput out = comm.search(in);
		assert(out.getData().size() == 2);
		
		keywords.add("clyde");
		keywords.add("SOMETHINGREALLYWRONG");
		fieldIds.add(11);
		
		in = new SearchInput(new ValidateUserInput("test1","test1"),fieldIds,keywords);
		out = comm.search(in);
		
		assert(out.getData().size() == 3);
		
		in = new SearchInput(new ValidateUserInput("test12","test1"),null,null);
		out = comm.search(in);
		assert(out == null);
		
	}
}
