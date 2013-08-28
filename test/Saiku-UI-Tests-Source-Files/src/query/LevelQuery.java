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
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * This class provides a number of tests when the level options are selected, the output are correct accordingly.
 * 
 * modifyQueryLevelKeepOnly
 * modifyQueryLevelInclude
 * modifyQueryLevelKeepAndInclude
 * modifyQueryLevelRemove
 * 
 */

public class LevelQuery  extends TestFunctionsBase {

	/**
	 * Initial setup to load browser, access url and login
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	@Override
	@Parameters({"browser", "platform"})
	@BeforeClass
	public void setup(String browser, String platform)
	{
		System.out.println(browser+ " " + platform);
		super.setup(browser,platform);
		super.login();
	}

	/**
	 * Tests the level option "Keep Only"
	 * @throws Exception
	 */
	@Test
	public void modifyQueryLevelKeepOnly() throws Exception{
		System.out.println("Test - modify a query level- Keep Only");

		super.crtQuery();

		// Click "Drink"
		driver.findElement(By.cssSelector("th[title=\"Drink\"]")).click();

		// Click "Keep Only
		driver.findElement(By.xpath("//html/body/ul/li[3]/span")).click();

		Thread.sleep(2000);
		// Assert the text"Beer and Wine" appears
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Drink[\\s\\S]*$"));
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Food[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the level option "Include level"
	 * @throws Exception
	 */
	@Test
	public void modifyQueryLevelInclude() throws Exception{
		System.out.println("Test - modify a query - Include level");

		super.crtQuery();

		// Click "Drink"
		driver.findElement(By.cssSelector("th[title=\"Drink\"]")).click();

		// Click "Include Level"
		driver.findElement(By.xpath("//html/body/ul/li[4]/span")).click();
		Thread.sleep(500);

		// Click "Product Category"
		driver.findElement(By.xpath("//html/body/ul/li[4]/ul/li[4]/span")).click();
		Thread.sleep(2000);

		// Assert the text"Beer and Wine" appears
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Drink[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Food[\\s\\S]*$"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Beer and Wine[\\s\\S]*$"),
				"No Beer and Wine session appears.");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Bread[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the level option "Keep and Include"
	 * @throws Exception
	 */
	@Test
	public void modifyQueryLevelKeepAndInclude() throws Exception{
		System.out.println("Test - modify a query level - Keep and Include");

		super.crtQuery();

		// Click "Drink"
		driver.findElement(By.cssSelector("th[title=\"Drink\"]")).click();

		// Click "Keep and Include Level"
		driver.findElement(By.xpath("//html/body/ul/li[5]/span")).click();
		Thread.sleep(500);

		// Click "Product Category"
		driver.findElement(By.xpath("//html/body/ul/li[5]/ul/li[4]/span")).click();
		Thread.sleep(2000);

		// Assert the text"Beer and Wine" appears
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Beer and Wine[\\s\\S]*$"), "No Beer and Wine session appears.");

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests the level option "Remove level"
	 * @throws Exception
	 */
	@Test
	public void modifyQueryLevelRemove() throws Exception{
		System.out.println("Test - Remove level");

		super.crtQuery();

		// Click "Drink"
		driver.findElement(By.cssSelector("th[title=\"Drink\"]")).click();
		Thread.sleep(500);

		// Click "Remove Level"
		Actions builder = new Actions(driver);
		builder.moveToElement(driver.findElement(By.xpath("//html/body/ul/li[6]/span")))
		.moveToElement(driver.findElement(By.xpath("//html/body/ul/li[6]/ul/li/span"))).click().perform();
		Thread.sleep(2000);

		// Assert the text"Beer and Wine" appears
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*You need to put at least*[\\s\\S]*$"),"The query is not removed.");

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
