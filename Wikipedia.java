import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Important: I have tried to help with the reading of code by trying to add "///" to  
 * separate functions for every question. This will be seen below.
 */


/**
 * This is HW03. Wikipedia Academy-Awards Scraper.
 * @author abdulmoizmunir
 *
 */
public class Wikipedia {
	private String baseUrl;
	private Document currentDoc;
	private URLGetter page;
	ArrayList<String> s;


	/**
	 * Constructor that initializes the base URL and loads the document produced from that URL
	 */
	public Wikipedia() {
		page = new URLGetter("https://en.wikipedia.org/wiki/91st_Academy_Awards");
		s = page.getContents();
		this.baseUrl = "https://en.wikipedia.org/wiki/91st_Academy_Awards";
		try {
			this.currentDoc = getDOMFromURL(baseUrl);
			// System.out.println(this.currentDoc);

		} catch (IOException e) {
			System.out.println("Could not get the Wikipedia home page!");
		}
	}


	/**
	 * Method to get a Document from a String  URL
	 * @param u(RL)
	 * @return Document
	 * @throws IOException
	 */
	public Document getDOMFromURL(String u) throws IOException {
		URL url = new URL(u);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		StringBuilder sb = new StringBuilder();
		String curr = in.readLine();
		while(curr != null) {
			sb.append(curr);
			curr = in.readLine();
		}
		return Jsoup.parse(sb.toString());
	}

///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Gets the main awards table on any Academy Awards' page.
	 * @return
	 */
	public ArrayList<String> getAwardsTable() {
		ArrayList<String> ans = new ArrayList<>();
		Elements banner = currentDoc.getElementsByClass("wikitable");
		Elements words = banner.select("a").attr("title", "");
		for(Element word: words) {
			if(!word.text().isEmpty()) { 
				ans.add(word.text());
			}
		}
		return ans;
	}


