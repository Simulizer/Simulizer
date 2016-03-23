# Compliance with Spim #

It was important to us to maintain compatibility with SPIM and other MIPS simulators because of our ambition to have the tool used by students. To achieve this we read the official MIPS32 ISA (Instruction Set Architecture) specification document and the SPIM documentation.

We then created a suite of automatic tests using a wrapper written in Java to launch an instance of SPIM and feed it program source code and also user input held in strings in Java. The output of this process is then captured and parsed.

The same programs were then fed to Simulizer or compared with expected results to document the assumptions we make about the behaviour of SPIM.

We used these tests to determine what behaviour to implement if integer literals are attempted to be stored into a variable or register that is too small to represent it, and also to document the exact behaviour of placing symbols such as semicolons or commas (which we found to be completely optional in MIPS).

One area we studied in depth was the handling of string literals in SPIM. We studied a copy of the c / YACC source code to determine the exact supported escape sequences (as the documentation was incorrect) and created a suite of automatic tests which gave feedback on the types of error messages we would expect.

During this process we discovered several bugs in SPIM's parsing of string literals, which also had poor support and many incorrect behaviours. We instead decided to prefer correct behaviour over compatible behaviour and implemented our own string literal parsing correctly with respect to the behaviour in Java.

These tests can be found in `src/test` however SPIM must be installed and in the user's `PATH` for the scripts to work. Using gradle these tests can be disabled as we annotated these tests with the category `SpimTest`, which can be excluded very easily.

