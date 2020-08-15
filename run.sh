#!/bin/bash

# The .env should contain GITLAB_DOMAIN, GITLAB_ACCESS_TOKEN, LOCAL_PROJECTS_PATH
# For example:
# export GITLAB_DOMAIN=https://gitlab.xxx.com
# export GITLAB_ACCESS_TOKEN=xxxxx
# export LOCAL_PROJECTS_PATH=/home/gitlab
source <(cat .env)

echo "GITLAB_DOMAIN=$GITLAB_DOMAIN"
echo "GITLAB_ACCESS_TOKEN=$GITLAB_ACCESS_TOKEN"
echo "LOCAL_PROJECTS_PATH=$LOCAL_PROJECTS_PATH"

mvn clean compile package

java -jar ./target/gitlab-fetcher-1.0-jar-with-dependencies.jar