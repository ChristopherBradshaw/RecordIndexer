package spell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import spell.ISpellCorrector.NoSimilarWordFoundException;

public class Words implements ITrie
{
	private WordNode root;
	private int nodeCount;
	private int wordCount;
	private static final String REGEX = "[a-zA-Z]+";
	private TreeSet<String> wordSet;

	private final boolean DEBUG = false;

	public Words()
	{
		this.root = new WordNode();
		this.root.setParent(null);
		this.root.setValue(-1);

		this.nodeCount = 1;
		this.wordCount = 0;

		this.wordSet = new TreeSet<String>();
	}

	public String getSimilarWord(String str) throws NoSimilarWordFoundException
	{
		str = str.toLowerCase();
		if (find(str) != null)
			return str;

		@SuppressWarnings("unchecked")
		TreeSet<String> tmpWordSet = (TreeSet<String>) wordSet.clone();

		filterEditDistance(str, tmpWordSet); // Eliminate all words with edit
												// distances > 1mbe
		filterFrequency(tmpWordSet); // Only keep strings with highest frequency

		String similarStr = "";

		if (tmpWordSet.size() != 0) // Found a similar word at edit size 1
		{
			similarStr = tmpWordSet.first();
		} else
		{
			TreeSet<String> words = generatePossibleStrings(str); // Get all
																	// strings
																	// with edit
																	// distance
																	// of one
																	// from
																	// input
																	// string
			TreeSet<String> resultWords = new TreeSet<String>(); //

			for (String s : wordSet)
			{
				for (String generatedStr : words)
				{
					if (this.getDeletionDistanceOne(s, generatedStr)
							|| this.getTranspositionDistanceOne(s, generatedStr)
							|| this.getAlterationDistanceOne(s, generatedStr)
							|| this.getInsertionDistanceOne(s, generatedStr))
						resultWords.add(s);
				}
			}

			filterFrequency(resultWords);
			if (resultWords.size() != 0)
			{
				return resultWords.first();
			} else
			{
				throw new NoSimilarWordFoundException();
			}
		}

		return similarStr;
	}

	// Added in for RecordIndexer
	public TreeSet<String> getSimilarWordList(String str)
	{
		str = str.toLowerCase();

		@SuppressWarnings("unchecked")
		TreeSet<String> tmpWordSet = (TreeSet<String>) wordSet.clone();
		
		filterEditDistance(str, tmpWordSet); // Eliminate all words with edit
												// distances > 1mbe

		TreeSet<String> words = generatePossibleStrings(str); // Get all strings
																// with edit
																// distance of
																// one from
																// input string
		
		TreeSet<String> resultWords = new TreeSet<String>(); //

		for (String s : wordSet)
		{
			for (String generatedStr : words)
			{
				if (this.getDeletionDistanceOne(s, generatedStr)
						|| this.getTranspositionDistanceOne(s, generatedStr)
						|| this.getAlterationDistanceOne(s, generatedStr)
						|| this.getInsertionDistanceOne(s, generatedStr))
					{
						resultWords.add(s);
					}
			}
		}

		tmpWordSet.addAll(resultWords);

		return tmpWordSet;
	}

	private void filterEditDistance(String str, TreeSet<String> tmpWordSet)
	{
		ArrayList<String> toRemove = new ArrayList<String>();

		for (String s : tmpWordSet) // If s does not have an edit distance of
									// one, remove it
		{
			if (!(this.getDeletionDistanceOne(s, str)
					|| this.getTranspositionDistanceOne(s, str)
					|| this.getAlterationDistanceOne(s, str) || this
						.getInsertionDistanceOne(s, str)))
				toRemove.add(s);
		}

		for (String s : toRemove)
			tmpWordSet.remove(s);
	}

	private void filterFrequency(TreeSet<String> tmpWordSet)
	{
		int highFreq = -1;
		for (String s : tmpWordSet) // Find highest frequency
		{
			if (find(s).getValue() > highFreq)
				highFreq = find(s).getValue();
		}

		ArrayList<String> toRemove = new ArrayList<String>();
		for (String s : tmpWordSet) // Mark all strings with lower frequencies
		{
			if (highFreq != find(s).getValue())
				toRemove.add(s);
		}

		for (String s : toRemove)
			// Remove all strings that are marked
			tmpWordSet.remove(s);
	}

	public void debug(String message)
	{
		if (!DEBUG)
			return;

		// If \n, \t, " "* etc
		if (message.trim().length() == 0)
		{
			System.out.println(message);
		} else
		{
			System.out.println("--DEBUG-- " + message);
		}
	}

	/**
	 * Adds the specified word to the trie (if necessary) and increments the
	 * word's frequency count
	 * 
	 * @param word
	 *            The word being added to the trie
	 */
	@Override
	public void add(String word)
	{
		word = word.toLowerCase();
		debug("Attempting to add: " + word);

//		// Invalid word?
//		if (!word.matches(REGEX))
//		{
//			debug(word + " failed to match regex: " + REGEX);
//			return;
//		}

		if (find(word) == null)
		{
			this.wordSet.add(word);
		} else
		{
			debug(word + " already encountered.");
		}

		WordNode currentNode = root;

		for (int i = 0; i < word.length(); ++i) // Traverse trie, add in string
												// if not contained
		{
			char tmpChar = word.charAt(i);

			if (currentNode.getChild(tmpChar) == null) // First occurrence of
														// character in trie
			{
				debug(tmpChar + " added to trie.");
				currentNode.setChild(tmpChar);
				++this.nodeCount;
			}

			currentNode = currentNode.getChild(tmpChar);
		}

		if (!currentNode.isWord())
		{
			debug("Word set: " + word);
			++this.wordCount;
			currentNode.setWord(true);
		}

		currentNode.setValue(currentNode.getValue() + 1);

		debug("Total words: " + wordCount);
		debug("Total nodes: " + nodeCount);
		debug("Occurances of " + word + ": " + currentNode.getValue());

		debug("\n\n");
	} // End add

