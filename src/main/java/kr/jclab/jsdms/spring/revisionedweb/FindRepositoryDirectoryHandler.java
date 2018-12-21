package kr.jclab.jsdms.spring.revisionedweb;

import java.io.File;

public interface FindRepositoryDirectoryHandler {
    File findByName(String repoName);
}
