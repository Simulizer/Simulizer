# A note on external software/languages #
Throughout the development of Simulizer, in order to aid in the production of a highly functional system in a limited time, external software and libraries have been utilised in some specific cases. This section of the report will explicitly declare these, where it was used in Simulizer, the license it is released under, as well as a link to the software to be examined.

<!-- Ace Editor -->
**Ace Editor** - a highly customisable text editor:

- **Where it is used:** The Ace editor is the basis for the editor component used in Simulizer. It provides us with many more options for extensibility than the alternatives we considered. Beyond simply rendering coloured characters of the source code and the line numbers, all of the logic was created by us on top of the framework that Ace offers us. This logic includes rules for syntax highlighting (including the nesting of pre-packaged javascript highlighting rules inside SIMP comments), code folding, problem analysis & feedback and interactivity with line highlighting during the simulation. (Note: the colour schemes and javascript highlighting rules are not our own and are also BSD licensed)
- **License:** The Ace Editor is released under the BSD license.
- **Link:** [https://ace.c9.io](https://ace.c9.io)

<!-- ANTLR -->
**ANTLR** - a parser generator for structured text:

- **Where it is used:** ANTLR is used in the back-end of the system, providing structured input into the assembler. Using a parser-generator along with a grammar for our language (written by us) allows the assembler to traverse the parse tree and perform much better analysis and error checking of the user's programs than would be possible if we wrote our own parser. The parser is also fast enough to continuously parse and feed to our error checking code which provides real-time feedback for problems as the user types.
- **License:** ANTLR is released under the BSD license.
- **Link:** [http://www.antlr.org/index.html](http://www.antlr.org/index.html)

<!-- JavaFX -->
**JavaFX** - a set of graphics and media packages for Java:

- **Where it is used:** JavaFX provides a GUI framework along with several utilities. It is a modern alternative to the old standard GUI libraries: Swing, with more of a focus on web technologies (including themability using css). Because of the better default look and feel along with the potential for interesting extensions (see JFXtras) we decided to use JavaFX over other considered libraries such as Swing or Qt. This choice paid off when we decided to use the Ace editor, as it is a web based component rendered inside a JavaFX web view, which is one of the main selling points of JavaFX.
- **License:** JavaFX is released under the GPL v2 license
- **Link:** [http://docs.oracle.com/javase/8/javase-clienttechnologies.htm](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)

<!-- JFXtras -->
**JFXtras** - a set of high quality controls and add-ons for JavaFX:

- **Where it is used:** Built on top of JavaFX, This library provides (among other things) the capability for drawing 'internal windows' which our application uses extensively to provide maximum UI flexibility.
- **License:** JFXtras is released under the New BSD License
- **Link:** [http://jfxtras.org/](http://jfxtras.org/)

<!-- GSon -->
**GSon** - a Java serialization library that can convert Java Objects into JSON and back.

- **Where it is used:** GSON is used as an easy way to store and retrieve the way that internal windows are laid out.
- **License:** GSon is released under the Apache 2.0 License
- **Link:** [https://github.com/google/gson](https://github.com/google/gson)

In addition to these two pieces of external software, for a small part of the project a different programming language has been used. The language used is JavaScript. JavaScript is being used to provide ‘annotations’ in the text editor. These are written into the comments of the MIPS program, and then are parsed as well. Once parsed, this JavaScript code allows the user to debug their programs by logging register values for example. It additionally allows easy control over the running of the high-level visualisations. Given that it is expected that a large number of second year Computer Science students will have at least basic experience with JavaScript, and the fact that a scripting language is a suitable language for this task, JavaScript seemed to be an appropriate choice. As a rough approximate, JavaScript contributes about 1.5% to the entire code base of Simulizer.
