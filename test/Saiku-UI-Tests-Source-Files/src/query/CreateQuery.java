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

package query;

import static org.testng.Assert.fail;
import main.Priority;
import main.TestFunctionsBase;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class provides the following tests.
 * 
 * CreateQuery
 * saveQuery
 * openQuery
 * deleteQuery
 * createFolder
 * deleteFolder
 * nonAutoRunQuery
 *
 */

/**
 * Test for creating a query
 */
public class CreateQuery extends TestFunctionsBase {

	/**
	 * Initial setup to load browser, access url and login
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	@Override
	@Parameters({"browser", "platform"})
	@BeforeClass
	public void setup(String browser, String platform){
		System.out.println(browser +" " + platform);
		super.setup(browser,platform);
		super.login();
	}

	/**
	 * Tests for creating a query
	 */
	@Priority(1)
	@Test(groups="DragandDrop")
	public void createQuery() throws Exception {
		System.out.println("Create a query");

		super.crtQuery_DragandDrop();

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Test for saving a query
	 */
	@Priority(2)
	@Test
	public void saveQuery() throws Exception {
		System.out.println("Test - Save a query");

		super.crtQuery();

		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li/a")).click(); //"Save Query" button
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*To save a new query, please select a folder and type a name in the text box below:[\\s\\S]*$"));

		driver.findElement(By.name("name")).clear();
		driver.findElement(By.name("name")).sendKeys("myQuery");
		driver.findElement(By.linkText(" Save ")).click();
		Thread.sleep(1000);
		driver.findElement(By.linkText("myQuery")).click();
		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
		driver.findElement(By.id("open_query")).click();
		driver.findElement(By.linkText("Repository")).click();
		Thread.sleep(500);
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().contains("myQuery"));
	}

	/**
	 * Test for opening a saved query
	 */
	@Priority(3)
	@Test
	public void openQuery() throws Throwable {
		System.out.println("Test - Open a Query");

		driver.findElement(By.id("open_query")).click();
		driver.findElement(By.linkText("Repository")).click();
		Thread.sleep(1000);
		driver.findElement(By.linkText("myQuery.saiku")).click();

		WebElement mouseOverElement = driver.findElement(By.linkText("myQuery.saiku"));
		Actions action = new Actions(driver);
		action.doubleClick(mouseOverElement).perform();
		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Store Name[\\s\\S]*$"),"No query output after double clicking the query");

		// close "myQuery" dialog box
		driver.findElement(By.linkText("myQuery.saiku")).click();
		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Test for deleting a saved query
	 */
	@Priority(4)
	@Test
	public void deleteQuery() throws Throwable {
		System.out.println("Test - Delete a query");

		driver.findElement(By.id("open_query")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.linkText("myQuery.saiku"), "myQuery.saiku"));
		driver.findElement(By.linkText("myQuery.saiku")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.delete")));
		driver.findElement(By.cssSelector("a.delete")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.dialog_icon")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Confirm deletion[\\s\\S]*$"),
				"Confirm popup box didn't appear");

		//Switch to new dialog box
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}
		driver.findElement(By.linkText(" Yes ")).click(); // Click the "Yes" button
		if (isElementPresent(By.linkText(" Yes ")))
		{
			driver.findElement(By.linkText(" Yes ")).click(); // Click the "Yes" button again
		}

		Thread.sleep(1000);
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*myQuery[\\s\\S]*$"),"The query hasn't been deleted.");
	}

	/**
	 * Test for creating a folder
	 */
	@Priority(5)
	@Test
	public void createFolder() throws Throwable {
		System.out.println("Test - Create a folder in repository");

		driver.navigate().refresh();
		Thread.sleep(1000);
		driver.findElement(By.id("open_query")).click();

		if (isElementPresent(By.cssSelector("a.add_folder")))
		{
			// Add a new directory called "newFolder" if the "Add folder" button is present.
			driver.findElement(By.cssSelector("a.add_folder")).click();

			Thread.sleep(1000);
			Assert.assertTrue(isElementPresent(By.cssSelector("div.dialog_icon")),"No new folder dialog box appeared");
			Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
					.matches("^[\\s\\S]*To add a new folder*[\\s\\S]*$"),"No \"add a new folder\" mesages appeared");

			driver.findElement(By.name("name")).clear();
			driver.findElement(By.name("name")).sendKeys("newFolder");
			driver.findElement(By.linkText(" OK ")).click();
			Thread.sleep(5000);
			if ( ! isElementPresent(By.linkText("newFolder")))
			{
				driver.navigate().refresh();
				Thread.sleep(1000);
				driver.findElement(By.id("open_query")).click();
			}
			Assert.assertTrue(isElementPresent(By.linkText("newFolder")),"No new folder appeared in Repository");
		}
		else
		{
			fail("\"Add folder\" button doesn't appear");
		}
	}

	/**
	 * Test for deleting a folder
	 */
	@Priority(6)
	@Test(enabled=true)
	// A bug here that deleting a folder is not working when the folder name contains a space. - Saiku version 2.4.
	public void deleteFolder() throws Throwable {
		System.out.println("Test - Delete a folder in repository");

		driver.findElement(By.id("open_query")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.linkText("newFolder"), "newFolder"));
		driver.findElement(By.linkText("newFolder")).click();
		driver.findElement(By.cssSelector("a[href='#delete_folder']")).click();

		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.contains("Are you sure you want to delete"),"Confirm deletion dialog box is not open");

		//Switch to new opened window
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}
		driver.findElement(By.linkText(" Yes ")).click(); // Click the "Yes" button
		if (isElementPresent(By.linkText(" Yes ")))
		{
			driver.findElement(By.linkText(" Yes ")).click(); // Click the "Yes" button again
		}
		Thread.sleep(2000);
		Assert.assertFalse(isElementPresent(By.linkText("newFolder")), "No newFolder is present in Repository list");
	}

	/**
	 * Test for running a query manually
	 */
	@Priority(6)
	@Test
	public void nonAutoRunQuery() throws Exception {
		System.out.println("Test - manually run a query");

		driver.findElement(By.id("new_query")).click();
		new Select(driver.findElement(By.cssSelector("select.cubes"))).selectByVisibleText("Warehouse");

		// Stop automation execution query
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[3]/a")).click();

		// Select items from Dimension list
		driver.findElement(By.linkText("Product")).click();
		driver.findElement(By.linkText("Product Family")).click();
		driver.findElement(By.linkText("Store")).click();
		driver.findElement(By.linkText("Store Name")).click();

		// Click "Run query" button
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[2]/a")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("th.row_header div")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Drink[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
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

	@Override
	@AfterClass
	public void closeBrowser() throws Exception {
		System.out.println("Clean up - Close the browser");
		super.closeBrowser();
	}
}




