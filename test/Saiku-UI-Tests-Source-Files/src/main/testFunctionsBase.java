/*
 *   Copyright 2013 Genius Digital Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package main;

import static org.testng.Assert.fail;

import java.awt.Robot;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.BeforeSuite;

/**
 * This class provides a number of functions which will be frequently called
 * by other classes
 * 
 * setup
 * login
 * crtQuery_DragandDrop
 * crtQuery
 * dragAndDrop
 * closeBrowser
 * takeScreenshot
 * deleteDir (BeforeSuite)
 */

public class TestFunctionsBase {

	protected String browser;
	protected String platform;
	protected RemoteWebDriver driver;
	protected WebDriverWait wait;
	private final String baseUrl = Config.BASE_URL;
	private final String seleniumUrl = Config.SELENIUM_URL;
	private final String username = Config.USERNAME;
	private final String password = Config.PASSWORD;
	private boolean acceptNextAlert = true;
	protected final StringBuffer verificationErrors = new StringBuffer();
	private final DesiredCapabilities capabilities = new DesiredCapabilities();
	private final int sp[] = Config.START_POINT;
	private final int dm[] = Config.DIMESION;
	private String screenshotPath;
	private String screenshotFile;

	/**
	 * Configures and launches a browser
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	public void setup(String browser, String platform) {
		System.out.println("setup"+browser+platform);

		//Browsers
		if(browser.equalsIgnoreCase("firefox")){
			capabilities.setBrowserName(DesiredCapabilities.firefox().getBrowserName());
		}

		if(browser.equalsIgnoreCase("iexplore")){
			capabilities.setBrowserName(DesiredCapabilities.internetExplorer().getBrowserName());
		}

		if(browser.equalsIgnoreCase("chrome")){
			capabilities.setBrowserName(DesiredCapabilities.chrome().getBrowserName());
		}

		//Platforms
		if(platform.equalsIgnoreCase("Windows")){
			capabilities.setPlatform(org.openqa.selenium.Platform.WINDOWS);
		}

		if(platform.equalsIgnoreCase("Linux")){
			capabilities.setPlatform(org.openqa.selenium.Platform.LINUX);
		}

		try {
			URL url = new URL(seleniumUrl);
			driver = new RemoteWebDriver(url, capabilities);

			driver.manage().window().setPosition(new Point(sp[0],sp[1]));
			driver.manage().window().setSize(new Dimension(dm[0],dm[1]));

			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);

			driver.get(baseUrl);
			wait = new WebDriverWait(driver, 60);
		}
		catch (MalformedURLException ex) {
			System.err.println(" Fail to start " + browser + " on " + platform);
			ex.printStackTrace();
		}
	}

	/**
	 * Logs onto the Saiku Server UI
	 */
	public void login() {
		System.out.println("Start to login");

		for (int second = 0;; second++) {
			if (second >= 60) fail("timeout");
			try { if (isElementPresent(By.id("ui-dialog-title-1"))) break; } catch (Exception e) {}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				System.out.println("Interrupted: " + ex.getCause());
			}
		}

		// Check dialog is shown along with Login Text
		if(isElementPresent(By.id("ui-dialog-title-1")))
		{
			getElement(By.id("username")).clear();
			getElement(By.id("username")).sendKeys(username);
			getElement(By.id("password")).clear();
			getElement(By.id("password")).sendKeys(password);
			getElement(By.linkText(" Login ")).click();

			// Slight sleep while wait for login to complete
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Sleep interrupted: " + e.getCause());
			}

