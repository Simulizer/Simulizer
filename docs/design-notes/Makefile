.PHONY: all, clean

PANDOCCMD = pandoc --table-of-contents \
--from=markdown_github+tex_math_dollars+inline_code_attributes

#all: annotations.pdf

#annotations.pdf: annotations.md
#	$(PANDOCCMD) $^ -o $@

clean:
	rm -f ./*.pdf
