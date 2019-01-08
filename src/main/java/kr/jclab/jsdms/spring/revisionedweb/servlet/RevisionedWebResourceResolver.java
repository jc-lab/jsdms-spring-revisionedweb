package kr.jclab.jsdms.spring.revisionedweb.servlet;

import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebResolver;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.List;

@Component
public class RevisionedWebResourceResolver implements ResourceResolver {
    private final RevisionedWebResolver revisionedWebResolver;

    public RevisionedWebResourceResolver(RevisionedWebResolver revisionedWebResolver) {
        this.revisionedWebResolver = revisionedWebResolver;
    }

    @Override
    public Resource resolveResource(HttpServletRequest request, String requestPath, List<? extends Resource> locations, ResourceResolverChain chain) {
        File file = revisionedWebResolver.resolveResourceFile(requestPath);
        if(file != null) {
            if(file.exists() && file.isFile())
                return new FileSystemResource(file);
            else
                return null;
        }
        return chain.resolveResource(request, requestPath, locations);
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {
        return chain.resolveUrlPath(resourcePath, locations);
    }
}
