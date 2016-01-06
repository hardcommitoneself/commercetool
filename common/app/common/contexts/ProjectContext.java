package common.contexts;

import com.neovisionaries.i18n.CountryCode;

import java.util.List;
import java.util.Locale;

import static io.sphere.sdk.utils.IterableUtils.requireNonEmpty;

public class ProjectContext {
    private final List<Locale> languages;
    private final List<CountryCode> countries;

    private ProjectContext(final List<Locale> languages, final List<CountryCode> countries) {
        requireNonEmpty(languages);
        requireNonEmpty(countries);
        this.languages = languages;
        this.countries = countries;
    }

    public List<Locale> languages() {
        return languages;
    }

    public List<CountryCode> countries() {
        return countries;
    }

    public Locale defaultLanguage() {
        return languages.stream().findFirst().get();
    }

    public CountryCode defaultCountry() {
        return countries.stream().findFirst().get();
    }

    public static ProjectContext of(final List<Locale> languages, final List<CountryCode> countries) {
        return new ProjectContext(languages, countries);
    }
}
