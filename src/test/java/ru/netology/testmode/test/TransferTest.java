package ru.netology.testmode.test;

import org.junit.jupiter.api.*;
import ru.netology.testmode.data.*;
import ru.netology.testmode.page.*;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

class TransferTest {
    private DashboardPage dashboardPage;
    private DataHelper.CardInfo firstCard;
    private DataHelper.CardInfo secondCard;

    @BeforeEach
    void setup() {
        open("http://localhost:9999");

        dashboardPage = new LoginPage()
                .validLogin(DataHelper.getAuthInfo())
                .validVerify(DataHelper.getVerificationCode());

        firstCard = DataHelper.getFirstCard();
        secondCard = DataHelper.getSecondCard();
    }

    @Test
    @DisplayName("Успешный перевод средств между картами")
    void shouldTransferMoneyBetweenCards() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        // Рассчитываем сумму перевода как 10% от баланса карты (но не менее 1 рубля)
        int transferAmount = Math.max(1, initialFirstCardBalance / 10);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.verifyTransferPageVisible();
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(transferAmount), firstCard.getFullNumber());

        assertEquals(initialFirstCardBalance - transferAmount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance + transferAmount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    @DisplayName("Попытка перевода суммы, превышающей баланс карты")
    void shouldNotTransferMoreThanBalance() {
        int initialBalance = dashboardPage.getCardBalance(firstCard);
        // Пытаемся перевести сумму, превышающую баланс на 1 рубль
        int excessiveAmount = initialBalance + 1;

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.makeTransfer(String.valueOf(excessiveAmount), firstCard.getFullNumber());
        transferPage.checkErrorNotification("Ошибка! Недостаточно средств на карте");

        assertEquals(initialBalance, dashboardPage.getCardBalance(firstCard));
    }

    @Test
    @DisplayName("Пополнение карты минимальной суммой (1 рубль)")
    void shouldTransferMinimumAmount() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);
        int transferAmount = 1; // Минимальная сумма

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.verifyTransferPageVisible();
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(transferAmount), firstCard.getFullNumber());

        assertEquals(initialFirstCardBalance - transferAmount, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance + transferAmount, dashboardPage.getCardBalance(secondCard));
    }

    @Test
    @DisplayName("Отмена перевода средств")
    void shouldCancelTransfer() {
        int initialFirstCardBalance = dashboardPage.getCardBalance(firstCard);
        int initialSecondCardBalance = dashboardPage.getCardBalance(secondCard);

        // Рассчитываем тестовую сумму перевода
        int transferAmount = Math.max(1, initialFirstCardBalance / 20);

        TransferPage transferPage = dashboardPage.selectCardToTransfer(secondCard);
        transferPage.verifyTransferPageVisible();
        transferPage.setAmount(String.valueOf(transferAmount));
        transferPage.setFromCard(firstCard.getFullNumber());
        dashboardPage = transferPage.cancelTransfer();

        assertEquals(initialFirstCardBalance, dashboardPage.getCardBalance(firstCard));
        assertEquals(initialSecondCardBalance, dashboardPage.getCardBalance(secondCard));
        dashboardPage.verifyDashboardVisible();
    }
}