package server;

import org.junit.* ;
import static org.junit.Assert.* ;

public class ServerUnitTests {
	
	@Before
	public void setup()
	{
	}
	
	@After
	public void teardown() 
	{
	}

	@Test
	public void test()
	{

	}
	
	public static void main(String[] args) 
	{
		
		String[] testClasses = new String[] 
		{
				"server.ServerUnitTests",
				"server.DAOTests",
				"server.ClientCommunicatorTests"
		};

		org.junit.runner.JUnitCore.main(testClasses);
	}
	
}

