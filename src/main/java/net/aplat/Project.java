package net.aplat;

public class Project {
    private String id;
    private String sshUrl;
    private String path;
    private String name;
    private String raw;

    public Project(String id, String sshUrl, String path, String name, String raw) {
        this.id = id;
        this.sshUrl = sshUrl;
        this.path = path;
        this.name = name;
        this.raw = raw;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSshUrl() {
        return sshUrl;
    }

    public void setSshUrl(String sshUrl) {
        this.sshUrl = sshUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }
}
