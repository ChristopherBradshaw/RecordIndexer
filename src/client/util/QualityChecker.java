package client.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import shared.model.Field;
import spell.Words;
import client.communication.ClientCommunicator;

public class QualityChecker
{
	private HashMap<Integer,Words> trieMap;		//map field id to trie
	
	public QualityChecker(List<Field> fields)
	{
		this.trieMap = new HashMap<Integer,Words>();
		
		for(Field f : fields)
		{
			if(f.getKnownData().trim().length() == 0)
				continue;
			
			trieMap.put(f.getId(), new Words());
			String path = ClientCommunicator.getURL()  + File.separator + f.getKnownData();
			
			URL url = null;
			try
			{
				url = new URL(path);
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
				continue;
			}
			
			Scanner s = null;
			try
			{
				s = new Scanner(url.openStream()).useDelimiter(",");
			} catch (IOException e)
			{
				e.printStackTrace();
				continue;
			}
						
			
			Words currentTrie = trieMap.get(f.getId());
			while(s.hasNext())
			{
				currentTrie.add(s.next());				
			}
			
			s.close();
		}
		
	}
	
	public QualityChecker(List<Field> fields, String urlpath)
	{
		this.trieMap = new HashMap<Integer,Words>();
		
		for(Field f : fields)
		{
			if(f.getKnownData().trim().length() == 0)
				continue;
			
			trieMap.put(f.getId(), new Words());
			String path = urlpath + f.getKnownData();
			
			URL url = null;
			try
			{
				url = new URL(path);
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
				continue;
			}
			
			Scanner s = null;
			try
			{
				s = new Scanner(url.openStream()).useDelimiter(",");
			} catch (IOException e)
			{
				e.printStackTrace();
				continue;
			}
						
			
			while(s.hasNext())
			{
				String str = s.next();
				trieMap.get(f.getId()).add(str);				
			}
			
			s.close();
		}
	}
	public boolean isValidIndexerInput(String enteredStr, Field f)
	{
		enteredStr = enteredStr.toLowerCase();
		
		return !trieMap.containsKey(f.getId()) || trieMap.get(f.getId()).find(enteredStr) != null;
	}
	
	public TreeSet<String> getSuggestions(String enteredStr, Field f)
	{
		TreeSet<String> set = trieMap.get(f.getId()).getSimilarWordList(enteredStr.toLowerCase());

		return set;
	}
}
