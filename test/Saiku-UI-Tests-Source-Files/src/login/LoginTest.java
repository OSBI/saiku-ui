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

package login;

import main.Config;
import main.TestFunctionsBase;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests login functionalities. The tests are provided below.
 * 
 * login
 * loginCookie
 * loginCookieDelete
 * logout
 * loginFail
 * 
 */

public class LoginTest extends TestFunctionsBase {

	private final String baseUrl = Config.BASE_URL;
	private final String username = Config.USERNAME;
	private final String password = Config.PASSWORD;
	private final String navUrl = Config.NAV_URL;
	private final String navUrl_title = Config.NAV_URL_TITLE;

	/**
	 * Initial setup to load browsers.
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	@Parameters({ "browser", "platform" })
	@Test
	public void loadBrowser(String browser, String platform) {
		System.out.println(" Test - load " + browser + " on " + " " + platform);

		super.setup(browser, platform);
	}

	/**
	 * Test login will succeed when correct credentials are provided.
	 */
	@Override
	@Test
	public void login() {
		System.out.println(" Test - Login success");

		// Slight sleep while wait for login page load
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		driver.findElement(By.id("username")).clear();
		driver.findElement(By.id("username")).sendKeys(username);
		driver.findElement(By.id("password")).clear();
		driver.findElement(By.id("password")).sendKeys(password);
		driver.findElement(By.linkText(" Login ")).click();

		// Slight sleep while wait for login to complete
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		// Login should be successful so header & tabs will be displayed
		Assert.assertTrue(driver.findElement(By.id("header")).isDisplayed(),
				"Header not displayed, therefore login unsuccessful");
		Assert.assertTrue(driver.findElement(By.id("tab_panel")).isDisplayed(),
				"Tab Panel not displayed, therefore login unsuccessful");

		driver.findElement(By.id("logout")).click();

	}

	/**
	 * Test will navigate away from Saiku then back, no login is required if the cookie exists.
	 */
	@Test
	public void loginCookie() {
		System.out.println(" Test - Login Cookie");

		super.login();

		// Navigate away then back to check Cookie storage
		driver.get(navUrl);

		// Slight sleep while wait for login to complete
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		Assert.assertEquals(navUrl_title, driver.getTitle());

		driver.get(baseUrl);

		// Slight sleep while wait for login page load
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		Assert.assertTrue(driver.findElement(By.id("header")).isDisplayed(),
				"Header not displayed, therefore Navigate back unsuccessful");
		Assert.assertTrue(driver.findElement(By.id("tab_panel")).isDisplayed(),
				"Tab Panel not displayed, therefore login unsuccessful");

		driver.findElement(By.id("logout")).click();

	}

	/**
	 * Test will navigate away from Saiku then navigate back, a login is required if the cookie is deleted.
	 * This test only works on Firefox Linux
	 * @throws InterruptedException
	 */
	@Test
	public void loginCookieDelete() {
		System.out.println("Test - Login Cookie Delete");

		super.login();
		JavascriptExecutor js;
		if (driver instanceof JavascriptExecutor) {
			js = (JavascriptExecutor) driver;
			js.executeScript("document.cookie='JSESSIONID=null;expires='+new Date(0).toUTCString()+';path=/saiku'");
		}

		driver.findElement(By.id("open_query")).click();

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		Alert alert = driver.switchTo().alert();
		alert.dismiss();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		Assert.assertTrue(isElementPresent(By.id("ui-dialog-title-1")),
				"Dialog not found");
		Assert.assertFalse(isElementPresent(By.id("tab_panel")),
				"Tab Panel displayed, therefore logout unsuccessful");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY"))
				.getText().matches("^[\\s\\S]*Username[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY"))
				.getText().matches("^[\\s\\S]*Password[\\s\\S]*$"));

	}

	/**
	 * Tests logout functionality
	 */
	@Test
	public void logout() {
		System.out.println(" Test - Logout successful");

		super.login();

		driver.findElement(By.id("logout")).click();

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.err.println("Sleep interrupted: " + e.getCause());
		}

		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Username[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Password[\\s\\S]*$"));
		Assert.assertFalse(isElementPresent(By.id("tab_panel")),
				"Tab Panel displayed, therefore logout unsuccessful");

	}

	/**
	 * Tests login failure when incorrect credentials are provided.
	 */
	@Test(enabled=false)
	public void loginFail() {
		System.out.println(" Test - Login Failed");

		if (isElementPresent(By.id("ui-dialog-title-1"))) {
			driver.findElement(By.id("username")).clear();
			driver.findElement(By.id("username")).sendKeys("test-user");
			driver.findElement(By.id("password")).clear();
			driver.findElement(By.id("password")).sendKeys("failure");
			driver.findElement(By.linkText(" Login ")).click();

			// Slight sleep while wait for login to complete
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				System.err.println("Sleep interrupted: " + e.getCause());
			}

			// Header shouldn't be displayed as login should fail
			Assert.assertFalse(driver.findElement(By.id("header"))
					.isDisplayed(),
					"Header is displayed, therefore login succesful");

		} else {
			Assert.fail("Login dialogue not found");
		}
	}

	/**
	 * Take a screenshot if a test fails
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	@Parameters({"browser", "platform"})
	@AfterMethod(alwaysRun = true)
	public void screenshotOnTestFailure(ITestResult result, String browser, String platform) {
		if (!result.isSuccess()) {
			super.takeScreenshot(result, browser, platform);
		}
	}

	/**
	 * Close browser after tests.
	 */
	@Override
	@AfterClass
	public void closeBrowser() throws Exception {
		System.out.println("Clean up - Close the browser");
		super.closeBrowser();
	}
}
