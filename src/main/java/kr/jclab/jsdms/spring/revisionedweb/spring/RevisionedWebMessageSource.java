package kr.jclab.jsdms.spring.revisionedweb.spring;

import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebResolver;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

public class RevisionedWebMessageSource implements MessageSource {
    private final RevisionedWebResolver revisionedWebResolver;

    public RevisionedWebMessageSource(RevisionedWebResolver revisionedWebResolver) {
        this.revisionedWebResolver = revisionedWebResolver;
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        MessageSource messageSource = this.revisionedWebResolver.resolveMessageSource();
        if(messageSource != null)
            return messageSource.getMessage(code, args, defaultMessage, locale);
        return null;
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        MessageSource messageSource = this.revisionedWebResolver.resolveMessageSource();
        if(messageSource != null)
            return messageSource.getMessage(code, args, locale);
        return code;
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        MessageSource messageSource = this.revisionedWebResolver.resolveMessageSource();
        if(messageSource != null)
            return messageSource.getMessage(resolvable, locale);
        return resolvable.getDefaultMessage();
    }
}
