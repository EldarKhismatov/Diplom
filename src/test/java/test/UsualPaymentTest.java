package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import data.CardInfo;
import data.DataHelperSQL;
import page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static data.DataHelper.*;

public class UsualPaymentTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        open("http://localhost:8080/");
        DataHelperSQL.clearTables();
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValidActiveCard() { // 1. Успешная оплата по активной карте
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkApprovedForm();
        assertEquals("APPROVED", DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValidDeclinedCard() { // 2. Отклонение оплаты по заблокированной карте
        CardInfo card = new CardInfo(getValidDeclinedCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkDeclinedForm();
        assertEquals("DECLINED", DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldStatusBuyPaymentValid2Symbol(){ // 3. Ввод данных в поле "Владелец" на латинице, фамилия и имя состоят из 2 символов.
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValid2SymbolCard(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkApprovedForm();
        assertEquals("APPROVED", DataHelperSQL.getPaymentStatus());
    }

   @SneakyThrows
   @Test
   void shouldStatusBuyPaymentValid35Symbol(){ // 4. Ввод данных в поле "Владелец" на латинице, фамилия и имя состоят из 35 символов.
       CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValid35SymbolCard(), getValidCVC());
       val mainPage = new StartPage();
       mainPage.checkPaymentButton().
               fillingForm(card).
               checkApprovedForm();
       assertEquals("APPROVED", DataHelperSQL.getPaymentStatus());
   }

   @SneakyThrows
   @Test
   void shouldStatusBuyPaymentValidSymbol(){ // 5. Ввод данных в поле "Владелец" на латинице, имя через дефис
       CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidSymbolCard(), getValidCVC());
       val mainPage = new StartPage();
       mainPage.checkPaymentButton().
               fillingForm(card).
               checkApprovedForm();
       assertEquals("APPROVED", DataHelperSQL.getPaymentStatus());
   }


    // НЕГАТИВНЫЕ СЦЕНАРИИ
    // "Номер карты"
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidCard() { // 1. В поле "Номер карты" ввести данные спец символами
        CardInfo card = new CardInfo(getInvalidNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkDeclinedForm();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidPatternCard() { // 2. В поле "Номер карты" ввести менее 16 цифр.
        CardInfo card = new CardInfo(getInvalidPatternNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyCard() { // 3. Поле "Номер карты" оставить пустым
        CardInfo card = new CardInfo(getEmptyNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroCard() { // 4. В поле "Номер карты" ввести 16 нулей
        CardInfo card = new CardInfo(getZeroNumberCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentRuCard(){ // 5. В поле "Номер карты" ввести данные на кирилице
        CardInfo card = new CardInfo(getRuSymbolNumberCard(),getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentEnCard(){ // 6. В поле "Номер карты" ввести данные на латинице
        CardInfo card = new CardInfo(getEngSymbolNumberCard(),getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentMaxSymbolCard(){ // 7. В поле "Номер карты" ввести более 16 цифр
        CardInfo card = new CardInfo(getMaxSymbolNumberCard(),getCurrentMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCardNumberError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    //Поле "Месяц"
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonthCardExpiredCardError() { // 8. В поле "Месяц" ввести невалидное значение (истекший срок действия карты)
        CardInfo card = new CardInfo(getValidActiveCard(), getPreviousMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkExpiredCardError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidMonth() { // 9. В поле "Месяц" ввести номер 13 месяца
        CardInfo card = new CardInfo(getValidActiveCard(), getLastMonth(), getCurrentYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroMonth() { // 10. В поле "Месяц" ввести 00
        CardInfo card = new CardInfo(getValidActiveCard(), getZeroMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyMonth() { // 11. Поле "Месяц" оставить пустым.
        CardInfo card = new CardInfo(getValidActiveCard(), getEmptyMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentSymbolMonth(){ // 12. В поле "Месяц" ввести данные спец символами
        CardInfo card = new CardInfo(getValidActiveCard(), getSymbolMonth(), getNextYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkMonthError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    // Поле Год
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidYearCard() { // 13. В поле "Год" ввести прошедший год
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getPreviousYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyYear() { // 14. Поле "Год" оставить пустым
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getEmptyYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroYear() { // 15. В поле "Год" ввести нулевой год "00"
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getZeroYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentSymbolYear(){ // 16. В поле "Год" ввести спец символы
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getSymbolYear(), getValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkYearError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    // Поле "Владелец"
    @SneakyThrows
    @Test
    void shouldBuyPaymentRussianOwner() { // 17. В поле "Владелец" ввести имя, фамилию на кирилице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidLocalOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentFirstNameOwner() { // 18. В поле "Владелец" ввести только имя на латинице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyOwner() { // 19. Поле "Владелец" оставить пустым
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getEmptyOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentNamesFirstRu(){ // 20. Имя на кирилице, фамилия на латинице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getNamesFirstRu(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void  shouldBuyPaymentNamesLastRu(){ // 21. Имя на латинице, фамилия на кирилице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getNamesLastRu(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidName(){ // 22. Имя и Фамилия на кирилице с цифрами
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidNameOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidOwner(){// 23. Имя и Фамилия на латинице с пробелами
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInValidOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidSymbolOwner(){ // 24. Имя и Фамилия спец символами
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getInvalidSymbolOwner(), getValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkOwnerError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    // Поле "CVC/CVV"
    @SneakyThrows
    @Test
    void shouldBuyPaymentInvalidCVC() { // 25. В поле "CVC/CVV" ввести только 2 цифры
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getInvalidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentEmptyCVC() { // 26. Поле "CVC/CVV" оставить пустым
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getEmptyCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }


    @SneakyThrows
    @Test
    void shouldBuyPaymentZeroCVC() { // 27. В поле "CVC/CVV" ввести "нули"
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getZeroCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentRuCVC(){ // 28. В поле "CVC/CVV" ввести данные на кирилице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getRuValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentEngCVC(){ // 29. В поле "CVC/CVV" ввести данные на латинице
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getEngInValidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentMaxCVC(){ // 30. В поле "CVC/CVV" ввести  4 цифры
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getMaxInvalidCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }

    @SneakyThrows
    @Test
    void shouldBuyPaymentSymbolCVC(){ // 31. В поле "CVC/CVV" ввести  спец символы
        CardInfo card = new CardInfo(getValidActiveCard(), getCurrentMonth(), getNextYear(), getValidOwner(), getSymbolCVC());
        val mainPage = new StartPage();
        mainPage.checkPaymentButton().
                fillingForm(card).
                checkCVCError();
        assertNull(DataHelperSQL.getPaymentStatus());
    }
}
