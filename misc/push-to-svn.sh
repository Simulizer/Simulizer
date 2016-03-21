#!/bin/bash

cd ..
SVNREPO=../A4

# Delete everything (other than hidden files) from the svn repo
cd ${SVNREPO}
rm -rf final
cd -

echo "Copying files to ${SVNREPO}/final ..."
for f in $(git ls-tree -r master --name-only); do
  cp --parents ${f} ${SVNREPO}/final
done
 
echo "Writing git history ..."
git log -p > ${SVNREPO}/final/git-history.txt
echo "Writing git log ..."
git log > ${SVNREPO}/final/git-log.txt

# echo "Generating statistics document ..."
# gitinspector --format=html --timeline --weeks --responsibilities >  ${SVNREPO}/stats.html

cd ${SVNREPO}
svn add --force *
svn commit -m "Daily commit"
