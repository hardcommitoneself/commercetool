package setupwidget.controllers;

import com.google.common.base.Predicate;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import play.*;
import play.inject.guice.GuiceApplicationBuilder;
import play.test.WithBrowser;
import testutils.EnvironmentKeys;

import javax.annotation.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;


public class SetupControllerTest extends WithBrowser {
    @Override
    protected Application provideApplication() {
        final Map<String, Object> additionalSettings = new HashMap<>();
        additionalSettings.put("sphere.project", System.getenv(EnvironmentKeys.PROJECT));
        additionalSettings.put("sphere.clientId", System.getenv(EnvironmentKeys.CLIENT_ID));
        additionalSettings.put("sphere.clientSecret", System.getenv(EnvironmentKeys.CLIENT_SECRET));
        return new GuiceApplicationBuilder()
                .loadConfig(Configuration.load(new Environment(Mode.TEST)).withFallback(new Configuration(additionalSettings)))
                .build();
    }

    @Test
    public void widget() throws Exception {
        if ("true".equals(System.getenv("CI"))) {
            assertThat(devConf().exists()).overridingErrorMessage("file dev.conf should be absent").isFalse();
            browser.goTo("http://localhost:" + port);
            assertThat(browser.title()).containsIgnoringCase("credentials setup");
            browser.fill("#projectKey").with(System.getenv(EnvironmentKeys.PROJECT));
            browser.fill("#clientId").with(System.getenv(EnvironmentKeys.CLIENT_ID));
            browser.fill("#clientSecret").with(System.getenv(EnvironmentKeys.CLIENT_SECRET));
            browser.submit("#submit");
            waitUntilIdVisible("reload-link");
            assertThat(devConf().exists()).overridingErrorMessage("dev.conf should exist.").isTrue();
        } else {
            Logger.warn("" + SetupControllerTest.class + " is ignored.");
        }
    }

    private File devConf() {
        return new File("conf/dev.conf");
    }

    private void waitUntilIdVisible(final String id) {
        browser.fluentWait().until(new Predicate<WebDriver>() {
            @Override
            public boolean apply(@Nullable WebDriver webDriver) {

                return webDriver.findElement(By.id(id)).isDisplayed();
            }
        });
    }
}