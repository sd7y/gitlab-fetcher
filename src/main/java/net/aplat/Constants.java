package net.aplat;

public class Constants {

    public static final String GITLAB_DOMAIN = System.getenv("GITLAB_DOMAIN");
    public static final String GITLAB_ACCESS_TOKEN = System.getenv("GITLAB_ACCESS_TOKEN");

    public static final String LOCAL_PROJECTS_PATH = System.getenv("LOCAL_PROJECTS_PATH");

    private Constants() {
    }
}
