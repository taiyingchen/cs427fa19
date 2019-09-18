package edu.ncsu.csc.itrust.selenium;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;


import com.meterware.httpunit.HttpUnitOptions;

import edu.ncsu.csc.itrust.enums.TransactionType;

/**
 * Test class for logging into iTrust.
 */
public class LoginTest extends iTrustSeleniumTest {
	
	/*
	 * The URL for iTrust, change as needed
	 */
	/**ADDRESS*/
	public static final String ADDRESS = "http://localhost:8080/iTrust/";

	/**
	 * Set up for testing.
	 */
	protected void setUp() throws Exception {
		super.setUp();
		gen.clearAllTables();
		gen.standardData();
		// turn off htmlunit warnings
	    java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
	    java.util.logging.Logger.getLogger("org.apache.http").setLevel(java.util.logging.Level.OFF);
		HttpUnitOptions.setExceptionsThrownOnScriptError(false);
	}

	/**
	 * Tear down from testing.
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	/**
	 * Test the behavior expected when a user enters a non numeric
	 * string into the username box. iTrust currently excpects
	 * to see a NumberFormatException.
	 */
	public void testNonNumericLogin() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.get(ADDRESS);
		// log in using the given username and password
		driver.findElement(By.id("j_username")).clear();
		driver.findElement(By.id("j_username")).sendKeys("foo");
		driver.findElement(By.id("j_password")).clear();
		driver.findElement(By.id("j_password")).sendKeys("1234");
		driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
		assertFalse(driver.getPageSource().contains("NumberFormatException"));
	}
	
	/**
	 * Test the standard login feature. After logging in, a user should end up
	 * at the itrust home page, and the login should be logged.
	 */
	public void testLogin() throws Exception {
		// Log in as a patient
		WebDriver driver = login("2", "pw");
		
		// Wait until redirected to page
		WebDriverWait wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
		wait.until(ExpectedConditions.titleIs("iTrust - Patient Home"));
		
		// Verify Logging
		assertLogged(TransactionType.LOGIN_SUCCESS, 2L, 2L, "");
	}

	public void testResetPassword() throws Exception {
		HtmlUnitDriver wd = new HtmlUnitDriver();
		wd.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		wd.get(ADDRESS);

		// Clicks "Reset Password" at the login screen.
		wd.findElement(By.linkText("Reset Password")).click();
		assertEquals("iTrust - Reset Password", wd.getTitle());

		// Selects "Patient" from the drop-down list.
		Select select;
		select = new Select (wd.findElement(By.name("role")));
		select.selectByValue("patient");
		// Enters "1" in the 'MID' field.
		wd.findElement(By.name("mid")).clear();
		wd.findElement(By.name("mid")).sendKeys("1");
		// Submits the form.
		wd.findElement(By.cssSelector("input[value='Submit']")).submit();
		assertTrue(wd.getPageSource().contains("what is your favorite color?"));

		// Enters "blue" in the 'answer' field.
		wd.findElement(By.name("answer")).clear();
		wd.findElement(By.name("answer")).sendKeys("blue");
		// Enters "newPw12345" in the 'New Password' field.
		wd.findElement(By.name("password")).clear();
		wd.findElement(By.name("password")).sendKeys("newPw12345");
		// Enters "newPw12345" in the 'Confirm' field.
		wd.findElement(By.name("confirmPassword")).clear();
		wd.findElement(By.name("confirmPassword")).sendKeys("newPw12345");
		// Submits the form.
		wd.findElement(By.cssSelector("input[value='Submit']")).submit();
		assertTrue(wd.getPageSource().contains("Password changed"));
	}
}