package kr.jclab.jsdms.spring.revisionedweb.annotations;

import kr.jclab.jsdms.spring.revisionedweb.JsDMSSpringRevisionedWebConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = { java.lang.annotation.ElementType.TYPE })
@Documented
@Import({ JsDMSSpringRevisionedWebConfiguration.class})
@Configuration
public @interface EnableJsDMSSpringRevisionedWeb {
}
