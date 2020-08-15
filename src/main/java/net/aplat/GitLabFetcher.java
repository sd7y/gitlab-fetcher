package net.aplat;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

import java.util.ArrayList;
import java.util.List;

public class GitLabFetcher {

    Log log = LogFactory.get();

    private static final String CACHE_FILE_NAME = ".project_info_cache";

    private ProjectInformation assembleProjectInformation(JSONObject jsonObject) {
        String pathWithNamespace = jsonObject.getStr("path_with_namespace");
        String id = jsonObject.getStr("id");
        String sshUrl = jsonObject.getStr("ssh_url_to_repo");
        int index = pathWithNamespace.lastIndexOf('/');
        String name = pathWithNamespace.substring(index + 1);
        String path = pathWithNamespace.substring(0, index);
        return new ProjectInformation(id, sshUrl, path, name, jsonObject.toJSONString(2));
    }

    private List<ProjectInformation> assembleProjectInformation(JSONArray jsonArray) {
        List<ProjectInformation> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(assembleProjectInformation(jsonArray.getJSONObject(i)));
        }
        return list;
    }

    private List<ProjectInformation> fetchProjects(int page, int prePage) {
        log.info("Fetch projects, page=" + page + ", pre_page=" + prePage);
        String url = Constants.GITLAB_DOMAIN + "/api/v4/projects" +
                "?private_token=" + Constants.GITLAB_ACCESS_TOKEN +
                "&page=" + page +
                "&pre_page=" + prePage +
                "&order_by=id" +
                "&sort=asc";
        String result = HttpUtil.get(url);
        return assembleProjectInformation(JSONUtil.parseArray(result));
    }

    private List<ProjectInformation> fetchAllProjectsFromGitLab() {
        List<ProjectInformation> projects = new ArrayList<>();
        for (int page = 1; ; page++) {
            List<ProjectInformation> onePage = fetchProjects(page, 20);
            if (onePage.isEmpty()) {
                break;
            }
            projects.addAll(onePage);
        }
        JSONArray jsonArray = new JSONArray();
        projects.forEach(projectInformation -> {
            jsonArray.add(JSONUtil.parseObj(projectInformation.getRaw()));
        });

        new FileWriter(CACHE_FILE_NAME).write(jsonArray.toJSONString(2));
        return projects;
    }

    private List<ProjectInformation> fetchAllProjectsFromCache() {
        String result = new FileReader(CACHE_FILE_NAME).readString();
        return assembleProjectInformation(JSONUtil.parseArray(result));
    }

    public List<ProjectInformation> fetchAllProjects() {
        if (FileUtil.file(CACHE_FILE_NAME).exists()) {
            log.info("Read gitlab from cache file.");
            return fetchAllProjectsFromCache();
        } else {
            return fetchAllProjectsFromGitLab();
        }
    }

}
