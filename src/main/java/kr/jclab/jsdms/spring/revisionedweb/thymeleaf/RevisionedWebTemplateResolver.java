package kr.jclab.jsdms.spring.revisionedweb.thymeleaf;

import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebResolver;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

import java.io.File;
import java.util.Map;

public class RevisionedWebTemplateResolver extends FileTemplateResolver {
    private final RevisionedWebResolver revisionedWebResolver;

    public RevisionedWebTemplateResolver(RevisionedWebResolver revisionedWebResolver) {
        this.revisionedWebResolver = revisionedWebResolver;
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        File file = this.revisionedWebResolver.resolveTemplateFile(resourceName);
        if(file.exists() && file.isFile()) {
            return super.computeTemplateResource(configuration, ownerTemplate, template, file.getAbsolutePath(), characterEncoding, templateResolutionAttributes);
        }
        return null;
    }
}
