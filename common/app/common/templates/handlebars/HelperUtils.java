package common.templates.handlebars;

import com.github.jknack.handlebars.Context;
import common.cms.CmsPage;
import io.sphere.sdk.models.Base;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static common.templates.handlebars.HandlebarsTemplateService.CMS_PAGE_IN_CONTEXT_KEY;
import static common.templates.handlebars.HandlebarsTemplateService.LANGUAGE_TAGS_IN_CONTEXT_KEY;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

final class HelperUtils extends Base {

    private HelperUtils() {
    }

    @SuppressWarnings("unchecked")
    public static List<Locale> getLocalesFromContext(final Context context) {
        final Object languageTagsAsObject = context.get(LANGUAGE_TAGS_IN_CONTEXT_KEY);
        if (languageTagsAsObject instanceof List) {
            final List<String> locales = (List<String>) languageTagsAsObject;
            return locales.stream()
                    .map(Locale::forLanguageTag)
                    .collect(toList());
        } else {
            return emptyList();
        }
    }

    public static Optional<CmsPage> getCmsPageFromContext(final Context context) {
        final Object cmsPageAsObject = context.get(CMS_PAGE_IN_CONTEXT_KEY);
        if (cmsPageAsObject instanceof CmsPage) {
            return Optional.of((CmsPage) cmsPageAsObject);
        } else {
            return Optional.empty();
        }
    }

    @Nullable
    public static String getPart(final String[] parts, final int position, final String defaultValue) {
        final boolean containsPosition = parts.length > position;
        return containsPosition ? parts[position] : defaultValue;
    }
}
