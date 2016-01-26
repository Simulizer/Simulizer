Gradle Tutorial
===============
Gradle expresses its build files in Groovy ([Groovy
tutorial](https://learnxinyminutes.com/docs/groovy/)). This allows you to
general-purpose programming tasks in your build file.

Read `prototypes/javafx-prototype/build.gradle` for a useful and working example
(it is well documented)

Install
-------
```
sudo add-apt-repository ppa:cwchien/gradle
sudo apt-get update
sudo apt-get install gradle
```
- If it complains about your Java version, open `/usr/bin/gradle` and comment
  out line 70.
- Check it's installed: `gradle -v`

Start a Project
---------------
Call `gradle init` to initialise a skeleton structure with a few files to get
started. You could also manually create the `build.gradle` file yourself.

The command also created 'wrappers' for different operating systems. They are
for the future when new versions of gradle and other dependencies are released,
the wrapper should still function exactly the same as it downloads the exact
version it was created with (or at least that is my understanding (Matt))


First Project
------------
Make a file called `build.gradle` and write this:
```
task helloWorld << {
  println 'hello, world'
}
```

Then run `gradle -q helloWorld` and it should print `hello, world`.

Eclipse Plugin
--------------
`Eclipse Marketplace -> Buildship`
Then you can open a new Gradle project.

Dependencies
------------
At the start of the `build.gradle` file, the line `apply plugin: 'java'` means
that your project will be built as a Java project. There is a `repositories`
configuration block and a `dependencies` configuration block. In the
`repositories` block, you specify where the dependencies will be downloaded
from. For example, you could write:

```groovy
repositories {
  jcenter()
}
```

which would indicate that you need dependencies from
[JCenter](https://bintray.com/bintray/jcenter). To denote a compile-time
dependency, write `compile "groupId:artifactId:version"`. For example, on the
JCenter site, you can find the Postgresql driver
[here](https://bintray.com/bintray/jcenter/postgresql%3Apostgresql/view). It
tells you that the groupId is `postgresql`, the artifactId is `postgresql`, and
the version is `9.2-1002.jdbc4`, so in our `build.gradle` file we would write:

```groovy
dependencies {
  compile "postgresql:postgresql:9.2-1002.jdbc4"
}
```

In order to download this file, right-click on the Eclipse project -> Gradle ->
Refresh Gradle Project. Other repositories exist, such as `mavenCentral()`,
which you can find [here](http://search.maven.org/).

Structure of Project
--------------------
(If you use the Eclipse Buildship plugin, it does this automatically)

The main code goes in `src/main/java`, and resources go in `src/main/resources`.
Test files go in `src/test/java`, and similarly resources for testing go in
`src/test/resources`.


