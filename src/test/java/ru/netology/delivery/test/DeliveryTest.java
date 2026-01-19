package ru.netology.delivery.test;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import ru.netology.delivery.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

class DeliveryTest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Should successful plan and replan meeting")
    void shouldSuccessfulPlanAndReplanMeeting() {
        Allure.step("Запуск тестируемой веб-формы", () -> {
            Selenide.open("http://localhost:9999");
        });

        var validUser = DataGenerator.Registration.generateUser("ru");
        var daysToAddForFirstMeeting = 4;
        var firstMeetingDate = DataGenerator.generateDate(daysToAddForFirstMeeting);
        var daysToAddForSecondMeeting = 7;
        var secondMeetingDate = DataGenerator.generateDate(daysToAddForSecondMeeting);

        Allure.step("Заполнение поля ввода города:", () -> {
            $("[data-test-id=city] input").setValue(validUser.getCity());
        });

        Allure.step("Очистка и заполнение поля ввода первой даты встречи:", () -> {
            $("[data-test-id=date] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
            $("[data-test-id=date] input").setValue(firstMeetingDate);
        });

        Allure.step("Заполнение поля ввода имени:", () -> {
            $("[data-test-id=name] input").setValue(validUser.getName());
        });

        Allure.step("Заполнение поля ввода мобильного телефона:", () -> {
            $("[data-test-id=phone] input").setValue(validUser.getPhone());
        });

        Allure.step("Включение чекбокса о согласии обработки и использования персональных данных:", () -> {
            $("[data-test-id=agreement]").click();
        });

        Allure.step("Отправка формы планирования первой даты встречи:", () -> {
            $(Selectors.byText("Запланировать")).click();
        });

        Allure.step("Проверка успешного планирования первой даты встречи:", () -> {
            $(Selectors.withText("Успешно!")).should(visible, Duration.ofSeconds(15));
            $("[data-test-id=success-notification] .notification__content")
                    .shouldHave(exactText("Встреча успешно запланирована на " + firstMeetingDate))
                    .shouldBe(visible);
        });

        Allure.step("Очистка поля даты и ввод другой даты встречи:", () -> {
            $("[data-test-id=date] input").press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
            $("[data-test-id=date] input").setValue(secondMeetingDate);
        });

        Allure.step("Повторная отправка формы планирования другой даты встречи:", () -> {
            $(Selectors.byText("Запланировать")).click();
        });

        Allure.step("Получение уведомления об уже запланированной встрече, с предложением перепланировать:",
                () -> {
                    $("[data-test-id=replan-notification] .notification__content")
                            .shouldHave(text("У вас уже запланирована встреча на другую дату. Перепланировать?"))
                            .shouldBe(visible);
                });

        Allure.step("Перепланирование даты встречи на другую:", () -> {
            $("[data-test-id=replan-notification] button").click();
        });

        Allure.step("Получение уведомления об успешно запланированной встрече на другую дату:",
                () -> {
                    $("[data-test-id=success-notification] .notification__content")
                            .shouldHave(exactText("Встреча успешно запланирована на " + secondMeetingDate))
                            .shouldBe(visible);
                });
    }
}