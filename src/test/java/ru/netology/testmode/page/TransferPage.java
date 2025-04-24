package ru.netology.testmode.page;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class TransferPage {
    private final SelenideElement amountInput = $("[data-test-id='amount'] input");
    private final SelenideElement fromInput = $("[data-test-id='from'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private final SelenideElement cancelButton = $("[data-test-id='action-cancel']");
    private final SelenideElement errorNotification = $("[data-test-id='error-notification'] .notification__content");
    private final SelenideElement heading = $("h1.heading.heading_size_xl.heading_theme_alfa-on-white");

    public TransferPage() {
        verifyTransferPageVisible();
    }

    public DashboardPage makeValidTransfer(String amount, String fromCard) {
        makeTransfer(amount, fromCard);
        return new DashboardPage();
    }

    public void makeTransfer(String amount, String fromCard) {
        setAmount(amount);
        setFromCard(fromCard);
        transferButton.click();
    }

    public TransferPage setAmount(String amount) {
        amountInput.setValue(amount);
        return this;
    }

    public TransferPage setFromCard(String cardNumber) {
        fromInput.setValue(cardNumber);
        return this;
    }

    public DashboardPage cancelTransfer() {
        cancelButton.click();
        return new DashboardPage();
    }

    public void checkErrorNotification(String expectedMessage) {
        errorNotification
                .shouldBe(visible)
                .shouldHave(text(expectedMessage));
    }

    public void verifyTransferPageVisible() {
        heading
                .shouldBe(visible)
                .shouldHave(text("Пополнение карты"));
    }
}