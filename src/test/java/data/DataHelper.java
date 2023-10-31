package data;

import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class DataHelper {

    //Генерация для поля "Номер карты"
    public static String getValidActiveCard(){
        return "4444 4444 4444 4441";
    }
    public static String getValidDeclinedCard(){
        return "4444 4444 4444 4442";
    }
    public static String getZeroNumberCard(){
        return "0000 0000 0000 0000";
    }
    public static String getEmptyNumberCard(){
        return "";
    }

    public static String getInvalidNumberCard(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("#### #### ##### ####");
    }
    public static String getInvalidPatternNumberCard(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("#### #### ##### ##");
    }
    public static String getRuSymbolNumberCard(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("ru"), new RandomService());
        return fakeValuesService.numerify("йцук енгш щзфы вапр");
    }
    public static String getEngSymbolNumberCard(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("qwer tyui opas dfgh");
    }
    public static String getMaxSymbolNumberCard(){
        return "1234 5678 9123 4567 8947";
    }

    // Генерация для поля "Месяц"
    public static String getCurrentMonth(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
    }
    public static String getPreviousMonth(){
        int currentMonth = LocalDate.now().getMonthValue();
        if (currentMonth == 1){
            return getLastMonth();
        } else {
            return LocalDate.now().minusMonths(2).format(DateTimeFormatter.ofPattern("MM"));
        }
    }
    public static String getLastMonth(){
        int plusMonth = LocalDate.now().getMonthValue();
        return LocalDate.now().plusMonths(12 - plusMonth).format(DateTimeFormatter.ofPattern("MM"));
    }
    public static  String getZeroMonth(){
        return "00";
    }
    public static String getInvalidMonth(){
        return "13";
    }
    public static String getEmptyMonth(){
        return "";
    }
    public static String getSymbolMonth(){ return "!@";}

    // Генерация для поля "Год"
    public static String getCurrentYear(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yy"));
    }
    public static String getPreviousYear(){
        return LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }
    public static String getNextYear(){
        return LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("yy"));
    }
    public static String getSymbolYear(){ return "!@";}
    public static String getZeroYear(){
        return "00";
    }
    public static String getEmptyYear(){
        return "";
    }

    //Генерация для поля "Владелец"
    public static String getValidOwner(){
        Faker faker = new Faker(new Locale("en"));
        return faker.name().firstName() + " " + faker.name().lastName();
    }
    public static String getValid35SymbolCard(){
        String firstNameRussian = "Ivanivanivanivanivanivanivanivaniva";
        String lastNameEnglish = "Ivanivanivanivanivanivanivanivaniva";
        return firstNameRussian + " " + lastNameEnglish;
    }
    public static String getValid2SymbolCard(){
        String firstNameRussian = "IV";
        String lastNameEnglish = "Iv";
        return firstNameRussian + " " + lastNameEnglish;
    }
    public static String getValidSymbolCard(){
        String firstNameRussian = "Ivan-Alex";
        String lastNameEnglish = "Ivanov";
        return firstNameRussian + " " + lastNameEnglish;
    }
    public static String getInvalidLocalOwner(){
        Faker faker = new Faker(new Locale("ru"));
        return faker.name().firstName() + " " + faker.name().lastName();
    }
    public static String getInvalidOwner(){
        Faker faker = new Faker(new Locale("en"));
        return faker.name().firstName();
    }
    public static String getNamesFirstRu() {
        String firstNameRussian = "Иван";
        String lastNameEnglish = "Ivanov";
        return firstNameRussian + " " + lastNameEnglish;
    }
    public static String getNamesLastRu() {
        String firstNameEnglish = "Ivan";
        String lastNameRussian = "Иванов";
        return firstNameEnglish + " " + lastNameRussian;
    }
    public static String getInvalidNameOwner(){
        Faker faker = new Faker(new Locale("ru"), new RandomService());
        return faker.name().firstName() + 45 + " " + faker.name().lastName() + 45;
    }
    public static String getInValidOwner(){
        Faker faker = new Faker(new Locale("en"));
        return faker.name().firstName() + " " + "al" + " " + faker.name().lastName();
    }
    public static String getInvalidSymbolOwner(){
        String firstNameRussian = "!@#$";
        String lastNameEnglish = "!@#$";
        return firstNameRussian + " " + lastNameEnglish;
    }
    public static String getEmptyOwner(){
        return "";
    }

    // Генерация для поля "CVC/CWW"
    public static String getValidCVC(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("###");
    }
    public static String getRuValidCVC(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("ru"), new RandomService());
        return fakeValuesService.numerify("ицу");
    }
    public static String getEngInValidCVC(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("qwe");
    }
    public static String getZeroCVC(){
        return "000";
    }
    public static String getInvalidCVC(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("##");
    }
    public static String getMaxInvalidCVC(){
        FakeValuesService fakeValuesService = new FakeValuesService(new Locale("en"), new RandomService());
        return fakeValuesService.numerify("1234");
    }
    public static String getEmptyCVC(){
        return "";
    }
    public static String getSymbolCVC(){ return "!@#";}

}
