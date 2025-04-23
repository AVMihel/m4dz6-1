package ru.netology.testmode.data;

import com.codeborne.selenide.SelenideElement;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class TransferPage {
    private SelenideElement amountInput = $("[data-test-id='amount'] input");
    private SelenideElement fromInput = $("[data-test-id='from'] input");
    private SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private SelenideElement cancelButton = $("[data-test-id='action-cancel']");

    public TransferPage() {
        $("h1.heading.heading_size_xl.heading_theme_alfa-on-white")
                .shouldBe(visible)
                .shouldHave(text("Пополнение карты"));
    }

    public DashboardPage makeValidTransfer(String amount, String fromCard) {
        makeTransfer(amount, fromCard);
        return new DashboardPage();
    }

    public void makeTransfer(String amount, String fromCard) {
        amountInput.setValue(amount);
        fromInput.setValue(fromCard);
        transferButton.click();
    }

    public DashboardPage cancelTransfer() {
        cancelButton.click();
        return new DashboardPage();
    }
}