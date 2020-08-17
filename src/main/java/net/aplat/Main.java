package net.aplat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.util.List;

public class Main {

    private static final Log log = LogFactory.get();

    public static void main( String[] args ) {
        ProjectListFetcher projectListFetcher = new ProjectListFetcher();
        List<Project> projects = projectListFetcher.fetchAllProjects();

        log.info("Project size: " + projects.size());
        LocalProjectManager localProjectManager = new LocalProjectManager();
        localProjectManager.fetchProjects(projects);
    }

}
