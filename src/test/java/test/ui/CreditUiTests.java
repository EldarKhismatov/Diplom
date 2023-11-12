package test.ui;

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

    // Негативные сценарии
    @Story("Пустое поле номер карты")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyNumber() {
        cardData = DataHelper.getValidApprovedCard();
        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue("", cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("number", "Поле обязательно для заполнения");
    }


    @Story("Заполнение поля номера карты c пробелами вначале и в конце")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldSuccessfulWithStartEndSpacebarInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = " " + cardData.getNumber() + " ";
        var matchesNumber = cardData.getNumber();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("number","Неверный формат");
    }


    @Story("13 цифр в поле номера карты")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldUnsuccessfulWith13DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith13Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("number", "Поле обязательно для заполнения");
    }

    @Story("18 цифр в поле номера карты")
    @Severity(SeverityLevel.BLOCKER)
    @Test
    public void shouldUnsuccessfulWith18DigitsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith18Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Cпец символы в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInNumber() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberWithRandomSymbols();
        var matchesNumber = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField( "number","Неверный формат");
    }

    @Story("Кирилица в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsRu() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateInvalidCardNumberRuSymbol();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField( "number","Неверный формат");
    }

    @Story("Нули в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidEmpty() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWith0Digits();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField( "number","Неверный формат");
    }

    @Story("Латиница в поле номера карты")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsEn() {
        cardData = DataHelper.getValidApprovedCard();
        var number = DataHelper.generateValidCardNumberWithEnSymbol();
        var matchesNumber = number;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(number, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(matchesNumber, cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField( "number","Неверный формат");
    }


    // Поле "Месяц"
    @Story("Пустое поле месяц")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "";
        var matchesMonth = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("month","Поле обязательно для заполнения");
    }

    @Story("Заполнение поля месяц значением 00")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith00InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "00";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("month","Неверный формат");
    }

    @Story("Заполнение поля месяц значением 13")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith13InMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = "13";
        var matchesMonth = month;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("month","Неверный формат");
    }

    @Story("Спец символы в поле месяц")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInMonth() {
        cardData = DataHelper.getValidApprovedCard();
        var month = DataHelper.generateMonthWithRandomSymbols();
        var matchesMonth = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), month, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), matchesMonth, cardData.getYear(), cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("month","Неверный формат");
    }


    // Поле "Год"
    @Story("Пустое поле год")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "";
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("year","Поля обязательно для заполнения");
    }

    @Story("Спец символы в поле год")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = DataHelper.generateMonthWithRandomSymbols();
        var matchesYear = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("year","Неверный фомат");
    }

    @Story("Нули в поле год")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalid0SymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "00";
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("year","Неверный фомат");
    }

    @Story("Истёкший срок действия карты в поле год")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidBigSymbolsInYear() {
        cardData = DataHelper.getValidApprovedCard();
        var year = "10";
        var matchesYear = year;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), year, cardData.getHolder(), cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), matchesYear, cardData.getHolder(), cardData.getCvc());
        tripForm.assertField("year","Истёк срок действия карты");
    }

    // Поле "Владелец"
    @Story("Пустое поле владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = "";
        var matchesHolder = holder;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("year","Поля обязательно для заполнения");
    }


    @Story("Пробелы вначале и в конце поля владелец")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldAutoDeletingStartEndHyphenInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = " " + cardData.getHolder() + " ";
        var matchesHolder = cardData.getHolder();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("year","Неверный фомат");
    }


    @Story("Кириллица в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithCyrillicInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Неверный фомат");
    }

    @Story("Кириллица + цифры в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithCyrillicSymbolInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderWithCyrillicSymbols45();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Неверный фомат");
    }

    @Story("Латинское имя без фамилии в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithFirstName() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderFirstNameEn();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Поля обязательно для заполнения");
    }

    @Story("Имя на кирилице, фамилия на латинице в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithFirstRuLastEn() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderFirstNameRuLastEn();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Неверный фомат");
    }

    @Story("Имя на латинице, фамилия на кирилице в поле владелец")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    public void shouldVisibleNotificationWithFirstEnLastRu() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateInvalidHolderFirstNameEnLastRu();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Неверный фомат");
    }


    @Story("Спец символы в поле владелец")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolInHolder() {
        cardData = DataHelper.getValidApprovedCard();
        var holder = DataHelper.generateHolderWithInvalidSymbols();
        var matchesHolder = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), holder, cardData.getCvc());
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), matchesHolder, cardData.getCvc());
        tripForm.assertField("holder","Неверный фомат");
    }


    // CVC/CVV
    @Story("Пустое поле CVC/CVV")
    @Severity(SeverityLevel.NORMAL)
    @Test
    public void shouldVisibleNotificationWithEmptyCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "";
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Поле обязательно для заполнения");
    }

    @Story("2 цифры в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWith2DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWith2Digit();
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Поле обязательно для заполнения");
    }

    @Story("4 цифры в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldSuccessfulWith4DigitsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = cardData.getCvc() + DataHelper.generateRandomOneDigit();
        var matchesCvc = cardData.getCvc();

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertBuyOperationIsSuccessful();
    }

    @Story("Спец символы в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithInvalidSymbolsInCVC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateInvalidCVCWithRandomSymbols();
        var matchesCvc = "";

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Неверный формат");
    }

    @Story("Нули в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationWithEmptyC() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = "000";
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Неверный формат");
    }

    @Story("Латиница в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationEnSymbol() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateINValidCVCEn();
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Неверный формат");
    }

    @Story("Кирилица в поле CVC/CVV")
    @Severity(SeverityLevel.MINOR)
    @Test
    public void shouldVisibleNotificationRuSymbol() {
        cardData = DataHelper.getValidApprovedCard();
        var cvc = DataHelper.generateINValidCVCRu();
        var matchesCvc = cvc;

        tripForm = tripCard.clickPayButton();
        tripForm.insertingValueInForm(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), cvc);
        tripForm.matchesByInsertValue(cardData.getNumber(), cardData.getMonth(), cardData.getYear(), cardData.getHolder(), matchesCvc);
        tripForm.assertField("cvc","Неверный формат");
    }
}
