import java.awt.AWTException;

import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Normalizer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.exec.util.StringUtils;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MatchMain {
	
	final static String RAW_LINK = "https://quizlet.com/11619425/barrons-ap-literature-vocabulary-flash-cards/";
	
	//cut raw link down to https://quizlet.com/_______/
	final static String QUIZLET_LINK = RAW_LINK.substring(0, RAW_LINK.substring(20, RAW_LINK.length()).indexOf("/") + 21);

	final static int NUMBER_OF_BOXES = 12;

	final static boolean ACCENT_CHECK = true;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(QUIZLET_LINK);

		String quizletHtml = getHTML(QUIZLET_LINK + "vocabulario-para-la-escuela-virtual-flash-cards/");

		// dictionary of words grabbed off quizlet, word then definition
		Hashtable<String, String> dict = mapWords(quizletHtml);

		// dictionary of box phrase, then clickable element, size should be
		// NUMBER_OF_BOXES
		Hashtable<String, WebElement> displayedBoxDict = new Hashtable<String, WebElement>();

		System.setProperty("webdriver.chrome.driver", "chromedriver//chromedriver.exe");

//		ChromeOptions options = new ChromeOptions();
//		options.addArguments(
//				"user-data-dir = C:\\Users\\Evan's PC\\AppData\\Local\\Google\\Chrome\\User Data\\Default");

		WebDriver driver = new ChromeDriver();

		// delay to finish loading words from quizlet
		driver.manage().timeouts().implicitlyWait(3000, TimeUnit.MILLISECONDS);

		driver.get(QUIZLET_LINK + "micromatch");

		while(true) {
			
			//detect if start button is available
			if(driver.findElements(By.xpath("/html/body/div[5]/div/div/div/div[2]/button")).size() > 0) {
				WebElement startGame = driver.findElement(By.xpath("/html/body/div[5]/div/div/div/div[2]/button"));
				startGame.click();
				
				//prepare on screen box dictionary for next round
				displayedBoxDict.clear();
				
				// load displayed boxes into dictionary
				for (int i = 0; i < NUMBER_OF_BOXES; i++) {
					WebElement box = driver.findElement(By.xpath(
							"//*[@id=\"MatchModeTarget\"]/div/div/div/div[2]/div/div/div[" + (i + 1) + "]/div/div/div/div"));
					WebElement clickable = driver.findElement(
							By.xpath("//*[@id=\"MatchModeTarget\"]/div/div/div/div[2]/div/div/div[" + (i + 1) + "]"));
					String boxText = ACCENT_CHECK ? removeAccents(box.getText(), ACCENT_CHECK) : box.getText();
					displayedBoxDict.put(boxText, clickable);
				}
				//slight delay to prevent mishaps
				driver.manage().timeouts().implicitlyWait(200, TimeUnit.MILLISECONDS);
				
				//run click script
				runBot(dict, displayedBoxDict);
			}
		}

	}

	public static void runBot(Hashtable<String, String> dict, Hashtable<String, WebElement> displayedBoxDict) {
		Enumeration displayedBoxes = displayedBoxDict.keys();
		while (displayedBoxes.hasMoreElements()) {
			String currentBox = displayedBoxes.nextElement().toString();
			String otherBox = dict.get(currentBox);
			if (otherBox != null) {
				displayedBoxDict.get(currentBox).click();
				displayedBoxDict.get(otherBox).click();
			}
		}
	}

	public static String getHTML(String urlString) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.addRequestProperty("User-Agent", "Mozilla/4.76");
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return (result.toString());
	}

	public static Hashtable<String, String> mapWords(String input) {
		Hashtable<String, String> dict = new Hashtable<String, String>();

		while (input.contains("\"word\":")) {
			input = input.substring((input.indexOf("\"word\":") + 8), input.length());
			String word = input.substring(0, input.indexOf("\","));

			input = input.substring((input.indexOf("\"definition\":") + 14), input.length());
			String definition = input.substring(0, input.indexOf("\","));
			System.out.println("word: " + removeAccents(word, ACCENT_CHECK) + " definition: " + removeAccents(definition, ACCENT_CHECK));
			dict.put(removeAccents(word, ACCENT_CHECK), removeAccents(definition, ACCENT_CHECK));
		}

		return dict;
	}

	public static String removeAccents(String input, boolean check) {
		if (check) {
			input = Normalizer.normalize(input, Normalizer.Form.NFD).toString();
			for (int i = 0; i < input.length(); i++) {
				// replace stupid questionmark that is not a questionmark
				if (input.charAt(i) == 769 || input.charAt(i) == 771) {
					input = input.substring(0, i) + "?" + input.substring(i + 1, input.length());
				}
				if (input.charAt(i) == 191) {
					input = input.substring(0, i) + "??" + input.substring(i + 1, input.length());
				}
			}
			input = input.replaceAll("\\\\u00f3", "o?");
			input = input.replaceAll("\\\\u00e1", "a?");
			input = input.replaceAll("\\\\u00ed", "i?");
			input = input.replaceAll("\\\\u00bf", "??");
			input = input.replaceAll("\\\\u00e9", "e?");
			input = input.replaceAll("\\\\u00fa", "u?");
			input = input.replaceAll("\\\\u00f1", "n?");

		}
		return input;
	}
}