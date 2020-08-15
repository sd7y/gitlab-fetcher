package net.aplat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final Log log = LogFactory.get();

    public static void main( String[] args ) {
        GitLabFetcher gitLabFetcher = new GitLabFetcher();
        List<ProjectInformation> projects = gitLabFetcher.fetchAllProjects();

        log.info("Project size: " + projects.size());
        LocalProjectManager localProjectManager = new LocalProjectManager();

        for (int i = 0; i < projects.size(); i++) {
            log.info("---------------------- [" + (i + 1) + "/" + projects.size() + "] ----------------------");
            try {
                localProjectManager.fetchProject(projects.get(i));
            } catch (IOException e) {
                log.error("Process project {} failed.", projects.get(i).getSshUrl(), e);
            }
        }
    }

}
