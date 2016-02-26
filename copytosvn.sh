SVNREPO=../A4

for f in $(git ls-tree -r master --name-only); do
  cp --parents ${f} ${SVNREPO}
done

git log > ${SVNREPO}/git-log.txt
gitinspector --format=html --timeline --weeks --responsibilities >  ${SVNREPO}/stats.html