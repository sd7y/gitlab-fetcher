package net.aplat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.io.IOException;

public class LocalProjectManager {

    private final Log log = LogFactory.get();

    public void fetchProject(ProjectInformation projectInformation) throws IOException {
        log.info("Processing project [{}]", projectInformation.getSshUrl());
        String parentPath = Constants.LOCAL_PROJECTS_PATH + File.separator + projectInformation.getPath();
        String projectLocalPath = parentPath + File.separator + projectInformation.getName();

        log.info("Local path is {}.", projectLocalPath);
        if (!new File(projectLocalPath).isDirectory()) {
            log.info("{} is not exists.", projectLocalPath);
            String result = ShellProcessor.exec("mkdir -p " + parentPath + "\n" +
                    "cd " + parentPath + "\n" +
                    "git clone " + projectInformation.getSshUrl() + "\n");
            log.info(result);
        } else {
            String result = ShellProcessor.exec("cd " + projectLocalPath + "\n" +
                    "git fetch\n");
            log.info(result);
        }
    }

}
