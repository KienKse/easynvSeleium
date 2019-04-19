package com.kien.easynvest.application;

import com.kien.easynvest.tools.PassCript;
import com.kien.easynvest.enums.Enums;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.runner.Runner;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@SpringBootApplication
public class EasyApplication implements CommandLineRunner {

	private static TelegramBot telegramBot = new TelegramBot(Enums.TELEGRAM_KEY.getValor());
	private static Logger logger = Logger.getLogger(String.valueOf(Runner.class));

	public static void main(String[] args) {
		SpringApplication.run(EasyApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		WebDriver driver;
//		ChromeOptions chrome = new ChromeOptions();
//        chrome.setHeadless(true);
//        driver = new ChromeDriver(chrome);
		driver = new ChromeDriver();

		if(grantApplicationAccess()) {
			try {

				driver.get(Enums.SITE_AUTENTICACAO_EASY.getValor());

				fecharPopUp(driver);

				WebElement login = new WebDriverWait(driver, 20).until(
						ExpectedConditions.presenceOfElementLocated(By.name("username")));

				performInput(driver, login, PassCript.sendHash(Enums.AUTH_1_1.getValor()));

				WebElement pass = driver.findElement(By.name("password"));

				performInput(driver, pass, PassCript.sendHash(Enums.AUTH_2_1.getValor()));

				driver.findElement(By.xpath("//button[@font-family='Rational-Medium']")).click();

				WebElement verificacaoLogado = getWebElement(driver, "//a[text()='Acompanhar']");
				if (verificacaoLogado == null) {
					botMessage("Infelizmente ocorreu algum erro durante a autenticação no site da corretora");
					finalizar(driver);
				}

				botMessage("Logado no site da corretora :)");
				botMessage(getDate());

				WebElement investimento1 = getWebElement(driver, "//p[text()='" + Enums.INVEST_1.getValor() + "']");
				clicar(investimento1);

				WebElement valorLiquido1 = getWebElement(driver, "//span[@class='sc-fONwsr lbnRXV']");
				String valor1 = valorLiquido1.getText();
				valor1 = retirarCaracteresValor(valor1);

				botMessage(messagemInvestimento(Enums.INVEST_1));
				botMessage(valorLiquido1.getText());
				Double variacao = Double.valueOf(valor1)- 1302.84;
				botMessage("Variação: " + variacao);

				fecharPopUp(driver);

				WebElement investimento2 = getWebElement(driver, "//p[text()='" + Enums.INVEST_2.getValor() + "']");
				clicar(investimento2);

				WebElement valorLiquido2 = getWebElement(driver, "//span[@class='sc-fONwsr lbnRXV']");
				String valor2 = valorLiquido2.getText();
				valor2 = retirarCaracteresValor(valor2);

				botMessage(messagemInvestimento(Enums.INVEST_2));
				botMessage(valorLiquido2.getText());
				Double variacao2 = Double.valueOf(valor2)- 701.66;
				botMessage("Variação2: " + variacao2);


				finalizar(driver);

			} catch (NoSuchElementException e) {
				logger.warning(e.getMessage());
				logger.warning(e.getLocalizedMessage());
				botMessage("Algum Erro ocorreu, talvez algum elemento não foi encontrado, verifique a conexão da internet");
			}

		} else {
			finalizar(driver);
		}
	}

	private String retirarCaracteresValor(String valor) {
		valor = valor.replace("R", "");
		valor = valor.replace("$", "");
		valor = valor.replace(" ", "");
		valor = valor.replace(".", "");
		valor = valor.replace(",", ".");
		return valor;
	}

	private void fecharPopUp(WebDriver driver) throws InterruptedException {
		WebElement popUp = getWebElement(driver, "//button[@data-modal='header-close-button']");
		clicar(popUp);
	}

	private void clicar(WebElement elemento) {
		if(elemento != null) {
			elemento.click();
		}
	}

	private void finalizar(WebDriver driver) {
		driver.close();
		driver.quit();
	}

	private WebElement getWebElement(WebDriver driver, String xpath) throws InterruptedException {
		Thread.sleep(1000);
		return exist(driver, By.xpath(xpath));
	}

	private static boolean grantApplicationAccess() {
		boolean auth1Match = PassCript.verifyUserPassword(Enums.AUTH_1_1.getValor(), Enums.AUTH_1_2.getValor(), Enums.AUTH_1_3.getValor());
		boolean auth2Match = PassCript.verifyUserPassword(Enums.AUTH_2_1.getValor(), Enums.AUTH_2_2.getValor(), Enums.AUTH_2_3.getValor());

		if(!auth2Match || !auth1Match) {
//			botMessage("Autenticação - Falhou -> Easynvest Application\n" + getDate());
			return false;
		}
//		botMessage("Autenticação - Permitida -> Easynvest Application\n" + getDate());
		return true;
	}

	private String messagemInvestimento(Enums enums) {
		return Enums.MSG_1.getValor() + enums.getValor();
	}

	private static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		return format.format(new Date());
	}

	private static void botMessage(String message) {
		telegramBot.execute(new SendMessage(Enums.CHAT_ID.getValor(), message));
		logger.info(message);
	}

	private static void performInput(WebDriver driver, WebElement campo, String campoEnum) {
		Actions actions = new Actions(driver);
		actions.moveToElement(campo);
		actions.click();
		actions.sendKeys(campoEnum);
		actions.build().perform();
	}

	private static WebElement exist(WebDriver driver, By by) {
		try {
			return driver.findElement(by);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
