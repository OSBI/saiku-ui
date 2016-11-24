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

import java.io.File;

import main.Config;
import main.TestFunctionsBase;

import org.apache.commons.io.FileUtils;
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
 * This class tests exporting to file functionalities.
 * The tests are provided below.
 * 
 * exportToXLSLink
 * exportToCVSLink
 * exportDrillThroughToCSVLink
 * 
 * The tests below currently only work on Windows Firefox and chrome, and the server and client have to be on same machine.
 * exportToXLSFile
 * exportToCVSFile
 * exportDrillThroughToCSVFile
 * 
 */

public class ExportQuery extends TestFunctionsBase {

	private final String fileName1 = "saiku-export.xls";
	private final String fileName2 = "saiku-export.csv";
	private final String fileName3 = "saiku-export-drillthrough.csv";
	private final String localDownloadPath = System.getProperty("user.home") + "/Downloads";
	private final Boolean fileToServer = true;
	private String fileToServerPath;
	private final String autoITExecutable_IE = Config.AUTO_IT_EXECUTABLE_IE;
	private final String autoITExecutable_FF = Config.AUTO_IT_EXECUTABLE_FF;
	private final String baseUrl = Config.BASE_URL;

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
	 * Test to check the link for exporting to XLS file
	 * @throws Exception
	 */
	@Test
	public void exportToXLSLink() throws Exception {
		System.out.println("Test - Check the link of exporting to XLS file");

		super.crtQuery();

		// Check "Export XLS" button contains the export link
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[title=\"Export XLS\"]")));
		Assert.assertEquals(driver.findElement(By.cssSelector("a[title=\"Export XLS\"]")).getAttribute("href"),baseUrl + "/#export_xls");
	}

	/**
	 * Test to check the link for exporting to CSV file
	 * @throws Exception
	 */
	@Test
	public void exportToCSVLink() throws Exception {
		System.out.println("Test - Check the link of exporting to CSV file");

		super.crtQuery();

		// Check "Export CSV" button contains the export link
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[title=\"Export CSV\"]")));
		Assert.assertEquals(driver.findElement(By.cssSelector("a[title=\"Export CSV\"]")).getAttribute("href"), baseUrl + "/#export_csv");
	}

	/**
	 * Test to check the link for exporting a drill through to CSV file
	 * @throws Exception
	 */
	@Test
	public void exportDrillThroughToCSVLink() throws Exception {
		System.out.println("Test - Check the link of export drill through to CSV file");

		super.crtQuery();

		// Check "Export Drill-Through on cell to CSV" button contains the export link
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[title=\"Export Drill-Through on cell to CSV\"]")));
		Assert.assertEquals(driver.findElement(By.cssSelector("a[title=\"Export Drill-Through on cell to CSV\"]")).getAttribute("href"),
				baseUrl + "/#export_drillthrough");
	}

	/**
	 * Tests the export to xls file functionality.
	 * The file will be exported to the local machine, then can be copied to the server.
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 * @throws Exception
	 */
	@Parameters({"browser", "platform"})
	@Test(groups="exportToFile")
	public void exportToXLSFile(String browser, String platform) throws Exception {
		System.out.println("Test - Export to XLS file");

		File file = new File(localDownloadPath + "/" + fileName1);
		file.deleteOnExit();

		super.crtQuery();
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[14]/a")).click();

		// Wait for the dialog box appearing
		Thread.sleep(5000);

		if(browser.equalsIgnoreCase("iexplore")){
			String[] dialog = new String[] { autoITExecutable_IE };
			Runtime.getRuntime().exec(dialog);
		}

		if(browser.equalsIgnoreCase("firefox")){
			String[] dialog = new String[] { autoITExecutable_FF,"Opening","OK" };
			System.out.println(dialog);
			Runtime.getRuntime().exec(dialog);
		}

		// Wait for file exported into the local machine
		Thread.sleep(10000);
		if (! file.exists()) {
			fail("The query wasn't exported into xls file on the local machine ");
		}

		// Copy the exported file to the server location if fileToServer signed to true
		if ( fileToServer ) {
			fileToServerPath = new File("").getAbsolutePath() + "/test-output/ExportedFiles/" + platform + "/" + browser + "/" + file.getName();

			FileUtils.copyFile(file, new File(fileToServerPath));
			if (! new File(fileToServerPath).exists()) {
				fail("The file is not saved to the server");
			}
		}
	}

	/**
	 * Tests the exporting to csv file functionality.
	 * The file will be exported into the local machine, then cab be copied to the server.
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 * @throws Exception
	 */
	@Parameters({"browser", "platform"})
	@Test(groups="exportToFile")
	public void exportToCVSFile(String browser, String platform) throws Exception {
		System.out.println("Test - Export to CSV file");

		File file = new File(localDownloadPath + "/" + fileName2);
		file.deleteOnExit();

		super.crtQuery();
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[15]/a")).click();

		// Wait for the dialog box appearing
		Thread.sleep(5000);

		if(browser.equalsIgnoreCase("iexplore")){
			String[] dialog = new String[] { autoITExecutable_IE };
			Runtime.getRuntime().exec(dialog);
		}

		if(browser.equalsIgnoreCase("firefox")){
			String[] dialog = new String[] { autoITExecutable_FF,"Opening","OK" };
			System.out.println(dialog);
			Runtime.getRuntime().exec(dialog);
		}

		// Wait for file exported into the local machine
		Thread.sleep(10000);
		if (! file.exists()) {
			fail("The query wasn't exported into cvs file on the local machine ");
		}

		// Copy the exported file to the server location if fileToServer signed to true
		if ( fileToServer ) {
			fileToServerPath = new File("").getAbsolutePath() + "/test-output/ExportedFiles/" + platform + "/" + browser + "/" + file.getName();

			FileUtils.copyFile(file, new File(fileToServerPath));
			if (! new File(fileToServerPath).exists()) {
				fail("The file is not saved to the server");
			}
		}
	}

	/**
	 * Tests the exporting Drill-Through on cell to CSV file functionality.
	 * The file will be exported into the local machine, then can be copied to the server.
	 * @param browser - The browser to run the test on
	 * @param platform - The platform to run the test on
	 * @throws Exception
	 */
	@Parameters({"browser", "platform"})
	@Test(groups="exportToFile")
	public void exportDrillThroughToCSVFile(String browser, String platform) throws Exception {
		System.out.println("Test - Export Drill-Through on cell to CSV file");

		File file = new File(localDownloadPath + "/" + fileName3);
		file.deleteOnExit();

		super.crtQuery();
		driver.findElement(By.xpath("//div[@id='tab_panel']/div/div/div/div/div/ul/li[13]/a")).click();
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

		// Wait for the dialog box appearing
		Thread.sleep(5000);

		if(browser.equalsIgnoreCase("iexplore")){
			String[] dialog = new String[] { autoITExecutable_IE };
			Runtime.getRuntime().exec(dialog);
		}

		if(browser.equalsIgnoreCase("firefox")){
			String[] dialog = new String[] { autoITExecutable_FF,"Opening","OK" };
			System.out.println(dialog);
			Runtime.getRuntime().exec(dialog);
		}

		// Wait for the file exported into the local machine
		Thread.sleep(10000);
		if (! file.exists()) {
			fail("The drillthrough query wasn't exported into cvs file on the local machine ");
		}

		// Copy the exported file to the server location if fileToServer signed to true
		if ( fileToServer ) {
			fileToServerPath = new File("").getAbsolutePath() + "/test-output/ExportedFiles/" + platform + "/" + browser + "/" + file.getName();

			FileUtils.copyFile(file, new File(fileToServerPath));
			if (! new File(fileToServerPath).exists()) {
				fail("The file is not saved to the server");
			}
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

	@Override
	@AfterClass
	public void closeBrowser() throws Exception {
		System.out.println("Clean up - Close the browser");

		driver.quit();
		String verificationErrorString = new StringBuffer().toString();
		if (!"".equals(verificationErrorString)) {
			System.out.println("verificationErrorString");
			fail(verificationErrorString);

		}
	}
}