	/**
	 * 
	 * @param A string that represents the keyword we're looking for. Returns list of nominations.
	 * @return
	 */
	public ArrayList<String> getNominations(String a) {
		ArrayList<String> ans = new ArrayList<>();
		Elements banner = currentDoc.getElementsByClass("wikitable");
		Elements words = banner.select("td").attr("div style", "");
		for(Element word: words) {
			if(!word.text().isEmpty()) {
				if (word.text().contains(a)) {
					ans.add(word.text().split("–")[0]);
				}
			}
		}
		return ans;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Check for films with nominations matching a given integer constraint.
	 * @param x, an integer constraint.
	 * @return all films that are >= constraint
	 */
	public ArrayList<String> filmsWithTotalNomOf(int x) {
		ArrayList<String> ans = new ArrayList<>();
		Elements banner = currentDoc.getElementsByTag("caption");

		Element weWant = null;
		for (Element tag: banner) {
			if (tag.text().equals("Films with multiple nominations")) {
				weWant = tag.nextElementSibling();
			}
		}

		Elements words = weWant.select("td").attr("div style", "");

		boolean check = false;

		//We assume all the table of multiple film nominations always start with a digit
		for(Element word: words) {
			if(!word.text().isEmpty()) {
				if (word.text().matches("[\\d]+[A-Za-z]?")) {
					if (Integer.parseInt(word.text()) >= x) {
						check = true;
					} else {
						check = false;
					}
				}

				if (check) {
					ans.add(word.text());
				}
			}
		}
		return ans;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param str, which is a string, most likely a category.
	 * @return A string array containing the first instance and recipient of a certain award.
	 * @throws IOException
	 */
	public String[] getFirstInstanceOf(String str) throws IOException {
		//First index of array will contain year, second will contain name.
		String[] arr = new String[2];

		Elements wanted = currentDoc.getElementsByTag("a");

		//Go to the page with answer
		Document pageNeeded = null;
		for (Element tag: wanted) {
			if (tag.text().equals(str)) {
				String redirect = tag.select("a").attr("href").toString();
				String linkRequired = "https://en.wikipedia.org" + redirect;
				pageNeeded = getDOMFromURL(linkRequired);
				break;
			}
		}

		//Look for all tables on that page
		Elements allTables = pageNeeded.getElementsByTag("table");

		//Find the table that we want
		Element tableReq = null;
		for (Element table: allTables) {
			if (table.getElementsByTag("tr").first().text().contains("Year")) {
				tableReq = table;
				break;
			}
		}

		Element firstRow = tableReq.getElementsByTag("tr").get(1);

		Elements head = firstRow.select("th");
		Element year = null;
		Element name = null;
		if (head.isEmpty()) {
			if (firstRow.text().charAt(4) == 's') {
				name = tableReq.getElementsByTag("tr").get(2).select("td").first();
				year = tableReq.getElementsByTag("tr").get(2).select("th").first();
			} else {
				name = tableReq.getElementsByTag("tr").get(2).select("td").first();
				year = firstRow.select("td").first();
			} 
		} else {
			year = firstRow.select("th").first();
			name = firstRow.select("td").first();
		}

		arr[0] = year.text();
		arr[1] = name.text();
		return arr;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param str, which is a string, most likely a category of (film).
	 * @return String array containing budget and box office revenue informations
	 * @throws IOException
	 */
	public String[] getStatsFor(String str) throws IOException {
		//First index of array will contain year, second will contain name.
		String[] arr = new String[2];

		Elements wanted = currentDoc.getElementsByTag("a");

		int i = 0;
		//Go to the page with answer
		Document pageNeeded = null;
		for (Element tag: wanted) {
			if (tag.text().equals(str)) {
				String redirect = wanted.get(i + 1).attr("href").toString();
				String linkRequired = "https://en.wikipedia.org" + redirect;
				pageNeeded = getDOMFromURL(linkRequired);
				break;
			}
			i++;
		}

		//Look for all tables on that page
		Elements allRows = pageNeeded.getElementsByTag("tr");

		//Find the table that we want
		String budget = "";
		String boxOffice = "";
		for (Element row: allRows) {
			if (row.text().contains("Budget")) {
				budget = row.text();
			} else if (row.text().contains("Box office")) {
				boxOffice = row.text();
			}
		}

		arr[0] = budget.split("\\[")[0];
		arr[1] = boxOffice.split("\\[")[0];
		return arr;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Provide a movie category and will return the shortest running time for all nominees in that
	 * respective categories for the current Academy Awards edition.
	 * @param str
	 * @return 
	 * @throws IOException
	 */
	public String getShortestRunningTimeNominee(String str) throws IOException {

		ArrayList<String> links = new ArrayList<>();

		Elements wanted = 
				currentDoc.getElementsByAttributeValue("style", "vertical-align:top; width:50%;");

		Elements moviesLinks = null;

		//The link we need
		for (Element e: wanted) {
			if (e.text().contains(str)) {
				moviesLinks = e.getElementsByTag("i");
			}
		}

		//Add links to list of links
		for (int i = 0; i < moviesLinks.size(); i++) {

			String hrefOfMovie = moviesLinks.get(i).getElementsByTag("a").attr("href");
			String link = ("https://en.wikipedia.org" + hrefOfMovie);

			if (link.equals("https://en.wikipedia.org")) { //getting rid of non-movie links
				continue;
			} else {
				links.add(link);
			}

		}

		int[] allRunningTimes = new int[links.size()];

		for (int i = 0; i < links.size(); i++) {
			allRunningTimes[i] = runningTime(links.get(i));
		}

		int shortestTimeIndex = minIndex(allRunningTimes);
		int runningTime = allRunningTimes[shortestTimeIndex];


		return links.get(shortestTimeIndex).substring(30).replaceAll("_", " ") + "\n" +
		"(Total time: " + runningTime + " minutes)";
	}

	/**
	 * Helper function:
	 * @param link (of a movie)
	 * @return the running time
	 * @throws IOException
	 */
	public int runningTime(String link) throws IOException {

		Document curr = getDOMFromURL(link);

		//Look for all tables on that page
		Elements allRows = curr.getElementsByTag("tr");

		//Find the table that we want
		int answer = 0;
		for (Element row: allRows) {
			if (row.text().contains("Running time")) {
				answer = Integer.parseInt((row.text().split(" ")[2]));
			}
		}
		return answer;
	}

	/**
	 * Helper function:
	 * @param an integer array (of movie times, in this case)
	 * @return index with the minimum value (i.e. shortest running time index)
	 */
	private static int minIndex(int[] array) {
		int index = 0;
		for (int i = 1; i < array.length; i++) {
			if ((array[i - 1] < array[i]) && (array[index] > array[i - 1])) {
				index = i - 1;
			} 
			else if (array[index] > array[i]) {
				index = i;
			} 
		}
		return index;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param str (either "produced" or "distributed")
	 * @return The most frequent occurring company (three helper function are below this function, & 
	 * those are used to make this work)
	 * @throws IOException
	 */
	public String successfulCompany(String str) throws IOException {

		ArrayList<String> links = new ArrayList<>();
		String answer = "";

		Elements banner = currentDoc.getElementsByTag("caption");

		//Get into the multiple nominated films' table
		Element weWant = null;
		for (Element tag: banner) {
			if (tag.text().equals("Films with multiple nominations")) {
				weWant = tag.nextElementSibling();
			}
		}

		//Get just the films from that table
		Elements moviesWithMultipleNom = weWant.getElementsByTag("i");

		//Get links of all those films
		for (int i = 0; i < moviesWithMultipleNom.size(); i++) {
			String hrefOfMovie = moviesWithMultipleNom.get(i).getElementsByTag("a").attr("href");
			String link = ("https://en.wikipedia.org" + hrefOfMovie);
			links.add(link);
		}


		String[] companies = new String[links.size()];

		//Take action
		if (str.equals("distributed")) {
			for (int i = 0; i < companies.length; i++) {
				companies[i] = distributer(links.get(i));
			}
			answer = mostFrequent(companies);
		} else if (str.equals("produced")) {
			for (int i = 0; i < companies.length; i++) {
				companies[i] = productionCompany(links.get(i));
			}
			answer = mostFrequent(companies);
		} else {
			answer = "Error: the input can only be either 'produced' or 'distributed'";
		}

		return answer;
	}


	/**
	 * Helper function that returns the most frequent string in an array of strings.
	 * @param String array, called "array"
	 * @return most frequent string in "array"
	 */
	public String mostFrequent(String[] array) {

		ArrayList<String> string;
		string = new ArrayList<>(Arrays.asList(array));

		Map<String, Integer> wordMap = new HashMap<String, Integer>();

		//Simple HashMapping string and key(s), keys being the number of times these strings exist
		for (String st : string) {
			if (st.equals("")) {
				continue;
			}
			String input = st.toUpperCase();
			if (wordMap.get(input) != null) {
				Integer count = wordMap.get(input) + 1;
				wordMap.put(input, count);
			} else {
				wordMap.put(input, 1);
			}
		}

		System.out.println(wordMap);
		String maxEntry = Collections.max(wordMap.entrySet(), Map.Entry.comparingByValue()).getKey();

		return maxEntry;
	}


	/**
	 * Output the distribution name of a company, given the link of a movie.
	 * @param link, for the movie.
	 * @return First distribution company of the movie.
	 * @throws IOException
	 */
	public String distributer(String link) throws IOException {

		Document curr = getDOMFromURL(link);

		//Look for all tables on that page
		Elements allRows = curr.getElementsByTag("tr");

		//Find the table that we want
		String answer = "";
		for (Element row: allRows) {
			if (row.text().contains("Distributed by")) {

				String name = (row.text().substring(15));

				if (name.contains("[")) {
					answer = name.substring(0, name.indexOf("[")).trim();
				} else if (name.contains("(")) {
					answer = name.substring(0, name.indexOf("(")).trim();
				} else {
					answer = name.trim();
				}
			}
		}
		return answer;
	}

	/**
	 * Output the production name of a company, given the link of a movie.
	 * @param link, for the movie.
	 * @return First production company of the movie.
	 * @throws IOException
	 */
	public String productionCompany(String link) throws IOException {

		Document curr = getDOMFromURL(link);

		//Look for all tables on that page
		Elements allRows = curr.getElementsByTag("tr");

		//Find the table that we want
		String name = "";
		Elements allProductionComp = null;

		for (Element e: allRows) {
			if (e.text().contains("Production")) {
				allProductionComp = e.getElementsByTag("li");
				break;
			}
		}

		//If there's no production company listed, then lol, exit.
		if (allProductionComp == null) {
			return "";
		}

		//Get the first production company, as that is all we'll be making our comparisons with
		for (Element item: allProductionComp) {
			name = item.text();
			break;
		}

		//Refine the name of the Production company a bit.
		if (name.contains("[")) {
			name = name.substring(0, name.indexOf('['));
		}

		return name;
	}

////////////////////////////////////////////////////////////////////////////////////////////////// 

	/**
	 * Provide a movie category and outputs total nominations until present time for the countries
	 * that given movie category's nominees are from.
	 * @param str, which is a category for movies.
	 * @return List of stats for all film nominees'countries
	 * @throws IOException
	 */
	public List<String> getPastStats(String str) throws IOException {

		ArrayList<String> links = new ArrayList<>();

		Elements wanted = 
				currentDoc.getElementsByAttributeValue("style", "vertical-align:top; width:50%;");


		Elements moviesLinks = null;

		//The movie links we need
		for (Element e: wanted) {
			if (e.text().contains(str)) {
				moviesLinks = e.getElementsByTag("i");
			}
		}

		//Store movie names for later use, and presentation purposes.
		String[] movieNames = new String[moviesLinks.size()];
		for (int i = 0; i < movieNames.length; i++) {
			movieNames[i] = moviesLinks.get(i).text();
		} 

		//Get movie links and add them to list of links
		for (int i = 0; i < moviesLinks.size(); i++) {
			String hrefOfMovie = moviesLinks.get(i).getElementsByTag("a").attr("href");
			String link = ("https://en.wikipedia.org" + hrefOfMovie);

			if (link.equals("https://en.wikipedia.org")) { //getting rid of non-movie links
				continue;
			} else {
				links.add(link);
			}
		}

		List<String> answer = new ArrayList<String>();

		for (int i = 0; i < movieNames.length; i++) {
			String country = getCountry(links.get(i));
			answer.add("Movie: " + movieNames[i] + " | " + "Country: " + country + " | Total: " + 
					countryNominations(country));
		}

		return answer;
	}

	/**
	 * @param Provide the string name of a country, and outputs total nominations for that country
	 * @return the number of total nominations for given country up until present time
	 * @throws IOException
	 */
	public String countryNominations(String country) throws IOException {

		//Special case, the country of United States:
		if (country.equals("United States")) {
			return "Since Academy Awards are national to the United States, its nominations'"
					+ "count is not kept track of :)";
		}

		/**
		 * This link always saves the updated nominations' information on every country, this is 
		 * the only place a hard-code makes our lives easier.
		 */
		Document curr = getDOMFromURL(
				"https://en.wikipedia.org/wiki/"
				+ "List_of_countries_by_number_of_Academy_Awards_for_Best_Foreign_Language_Film");

		//Get rows
		Elements allRows = curr.getElementsByTag("tr");

		//Find the country you're looking for
		Elements numbers = null;
		for (Element row: allRows) {
			if (row.text().contains(country)) {
				numbers = row.getElementsByTag("td");
				break;
			}
		}

		//Get the nominations for that country because we know
		String answer = numbers.get(2).text().replaceAll("[^0-9]", "");

		return answer;
	}

	/**
	 * Gets the country of a movie. The first one, if there are multiple.
	 * @param link of the movie we're talking about.
	 * @return name of country as a String.
	 * @throws IOException
	 */
	public String getCountry(String link) throws IOException {

		Document curr = getDOMFromURL(link);

		//Look for all tables on that page
		Elements allRows = curr.getElementsByTag("tr");



		//Find the table that we want
		String name = "";
		Elements countries = null;

		for (Element e: allRows) {
			if (e.text().contains("Country")) {
				countries = e.getElementsByTag("li");
				if (countries.size() == 0) {
					countries = e.getElementsByTag("td");
				}
				break;
			}
		}

		//If there's no Country listed, then look in another way
		if (countries == null) {
			return "";
		}

		//Get the first country listed
		for (Element e: countries) {
			name = e.text();
			break;
		}

		//Refine the name of the country a bit.
		if (name.contains("[")) {
			name = name.substring(0, name.indexOf('['));
		}

		return name;
	}


///////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param str, mostly likely an edition of the Academy Awards i.e 90th, 89th etc.
	 * @return a string evaluation of the provided edition of the Academy Awards in comparison
	 * to the one that preceded it.
	 * @throws IOException
	 */
	public String betterThanPreviousYear(String str) throws IOException {
		//First index of array will contain year, second will contain name.

		String currLink = "https://en.wikipedia.org/wiki/" + str + "_Academy_Awards";
		double curr = getNumberOfViewersFromLink(currLink);

		int prevYear = Integer.parseInt(str.substring(0, 2));
		prevYear = prevYear - 1;
		String str2 = ordinal(prevYear);

		String prevLink = "https://en.wikipedia.org/wiki/" + str2 + "_Academy_Awards";
		double prev = getNumberOfViewersFromLink(prevLink);

		String answer = "";

		if (prev > curr) {
			answer = "With " + prev + " million viewers in previous year, " + str + 
					" Academy Awards " + "(" + curr + ")" + " could've done much better!";
		} else if (curr > prev) {
			answer = "With " + prev + " million viewers in previous year, " + str + 
					" Academy Awards " + "(" + curr + " million)" + " were watched by much more "
					+ "folks!";
		} else {
			answer = "No way! This is rare :O \n" + "With " + prev + " viewers, between both years"
					+ " this is a tie!";
		}

		return answer;
	}

	/**
	 * Helper function that takes in an int and adds "th", "st", "rd", or "nd".
	 * Use this to produce proper href ending for previous year's Academy Awards' link.
	 * @param num, which is an integer.
	 * @return
	 */
	String ordinal(int num)
	{
		String[] suffix = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
		int m = num % 100;
		return String.valueOf(num) + suffix[(m > 3 && m < 21) ? 0 : (m % 10)];
	}

	/**
	 * Helper function helps return a double that represents the number of viewers/watchers.
	 * @param link
	 * @return double, number of viewers/watchers of an Academy Awards edition.
	 * @throws IOException
	 */
	public double getNumberOfViewersFromLink(String link) throws IOException {
		Document curr = getDOMFromURL(link);

		//Look for all tables on that page
		Elements allRows = curr.getElementsByTag("tr");

		//Find the table that we want
		double numViewers = 0;
		for (Element row: allRows) {
			if (row.text().contains("Ratings")) {
				numViewers = Double.parseDouble(row.text().split(" ")[1]);
			}
		}
		return numViewers;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////
}
