package net.aplat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LocalProjectManager {

    private final Log log = LogFactory.get();

    private final ExecutorService executorService = Executors.newFixedThreadPool(50);

    private List<Project> failedProjectList = new ArrayList<>();

    private int processed = 0;
    private int success = 0;
    private int processing = 0;
    private int totalCount = 0;

    private void fetchProject(Project project) throws IOException, InterruptedException {
        printStart();
        StringBuilder logContent = new StringBuilder();
        logContent.append("Processing project [").append(project.getSshUrl()).append("]").append("\n");
        String parentPath = Constants.LOCAL_PROJECTS_PATH + File.separator + project.getId();
        String projectLocalPath = parentPath + File.separator + project.getName();

        logContent.append("Local path is ").append(projectLocalPath).append(".").append("\n");
        ShellUtil.Result result;
        if (!new File(projectLocalPath).isDirectory()) {
            logContent.append(projectLocalPath).append(" is not exists.").append("\n");
            result = ShellUtil.exec("mkdir -p " + parentPath + "\n" +
                    "cd " + parentPath + "\n" +
                    "git clone " + project.getSshUrl() + "\n");
        } else {
            result = ShellUtil.exec("cd " + projectLocalPath + "\n" +
                    "git fetch --prune --prune-tags\n");
        }
        if (result.getExitValue() != 0) {
            addToFailedList(project);
        }
        logContent.append(result);
        printEnd(logContent.toString(), result.getExitValue());
    }

    private synchronized void printStart() throws InterruptedException {
        log.info("---------------------- [{}({})/{}/{}] ----------------------", processed, success, processing, totalCount);
        processing++;
        TimeUnit.MILLISECONDS.sleep(300);
    }

    private synchronized void printEnd(String logContent, int exitValue) {
        processed++;
        logContent = "\n========================================================\n" + logContent + "\n========================================================\n";
        if (exitValue == 0) {
            success++;
            log.info(logContent);
        } else {
            log.error("\nExit value is " + exitValue + "." + logContent);
        }
        log.info("---------------------- [{}({})/{}/{}] ----------------------", processed, success, processing, totalCount);
        if (processed == totalCount) {
            end();
        }
    }

    private void end() {
        if (!failedProjectList.isEmpty()) {
            failedProjectList.forEach(project -> log.error("Processing project {} failed, id: {}, url: {}", project.getName(), project.getId(), project.getSshUrl()));
            log.error("Failed count {}", failedProjectList.size());
            log.error("Retrying...");
            List<Project> retriedProjectList = new ArrayList<>(failedProjectList);
            fetchProjects(retriedProjectList);
        } else {
            log.info("All projects have been processed");
            executorService.shutdown();
        }
    }

    private synchronized void addToFailedList(Project project) {
        log.error("Processing project {} failed, id: {}, url: {}", project.getName(), project.getId(), project.getSshUrl());
        failedProjectList.add(project);
    }

    public void fetchProjects(List<Project> projects) {
        processed = 0;
        success = 0;
        processing = 0;
        failedProjectList = new ArrayList<>();
        totalCount = projects.size();
        for (final Project project : projects) {
            executorService.execute(() -> {
                try {
                    fetchProject(project);
                } catch (IOException e) {
                    log.error("Process project {} failed.", project.getSshUrl(), e);
                } catch (InterruptedException e) {
                    log.error(e);
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

}
