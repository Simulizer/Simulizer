Example 2
=========
Run `gradle hello`.

We can declare a task by writing `task hello`, then we can append code to be executed in this task using the `<<` operator in Groovy. So writing

```groovy
task hello // declare the task

// make the first action print 'hello, '
hello << {
  print 'hello, '
}

// make the second action print 'world' and then a newline character
hello << {
  println 'world'
}
```
