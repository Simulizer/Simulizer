ANTLR
=====

Installation
------------
(It can be found on MavenCentral. In Gradle: `compile 'org.antlr:antlr4:4.5.1-1'`)

```
$ cd /usr/local/lib
$ curl -O http://www.antlr.org/download/antlr-4.5-complete.jar
```

Add `export CLASSPATH=".:/usr/local/lib/antlr-4.5-complete.jar:$CLASSPATH"` to your bashrc. Also add
```
$ alias antlr4='java -Xmx500M -cp $CLASSPATH org.antlr.v4.Tool'
$ alias grun='java org.antlr.v4.runtime.misc.TestRig'
```

Running
-------
Example: `Hello.g4`
```g4
// Define a grammar called Hello
grammar Hello;
r  : 'hello' ID ;         // match keyword hello followed by an identifier
ID : [a-z]+ ;             // match lower-case identifiers
WS : [ \t\r\n ]+ -> skip ; // skip spaces, tabs, newlines
```

Then
```
$ antlr4 Hello.g4
$ javac Hello*.java
$ grun Hello r -gui
hello part
^D
```

Example
-------
`cd` into the `small-mips` folder. Run
```
$ antlr4 SmallMips.g4
$ javac SmallMips*.java
$ grun SmallMips prog -gui smallmips-example.txt
```
This should open a GUI showing the parsing.

