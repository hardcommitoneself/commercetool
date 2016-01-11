package inject;

import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.inject.Provider;
import common.i18n.I18nResolver;
import common.templates.HandlebarsTemplateService;
import common.templates.TemplateService;
import play.Configuration;
import play.Logger;

import javax.inject.Inject;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class TemplateServiceProvider implements Provider<TemplateService> {
    private static final String CONFIG_TEMPLATE_LOADERS = "handlebars.templateLoaders";
    private static final String CONFIG_FALLBACK_CONTEXTS = "handlebars.fallbackContexts";
    private static final String CONFIG_CACHE_ENABLED = "handlebars.cache.enabled";
    private static final String CLASSPATH_TYPE = "classpath";
    private static final String FILE_TYPE = "file";
    private static final String TYPE_ATTR = "type";
    private static final String PATH_ATTR = "path";
    private final Configuration configuration;
    private final I18nResolver i18NResolver;

    @Inject
    public TemplateServiceProvider(final Configuration configuration, final I18nResolver i18NResolver) {
        this.configuration = configuration;
        this.i18NResolver = i18NResolver;
    }

    @Override
    public TemplateService get() {
        final List<TemplateLoader> templateLoaders = initializeTemplateLoaders(CONFIG_TEMPLATE_LOADERS);
        final List<TemplateLoader> fallbackContexts = initializeTemplateLoaders(CONFIG_FALLBACK_CONTEXTS);
        final boolean cacheIsEnabled = configuration.getBoolean(CONFIG_CACHE_ENABLED, false);
        Logger.debug("Provide HandlebarsTemplateService: template loaders [{}], cache enabled {}",
                templateLoaders.stream().map(TemplateLoader::getPrefix).collect(joining(", ")),
                cacheIsEnabled);
        return HandlebarsTemplateService.of(templateLoaders, fallbackContexts, i18NResolver, cacheIsEnabled);
    }

    private List<TemplateLoader> initializeTemplateLoaders(final String configKey) {
        final List<TemplateLoader> templateLoaders = configuration.getConfigList(configKey, emptyList())
                .stream()
                .map(this::initializeTemplateLoader)
                .collect(toList());
        if (templateLoaders.isEmpty()) {
            throw new SunriseInitializationException("No Handlebars template loaders found in configuration '" + CONFIG_TEMPLATE_LOADERS + "'");
        }
        return templateLoaders;
    }

    private TemplateLoader initializeTemplateLoader(final Configuration loaderConfig) {
        final String type = loaderConfig.getString(TYPE_ATTR);
        final String path = loaderConfig.getString(PATH_ATTR);
        if (CLASSPATH_TYPE.equals(type)) {
            return new ClassPathTemplateLoader(path);
        } else if (FILE_TYPE.equals(type)) {
            return new FileTemplateLoader(path);
        } else {
            throw new SunriseInitializationException("Not recognized template loader: " + type);
        }
    }
}
