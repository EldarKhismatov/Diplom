package test.Ui;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.DataHelperSQL;
import io.qameta.allure.*;
import io.qameta.allure.selenide.AllureSelenide;
import org.testng.annotations.*;
import page.TripCardPage;
import page.TripFormPage;

import java.util.List;

import static com.codeborne.selenide.Selenide.open;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

@Epic("UI тестирование функционала Путешествие дня")
@Feature("Покупка тура в кредит")
public class CreditUiTests {
    private static DataHelper.CardData cardData;
    private static TripCardPage tripCard;
    private static TripFormPage tripForm;
    private static List<DataHelperSQL.PaymentEntity> payments;
    private static List<DataHelperSQL.CreditRequestEntity> credits;
    private static List<DataHelperSQL.OrderEntity> orders;

    @BeforeClass
    public void setupClass() {
        DataHelperSQL.setDown();
        SelenideLogger.addListener("allure", new AllureSelenide()
                .screenshots(true).savePageSource(true));
    }

    @BeforeMethod
    public void setupMethod() {
        open("http://localhost:8080/");
        tripCard = new TripCardPage();
    }

    @AfterMethod
    public void setDownMethod() {
        DataHelperSQL.setDown();
    }

    @AfterClass
    public void setDownClass() {
        SelenideLogger.removeListener("allure");
    }

    @Story("HappyPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldHappyPath() {
        cardData = DataHelper.getValidApprovedCard();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();

        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("approved"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Story("SadPath")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldSadPath() {
        cardData = DataHelper.getValidDeclinedCard();

        tripForm = tripCard.clickCreditButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationWithErrorNotification();

        payments = DataHelperSQL.getPayments();
        credits = DataHelperSQL.getCreditsRequest();
        orders = DataHelperSQL.getOrders();
        assertEquals(0, payments.size());
        assertEquals(1, credits.size());
        assertEquals(1, orders.size());

        assertTrue(credits.get(0).getStatus().equalsIgnoreCase("declined"));
        assertEquals(credits.get(0).getBank_id(), orders.get(0).getPayment_id());
        assertEquals(credits.get(0).getId(), orders.get(0).getCredit_id());
    }

    @Story("Переключение с формы оплаты на форму кредита")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldImmutableInputValuesAfterClickButton() {
        cardData = DataHelper.getValidApprovedCard();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripCard.clickCreditButton();
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
    }
}
