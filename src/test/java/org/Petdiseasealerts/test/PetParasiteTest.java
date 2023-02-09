package org.Petdiseasealerts.test;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import org.apache.commons.io.FileUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;

import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class PetParasiteTest {

	EdgeDriver driver;
	JavascriptExecutor js;

	@BeforeMethod
	public void setUp() {
		driver = new EdgeDriver();
		js = (JavascriptExecutor) driver;
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
		String url = "https://petdiseasealerts.org/forecast-map#/";
		driver.get(url);
	}

	@AfterMethod
	public void tearDown() {
		driver.quit();
	}

	@Test(priority = 1)
	public void getAllRegions() {

		WebElement frameId = driver.findElement(By.xpath("//iframe[contains(@id,'map-instance-')]"));
		driver.switchTo().frame(frameId);
		String xpathRegion = "//*[@class='region']/*/*";
		List<WebElement> elements = driver.findElements(By.xpath(xpathRegion));
		for (int i = 0; i < elements.size(); i = i + 2) {
			js.executeScript("arguments[0].scrollIntoView(true);", elements.get(i));
			String region = elements.get(i).getAttribute("name");
			System.out.println(region);
		}

	}

	@Test(dataProvider = "getStates", priority = 2)
	public void viewForecastDataTest(String region) {

		try {
			String dir = System.getProperty("user.dir");
			String fileName = dir + "/screenshot/" + region + ".png";
			System.out.println(fileName);
			WebElement frameId = driver.findElement(By.xpath("//iframe[contains(@id,'map-instance-')]"));
			driver.switchTo().frame(frameId);
			String xpathRegion = "//*[@class='region']/*/*[@name='" + region + "']";
			System.out.println("xpathRegion: " + xpathRegion);
			WebElement state = driver.findElement(By.xpath(xpathRegion));
			js.executeScript("arguments[0].scrollIntoView(true);", state);
			state.click();
			Thread.sleep(3000);
			// Assert breadCrum
			WebElement breadCrumText = driver
					.findElement(By.xpath("// ul[@class='breadcrumb']/*/span[contains(text(),'" + region + "')]"));
			js.executeScript("arguments[0].scrollIntoView(true);", breadCrumText);
			Assert.assertTrue(breadCrumText.isDisplayed());
			// Take Screenshot
			File file = driver.getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(file, new File(fileName));
		} catch (WebDriverException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@DataProvider
	public Object[] getStates()
	{
		Object[] arr = { "California", "Florida", "New York", "Maryland" };
		return arr;
	}

}
