Final Report
============

Workflow
--------
Write your source files in `chapters` and `segments`. Segments is a folder used for smaller snippets, e.g. a table. To include your source in the main document, add a line `\input{filename.tex}` in `main.tex`. Put any package imports in `mystyle.sty`, e.g. `\usepackage{graphicx}`.

To generate a pdf, you can run the `generate-pdf` script, or you can manually run `pdflatex main.tex -o Final-Report.pdf`.

Format
------
You can write your source files in markdown or latex, e.g. creating a file `1-introduction.md` in the `chapters` folder. To include use either of these formats in the main document, you still use `\input{filename.tex}` (with the `tex` extension).

If using markdown, you will need to convert it latex, so you can use the `convert-markdown` script. Alternatively, you can manually run `pandoc -f markdown -t latex filename.md > filename.tex` on each file.
