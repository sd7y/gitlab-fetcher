# GitLab Fetcher

This project is used to fetch all projects in the GitLab

## Run

1. Create a file named`.env`, its content is

```bash
export GITLAB_DOMAIN=<your-gitlab-domain>
export GITLAB_ACCESS_TOKEN=<access-token-created-in-gitlab>
export LOCAL_PROJECTS_PATH=<the-local-path-where-you-store-the-projects>
```

2. Execute `./run.sh`


## Search commit in all git project

`./find.sh <key-word>`