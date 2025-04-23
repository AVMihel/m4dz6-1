package ru.netology.testmode.test;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import ru.netology.testmode.data.*;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

class TransferTest {
    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCard;
    private DataHelper.CardInfo secondCard;

    @BeforeAll
    static void setUpAll() {
        Configuration.browser = "chrome";
        Configuration.timeout = 15_000;
    }

    @BeforeEach
    void setup() {
        Configuration.headless = true;
        open("http://localhost:9999");

        // Авторизация и переход на dashboard
        dashboardPage = new LoginPage()
                .validLogin(DataHelper.getAuthInfo())
                .validVerify(DataHelper.getVerificationCode());

        firstCard = DataHelper.getFirstCard();
        secondCard = DataHelper.getSecondCard();
    }

    @Test
    @DisplayName("Успешный перевод средств между картами")
    void shouldTransferMoneyBetweenCards() {
        // Получаем начальные балансы
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        int transferAmount = 1_000;

        // Выполняем перевод
        dashboardPage = dashboardPage
                .selectCardToTransfer(secondCard)
                .makeValidTransfer(String.valueOf(transferAmount), firstCard.getFullNumber());

        // Проверяем изменения балансов
        assertEquals(initialFirstCardBalance - transferAmount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance + transferAmount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    @DisplayName("Попытка перевода суммы, превышающей баланс карты")
    void shouldNotTransferMoreThanBalance() {
        int initialBalance = dashboardPage.getCardBalance(firstCard);
        int excessiveAmount = initialBalance + 10_000;

        dashboardPage
                .selectCardToTransfer(secondCard)
                .makeTransfer(String.valueOf(excessiveAmount), firstCard.getFullNumber());

        // Проверяем сообщение об ошибке
        $("[data-test-id='error-notification'] .notification__content")
                .shouldBe(visible)
                .shouldHave(text("Ошибка! Недостаточно средств на карте"));

        // Проверяем, что балансы не изменились
        assertEquals(initialBalance, dashboardPage.getCardBalance(firstCard));
    }

    @Test
    @DisplayName("Пополнение карты минимальной суммой (1 рубль)")
    void shouldTransferMinimumAmount() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        int transferAmount = 1;

        dashboardPage = dashboardPage
                .selectCardToTransfer(secondCard)
                .makeValidTransfer(String.valueOf(transferAmount), firstCard.getFullNumber());

        assertEquals(initialFirstCardBalance - transferAmount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance + transferAmount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    @DisplayName("Отмена перевода средств")
    void shouldCancelTransfer() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        // Начинаем перевод, но отменяем
        dashboardPage = dashboardPage
                .selectCardToTransfer(secondCard)
                .cancelTransfer();

        // Проверяем, что балансы не изменились
        assertEquals(initialFirstCardBalance, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance, dashboardPage.getCardBalance(secondCard));

        // Проверяем, что вернулись на страницу dashboard
        $("[data-test-id=dashboard]").shouldBe(visible);
    }

    @AfterEach
    void tearDown() {
        refresh(); // Обновляем страницу после каждого теста
    }
}