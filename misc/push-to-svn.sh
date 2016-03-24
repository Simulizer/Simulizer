#!/bin/bash

cd ..
SVNREPO=../A4
GUIDE=${SVNREPO}/final/docs/user-guide/marker-guide.md

# Delete everything (other than hidden files) from the svn repo
cd ${SVNREPO}
ls | xargs rm -rf # delete all visible files
rm -rf .gitignore # delete hidden files
mkdir final       # recreate the final folder
cd -

echo "Copying files to ${SVNREPO}/final ..."
for f in $(git ls-tree -r master --name-only); do
  cp --parents ${f} ${SVNREPO}/final
done

echo "Copying ace to ${SVNREPO}/final/build ..."
mkdir ${SVNREPO}/final/build
cp -r build/ace ${SVNREPO}/final/build

echo "Copying custom build.gradle ..."
cp misc/svn-build.gradle ${SVNREPO}/final/build.gradle
 
echo "Writing git history ..."
git log -p > ${SVNREPO}/final/git-history.txt
echo "Writing git log ..."
git log > ${SVNREPO}/final/git-log.txt

# echo "Generating statistics document ..."
# gitinspector --format=html --timeline --weeks --responsibilities >  ${SVNREPO}/stats.html

echo "Copying marker guide to top level README.md ..."
cp ${GUIDE} ${SVNREPO}/final/README.md

echo "Copying distribution zip ..."
cp build/distributions/Simulizer-0.3.zip ${SVNREPO}/final

cd ${SVNREPO}
svn add --force *
svn commit -m "Daily commit"
