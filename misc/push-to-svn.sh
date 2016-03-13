#!/bin/bash

cd ..
SVNREPO=../A4

# Delete everything (other than hidden files) from the svn repo
cd ${SVNREPO}
ls | xargs rm -rf
cd -

echo "Copying files to ${SVNREPO} ..."
for f in $(git ls-tree -r master --name-only); do
  cp --parents ${f} ${SVNREPO}
done
 
echo "Writing git log ..."
git log > ${SVNREPO}/git-log.txt

echo "Generating statistics document ..."
gitinspector --format=html --timeline --weeks --responsibilities >  ${SVNREPO}/stats.html

cd ${SVNREPO}
svn add --force *
svn commit -m "Daily commit"
