REPORT_HOME=../

cd "${REPORT_HOME}/tables"
for f in $( find . -name *.md ); do
  pandoc -f markdown -t latex ${f} > "$(basename ${f} .md).tex"
done
cd -

