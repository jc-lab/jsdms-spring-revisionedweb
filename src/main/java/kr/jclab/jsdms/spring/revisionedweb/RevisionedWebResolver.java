package kr.jclab.jsdms.spring.revisionedweb;

import kr.jclab.jsdms.spring.revisionedweb.spring.RevisionedWebMessageSource;
import kr.jclab.jsdms.spring.revisionedweb.servlet.RevisionedWebResourceResolver;
import kr.jclab.jsdms.spring.revisionedweb.thymeleaf.RevisionedWebLinkBuilder;
import kr.jclab.jsdms.spring.revisionedweb.thymeleaf.RevisionedWebTemplateResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.util.UriUtils;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RevisionedWebResolver {
    private Map<String, RevisionedWebBuilder.RepositoryContext> repositories = new HashMap<>();
    private FindRepositoryDirectoryHandler findRepositoryDirectoryHandler = null;

    private ConcurrentHashMap<String, MessageSource> revisionedMessageSources = new ConcurrentHashMap<>();

    void build(RevisionedWebBuilder builder) {
        this.findRepositoryDirectoryHandler = builder.findRepositoryDirectoryHandler;
        for(RevisionedWebBuilder.RepositoryContext repositoryContext : builder.repositories) {
            this.repositories.put(repositoryContext.repoName, repositoryContext);
        }
    }

    @PostConstruct
    protected void init() {
        // After create bean
        System.out.println(repositories);
    }

    @ConditionalOnMissingClass(value = { "org.thymeleaf.ITemplateResolver" })
    @Bean
    public AbstractConfigurableTemplateResolver templateResolver() {
        return new RevisionedWebTemplateResolver(this);
    }

    @ConditionalOnMissingClass(value = { "org.thymeleaf.linkbuilder.RevisionedWebLinkBuilder" })
    @Bean
    public RevisionedWebLinkBuilder linkBuilder() {
        return new RevisionedWebLinkBuilder(this);
    }

    @ConditionalOnMissingClass(value = { "org.springframework.web.servlet.resource.ResourceResolver" })
    @Bean
    public ResourceResolver resourceResolver() {
        return new RevisionedWebResourceResolver(this);
    }

    @ConditionalOnMissingClass(value = { "org.springframework.context.MessageSource" })
    @Bean
    public MessageSource messageSource() {
        return new RevisionedWebMessageSource(this);
    }

    public File resolveResourceFile(String requestPath) {
        RequestContext requestContext = RequestContext.getInstance(this);
        if(requestContext.repositoryContext != null) {
            File resourceDir = new File(requestContext.revisionDir, "static/");
            File resourceFile = new File(resourceDir, UriUtils.decode(requestContext.extractPath, "UTF-8"));
            return resourceFile;
        }
        return null;
    }

    public File resolveTemplateFile(String templateFileName) {
        RequestContext requestContext = RequestContext.getInstance(this);
        if(requestContext.repositoryContext != null) {
            File resourceFile = new File(requestContext.revisionDir, templateFileName);
            return resourceFile;
        }
        return null;
    }

    public MessageSource resolveMessageSource() {
        RequestContext requestContext = RequestContext.getInstance(this);
        if(requestContext.repositoryContext != null) {
            String key = requestContext.repositoryContext.repoName + ":" + requestContext.revision;
            MessageSource messageSource = this.revisionedMessageSources.get(key);
            if(messageSource == null) {
                synchronized (this.revisionedMessageSources) {
                    messageSource = new ReloadableResourceBundleMessageSource();
                    File basename = new File(requestContext.revisionDir.getAbsolutePath(), requestContext.repositoryContext.messageSourceBasename);
                    ((ReloadableResourceBundleMessageSource) messageSource).setBasename("file:" + basename.getAbsolutePath());
                    this.revisionedMessageSources.put(requestContext.revision, messageSource);
                }
            }
            return messageSource;
        }
        return null;
    }

    public static HttpServletRequest getCurrentRequestContext(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
            return request;
        }
        return null;
    }

    private static class RequestContext {
        public HttpServletRequest httpServletRequest;
        public RevisionedWebBuilder.RepositoryContext repositoryContext = null;
        public String revision = null;
        public String extractPath = null;
        public File repoDir;
        public File revisionDir;

        public static RequestContext getInstance(RevisionedWebResolver revisionedWebResolver) {
            return getInstance(revisionedWebResolver, null);
        }

        public static RequestContext getInstance(RevisionedWebResolver revisionedWebResolver, String requestUri) {
            HttpServletRequest httpServletRequest = getCurrentRequestContext();
            RequestContext instance = (RequestContext)httpServletRequest.getAttribute("__jsdmsrevweb_reqctx");
            if(instance == null) {
                instance = new RequestContext(httpServletRequest, revisionedWebResolver, requestUri);
                httpServletRequest.setAttribute("__jsdmsrevweb_reqctx", instance);
            }
            return instance;
        }

        public RequestContext(HttpServletRequest httpServletRequest, RevisionedWebResolver revisionedWebResolver, String requestUri) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            this.httpServletRequest = httpServletRequest;
            if(requestUri == null) {
                requestUri = httpServletRequest.getRequestURI();
            }
            for(Map.Entry<String, RevisionedWebBuilder.RepositoryContext> entry : revisionedWebResolver.repositories.entrySet()) {
                RevisionedWebBuilder.RepositoryContext repositoryContext = entry.getValue();
                Map<String, String> matchers = antPathMatcher.extractUriTemplateVariables(repositoryContext.urlAntMatcher, requestUri);
                this.revision = (matchers == null) ? null : matchers.get("revision");
                if(this.revision != null) {
                    this.repositoryContext = repositoryContext;
                    this.extractPath = antPathMatcher.extractPathWithinPattern(repositoryContext.urlAntMatcher, requestUri);
                    this.repoDir = (revisionedWebResolver.findRepositoryDirectoryHandler != null) ? revisionedWebResolver.findRepositoryDirectoryHandler.findByName(repositoryContext.repoName) : repositoryContext.repoDir;
                    this.revisionDir = new File(repoDir, this.revision);
                    return;
                }
            }
        }
    }
}
