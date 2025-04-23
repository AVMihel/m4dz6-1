package ru.netology.testmode.data;

import static com.codeborne.selenide.Selenide.$;

public class VerificationPage {
    public DashboardPage validVerify(String verificationCode) {
        $("[data-test-id=code] input").setValue(verificationCode);
        $("[data-test-id=action-verify]").click();
        return new DashboardPage();
    }
}
