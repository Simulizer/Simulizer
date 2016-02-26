SVNREPO=../A4

echo "Copying files to ${SVNREPO} ..."
for f in $(git ls-tree -r master --name-only); do
  cp --parents ${f} ${SVNREPO}
done
 
echo "Writing git log ..."
git log > ${SVNREPO}/git-log.txt

echo "Generating statistics document ..."
gitinspector --format=html --timeline --weeks --responsibilities >  ${SVNREPO}/stats.html
