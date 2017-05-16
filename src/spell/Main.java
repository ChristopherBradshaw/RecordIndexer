package spell;

import java.io.IOException;

import spell.ISpellCorrector.NoSimilarWordFoundException;

/**
 * A simple main class for running the spelling corrector. This class is not
 * used by the passoff program.
 */
public class Main
{
	/**
	 * Give the dictionary file name as the first argument and the word to
	 * correct as the second argument.
	 */
	public static void main(String[] args) throws NoSimilarWordFoundException,
			IOException
	{

		String dictionaryFileName = null;
		String inputWord = null;

		if (args.length == 0)
		{
			dictionaryFileName = "src/words.txt";
			inputWord = "bry";
		} else
		{
			dictionaryFileName = args[0];
			inputWord = args[1];
		}

		/**
		 * Create an instance of your corrector here
		 */
		ISpellCorrector corrector = new SpellCorrector();

		try
		{
			corrector.useDictionary(dictionaryFileName);
		} catch (IOException e)
		{
			System.exit(0);
		}

		String suggestion = corrector.suggestSimilarWord(inputWord);

		System.out.println("Suggestion is: " + suggestion);
	}

}
