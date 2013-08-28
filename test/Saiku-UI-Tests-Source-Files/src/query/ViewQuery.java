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

import main.TestFunctionsBase;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class tests the most of the buttons on tool bar, it contains the following tests.
 * 
 * nonEmpty
 * swapAxis
 * viewMDX
 * showExplainPlan
 * hideParents
 * toggleFields
 * toggleSidebar
 * switchMDXMode
 * toggleChart
 * basicStatistics
 * tags
 * 
 */

public class ViewQuery extends TestFunctionsBase {

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
	 * Tests the "Non-empty" button
	 * @throws Exception
	 */
	@Test
	public void nonEmpty() throws Exception {
		// "Non-Empty" button is not working properly on IE. This is a bug. Please check Saiku 2.5.
		System.out.println("Test - Non-Empty");

		super.crtQuery();

		// Keep clicking "Non-Empty" button until empty cells appear, as the button is not working stably.
		while (true){
			driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[7]/a")).click();

			Thread.sleep(500);
			if (driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div")).getText()
					.matches("^[\\s\\S]*Store 19[\\s\\S]*$"))
				break;
		}

		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div"))
				.getText().matches("^[\\s\\S]*Store 19[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td"))
				.getText().matches("^[\\s\\S]*$"));

		// Keep clicking "Non-Empty" button until empty cells are hidden, as the button is not working stably.
		while (true){
			driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div/div/ul/li[7]/a")).click();
			if (driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td"))
					.getText().matches("^[\\s\\S]*590\\.419[\\s\\S]*$"))
				break;
		}

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Swap axis" button
	 * @throws Exception
	 */
	@Test
	public void swapAxis() throws Exception {
		System.out.println("Test - Swap Axis");

		super.crtQuery();
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div"))
				.getText().matches("^[\\s\\S]*Store 6[\\s\\S]*$"));

		// Click "Swap Axis" button
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[8]/a")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.cssSelector("th.row_header div"),"Product Family"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th[2]/div"))
				.getText().matches("^[\\s\\S]*Store 6[\\s\\S]*$"));

		// Click "Swap Axis" button again
		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[8]/a")));
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[8]/a")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.cssSelector("th.row_header div"),"Store Name"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div"))
				.getText().matches("^[\\s\\S]*Store 6[\\s\\S]*$"));

		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("li.selected > span.close_tab.sprite")));
		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "View MDX" button
	 * @throws Exception
	 */
	@Test
	public void viewMDX() throws Exception {
		System.out.println("Test - View MDX");

		super.crtQuery();
		//Save the WindowHandle of Parent Browser Window
		String parentWindowId = driver.getWindowHandle();

		// Click "View MDX" button
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[9]/a")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.dialog_body > textarea")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*View MDX[\\s\\S]*$"),"View MDX dialog box did not appear");

		//Switch to new window opened
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}
		driver.findElement(By.linkText(" OK ")).click();

		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div.dialog_body > textarea")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Store Name[\\s\\S]*$"));
		driver.switchTo().window(parentWindowId);

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Show Explain Plan" button
	 * @throws Exception
	 */
	@Test
	public void showExplainPlan() throws Exception {
		System.out.println("Test - Show Explain Plan");

		super.crtQuery();

		// Click "Show Explain Plan" button
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[10]/a")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div#fancy_results.workspace_results")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Explain Plan[\\s\\S]*$"),"Explain plan dialog box did not appear");

		driver.findElement(By.id("fancybox-close")).click();
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div#fancy_results.workspace_results")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Drink[\\s\\S]*$"),"Explain plan dialog box was not closed");

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Hide Parents" button
	 * @throws Exception
	 */
	@Test
	public void hideParents() throws Exception {
		System.out.println("Test - Hide Parents");

		super.crtQuery();

		// Add a parent item into the query
		driver.findElement(By.linkText("Store State")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.cssSelector("th.row_header div"),"Store State"));

		// Click "HideParent" button to show the parent summary results
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[6]/a")).click();
		Thread.sleep(2000);

		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*2,292\\.651[\\s\\S]*$"),"The parent summary did not show up");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*2,057\\.232[\\s\\S]*$"));

		// Click "HideParent" button to hide the parent summary results
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[6]/a")).click();
		Thread.sleep(2000);
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*2,292\\.651[\\s\\S]*$"),"The parent summary was not hidden");
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*2,057\\.232[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Toggle Field" button
	 * @throws Exception
	 */
	@Test
	public void toggleFields() throws Exception {
		System.out.println("Test - Toggle Field");

		super.crtQuery();
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Filter[\\s\\S]*$"));

		// Click "Toggle Field" button to hide "Columns","Rows" and "Filter" fields
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[4]/a")).click();
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Filter[\\s\\S]*$"));

		// Click "Toggle Field" button to show "Columns" and "Rows" fields
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[4]/a")).click();
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Filter[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Toggle Sidebar" button
	 * @throws Exception
	 */
	@Test
	public void toggleSidebar() throws Exception {
		System.out.println("Test - Toggle Sidebar");

		super.crtQuery();
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Cubes[\\s\\S]*$"));

		// Click "Toggle Sidebar" button to hide the side bar
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[5]/a")).click();
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Cubes[\\s\\S]*$"));

		// Click "Toggle Sidebar" button again to show the side bar
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[5]/a")).click();
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Cubes[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Switch to MDX Mode" button
	 * @throws Exception
	 */
	@Test
	public void switchMDXMode() throws Exception {
		System.out.println("Test - Switch MDX Mode");

		super.crtQuery();

		// Click "Switch MDX Mode" button
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[16]/a")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("textarea.mdx_input")));
		try {
			Assert.assertEquals("SELECT\nNON EMPTY {Hierarchize({[Product].[Product Family].Members})} ON COLUMNS," +
					"\nNON EMPTY {Hierarchize({[Store].[Store Name].Members})} ON ROWS\nFROM [Warehouse]",
					driver.findElement(By.cssSelector("textarea.mdx_input")).getAttribute("value"));
		} catch (Error e) {
			verificationErrors.append(e.toString());
		}

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Toggle Chart" button
	 * @throws Exception
	 */
	// TODO - make sreenshot and do comparison
	@Test
	public void toggleChart() throws Exception {
		System.out.println("Test - toggle chart");

		super.crtQuery();
		// Click "stacked bar", "line", "pie" and "heatgrid" one by one"

		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[17]/a")).click();
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*bar[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*heatgrid[\\s\\S]*$"));

		// Click each option one by one to have a different view
		// Set waiting time for display purpose
		driver.findElement(By.linkText("bar")).click();
		Thread.sleep(500);
		/* Comment out the code of making sreenshot until sreenshots comparison code is in place.
		driver = new Augmenter().augment(driver);
		File scrFile =
				((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File("c:\\tmp\\toggleChart-bar.png"));
		 */
		driver.findElement(By.linkText("stacked bar")).click();
		Thread.sleep(500);

		driver.findElement(By.linkText("line")).click();
		Thread.sleep(500);

		driver.findElement(By.linkText("pie")).click();
		Thread.sleep(500);

		driver.findElement(By.linkText("heatgrid")).click();
		Thread.sleep(500);

		driver.findElement(By.linkText("bar")).click();
		Thread.sleep(500);

		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[17]/a")).click();
		Thread.sleep(500);
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*bar[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Basic Statistics" button
	 * @throws Exception
	 */
	@Test
	public void basicStatistics() throws Exception {
		System.out.println("Test - Basic Statistics");

		super.crtQuery();

		// Click "Statistics" button to show the statistics
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[18]/a")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By.cssSelector("th.row_header div.i18n"),"Statistics"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Statistics[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Std\\. Deviation[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*538\\.632[\\s\\S]*$"));

		// Click "Statistics" button to hide the statistics
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[18]/a")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By
				.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th/div"),"Store Name"));
		Assert.assertTrue(isElementPresent(By.cssSelector("div[alt=\"590.4193\"]")));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the "Tags" button
	 * @throws Exception
	 */
	@Test
	public void tags() throws Exception {
		System.out.println("Test - Tags");

		super.crtQuery();

		// Click "Tags" button
		driver.findElement(By.cssSelector("a[title=\"Tags\"]")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.add_bucket")));

		// Remove new_tag if it exists
		if (isElementPresent(By.linkText("new_tag"))){
			driver.findElement(By.linkText("x")).click();
		}

		// Click "Add tags" to create a tag
		driver.findElement(By.cssSelector("a.add_bucket")).click();

		// Select cells
		driver.findElement(By.cssSelector("div[alt=\"590.4193\"]")).click();
		driver.findElement(By.cssSelector("div[alt=\"3635.1324\"]")).click();
		driver.findElement(By.cssSelector("div[alt=\"907.3457\"]")).click();

		// Give the tag a name and save it
		driver.findElement(By.id("new_bucket")).clear();
		driver.findElement(By.id("new_bucket")).sendKeys("new_tag");
		driver.findElement(By.cssSelector("a[title=\"Save Tag\"]")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a.bucket")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*new_tag[\\s\\S]*$"),"\"No new_tag created\"");

		// Display the tagged cells.
		driver.findElement(By.linkText("new_tag")).click();
		wait.until(ExpectedConditions.invisibilityOfElementWithText(By
				.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th/div"),"Store Name"));
		// Check new_tag outcome
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[3]/td/div"))
				.getText().matches("^[\\s\\S]*590\\.419[\\s\\S]*$"),"No new output after clicking newly added new_tag");
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[3]/td[2]/div"))
				.getText().matches("^[\\s\\S]*3,635\\.132[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[3]/td[3]/div"))
				.getText().matches("^[\\s\\S]*907\\.346[\\s\\S]*$"));

		// Click "new tag" again to display the original query
		driver.findElement(By.linkText("new_tag")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By
				.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th/div"),"Store Name"));
		// Display original data
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td/div"))
				.getText().matches("^[\\s\\S]*590\\.419[\\s\\S]*$"),"Original data not displayed");
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td[2]/div"))
				.getText().matches("^[\\s\\S]*3,635\\.132[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td[3]/div"))
				.getText().matches("^[\\s\\S]*907\\.346[\\s\\S]*$"));

		// Delete the tag
		driver.findElement(By.linkText("x")).click();
		// Check the tag is deleted");
		wait.until(ExpectedConditions.invisibilityOfElementWithText(By
				.cssSelector("a.delete"),"x"));
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*new_tag[\\s\\S]*$"),"New_tag is not deleted");

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