			// Login should be successful so header & tabs will be displayed
			Assert.assertTrue(getElement(By.id("header")).isDisplayed(),"Header not displayed, therefore login unsuccessful" );
			Assert.assertTrue(getElement(By.id("tab_panel")).isDisplayed(), "Tab Panel not displayed, therefore login unsuccessful");
		}
		else
		{
			fail("Login dialogue not found");
		}
		System.out.println("Login successfully");
	}

	/**
	 * Creates a query by drag and drop "Product Family" and "Store Name" to Columns and Rows fields
	 */
	public void crtQuery_DragandDrop() throws Exception {

		driver.findElement(By.id("new_query")).click();
		new Select(driver.findElement(By.cssSelector("select.cubes"))).selectByVisibleText("Warehouse");
		driver.findElement(By.linkText("Product")).click();

		Robot r = new Robot();
		r.mouseMove(0,0);

		// Drag "Product Family" and drop into the column field
		WebElement selection1 = driver.findElement(By.linkText("Product Family"));
		WebElement target1 =  driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div/div[2]/ul")); // This is Columns field
		dragAndDrop(selection1,target1);

		// Drag "Store Name" and drop into the row field
		driver.findElement(By.linkText("Store")).click();

		r.mouseMove(0,0);

		WebElement selection2 = driver.findElement(By.linkText("Store Name"));
		WebElement target2 = driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div[2]/div[2]/ul")); // This is Row field
		dragAndDrop(selection2,target2);

		Thread.sleep(2000);
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Drink[\\s\\S]*$"));
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.contains("You need to put at least"));
	}

	/**
	 * Creates a query by clicking "Product Family" and "Store Name" to Columns and Rows fields
	 */
	public void crtQuery() throws Exception {

		driver.findElement(By.id("new_query")).click();
		new Select(driver.findElement(By.cssSelector("select.cubes"))).selectByVisibleText("Warehouse");

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Product")));
		driver.findElement(By.linkText("Product")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Product Family")));
		driver.findElement(By.linkText("Product Family")).click();

		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.contains("You need to put at least"),"No texts \"You need to put at least ...\" appear.");

		driver.findElement(By.linkText("Store")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Store Name")));
		driver.findElement(By.linkText("Store Name")).click();

		Thread.sleep(5000);
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*NullPointerException: [\\s\\S]*$"),"\"NullPointerExceptio\" is displayed");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Drink[\\s\\S]*$"),"No query output displays after query creation.");
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.contains("You need to put at least"));
	}

	/**
	 * Drags an element and drop to a target place
	 */
	public void dragAndDrop(WebElement src, WebElement des)
	{
		Actions act = new Actions(driver);
		act.clickAndHold(src).build().perform();
		act.moveToElement(des).build().perform();
		act.release(des).build().perform();
	}

	/**
	 * Closes the current browser
	 */
	public void closeBrowser() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	/**
	 *  Deletes screenshot directory if it exists, this runs before the test suite starts running
	 */
	@BeforeSuite
	public void deleteDir() {

		File file = new File("");
		String tmpPath [] = { file.getAbsolutePath()+ "/test-output/screenshots/",
				file.getAbsolutePath() + "/test-output/ExportedFiles/" };

		for (String tmp: tmpPath){
			File dir = new File(tmp);
			if (dir.exists()) {
				System.out.println("The directory " + tmp + " exists, and will be deleted");
				if (dir.isDirectory()) {
					try {
						FileUtils.deleteDirectory(dir);
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Takes a screenshot and saves it to a specific folder (/test-output/screenshots)
	 * @param result - The result of the test method that was just run
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	public void takeScreenshot(ITestResult result, String browser, String platform) {

		File file = new File("");

		screenshotPath = file.getAbsolutePath()+ "/test-output/screenshots/";
		screenshotFile = screenshotPath + platform + "/" + browser + "/" + result.getName() + ".jpg";

		// Take a screenshot and save to the directory
		driver = (RemoteWebDriver) new Augmenter().augment(driver);
		File scrFile =
				((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

		try {
			FileUtils.copyFile(scrFile, new File(screenshotFile));
		} catch (IOException e) {
			Reporter.log("error generating screenshot for "+result.getName()+": "+e, true);
			e.printStackTrace();
		}
		Reporter.setCurrentTestResult(null);
	}

	protected boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private WebElement getElement(By by) {
		try {
			return driver.findElement(by);
		} catch (NoSuchElementException e) {
			return null;
		}
	}


	@SuppressWarnings("unused")
	private String closeAlertAndGetItsText() {
		try {
			Alert alert = driver.switchTo().alert();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alert.getText();
		} finally {
			acceptNextAlert = true;
		}
	}

}
