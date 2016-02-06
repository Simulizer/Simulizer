Example 4
=========
Run `gradle three`. If you look at the code, you can see that the task `three` depends on the tasks `one` and `two`, which means that before `three` is executed, `one` and `two` will be executed.

Explanation
-----------
We can say that one task depends on other tasks in many different ways. For example, imagine we have a task `loadTestData` which depends on the tasks `compileTestClasses` and `createSchema`, we can declare this dependency in the following ways:

- 
  ```groovy
  task loadTestData << {
    dependsOn << compileTestClasses
    dependsOn << createSchema
  }
  ```
-
  ```groovy
  task loadTestData << {
    dependsOn compileTestClasses, createSchema
  } 
  ```
-
  ```groovy
  task loadTestData (dependsOn: [compileTestClasses, createSchema]) << {

  } 
  ```
- 
  ```groovy
  task loadTestData
  loadTestData.dependsOn compileTestClasses, createSchema
  ```

