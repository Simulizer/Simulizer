Gradle Tutorial
===============
Gradle expresses its build files in Groovy ([Groovy tutorial](https://learnxinyminutes.com/docs/groovy/)). This allows you to general-purpose programming tasks in your build file.

Install
-------
- `sudo apt-get install gradle`
- Open `/usr/bin/gradle` and comment out line 70.
- Check it's installed: `gradle -v`

First Project
------------
Make a file called `build.gradle` and write this:
```
task helloWorld << {
  println 'hello, world'
}
```

Then run `gradle -q helloWorld` and it should print `hello, world`.


