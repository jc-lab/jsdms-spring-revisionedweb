package kr.jclab.jsdms.spring.revisionedweb;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RevisionedWebBuilder {
    FindRepositoryDirectoryHandler findRepositoryDirectoryHandler = null;
    List<RepositoryBuilder> repositories = new ArrayList<>();

    public class RepositoryContext {
        protected final String repoName;
        protected final String urlAntMatcher;
        protected final File repoDir;

        protected String messageSourceBasename = null;

        public RepositoryContext(String repoName, String urlAntMatcher, File repoDir) {
            this.repoName = repoName;
            this.urlAntMatcher = urlAntMatcher;
            this.repoDir = repoDir;
        }

        public String getRepoName() {
            return repoName;
        }

        public String getUrlAntMatcher() {
            return urlAntMatcher;
        }

        public File getRepoDir() {
            return repoDir;
        }

        public String getMessageSourceBasename() {
            return messageSourceBasename;
        }
    }

    public class RepositoryBuilder extends RepositoryContext {
        private final RevisionedWebBuilder builder;

        public RepositoryBuilder(RevisionedWebBuilder builder, String repoName, String urlAntMatcher, File repoDir) {
            super(repoName, urlAntMatcher, repoDir);
            this.builder = builder;
        }

        public RepositoryBuilder messageSourceBasename(String messageSourceBasename) {
            this.messageSourceBasename = messageSourceBasename;
            return this;
        }

        public RevisionedWebBuilder and() {
            return this.builder;
        }
    }

    public RevisionedWebBuilder setFindRepositoryDirectoryHandler(FindRepositoryDirectoryHandler findRepositoryDirectoryHandler) {
        this.findRepositoryDirectoryHandler = findRepositoryDirectoryHandler;
        return this;
    }

    public RepositoryBuilder addRepository(String repoName, String urlAntMatcher, File repoDir) {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder(this, repoName, urlAntMatcher, repoDir);
        repositories.add(repositoryBuilder);
        return repositoryBuilder;
    }
}
