package net.aplat;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
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
        String parentPath = Constants.LOCAL_PROJECTS_PATH;
        String localDirName = project.getId() + "-" + project.getName();
        String localPath = Constants.LOCAL_PROJECTS_PATH + File.separator + localDirName;

        logContent.append("Local path is ").append(localPath).append(".").append("\n");
        ShellUtil.Result result;
        if (!new File(localPath).isDirectory()) {
            logContent.append(localPath).append(" is not exists.").append("\n");
            result = ShellUtil.exec("mkdir -p " + parentPath + "\n" +
                    "cd " + parentPath + "\n" +
                    "git clone " + project.getSshUrl() + " " + localDirName + "\n");
        } else {
            result = ShellUtil.exec("cd " + localPath + "\n" +
                    "git fetch --prune --prune-tags\n" +
                    // "default_branch=\"$(git remote show origin | grep 'HEAD branch' | awk '{print $3}')\"\n" +
                    "default_branch=\"$(git branch -r --points-at refs/remotes/origin/HEAD | grep '\\->' | cut -d' ' -f5 | cut -d/ -f2)\"\n" +
                    // "if [[ \"$default_branch\" == \"(unknown)\" ]]; then\n" +
                    "if [[ -z \"$default_branch\" ]]; then\n" +
                    "  exit 0\n" +
                    "fi\n" +
                    "git checkout $default_branch\n" +
                    "git reset --hard origin/$default_branch\n");
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

    private long getLastUpdatedDate() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Constants.LOCAL_PROJECTS_PATH + File.separator + "lastUpdated"))) {
            return DateUtil.parseDate(bufferedReader.readLine()).getTime();
        } catch (FileNotFoundException e) {
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    private void updateLastUpdatedDate(Date date) {
        new cn.hutool.core.io.file.FileWriter(Constants.LOCAL_PROJECTS_PATH + File.separator + "lastUpdated").write(DateUtil.format(date));
    }

    public void fetchProjects(List<Project> projects) {
        processed = 0;
        success = 0;
        processing = 0;
        failedProjectList = new ArrayList<>();
        totalCount = projects.size();
        long lastUpdatedDate = getLastUpdatedDate();
        for (final Project project : projects) {
            if (project.getLastActivityAt().getTime() < lastUpdatedDate) {
                log.info("Skip project {}", project.getName());
                totalCount--;
                continue;
            }
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
        updateLastUpdatedDate(new Date());
    }

}
