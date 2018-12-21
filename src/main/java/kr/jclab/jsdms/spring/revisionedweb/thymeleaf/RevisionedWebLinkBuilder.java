package kr.jclab.jsdms.spring.revisionedweb.thymeleaf;

import kr.jclab.jsdms.spring.revisionedweb.RevisionedWebResolver;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;

import java.util.Map;

public class RevisionedWebLinkBuilder implements ILinkBuilder {
    private final RevisionedWebResolver revisionedWebResolver;
    private final ILinkBuilder parentLinkBuilder = new StandardLinkBuilder();

    public RevisionedWebLinkBuilder(RevisionedWebResolver revisionedWebResolver) {
        this.revisionedWebResolver = revisionedWebResolver;
    }

    @Override
    public String getName() {
        return parentLinkBuilder.getName();
    }

    @Override
    public Integer getOrder() {
        return parentLinkBuilder.getOrder();
    }

    @Override
    public String buildLink(IExpressionContext context, String base, Map<String, Object> parameters) {
        return parentLinkBuilder.buildLink(context, base, parameters);
    }
}
