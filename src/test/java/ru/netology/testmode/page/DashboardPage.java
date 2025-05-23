package ru.netology.testmode.page;

import ru.netology.testmode.data.DataHelper;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class DashboardPage {
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";

    public DashboardPage() {
        $("[data-test-id=dashboard]").shouldBe(visible);
    }

    public int getCardBalance(DataHelper.CardInfo card) {
        String text = $("[data-test-id='" + card.getTestId() + "']").getText();
        return extractBalance(text);
    }

    public TransferPage selectCardToTransfer(DataHelper.CardInfo card) {
        $("[data-test-id='" + card.getTestId() + "'] [data-test-id='action-deposit']").click();
        return new TransferPage();
    }

    public void verifyCardVisible(DataHelper.CardInfo card) {
        $("[data-test-id='" + card.getTestId() + "']")
                .shouldBe(visible)
                .shouldHave(text(maskCardNumber(card.getFullNumber())));
    }

    private int extractBalance(String text) {
        int start = text.indexOf(balanceStart);
        int finish = text.indexOf(balanceFinish);
        String value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    private String maskCardNumber(String fullNumber) {
        return "**** **** **** " + fullNumber.substring(fullNumber.length() - 4);
    }

    public void verifyDashboardVisible() {
        $("[data-test-id=dashboard]").shouldBe(visible);
    }
}