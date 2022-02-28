package com.amf.pocsecured.di.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;

import javax.inject.Scope;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The ActivityScoped scoping annotation specifies that the lifespan of a dependency be the same
 * as that of an Activity. This is used to annotate dependencies that behave like a singleton
 * within the lifespan of an Activity.
 */
@Scope
@Documented
@Retention(RUNTIME)
public @interface ActivityScoped {
}
