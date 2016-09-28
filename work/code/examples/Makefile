.PHONY: re all clean

COMPILE=../../gcc-mips.sh

re: clean all

all: \
	simple-arithmetic/add-two-vars.s \
	simple-arithmetic/factorial.s \
	simple-arithmetic/fibonacci.s \
	functions/function-optimised.s \
	functions/function.s \
	memory/memory-dump.s \
	flow-control/flow-control.s


clean:
	rm -f simple-arithmetic/*.s
	rm -f functions/*.s
	rm -f memory/*.s
	rm -f flow-control/*.s


simple-arithmetic/add-two-vars.s: simple-arithmetic/add-two-vars.c
	$(COMPILE) $< s

simple-arithmetic/factorial.s: simple-arithmetic/factorial.c
	$(COMPILE) $< s

simple-arithmetic/fibonacci.s: simple-arithmetic/fibonacci.c
	$(COMPILE) $< s


functions/function-optimised.s: functions/function.c
	$(COMPILE) $< s
	mv functions/function.s functions/function-optimised.s

functions/function.s: functions/function.c
	$(COMPILE) $< 0


memory/memory-dump.s: memory/memory-dump.c
	$(COMPILE) $< s

flow-control/flow-control.s: flow-control/flow-control.c
	$(COMPILE) $< 0
