package server;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.database.BatchDAO;
import server.database.Database;
import server.database.DatabaseException;
import server.database.FieldDAO;
import server.database.IndexedDataDAO;
import server.database.ProjectDAO;
import server.database.UserDAO;
import shared.model.Batch;
import shared.model.Field;
import shared.model.IndexedData;
import shared.model.Project;
import shared.model.User;

public class DAOTests
{
	private Database db;
	private BatchDAO batchDAO;
	private FieldDAO fieldDAO;
	private IndexedDataDAO indexedDataDAO;
	private ProjectDAO projectDAO;
	private UserDAO userDAO;
	
	@Before
	public void init() throws DatabaseException
	{
		Database.initialize();
		db = new Database();
						
		batchDAO = db.getBatchDAO();
		fieldDAO = db.getFieldDAO();
		indexedDataDAO = db.getIndexedDataDAO();
		projectDAO = db.getProjectDAO();
		userDAO = db.getUserDAO();
		
		assert(db.getConnection() == null);
		
		db.startTransaction();
		assert(db.getConnection() != null);
		
		db.endTransaction(false);
		assert(db.getConnection() == null);

	}
	
	private void clearDB() throws DatabaseException
	{
		db.clearTable("user");
		db.clearTable("project");
		db.clearTable("field");
		db.clearTable("batch");
		db.clearTable("indexedValues");
	}
	//Test getFile?
	
	@Test
	public void batchDAOTest() throws DatabaseException
	{
		db.startTransaction();
		clearDB();
		
		batchTestAddAndGet();
		batchTestSample();
		batchTestDownload();

		db.endTransaction(false);
	}
	
	@Test
	public void fieldDAOTest() throws DatabaseException
	{
		db.startTransaction();
		clearDB();
		
		fieldTest();
		
		db.endTransaction(false);

	}
	
	@Test
	public void indexedDataDAOTest() throws DatabaseException
	{
		db.startTransaction();
		clearDB();
		
		indexedDataTest();
		
		db.endTransaction(false);
	}
	
	@Test
	public void projectDAOTest() throws DatabaseException
	{
		db.startTransaction();
		clearDB();
		
		projectTest();
		
		db.endTransaction(false);
	}
	
	@Test
	public void userDAOTest() throws DatabaseException
	{
		db.startTransaction();
		clearDB();
		
		userTest();
		
		db.endTransaction(false);
	}
	
	public void userTest() throws DatabaseException
	{
		assert(userDAO.isValidUser("asdfasdf", "asdasd") == null);
		
		User bob = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob);
		assert(userDAO.isValidUser("bob123", "bobiscool") != null);

		User chris = new User(-1, "cbrad97", "aPassword", "Chris", "Bradshaw", "chris@gmail.com");
		userDAO.add(chris);

		User jim = new User(-1, "jimmy", "anotherPassword", "Jim", "Jones", "jimj@gmail.com");
		userDAO.add(jim);

		assert(userDAO.isValidUser("hakakakaka", "xzcvzxcz") == null);
		assert(userDAO.isValidUser("cbrad97", "aPassword") != null);
		assert(userDAO.isValidUser("jimmy", "anotherPassword") != null);
		
		assert(userDAO.isValidUser("cbrad97", "aWRONGpassword") == null);
		assert(userDAO.isValidUser("awrongusername", "aPassword") == null);

		
		bob.setCompletedBatches(10);
		userDAO.update(bob);
		
