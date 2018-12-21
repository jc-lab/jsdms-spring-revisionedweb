package kr.jclab.jsdms.spring.revisionedweb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JsDMSSpringRevisionedWebConfiguration implements BeanPostProcessor {

    private RevisionedWebResolver revisionedWebResolver = new RevisionedWebResolver();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JsDMSSpringRevisionedWebConfigurerAdapter) {
            RevisionedWebBuilder builder = new RevisionedWebBuilder();
            ((JsDMSSpringRevisionedWebConfigurerAdapter) bean).configure(builder);
            revisionedWebResolver.build(builder);
        }
        return bean;
    }

    @ConditionalOnMissingBean
    @Bean
    public RevisionedWebResolver revisionedWebResolver() {
        return this.revisionedWebResolver;
    }

}
