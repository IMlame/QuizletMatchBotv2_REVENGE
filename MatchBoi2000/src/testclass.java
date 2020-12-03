import java.awt.AWTException;
import java.awt.Robot;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class testclass {

	public static void main(String[] args) {
		System.setProperty("webdriver.chrome.driver", "chromedriver//chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments(
				"user-data-dir = C:\\Users\\Evan's PC\\AppData\\Local\\Google\\Chrome\\User Data\\Default");

		WebDriver driver = new ChromeDriver();

		driver.get(
				"https://quizlet.com/535189275/micromatch");
		
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		
		for(int i = 0; i < 100; i++) {
			int boxSelected = (int)(Math.random()*12) + 1;
			WebElement box = driver.findElement(By.xpath("//*[@id=\"MatchModeTarget\"]/div/div/div/div[2]/div/div/div[" + boxSelected + "]/div"));
			box.click();
		}

//		WebElement email = driver.findElement(By.id("identifierId"));
//		email.sendKeys("moomoocowmooo@gmail.com");
//
//		WebElement next = driver.findElement(By.id("identifierNext"));
//		next.click();
//			
//		driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
//		
//		WebElement password = driver.findElement(By.id("password"));
//		System.out.print("Element is visible? " + password.isDisplayed());
//		password.sendKeys("cowsarecool!");
//
//		WebElement next2 = driver.findElement(By.id("passwordNext"));
//		next2.click();


		 

	}

}
