package com.commercetools.sunrise.ctp.categories;

import javax.inject.Named;
import javax.inject.Qualifier;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * {@linkplain Qualifier Qualifier} for the category tree used for navigation.
 */
@Named("navigationCategoryTree")
@Qualifier
@Documented
@Retention(RUNTIME)
public @interface NavigationCategoryTree {
}