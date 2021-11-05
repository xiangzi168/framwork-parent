package com.amg.framework.boot.utils.git;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:git.properties" }, ignoreResourceNotFound = true)
public class GitUtils {

    @Value("${git.branch}")
    private String branch;

    @Value("${git.commit.id}")
    private String gitCommitId;

    @Value("${git.remote.origin.url}")
    private String gitUrl;

    @Value("${git.build.time}")
    private String buildDate;

    @Value("${git.commit.id.abbrev}")
    private String commitIdShort;

    public String getGitCommitId() {
        return gitCommitId;
    }

    public String getBranch() {
        return branch;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getBuildDate() {
        return buildDate;
    }

    public String getCommitIdShort() {
        return commitIdShort;
    }
}
