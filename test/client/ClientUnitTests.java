package client;

import shared.model.Field;

import java.util.ArrayList;
import java.util.TreeSet;

import org.junit.*;

import client.util.QualityChecker;
import server.Server;
import server.database.DataImporter;
import static org.junit.Assert.*;

public class ClientUnitTests {
	
	@Before
	public void init()
	{
		DataImporter.main(new String[]{"Records/Records.xml"});
		Server.main(new String[]{"39640"});
	}
	@Test
	public void FieldChecker()
	{
		ArrayList<Field> fields = new ArrayList<Field>();
		
		Field f1 = new Field(1,"Last Name",-1,-1,"-1","knowndata/1890_last_names.txt",-1);
		fields.add(f1);
		
		Field f2 = new Field(2,"First Name",-1,-1,"-1","knowndata/1890_first_names.txt",-1);
		fields.add(f2);
		
		Field f3 = new Field(3,"Gender",-1,-1,"-1","knowndata/genders.txt",-1);
		fields.add(f3);
		
		QualityChecker checker = new QualityChecker(fields,"http://localhost:39640/");
		
		assert(checker.isValidIndexerInput("bolton", f1));
		assert(checker.isValidIndexerInput("BOLTon", f1));
		assert(checker.isValidIndexerInput("COMER", f1));
		assert(checker.isValidIndexerInput("coMER", f1));
		assert(checker.isValidIndexerInput("COOKe", f1));
		assert(checker.isValidIndexerInput("cooke", f1));
		
		TreeSet<String> f1Result = checker.getSuggestions("boltna", f1);
		assert(f1Result.contains("bolton"));
		assert(f1Result.size() == 1);
		
		
		f1Result = checker.getSuggestions("kay", f1);
		assert(f1Result.contains("gay"));
		assert(f1Result.contains("key"));
		assert(f1Result.size() == 4);

		
		
		assert(checker.isValidIndexerInput("fabian", f2));
		assert(checker.isValidIndexerInput("caren", f2));
		assert(checker.isValidIndexerInput("lura", f2));
		assert(!checker.isValidIndexerInput("zxcccvs", f2));

		TreeSet<String> f2Result = checker.getSuggestions("iss", f2);
		assert(f2Result.contains("issac"));		
		assert(f2Result.size() > 1);	
	}
	
	
	public static void main(String[] args) {

		String[] testClasses = new String[] {
				"client.ClientUnitTests"
		};
		
		org.junit.runner.JUnitCore.main(testClasses);
	}
	
}