		assert(userDAO.isValidUser("bob123", "bobiscool") != null);
		assert(bob.getCompletedBatches() == 10);
	}
	
	public void projectTest() throws DatabaseException
	{
		assert(projectDAO.getAll().size() == 0);
		assert(projectDAO.getProject(5) == null);
		projectDAO.add(new Project(-1,"Some Project",50,20,23));
		assert(projectDAO.getAll().size() == 1);
		
		projectDAO.add(new Project(-1,"Another Project",50,20,75));
		projectDAO.add(new Project(-1,"Not another Project",50,20,44));
		projectDAO.add(new Project(-1,"Another dang Project",50,20,15));
		assert(projectDAO.getAll().size() == 4);
		
		List<Project> projects = projectDAO.getAll();
		int id = projects.get(0).getId();
		assert(projectDAO.getProject(id).getTitle().equals("Some Project"));
		assert(projectDAO.getProject(id).getFirstYCoord() == 20);

		int sum = 0;
		for(int i = 0; i < 4; ++i)
		{
			sum += projects.get(i).getRecordHeight();
		}
		
		assert(sum == 157);
	}
	
	public void indexedDataTest() throws DatabaseException
	{
		indexedDataDAO.add(new IndexedData(-1,"apple","/image1",5,100,1));
		indexedDataDAO.add(new IndexedData(-1,"potato","/image1",5,100,2));
		indexedDataDAO.add(new IndexedData(-1,"grape","/imageapple",5,100,3));
		indexedDataDAO.add(new IndexedData(-1,"orange","/imageapple",5,100,4));

		indexedDataDAO.add(new IndexedData(-1,"carrot","/imageasd",50,100,1));
		indexedDataDAO.add(new IndexedData(-1,"squash","/imagepsdfs",50,100,2));
		indexedDataDAO.add(new IndexedData(-1,"tomato","/imageapple",50,100,3));
		indexedDataDAO.add(new IndexedData(-1,"pickle","/image1",50,100,4));
		
		ArrayList<Integer> fieldIds = new ArrayList<Integer>();
		fieldIds.add(5);
		fieldIds.add(50);
		ArrayList<String> keywords = new ArrayList<String>();
		keywords.add("apple");
		keywords.add("POTatO");
		keywords.add("PIcklE");
		List<IndexedData> searchResult = indexedDataDAO.search(fieldIds, keywords);
		
		assert(searchResult.size() == 3);
		for(int i = 0; i < 3; ++i)
		{
			assert(searchResult.get(i).getImageURL().equals("/image1"));
			assert(searchResult.get(i).getFieldID() == 5 || searchResult.get(i).getFieldID() == 50);
		}
		
		
		for(int i = 0; i < 3; ++i)
		{
			IndexedData data = searchResult.get(i);
			String old = data.getTextInput();
			data.setTextInput(old + " and some more");
			indexedDataDAO.update(data);
			
		}
		
		fieldIds = new ArrayList<Integer>();
		fieldIds.add(5);
		fieldIds.add(50);
		keywords = new ArrayList<String>();
		keywords.add("apple and some more");
		keywords.add("POTatO and SOMe more");
		keywords.add("PIcklE and SOME MoRE");
		searchResult = indexedDataDAO.search(fieldIds, keywords);
		
		assert(searchResult.size() == 3);
		for(int i = 0; i < 3; ++i)
		{
			assert(searchResult.get(i).getImageURL().equals("/image1"));
			assert(searchResult.get(i).getFieldID() == 5 || searchResult.get(i).getFieldID() == 50);
		}
	}
	
	public void fieldTest() throws DatabaseException
	{
		fieldDAO.add(new Field(-1,"name",0,5,"/namehtml","",5));
		fieldDAO.add(new Field(-1,"age",2,10,"/agehtml","",5));
		fieldDAO.add(new Field(-1,"address",3,9,"/addresshtml","",5));
		fieldDAO.add(new Field(-1,"phone",4,11,"/phonehtml","",5));
		fieldDAO.add(new Field(-1,"gender",9,15,"/genderhtml","",5));
		
		assert(fieldDAO.getAll().size() == 5);
		for(int i = 0; i < 5; ++i)
		{
			Field f = fieldDAO.getAll().get(i);
			assert(f.getHelpHtml().equals("/" + f.getTitle() + "html"));
			assert(f.getForeignProjectId() == 5);
		}

		
		fieldDAO.add(new Field(-1,"asdfd",2,10,"/asdfdhtml","",3));
		fieldDAO.add(new Field(-1,"abc",2,10,"/abchtml","",3));
		fieldDAO.add(new Field(-1,"def",2,10,"/defhtml","",3));
		fieldDAO.add(new Field(-1,"xyz",2,10,"/xyzhtml","",5));
		fieldDAO.add(new Field(-1,"ghi",2,10,"/ghihtml","",3));
		fieldDAO.add(new Field(-1,"jkl",2,10,"/jklhtml","",3));
		fieldDAO.add(new Field(-1,"mno",2,10,"/mnohtml","",5));
		
		assert(fieldDAO.getFieldsInProject(3).size() == 5);
		assert(fieldDAO.getFieldsInProject(5).size() == 7);
		
		for(int i = 0; i < 5; ++i)
		{
			Field f = fieldDAO.getFieldsInProject(3).get(i);
			assert(f.getHelpHtml().equals("/" + f.getTitle() + "html"));
			assert(f.getForeignProjectId() == 3);
		}
	}
	
	public void batchTestAddAndGet() throws DatabaseException
	{
		List<Batch> batchList = batchDAO.getBatchesInProject(55);
		assert(batchList.size() == 0);
		
		batchList = batchDAO.getBatchesInProject(-1);
		assert(batchList.size() == 0);
		
		batchDAO.add(new Batch(-1, "/projectB/a.png", 2));
		batchDAO.add(new Batch(-1, "/projectA/a.png", 1));
		batchDAO.add(new Batch(-1, "/projectB/b.png", 2));
		batchDAO.add(new Batch(-1, "/projectA/b.png", 1));
		batchDAO.add(new Batch(-1, "/projectB/c.png", 2));
		batchDAO.add(new Batch(-1, "/projectB/d.png", 2));
		batchDAO.add(new Batch(-1, "/projectA/c.png", 1));
		batchDAO.add(new Batch(-1, "/projectA/d.png", 1));
		batchDAO.add(new Batch(-1, "/projectB/e.png", 2));
		batchDAO.add(new Batch(-1, "/projectA/e.png", 1));
		
		
		//Test group 1
		batchList = batchDAO.getBatchesInProject(1);
		assert(batchList.size() == 5);
		
		for(int i = 0; i < batchList.size(); ++i)
		{
			Batch b = batchList.get(i);
			String expectedFilePath = "/projectA/" + ((char)('a' + i)) + ".png";
			
			assert(b.getFilePath().equals(expectedFilePath));
			assert(b.getForeignProjectId() == 1);
			
		}
		
		//Test group 2
		batchList = batchDAO.getBatchesInProject(2);
		assert(batchList.size() == 5);
		
		for(int i = 0; i < batchList.size(); ++i)
		{
			Batch b = batchList.get(i);
			String expectedFilePath = "/projectB/" + ((char)('a' + i)) + ".png";
			
			assert(b.getFilePath().equals(expectedFilePath));
			assert(b.getForeignProjectId() == 2);
		}
	}
	
	public void batchTestSample() throws DatabaseException
	{
		Batch b = batchDAO.getSampleBatch(4);
		assert(b == null);
		
		b = batchDAO.getSampleBatch(-1);
		assert(b == null);
		
		//Group 1
		b = batchDAO.getSampleBatch(1);
		assert(b != null);
		assert(b.getFilePath().contains("A"));
		assert(!b.getFilePath().contains("B"));
		assert(b.getFilePath().contains(".png"));
		assert(b.getForeignProjectId() == 1);
		assert(b.getId() >= 0);
		
		//Group 2
		b = batchDAO.getSampleBatch(2);
		assert(b != null);
		assert(b.getFilePath().contains("B"));
		assert(!b.getFilePath().contains("A"));
		assert(b.getFilePath().contains(".png"));
		assert(b.getForeignProjectId() == 2);
		assert(b.getId() > 0);
	}

	public void batchTestDownload() throws DatabaseException
	{	
		//Test invalid, no assigned batches----------------------------------------------
		Batch b = batchDAO.downloadBatch(-1);
		assert(b == null);
		
		b = batchDAO.downloadBatch(0);
		assert(b == null);
		
		
		//Test valid, no assigned batches------------------------------------------------
		b = batchDAO.downloadBatch(1);
		assert(b != null);
		assert(b.getForeignProjectId() == 1);
		assert(b.getId() >= 0);
		assert(b.getFilePath().contains("A"));
		assert(!b.getFilePath().contains("B"));
		
		b = batchDAO.downloadBatch(2);
		assert(b != null);
		assert(b.getForeignProjectId() == 2);
		assert(b.getId() >= 0);
		assert(b.getFilePath().contains("B"));
		assert(!b.getFilePath().contains("A"));
		
		int tmpbatchId = b.getId();
		Batch tmpBatch = batchDAO.getBatch(tmpbatchId);
		assert(tmpBatch.getFilePath().equals(b.getFilePath()));
		assert(tmpBatch.getForeignProjectId() == b.getForeignProjectId());
		assert(tmpBatch.getId() == b.getId());


		
		//Test valid, assigned batches---------------------------------------------------
		//Check for last index available
		User bob0 = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob0);
		bob0.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(0).getId());
		userDAO.update(bob0);

		User bob1 = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob1);
		bob1.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(1).getId());
		userDAO.update(bob1);

		User bob2 = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob2);
		bob2.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(2).getId());
		userDAO.update(bob2);

		User bob3 = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob3);
		bob3.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(3).getId());
		userDAO.update(bob3);

		
		b = batchDAO.downloadBatch(1);
		assert(b.getId() == batchDAO.getBatchesInProject(1).get(4).getId());
		
		//Check for middle index available
		bob0.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(0).getId());
		userDAO.update(bob0);
		bob1.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(1).getId());
		userDAO.update(bob1);
		bob2.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(3).getId());
		userDAO.update(bob2);
		bob3.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(4).getId());
		userDAO.update(bob3);
		
		b = batchDAO.downloadBatch(1);
		assert(b.getId() == batchDAO.getBatchesInProject(1).get(2).getId());
		
		//Check for first index available
		bob0.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(1).getId());
		userDAO.update(bob0);
		bob1.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(2).getId());
		userDAO.update(bob1);
		bob2.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(3).getId());
		userDAO.update(bob2);
		bob3.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(4).getId());
		userDAO.update(bob3);
		
		b = batchDAO.downloadBatch(1);
		assert(b.getId() == batchDAO.getBatchesInProject(1).get(0).getId());

		//Check for every index assigned
		User bob4 = new User(-1, "bob123", "bobiscool", "Bob", "Smith", "bobsmith@gmail.com");
		userDAO.add(bob4);
		bob4.setCurrentBatchID(batchDAO.getBatchesInProject(1).get(0).getId());
		userDAO.update(bob4);
		
		b = batchDAO.downloadBatch(1);
		assert(b == null);


	}	
}
