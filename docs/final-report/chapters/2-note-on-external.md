A note on external software/languages used in Simulizer
=======================================================
Throughout the development of Simulizer, in order to aid in the production of a highly functional system in a limited time, external software and libraries have been utilised in some specific cases. This section of the report will explicitly declare these, where it was used in Simulizer, the license it is released under, as well as a link to the software to be examined.

**Ace Editor** - a highly customisable text editor:

- **Where it is used:** The Ace editor is used for the code editor part of Simulizer, this provides the software with a much more professional (and recognizable) editor for the users to work within. Any syntax highlighting within the editor has been written by the team; the editor provides the ability to do this in an attractive matter.
- **License:** The Ace Editor is released under the BSD license.
- **Link:** [https://ace.c9.io](https://ace.c9.io)

**ANTLR** - a parser generator for structured text:

- **Where it is used:** ANTLR is used in the back-end of the system, more specifically, in the assembler. Using a parser allows us to take the grammar being used (in this case a simplified MIPS language), and create a parser which allows the converting of user programs into a form which can be executed by the CPU simulation. Using a parser also allows the ability to additionally check for errors in user programs - a very useful feature. Using the parser along with the Ace Editor makes syntax highlighting a significantly simpler task.
- **License:** ANTLR is released under the BSD license.
- **Link:** [http://www.antlr.org/index.html](http://www.antlr.org/index.html)

**JavaFX** - a set of graphics and media packages for Java:

- **Where it is used:** JavaFX is the main source of all of the UI code built for Simulizer. It is essentially an alternative to the standard Swing libraries in Java, which can sometimes be less flexible on larger applications. Therefore, the decision was taken to use JavaFX instead. JavaFX is also slowly becoming the Java standard, and so there is a possibility it will take over from Swing completely. As a consequence, using JavaFX has most likely improved the lifetime of Simulizer considerably.
- **License:** JavaFX is released under the GPL v2 license
- **Link:** [http://docs.oracle.com/javase/8/javase-clienttechnologies.htm](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)

**JFxtras** - a set of high quality controls and add-ons for JavaFX:

- **Where it is used:** In addition to JavaFX, these libraries act as extensions to our JavaFX usage, in some cases throughout the system.
- **License:** JFXtras is released under the New BSD License
- **Link:** [http://jfxtras.org/](http://jfxtras.org/)

**GSon** - a Java serialization library that can convert Java Objects into JSON and back.

- **Where it is used:** GSON is used as an easy way to store and retrieve the way that internal windows are laid out.
- **License:** GSon is released under the Apache 2.0 License
- **Link:** [https://github.com/google/gson](https://github.com/google/gson)

In addition to these two pieces of external software, for a small part of the project a different programming language has been used. The language used is JavaScript. JavaScript is being used to provide ‘annotations’ in the text editor. These are written into the comments of the MIPS program, and then are parsed as well. Once parsed, this JavaScript code allows the user to debug their programs by logging register values for example. It additionally allows easy control over the running of the high-level visualisations. Given that it is expected that a large number of second year Computer Science students will have at least basic experience with JavaScript, and the fact that a scripting language is a suitable language for this task, JavaScript seemed to be an appropriate choice. As a rough approximate, JavaScript contributes about 1.5% to the entire code base of Simulizer.
