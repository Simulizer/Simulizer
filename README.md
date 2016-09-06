![banner](misc/SimulizerLogo.png)

Simulizer allows you to write assembly code and run it on a simulated and visualised CPU. It has been designed to improve various features of [SPIM](http://spimsimulator.sourceforge.net).

[![forthebadge](http://forthebadge.com/images/badges/built-with-love.svg)](http://forthebadge.com)

[User Guide](work/guide.pdf)

[Final Report](https://github.com/mbway/Simulizer/raw/master/docs/Final-Report.pdf) (contains a more in depth description)

Features
--------
- A code editor with syntax highlighting for the MIPS language, along with real-time error checking and tooltips.
- Simulation and visualisation of an abstract simplified CPU.
- A window showing the values of the registers as programs are running.
- Interaction with the CPU, e.g. controlling the clock speed, pausing, stepping through execution, etc.
- Helpful messages and animations as the simulation is running to help the user understand how the CPU is operating.
- High-level visualisation of annotated programs, see below.

Screenshots
-----------

Example Algorithm Visualisation:

![](misc/screenshot1.png)

Example Pipeline Visualisation:

![](misc/screenshot2.png)

Example CPU Visualisation:

![](misc/screenshot3.png)

Annotations
-----------
Simulizer uses a JavaScript engine along side the CPU emulation. Javascript code is placed between `@{` and `}@` delimiters inside MIPS comments. The JavaScript code can access information about the current state of the CPU (eg registers, memory etc) and can be used to:
- signal 'high level' algorithm visualisations (eg integer lists: swapping elements and highlighting elements, tower of Hanoi: disk swapping, bitmap rendering of memory location etc)
- prototype complex code before transcribing to assembly
- debugging tool (very useful for `printf` debugging or conditional breakpoints)

High-Level Visualiation
-----------------------
The current data structures/graphics that can be visualised are:
- Lists
- Tower of Hanoi

Meet the Team
-------------
[![Charlie Street](https://avatars3.githubusercontent.com/u/11256801?v=3&s=150)](https://github.com/charlie1329) | [![Kelsey McKenna](https://avatars1.githubusercontent.com/u/3618330?v=3&s=150)](https://github.com/ToastNumber) | [![Matthew Broadway](https://avatars3.githubusercontent.com/u/4923501?v=3&s=150)](https://github.com/mbway) | [![Michael Oultram](https://avatars0.githubusercontent.com/u/9907700?v=3&s=150)](https://github.com/MichaelOultram) | [![Theo Styles](https://avatars2.githubusercontent.com/u/2779884?v=3&s=150)](https://github.com/ThusStyles)
---|---|---|---|---|
[Charlie Street](https://github.com/charlie1329) | [Kelsey McKenna](https://github.com/ToastNumber) | [Matthew Broadway](https://github.com/mbway) | [Michael Oultram](https://github.com/MichaelOultram) | [Theo Styles](https://github.com/ThusStyles)

Building
--------
Dependencies to build on 64 bit Debian based Linux (eg Ubuntu)
- `openjdk-8-jdk`
- `spim` (for compatiability tests)
- `openjfx` (JavaFX for openJDK-8)
- `gradle` (or use gradle plugin with an IDE)
- add `/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/ext/jfxrt.jar` to the SDK classpath
    - your placement might be different. try: `find /usr -name 'jfxrt.jar'`
    - in Intellij IDEA: File > Project Structure > SDKs > 1.8 > Classpath
- gradle will handle the rest of the dependencies


Compiler Compatability
----------------------
By installing the `g++-5-mips-linux-gnu` package in Ubuntu, you can compile C
to MIPS assembly. use the command `mips-linux-gnu-g++-5 -O0 -march=r3000 -meb
-mfp32 -mgp32 -S my_file.c`. Discard all the unsupported assembler directives (lines
beginning with `.`). Note: not all of these flags may be necessary, but
informing the compiler as much as possible is probably a good thing
- `-O0` to disable optmizations
- `-march=r3000` to target the R3000 processor (which simulizer emulates)
- `-meb` to use big endian (like Simulizer)
- `-mfp32` to use 32 bit floating point registers
- `-mgp32` to use 32-bit general registers
- `-S` to output an assembly (.s) file


Licence
-------
**Simulizer** is released under the [GNU General Public License v3.0](LICENCE)
