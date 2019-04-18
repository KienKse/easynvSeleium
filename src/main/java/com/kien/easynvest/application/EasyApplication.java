package com.kien.easynvest.application;

import com.kien.easynvest.enums.Enums;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EasyApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(EasyApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		WebDriver driver;
		driver = new ChromeDriver();

		driver.get(Enums.SITE_AUTENTICACAO_EASY.getValor());

		/** Login */
		WebElement login = new WebDriverWait(driver, 20).until(
				ExpectedConditions.presenceOfElementLocated(By.name("username")));

		performInput(driver, login, Enums.CPF);

		WebElement pass = driver.findElement(By.name("password"));
		performInput(driver, pass, Enums.PASS);

		driver.findElement(By.xpath("//button[@font-family='Rational-Medium']")).click();

	}

	private void performInput(WebDriver driver, WebElement campo, Enums campoEnum) {
		Actions actions = new Actions(driver);
		actions.moveToElement(campo);
		actions.click();
		actions.sendKeys(campoEnum.getValor());
		actions.build().perform();
	}
}
