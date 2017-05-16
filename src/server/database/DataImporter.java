package server.database;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import shared.model.Batch;
import shared.model.Field;
import shared.model.IndexedData;
import shared.model.Project;
import shared.model.User;

import org.apache.commons.io.FileUtils;

public class DataImporter
{
	
	public static void main(String args[])
	{
		if(args.length != 1)
			return;
		
		new DataImporter(args[0]).importNewData();
	}
	
	
	private Database database;
	private String filePath;
	
	public DataImporter(String filePath)
	{
		try
		{
			Database.initialize();
		} 
		catch (DatabaseException e)
		{
			System.out.println("Could not find database");
			e.printStackTrace();
		}
		
		this.filePath = filePath;
		this.database = new Database();
	}
	
	public void importNewData()
	{		
		File xmlFile = new File(filePath);
		File dest = new File("data");
		
		File emptydb = new File("database" + File.separator + "indexer_server_empty.sqlite");
		File currentdb = new File("database" + File.separator + "recordindexer.sqlite");
		
		try
		{
			if(!xmlFile.getParentFile().getCanonicalPath().equals(dest.getCanonicalPath()))
				FileUtils.deleteDirectory(dest);
				
			FileUtils.copyDirectory(xmlFile.getParentFile(), dest);
		
			FileUtils.copyFile(emptydb, currentdb);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		
		Document doc = null;
		try
		{
			DocumentBuilder docB = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			File f = new File(filePath);
			doc = docB.parse(f);
		} 
		catch (ParserConfigurationException e)
		{
			e.printStackTrace();
		} 
		catch (SAXException e)
		{
			e.printStackTrace();
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			database.startTransaction();
			parseUsers(doc);
			parseProjects(doc);
			database.endTransaction(true);
		} 
		catch (DatabaseException e)
		{
			database.endTransaction(false);
			System.out.println("Error occured while importing");
			e.printStackTrace();
		}
	}
	
	private void parseUsers(Document doc) throws DatabaseException
	{
		NodeList userNodes = doc.getElementsByTagName("user");
		
		for(int i = 0; i < userNodes.getLength(); ++i)
		{
			Element currentUser = (Element) userNodes.item(i);
			String tmpUsername = currentUser.getElementsByTagName("username").item(0).getTextContent();
			String tmpPassword = currentUser.getElementsByTagName("password").item(0).getTextContent();
			String tmpFirstName = currentUser.getElementsByTagName("firstname").item(0).getTextContent();
			String tmpLastName = currentUser.getElementsByTagName("lastname").item(0).getTextContent();
			String tmpEmail = currentUser.getElementsByTagName("email").item(0).getTextContent();
			int indexedRecords = Integer.parseInt(currentUser.getElementsByTagName("indexedrecords").item(0).getTextContent());
			
			User tmpUser = new User(-1,tmpUsername,tmpPassword,tmpFirstName,tmpLastName,tmpEmail,indexedRecords);
			database.getUserDAO().add(tmpUser);
		}
	}
	
	private void parseProjects(Document doc) throws DatabaseException
	{
		NodeList projectNodes = doc.getElementsByTagName("project");
		
		for(int i = 0; i < projectNodes.getLength(); ++i)
		{
			Element currentProject = (Element) projectNodes.item(i);
			String tmpTitle = currentProject.getElementsByTagName("title").item(0).getTextContent();
			int tmpRecordsPerImage = Integer.parseInt(currentProject.getElementsByTagName("recordsperimage").item(0).getTextContent());
			int tmpFirstYCoord = Integer.parseInt(currentProject.getElementsByTagName("firstycoord").item(0).getTextContent());
			int tmpRecordHeight = Integer.parseInt(currentProject.getElementsByTagName("recordheight").item(0).getTextContent());
			Project tmpProject = new Project(-1,tmpTitle,tmpRecordsPerImage,tmpFirstYCoord,tmpRecordHeight);
			database.getProjectDAO().add(tmpProject);
			
			int projectId = tmpProject.getId();
			parseFields(projectId, currentProject.getElementsByTagName("field"));
			parseBatches(projectId, currentProject.getElementsByTagName("image"));
		}
	}
	
	private void parseBatches(int projectId, NodeList nodes) throws DatabaseException
	{
		for(int i = 0; i < nodes.getLength(); ++i)
		{
			Element currentBatch = (Element) nodes.item(i);
			String tmpFilePath = currentBatch.getElementsByTagName("file").item(0).getTextContent();
			Batch tmpBatch = new Batch(-1,tmpFilePath,projectId);
			database.getBatchDAO().add(tmpBatch);

			parseRecords(tmpBatch.getId(), tmpFilePath, currentBatch.getElementsByTagName("record"));
		}
	}

	private void parseRecords(int batchId, String imageUrl, NodeList nodes) throws DatabaseException
	{
		for(int i = 0; i < nodes.getLength(); ++i)	//For each record
		{
			Element currentRecord = (Element) nodes.item(i);
			NodeList valueNodes = currentRecord.getElementsByTagName("value");
			
			for(int j = 0; j < valueNodes.getLength(); ++j)
			{
				Element currentValue = (Element) valueNodes.item(j);
				String tmpText = currentValue.getTextContent();
				int tmpFieldId = database.getFieldDAO().getFieldsInProject(database.getBatchDAO().getProjectId(batchId)).get(j).getId();
				IndexedData tmpData = new IndexedData(-1,tmpText,imageUrl,tmpFieldId,batchId,i);
				database.getIndexedDataDAO().add(tmpData);
			}
		}
	}
	private void parseFields(int projectId, NodeList nodes) throws DatabaseException
	{
		for(int i = 0; i < nodes.getLength(); ++i)
		{
			Element currentField = (Element) nodes.item(i);
			String tmpTitle = currentField.getElementsByTagName("title").item(0).getTextContent();
			int tmpXCoord = Integer.parseInt(currentField.getElementsByTagName("xcoord").item(0).getTextContent());
			int tmpWidth = Integer.parseInt(currentField.getElementsByTagName("width").item(0).getTextContent());
			String tmpHelpHtml = currentField.getElementsByTagName("helphtml").item(0).getTextContent();
			String tmpKnownData = "";
			
			if(currentField.getElementsByTagName("knowndata").item(0) != null)
			{
				tmpKnownData = currentField.getElementsByTagName("knowndata").item(0).getTextContent();
			}
			
			Field f = new Field(-1,tmpTitle,tmpXCoord, tmpWidth, tmpHelpHtml, tmpKnownData,projectId);
			database.getFieldDAO().add(f);
		}
	}
}
