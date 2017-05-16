package spell;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class SpellCorrector implements ISpellCorrector
{
	private Words words;
	private Scanner in;

	public SpellCorrector()
	{
		words = null;
		in = null;
	}

	/**
	 * Tells this <code>ISpellCorrector</code> to use the given file as its
	 * dictionary for generating suggestions.
	 * 
	 * @param dictionaryFileName
	 *            File containing the words to be used
	 * @throws IOException
	 *             If the file cannot be read
	 */
	@Override
	public void useDictionary(String dictionaryFileName) throws IOException
	{
		this.in = new Scanner(new File(dictionaryFileName));
		this.words = new Words();

		String tmpStr;
		try
		{
			tmpStr = this.in.next();
		} catch (NoSuchElementException e)
		{
			tmpStr = null;
		}
		
		while (tmpStr != null)
		{
			words.add(tmpStr);

			try
			{
				tmpStr = this.in.next();
			} catch (NoSuchElementException e)
			{
				tmpStr = null;
			}
		}
		
	}

	@Override
	/**
	 * Suggest a word from the dictionary that most closely matches
	 * <code>inputWord</code>
	 * 
	 * @param inputWord
	 * @return The suggestion
	 * @throws NoSimilarWordFoundException
	 *             If no similar word is in the dictionary
	 */
	public String suggestSimilarWord(String inputWord)
			throws NoSimilarWordFoundException
	{
		if (words == null)
			return null;

		return words.getSimilarWord(inputWord.toLowerCase());
	}

}