	/**
	 * Searches the trie for the specified word
	 * 
	 * @param word
	 *            The word being searched for
	 * 
	 * @return A reference to the trie node that represents the word, or null if
	 *         the word is not in the trie
	 */
	@Override
	public INode find(String word)
	{
		word = word.toLowerCase();

		debug("Finding: " + word);

		WordNode tmpNode = root;
		try
		{
			for (int i = 0; i < word.length(); ++i)
			{
				char c = word.charAt(i);
				debug("On: " + c);
				tmpNode = tmpNode.getChild(c);

				if (tmpNode == null)
				{
					debug("tmpNode is null. Breaking..");
					break;
				}
			}
		} catch (Exception e)
		{
			debug(word + " not found in trie.");
			return null;
		}

		debug("\n\n");
		if (tmpNode == null || !tmpNode.isWord())
		{
			debug("After searching, tmpNode == null or !isWord(tmpNode)");
			return null;
		}

		return (tmpNode.isWord() ? tmpNode : null);
	}

	/**
	 * Returns the number of nodes in the trie
	 * 
	 * @return The number of nodes in the trie
	 */
	@Override
	public int getWordCount()
	{
		return this.wordCount;
	}

	/**
	 * The toString specification is as follows: For each word, in alphabetical
	 * order: <word>\n
	 */
	@Override
	public String toString()
	{

		StringBuffer strBuff = new StringBuffer();
		for (String s : this.wordSet)
		{
			strBuff.append(s + "\n");
		}

		return strBuff.toString();
	}

	@Override
	public int getNodeCount()
	{
		return this.nodeCount;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + nodeCount;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
		result = prime * result + wordCount;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Words other = (Words) obj;
		if (nodeCount != other.nodeCount)
			return false;
		if (root == null)
		{
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (wordCount != other.wordCount)
			return false;
		return true;
	}

	private boolean getDeletionDistanceOne(String validStr, String str)
	{

		int sizeDiff = validStr.length() - str.length();

		if (sizeDiff <= 0) // T should be a subset of characters in s
			return false;

		for (int i = 0; i < validStr.length(); i++)
		{
			String tmpStr = validStr.substring(0, i)
					+ validStr.substring(i + 1, validStr.length());

			if (tmpStr.equalsIgnoreCase(str))
				return true;
		}

		return false;
	}

	private boolean getTranspositionDistanceOne(String validStr, String str)
	{
		for (int i = 0; i < validStr.length() - 1; i++)
		{
			String tmpStr = validStr.substring(0, i) + validStr.charAt(i + 1)
					+ validStr.charAt(i);

			if (i + 1 < validStr.length() - 1)
				tmpStr += validStr.substring(i + 2);

			if (tmpStr.equalsIgnoreCase(str))
				return true;
		}

		return false;
	}

	private boolean getAlterationDistanceOne(String validStr, String str)
	{
		if (validStr.length() != str.length())
			return false;

		validStr = validStr.toLowerCase();
		str = str.toLowerCase();

		boolean foundDiff = false;

		for (int i = 0; i < validStr.length(); i++) // validStr and str have
													// same length
		{
			if (validStr.charAt(i) != str.charAt(i))
			{
				if (foundDiff) // Already seen a mismatch?
					return false;

				foundDiff = true;
			}
		}

		return foundDiff;
	}

	private boolean getInsertionDistanceOne(String validStr, String str)
	{
		if (str.length() - validStr.length() != 1)
			return false;

		int validStrIndex = 0;
		boolean foundDiff = false;

		for (int i = 0; i < str.length(); i++)
		{
			if (validStrIndex < validStr.length()
					&& str.charAt(i) == validStr.charAt(validStrIndex))
			{
				validStrIndex++;
				continue;
			} else
			{
				if (foundDiff)
					return false;

				foundDiff = true;
			}
		}

		return foundDiff;
	}

	private TreeSet<String> generatePossibleStrings(String str)
	{
		TreeSet<String> treeSet = new TreeSet<String>();

		// Generate deletions
		for (int i = 0; i < str.length(); i++)
		{
			String tmpStr = str.substring(0, i) + str.substring(i + 1);
			treeSet.add(tmpStr);
		}

		// Generate transposition
		for (int i = 0; i < str.length() - 1; i++)
		{
			String tmpStr = str.substring(0, i) + str.charAt(i + 1)
					+ str.charAt(i);

			if (i + 1 < str.length() - 1)
				tmpStr += str.substring(i + 2);

			treeSet.add(tmpStr);
		}

		// Generate alteration
		for (int i = 0; i < str.length(); i++)
		{
			for (int j = 0; j < 26; j++)
			{
				if (((char) j + 97) == str.charAt(i))
					continue;

				String tmpStr = str.substring(0, i) + (char) (j + 97)
						+ str.substring(i + 1);

				treeSet.add(tmpStr);
			}
		}

		// Generate insertion
		for (int i = 0; i < str.length() + 1; i++)
		{
			for (int j = 0; j < 26; j++)
			{

				String tmpStr = str.substring(0, i) + (char) (j + 97)
						+ str.substring(i);

				treeSet.add(tmpStr);
			}
		}
		return treeSet;
	}
}
