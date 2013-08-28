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
import org.openqa.selenium.WebElement;
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
 * filterQuery
 * modifyQuery
 * zoomInQuery
 * sortQueryDimension
 * sortQueryMeasure
 * modifyQueryMDX
 * drillThroughQuery
 * 
 */

public class ModifyQuery extends TestFunctionsBase {

	/**
	 * Initial setup to load browser, access url and login
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 */
	@Override
	@Parameters({"browser", "platform"})
	@BeforeClass
	public void setup(String browser, String platform){
		System.out.println(browser+ " " + platform);
		super.setup(browser,platform);
		super.login();

	}

	/**
	 * Tests that when an item is added to the Filter field, a new query output will be generated.
	 */
	@Test(groups="DragandDrop")
	public void filterQuery() throws Throwable {
		System.out.println("Test - Filter a query");

		super.crtQuery();

		// Drag "Units Ordered" and drop into the filter field
		WebElement selection3 = driver.findElement(By.linkText("Units Ordered"));
		WebElement target3 = driver.findElement(By.cssSelector("div.fields_list_body.filter > ul.connectable.ui-sortable")); // This is Filter field

		super.dragAndDrop(selection3,target3);
		Thread.sleep(2000);

		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Store Name[\\s\\S]*$"),"No new output after add Units Order into filter field");
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td/div")).getText()
				.matches("^[\\s\\S]*1471.0[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests that when an item is added, a new query output will be generated.
	 */
	@Test
	public void modifyQuery() throws Throwable {
		System.out.println("Test - Modify a query");

		super.crtQuery();

		driver.findElement(By.linkText("Store Type")).click();
		driver.findElement(By.xpath("(//a[contains(text(),'Store Type')])[2]")).click();
		wait.until(ExpectedConditions.textToBePresentInElement(By
				.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th[2]/div"),"Store Type"));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Supermarket[\\s\\S]*$"),
				"No query output after the query modification");

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests 'Filter' button displays a dialog of potential query items
	 */
	@Test
	public void zoomInQuery() throws Exception {
		System.out.println("Test - Zoom in a query");
		super.crtQuery();

		driver.findElement(By.linkText("Product Family")).click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//html/body/div[11]/div")));
		Assert.assertTrue(driver.findElement(By.id("ui-dialog-title-3")).getText()
				.matches("^[\\s\\S]*Selections for Product Family[\\s\\S]*$"),"No texts \"Selections fo Product Family\" appears");
		driver.findElement(By.cssSelector("option[value=\"%5BProduct%5D.%5BDrink%5D\"]")).click();
		driver.findElement(By.id("add_members")).click();
		driver.findElement(By.linkText(" OK ")).click();

		Thread.sleep(2000);
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*NullPointerException: [\\s\\S]*$"),"\"NullPointerExceptio\" is displayed");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Drink[\\s\\S]*$"),"Drink column doesn't appear after ZoomIn");
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*Food[\\s\\S]*$"),"Food column still exists after ZoomIn");

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests sorting by Dimension (column name or row name)
	 */
	@Test
	public void sortQueryDimension() throws Exception {
		System.out.println("Test - Sort a query by Dimension ");
		super.crtQuery();

		// "Sort" function is only working on sorting by column names or rows names
		// Sort by columns
		driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div/div[2]/ul/li/span[2]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th[2]/div")).getText()
				.matches("^[\\s\\S]*Drink[\\s\\S]*$"));

		driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div/div[2]/ul/li/span[2]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr/th[2]/div")).getText()
				.matches("^[\\s\\S]*Non-Consumable[\\s\\S]*$"));

		// Sort by rows
		// Not sorted
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div")).getText()
				.matches("^[\\s\\S]*Store 6[\\s\\S]*$"));
		// Sorted by ascent
		driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div[2]/div[2]/ul/li/span[2]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div")).getText()
				.matches("^[\\s\\S]*Store 11[\\s\\S]*$"));
		// Sorted by descend
		driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[2]/div/div/div[2]/div[2]/ul/li/span[2]")).click();
		Thread.sleep(1000);
		Assert.assertTrue(driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/th/div")).getText()
				.matches("^[\\s\\S]*Store 7[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}
	/**
	 * Tests sorting by Measure
	 */
	@Test
	public void sortQueryMeasure() throws Exception {
		System.out.println("Test - Sort a query by Measure");

		driver.findElement(By.id("new_query")).click();
		new Select(driver.findElement(By.cssSelector("select.cubes"))).selectByVisibleText("Warehouse");
		driver.findElement(By.linkText("Warehouse Profit")).click();
		driver.findElement(By.linkText("Product")).click();
		driver.findElement(By.linkText("Product Family")).click();

		Thread.sleep(2000);
		Assert.assertEquals("77,272.651",driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[3]/td/div")).getText(),
				"77,272.651 should be in the middle");

		driver.findElement(By.cssSelector("span.sort.none")).click();
		Thread.sleep(1000);
		Assert.assertEquals("77,272.651",driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[4]/td/div")).getText(),
				"77,272.651 should be at the bottom");

		driver.findElement(By.cssSelector("span.sort.BASC")).click();
		Thread.sleep(1000);
		Assert.assertEquals("77,272.651",driver.findElement(By.xpath("//html/body/div[9]/div/div/div/div[4]/table/tbody/tr[2]/td/div")).getText(),
				"77,272.651 should be at the top");

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests modifying a query by using MDX dialog box and inputing a new query
	 */
	@Test
	public void modifyQueryMDX() throws Exception {
		System.out.println("Test - Switch to MDX mode");

		super.crtQuery();

		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[16]/a")).click();
		Assert.assertTrue(isElementPresent(By.cssSelector("textarea.mdx_input")));

		driver.findElement(By.cssSelector("textarea.mdx_input")).clear();
		driver.findElement(By.cssSelector("textarea.mdx_input"))
		.sendKeys("SELECT\nNON EMPTY {Hierarchize({[Product].[Product Family].Members})} ON COLUMNS," +
				"\nNON EMPTY {Hierarchize({{[Store].[Store State].Members}, " +
				"{[Store].[Store Name].Members}})} ON ROWS\nFROM [Warehouse]");
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[2]/a")).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("th.row_header div")));
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*Store State[\\s\\S]*$"));

		driver.findElement(By.cssSelector("li.selected > span.close_tab.sprite")).click();
	}

	/**
	 * Tests drillthrough functionality
	 */
	@Test
	public void drillThroughQuery() throws Exception {
		System.out.println("Test - Drill through a query");

		super.crtQuery();
		// Click "Drill-through" button and click a cell to drill through
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[12]/a")).click();
		driver.findElement(By.cssSelector("div[alt=\"590.4193\"]")).click();
		driver.findElement(By.cssSelector("div.sidebar_inner.dimension_tree > ul > li.parent_dimension > a.folder_collapsed.sprite")).click();

		//Switch to the dialog box
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}

		// Make some selections on Drill through dialog box
		driver.findElement(By.xpath("//input[@value='[Product].[Product Name]']")).click();
		driver.findElement(By.xpath("//input[@value='[Measures].[Warehouse Sales]']")).click();
		driver.findElement(By.linkText(" Ok ")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//html/body/div[7]/div/div[9]/div/div/table/tbody/tr[2]/td/div"))); //The element of "BBB Best Columbian Coffee"
		//Switch to the dialog box
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}
		// Verify a new dialog box appeared with the texts defined
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*BBB Best Columbian Coffee[\\s\\S]*$"),"Drill through dialog box does not appear");
		Assert.assertTrue(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*25\\.8912[\\s\\S]*$"));

		// Click "OK" to close the dialog box
		wait.until(ExpectedConditions.elementToBeClickable(By.id("fancybox-close")));
		driver.findElement(By.id("fancybox-close")).click();
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("th.row_header div")));
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText()
				.matches("^[\\s\\S]*BBB Best Columbian Coffee[\\s\\S]*$"),"Drill through dialog box is not closed");
		Assert.assertFalse(driver.findElement(By.cssSelector("BODY")).getText().matches("^[\\s\\S]*25\\.8912[\\s\\S]*$"));

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
