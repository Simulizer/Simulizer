#!/usr/bin/env bash
set -e
VERSION=$(cat VERSION)

if [[ "${CIRCLE_BRANCH}" != "master" ]]; then
    echo "Skipping release as ${CIRCLE_BRANCH} is not master"
    exit 0
fi

if [[ -z `git diff --name-only HEAD~ | grep ^VERSION$`]]; then 
    echo "Skipping release as VERSION file has not changed"
    exit 0
fi

echo "Releasing ${VERSION} on Github"
ghr -t ${GITHUB_TOKEN} -u ${CIRCLE_PROJECT_USERNAME} -r ${CIRCLE_PROJECT_REPONAME} -c ${CIRCLE_SHA1} -delete ${VERSION} ./build/distributions

