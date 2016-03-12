.PHONY: clean

PANDOCCMD = pandoc -V geometry:margin=2.5cm \
--from=markdown_github+tex_math_dollars+inline_code_attributes

beta-how-to.pdf: beta-how-to.md
	$(PANDOCCMD) $^ -o $@

clean:
	rm -f ./*.pdf
