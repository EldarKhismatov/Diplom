package page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class StartPage {
    private SelenideElement paymentButton = $(byText("Купить"));
    private SelenideElement creditButton = $(byText("Купить в кредит"));

    public PaymentPage checkPaymentButton() {
        paymentButton.click();
        return new PaymentPage();
    }

    public CreditPage checkCreditButton() {
        creditButton.click();
        return new CreditPage();
    }
}
