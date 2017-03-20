package com.commercetools.sunrise.framework.template.engine;

/**
 * Service that provides HTML pages, using some sort of template engine.
 */
@FunctionalInterface
public interface TemplateEngine {

    /**
     * Injects the page data into the template with the given name.
     * @param templateName name of the template
     * @param templateContext context for the template, including the data to be injected in
     * @return string of the HTML generated with the template and the given page data
     * @throws TemplateNotFoundException when the given template name does not correspond to an existing template
     * @throws TemplateRenderException when the provided page data could not be injected to the template
     */
    String render(final String templateName, final TemplateContext templateContext);
}
