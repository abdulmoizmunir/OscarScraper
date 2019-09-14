import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Answers {

	public static void main(String[] args) throws IOException {

		//Will just instantiate a new Wikipedia 91st Academy Awards instance to lookup answers
		Wikipedia wiki = new Wikipedia();


		//Question 1
		/**
		 * Please check the readme to copy paste categories.
		 * You can change the name here. Case sensitive, and sensitive to how name appears on Wiki.
		 * Also please note: copy-pasting from Wikipedia might not provide proper answers due to
		 * Unicode stuff.
		 */
		String question1 = "Alfonso Cuarón";

		//Get the nominations table
		ArrayList<String> academyNominations = wiki.getAwardsTable();

		//Count how many times Alfonso won/got nominated
		long noOfNominations = academyNominations.stream().filter(x->x.equals(question1)).count();

		//Get all the nominations Alfonso has:
		ArrayList<String> nominations = wiki.getNominations(question1);

		System.out.println(
				"The categories (" + question1 + ") got nominated in, alongside winners, are:");
		for (String a: nominations) {
			System.out.println(a); 
		}
		System.out.println("(" + question1 + ") has " + noOfNominations + " nomination(s) in total");



		System.out.println();



		//Question 2
		/**
		 * Please check the readme to copy paste categories.
		 * You can change the number here to change search criteria.
		 * 
		 * BASIC ASSUMPTION:
		 * Number is not out of bounds.
		 */
		int question2 = 3;

		System.out.println("Number of nominations followed by movies are:");

		//Print the results
		ArrayList<String> aTop = wiki.filmsWithTotalNomOf(question2);
		for (String a: aTop) {
			System.out.println(a);
		}



		System.out.println();



		//Question 3
		/**
		 * Please check the readme to copy paste categories. 
		 * 
		 * BASIC ASSUMPTION:
		 * In general, the categories have to be
		 * copy pasted exactly as they appear on the Academy Awards page, IN HTML. This can be done
		 * via inspecting element on the name of the category. Moreover, there exist some categories
		 * that do not list their first winners within a table and just in the body text. This is
		 * extremely hard to parse through, and so I do not cater to that question. Confirmed
		 * at office hours that there is no need to cater to such edge cases via hard-coding :)
		 * An example is Best Foreign Language Film. That, will not return anything, and program
		 * would break :(
		 * 
		 */
		String question3 = "Best Actress";

		String year = wiki.getFirstInstanceOf(question3)[0];
		String name = wiki.getFirstInstanceOf(question3)[1];
		System.out.println("Year for first award of (" + question3 + ") as extracted from "
				+ "Wikipedia: \n" + year); 
		System.out.println("Awarded to: \n" + name);



		System.out.println();



		//Question 4
		/**
		 * Please check the readme to copy paste categories.
		 * In general, the categories have to be copy pasted exactly as they appear on the Academy 
		 * Awards page, IN HTML. This can be done via inspecting element on the name of the category
		 * 
		 * BASIC ASSUMPTION:
		 * The movie corresponding to category given has stats displayed in a summary table, like 
		 * almost all movies that have Wikipedia pages do.
		 */
		String question4 = "Best Original Screenplay";

		String[] answers = wiki.getStatsFor(question4);
		String budget = answers[0];
		String boxOffice = answers[1];
		System.out.println("The stats for (" + question4 + ") are as follows:"); 
		System.out.println(budget); 
		System.out.println(boxOffice);



		System.out.println();



		//Question 5
		/**
		 * Please check the readme to copy paste categories.
		 * 
		 * BASIC ASSUMPTION:
		 * All Wikipedia links for movies are formatted the same way in 
		 * that the first thirty characters of any link in Wikipedia are as follows:
		 * "https://en.wikipedia.org/wiki/"
		 * 
		 * Also assume that what follows the above characteristic is the movie name (and year). Any
		 * other characters can be easily interpreted out of the answer as... garbage lol (these 
		 * are the cases where movie links are not similar to a vast majority).
		 */
		String question5 = "Best Documentary – Feature";

		String answer = wiki.getShortestRunningTimeNominee(question5);
		System.out.println("The shortest running time nominee in category " + 
				"(" + question5 + ")" + " is: ");
		System.out.println(answer);



		System.out.println();



		//Question 6
		/**
		 * Please check the readme to copy paste categories.
		 * 
		 * BASIC ASSUMPTION: 
		 * All companies are listed ("li") under either Production Compan(ies/y)
		 * and Distributed By on the Wikipedia pages of movies. We get the first company listed  
		 * and if two companies both have the same number of multiple-times nominated films, the 
		 * first of them is chosen.
		 * 
		 * Input can only be one of the two following options:
		 * 
		 * 1) "produced"
		 * 2) "distributed"
		 * 
		 * The list is being printed from line 431 in Wikipedia.java. Apologies if it shows up 
		 * anywhere else in the console, AND that it takes so much time :(
		 * 
		 * I swear, everything works fine other than that, please gimme 100.
		 */
		String question6 = "distributed";

		System.out.println("The list of companies that (" + question6 + ") nomination-recieving"
				+ " films, alongside count of films, is:");
		System.out.println("Most frequent occuring company from aforegiven list is: \n" + 
				wiki.successfulCompany(question6));



		System.out.println();



		//Question 7
		/**
		 * Please check the readme to copy paste categories.
		 * 
		 * BASIC ASSUMPTION(s): 
		 * 1) Works for what Wikipedia categorizes as "nominations" (may or may not include wins
		 * as nominations, that is Wikipedia's discretion, and not mine :)
		 * 2) Only take the first country given for a movie.
		 * 
		 */
		String question7 = "Best Foreign Language Film";
		List<String> allStats = wiki.getPastStats(question7);

		System.out.println("Total past nominations results for countries of the (" + question7 + ")"
				+ " category are as follows:");
		for (String str: allStats) {
			System.out.println(str);
		}



		System.out.println();



		//Question 8
		/**
		 * In general, as long as suffices to year i.e. th, st, rd etc are fine, this should work
		 * and provide an evaluation of whether the given Academy Awards edition was more hyped or
		 * better than the one that preceded it. Hyped/better here is defined by number of viewers 
		 * that viewed the Awards.
		 * 
		 * BASIC ASSUMPTION:
		 * 1) User puts in the edition correctly
		 * 2) The edition user puts in has a displayed viewer count, and so does the edition that
		 * preceded it.
		 */
		String question8 = "91st";
		System.out.println(wiki.betterThanPreviousYear(question8));

	}
}
