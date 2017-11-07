package com.commercetools.sunrise.ctp.project;

import com.commercetools.sunrise.framework.localization.NoCountryFoundException;
import com.commercetools.sunrise.framework.localization.NoCurrencyFoundException;
import com.commercetools.sunrise.framework.localization.NoLocaleFoundException;
import com.neovisionaries.i18n.CountryCode;
import io.sphere.sdk.projects.Project;
import org.junit.Test;
import play.Configuration;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ProjectContextTest {

    private static final CurrencyUnit GBP = Monetary.getCurrency("GBP");
    private static final CurrencyUnit CNY = Monetary.getCurrency("CNY");
    private static final CurrencyUnit EUR = Monetary.getCurrency("EUR");
    private static final CurrencyUnit USD = Monetary.getCurrency("USD");
    private static final List<Locale> LOCALES = asList(ENGLISH, CHINESE, GERMAN);
    private static final List<CountryCode> COUNTRIES = asList(CountryCode.UK, CountryCode.CN, CountryCode.DE);
    private static final List<CurrencyUnit> CURRENCIES = asList(GBP, CNY, EUR);
    private static final ProjectContext PROJECT_CONTEXT = createProjectContext(LOCALES, COUNTRIES, CURRENCIES);

    @Test
    public void defaultLocale() {
        assertThat(PROJECT_CONTEXT.defaultLocale()).isEqualTo(ENGLISH);
    }

    @Test
    public void supportedLocales() throws Exception {
        assertThat(PROJECT_CONTEXT.isLocaleSupported(GERMAN)).isTrue();
        assertThat(PROJECT_CONTEXT.isLocaleSupported(ENGLISH)).isTrue();
        assertThat(PROJECT_CONTEXT.isLocaleSupported(CHINESE)).isTrue();
        assertThat(PROJECT_CONTEXT.isLocaleSupported(FRENCH)).isFalse();
    }

    @Test
    public void throwsExceptionOnEmptyLocales() throws Exception {
        assertThatThrownBy(() -> createProjectContext(emptyList(), COUNTRIES, CURRENCIES).defaultLocale())
                .isInstanceOf(NoLocaleFoundException.class)
                .hasMessageContaining("Project does not have any valid locale associated");
    }

    @Test
    public void throwsExceptionOnOnlyNullLocales() throws Exception {
        assertThatThrownBy(() -> createProjectContext(asList(null, null, null), COUNTRIES, CURRENCIES).defaultLocale())
                .isInstanceOf(NoLocaleFoundException.class)
                .hasMessageContaining("Project does not have any valid locale associated");
    }

    @Test
    public void defaultCountry() {
        assertThat(PROJECT_CONTEXT.defaultCountry()).isEqualTo(CountryCode.UK);
    }

    @Test
    public void supportedCountries() throws Exception {
        assertThat(PROJECT_CONTEXT.isCountrySupported(CountryCode.DE)).isTrue();
        assertThat(PROJECT_CONTEXT.isCountrySupported(CountryCode.UK)).isTrue();
        assertThat(PROJECT_CONTEXT.isCountrySupported(CountryCode.CN)).isTrue();
        assertThat(PROJECT_CONTEXT.isCountrySupported(CountryCode.FR)).isFalse();
    }

    @Test
    public void throwsExceptionOnEmptyCountries() throws Exception {
        assertThatThrownBy(() -> createProjectContext(LOCALES, emptyList(), CURRENCIES).defaultCountry())
                .isInstanceOf(NoCountryFoundException.class)
                .hasMessageContaining("Project does not have any valid country code associated");
    }

    @Test
    public void throwsExceptionOnOnlyNullCountries() throws Exception {
        assertThatThrownBy(() -> createProjectContext(LOCALES, asList(null, null, null), CURRENCIES).defaultCountry())
                .isInstanceOf(NoCountryFoundException.class)
                .hasMessageContaining("Project does not have any valid country code associated");
    }

    @Test
    public void defaultCurrency() {
        assertThat(PROJECT_CONTEXT.defaultCurrency()).isEqualTo(GBP);
    }

    @Test
    public void supportedCurrencies() throws Exception {
        assertThat(PROJECT_CONTEXT.isCurrencySupported(EUR)).isTrue();
        assertThat(PROJECT_CONTEXT.isCurrencySupported(GBP)).isTrue();
        assertThat(PROJECT_CONTEXT.isCurrencySupported(CNY)).isTrue();
        assertThat(PROJECT_CONTEXT.isCurrencySupported(USD)).isFalse();
    }

    @Test
    public void throwsExceptionOnEmptyCurrencies() throws Exception {
        assertThatThrownBy(() -> createProjectContext(LOCALES, COUNTRIES, emptyList()).defaultCurrency())
                .isInstanceOf(NoCurrencyFoundException.class)
                .hasMessageContaining("Project does not have any valid currency unit associated");
    }

    @Test
    public void throwsExceptionOnOnlyNullCurrencies() throws Exception {
        assertThatThrownBy(() -> createProjectContext(LOCALES, COUNTRIES, asList(null, null, null)).defaultCurrency())
                .isInstanceOf(NoCurrencyFoundException.class)
                .hasMessageContaining("Project does not have any valid currency unit associated");
    }

    @Test
    public void createsInstanceWithStaticConstructor() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("languages", asList("de", "en-US"));
        map.put("countries", asList("DE", "US"));
        map.put("currencies", asList("EUR", "USD"));
        final Configuration configuration = new Configuration(singletonMap("foo", map));

        final Project dummyProject = mock(Project.class);
        final ProjectContext projectContext = ProjectContext.of(configuration, "foo", dummyProject);
        assertThat(projectContext.locales()).containsExactly(Locale.GERMAN, Locale.US);
        assertThat(projectContext.countries()).containsExactly(CountryCode.DE, CountryCode.US);
        assertThat(projectContext.currencies()).containsExactly(EUR, USD);
    }

    private static ProjectContext createProjectContext(final List<Locale> locales, final List<CountryCode> countries,
                                                       final List<CurrencyUnit> currencies) {
        return new ProjectContext() {
            @Override
            public List<Locale> locales() {
                return locales;
            }

            @Override
            public List<CountryCode> countries() {
                return countries;
            }

            @Override
            public List<CurrencyUnit> currencies() {
                return currencies;
            }
        };
    }
}
