package main;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Refer to the web page below:
 * http://beust.com/weblog/2008/03/29/test-method-priorities-in-testng/
 */

@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD, ElementType.TYPE})
public @interface Priority {
	int value() default 0;
}