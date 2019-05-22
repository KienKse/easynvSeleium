package com.kien.easynvest.application;

import com.kien.easynvest.enums.Enums;
import com.kien.easynvest.tools.PassCript;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.runner.Runner;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

@SpringBootApplication
public class EasyApplication implements CommandLineRunner {

	private static final String ELEMENTO_CELULA_VALOR_NAO_ENCONTRADO = "Elemento celula -> valor - não encontrado";
	private static TelegramBot telegramBot = new TelegramBot(Enums.TELEGRAM_KEY.getValor());
	private static Logger logger = Logger.getLogger(String.valueOf(Runner.class));
	private static final String VALOR_INVESTIDO_CELL_TEXT = "Valor investido";

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

		if(verificarCredenciais()) {
			try {

				driver.get(Enums.SITE_AUTENTICACAO_EASY.getValor());

				dontLoadPage(driver, 2000L);

//				popUp Easy
//				fecharPopUp(driver);

				WebElement login = new WebDriverWait(driver, 20).until(
						ExpectedConditions.presenceOfElementLocated(By.name("username")));

				performInput(driver, login, performCript(Enums.AUTH_1_1));

				WebElement pass = driver.findElement(By.name("password"));

				performInput(driver, pass, performCript(Enums.AUTH_2_1));

				driver.findElement(By.xpath("//button[@font-family='Rational-Medium']")).click();

				WebElement verificacaoLogado = new WebDriverWait(driver, 20).until(
						ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Acompanhar']")));
				if (verificacaoLogado == null) {
					botMessage("Infelizmente ocorreu algum erro durante a autenticação no site da corretora");
					finalizar(driver);
				}

				botMessage("Logado no site da corretora  ( ͡° ͜ʖ ͡°)");
				botMessage(getDate());


//				WebElement investimento1 = getWebElement(driver, "//p[text()='" + Enums.INVEST_1.getValor() + "']");
				WebElement investimento1 = getWebElement(driver, "//div[@class='sc-fhYwyz ezDOlm']");
				clicar(investimento1);

				WebElement valorLiquido1 = investElementScreenLiquid(driver);
				String valor1 = getStringValueToSheet(valorLiquido1);

				botMessage(messagemInvestimento(Enums.INVEST_1));
				botMessage(valorLiquido1.getText());

				fecharPopUp(driver);

//				WebElement investimento2 = getWebElement(driver, "//p[text()='" + Enums.INVEST_2.getValor() + "']");
				WebElement investimento2 = ((ChromeDriver) driver).findElementByXPath("//p[text()='" + Enums.INVEST_2.getValor() + "']");
//				WebElement investimento2 = getWebElement(driver, "//*[@id='app']/div[2]/div[2]/div/div/div/div[3]/div");
				clicar(investimento2);

				WebElement valorLiquido2 = investElementScreenLiquid(driver);
				String valor2 = getStringValueToSheet(valorLiquido2);

				botMessage(messagemInvestimento(Enums.INVEST_2));
				botMessage(valorLiquido2.getText());

				peformPlanilha(driver, valor1, valor2) ;

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

	private static boolean verificarCredenciais() {
		if(grantApplicationAccess(Enums.AUTH_1_1, Enums.AUTH_1_2, Enums.AUTH_1_3)) {
			logger.info("\nGranted 1 ");
			if(grantApplicationAccess(Enums.AUTH_2_1, Enums.AUTH_2_2, Enums.AUTH_2_3)) {
				logger.info("\nGranted 2 ");
				if(grantApplicationAccess(Enums.AUTH_3_1, Enums.AUTH_3_2, Enums.AUTH_3_3)) {
					logger.info("\nGranted 3 ");
					if(grantApplicationAccess(Enums.AUTH_4_1, Enums.AUTH_4_2, Enums.AUTH_4_3)) {
						botMessage("Todas as 4 camadas de credenciais estão - OK");
						return true;
					} else {
						logger.info("\nERRO 4 ");
						return false;
					}
				} else {
					logger.info("\nERRO 3 ");
					return false;
				}
			} else {
				logger.info("\nERRO 2 ");
				return false;
			}
		} else {
			logger.info("\nERRO 1");
			return false;
		}
	}

	private WebElement investElementScreenLiquid(WebDriver driver) throws InterruptedException {
		return getWebElement(driver, "//span[@class='sc-fONwsr lbnRXV']");
	}

	private String getStringValueToSheet(WebElement valorLiquido1) {
		String valor1 = null;
		if(valorLiquido1 != null) {
			valor1 = valorLiquido1.getText();
			valor1 = retirarCaracteresValor(valor1);
		}
		return valor1;
	}

	private void peformPlanilha(WebDriver driver, String valorGmail, String valorGmail2) throws InterruptedException {
		driver.get(Enums.SITE_SHEET.getValor());

		WebElement logarButton = getWebElement(driver, "//div[@id='gb']");
		clicar(logarButton);

		WebElement loginGmail = new WebDriverWait(driver, 20).until(
				ExpectedConditions.presenceOfElementLocated(By.id("identifierId")));

		performInput(driver, loginGmail, performCript(Enums.AUTH_3_1));


		WebElement next = ((ChromeDriver) driver).findElementById("identifierNext");
		clicar(next);

		WebElement passGmail = new WebDriverWait(driver, 20).until(
				ExpectedConditions.presenceOfElementLocated(By.name("password")));

		Thread.sleep(1000L);

		performInput(driver, passGmail, performCript(Enums.AUTH_4_1));


		WebElement next2 = ((ChromeDriver) driver).findElementById("passwordNext");
		clicar(next2);

		botMessage("SHEET -> Logado | Verifique seu navegador");


		logger.info("Trying to wait...");

		Thread.sleep(20000L);

		Actions planilha = new Actions(driver);
		precionarTecla(planilha, Keys.ARROW_RIGHT, 4);
		precionarTecla(planilha, Keys.ARROW_DOWN, 7);

		WebElement cell = getWebElement(driver, "//div[@class='cell-input']");
		verificarCellContent("Valor Liquido", planilha, cell, Arrays.asList(valorGmail));

		precionarTecla(planilha, Keys.ARROW_DOWN, 4);

		performCellEdit(valorGmail2, planilha, cell);

		precionarTecla(planilha, Keys.ARROW_LEFT, 5);
		precionarTecla(planilha, Keys.ARROW_UP, 14);

		precionarTecla(planilha, Keys.ARROW_RIGHT, 8);
		precionarTecla(planilha, Keys.ARROW_DOWN, 8);

		verificarHistorico(planilha, cell, Arrays.asList(valorGmail, valorGmail2));
		botMessage("Valores atualizados na planilha!");
	}

	private static String performCript(Enums auth) {
		return PassCript.sendHash(auth.getValor());
	}

	private void verificarHistorico(Actions planilha, WebElement cell, List<String> valoresGmail) throws InterruptedException {
		if(cell != null) {
			while (cell.getText() != null && !cell.getText().equals("")) {
				bugColunaVlInvestido(planilha, cell);
				precionarTecla(planilha, Keys.ARROW_DOWN, 1);
			}
			cell.sendKeys(getDate());
			precionarTecla(planilha, Keys.TAB, 1);
			enviarCalculo(planilha, cell, valoresGmail);
		} else {
			logger.warning(ELEMENTO_CELULA_VALOR_NAO_ENCONTRADO);
		}
	}

	private void bugColunaVlInvestido(Actions planilha, WebElement cell) throws InterruptedException {
		if(cell.getText().equals(VALOR_INVESTIDO_CELL_TEXT)) {
			precionarTecla(planilha, Keys.ARROW_RIGHT, 1);
		}
	}

	private void performCellEdit(String valorGmail, Actions planilha, WebElement cell) throws InterruptedException {
		if (cell != null) {
			planilha.sendKeys(Keys.DELETE);
			actionTime(planilha, 500L);
			cell.sendKeys(valorGmail);
			planilha.sendKeys(Keys.ENTER);
			actionTime(planilha, 1000L);
		} else {
			logger.warning(ELEMENTO_CELULA_VALOR_NAO_ENCONTRADO);
		}
	}

	private void verificarCellContent(String cellContent, Actions planilha, WebElement cell, List<String> valores) throws InterruptedException {
 		if (cell != null) {
			boolean preenchido = cell.getText() != null && !cell.getText().equals(cellContent);
			if (preenchido) {
				while (cell.getText() != null && !cell.getText().equals(cellContent)) {
					precionarTecla(planilha, Keys.ARROW_DOWN, 1);
				}
			}
			precionarTecla(planilha, Keys.ARROW_DOWN, 1);
			enviarCalculo(planilha, cell, valores);
		} else {
			logger.warning(ELEMENTO_CELULA_VALOR_NAO_ENCONTRADO);
		}
	}

	private void enviarCalculo(Actions planilha, WebElement cell, List<String> valores) throws InterruptedException {
		precionarTecla(planilha, Keys.DELETE, 1);
		cell.sendKeys("=");
		for (String valor : valores) {
			cell.sendKeys(valor);
			if((valores.size()-1) != (valores.indexOf(valor))) {
				cell.sendKeys(" + ");
			}
		}
		precionarTecla(planilha, Keys.ENTER, 1);
	}

	private void actionTime(Actions planilha, long l) throws InterruptedException {
		planilha.build().perform();
		Thread.sleep(l);
	}

	private void precionarTecla(Actions planilha, Keys key, int qtd) throws InterruptedException {
		for (int i = 0; i < qtd; i++) {
			planilha.sendKeys(key);
		}
		actionTime(planilha, 1000L);
	}

	private void dontLoadPage(WebDriver driver, Long tempo) {
		new WebDriverWait(driver, tempo).until(
				webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
	}


	private String retirarCaracteresValor(String valor) {
		valor = valor.replace("R", "");
		valor = valor.replace("$", "");
		valor = valor.replace(" ", "");
		valor = valor.replace(".", "");
		return valor;
	}

	private void fecharPopUp(WebDriver driver) throws InterruptedException {
		WebElement popUp = getWebElement(driver, "//button[@data-modal='header-close-button']");
		clicar(popUp);
	}

	private void clicar(WebElement elemento) {
		if(elemento != null) {
			elemento.click();
		} else {
			logger.info("Elemento não clicavel -> nulo");
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

	private static boolean grantApplicationAccess(Enums a11, Enums a12, Enums a13) {
		boolean auth1Match = PassCript.verifyUserPassword(a11.getValor(), a12.getValor(), a13.getValor());

		if(!auth1Match) {
//			botMessage("Autenticação - Falhou -> Easynvest Application\n" + getDate());
			return false;
		}
//		botMessage("Autenticação - Permitida -> Easynvest Application\n");
		return true;
	}

	private String messagemInvestimento(Enums enums) {
		return Enums.MSG_1.getValor() + enums.getValor();
	}

	private static String getDate() {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
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
