Example 3
=========
Run `gradle initializeDatabase`.

Notice that it prints:
```
configuring database connection
:initializeDatabase
connect to database
update database schema
```

but we would expect
```
connect to database
update database schema
configuring database connection
```

The reason for this is that the statement on line 5 does not have the `<<` operator, which makes it a configuration block rather than just a simple closure (a closure is basically code between two curly braces). 

(Every time Gradle executes a build, it runs through three lifecycle phases: initialization, configuration, and execution. Execution is the phase in which build tasks are executed in the order required by their dependency relationships. Configuration is the phase in which those task objects are assembled into an internal object model, usually called the DAG (for directed acyclic graph). Initialization is the phase in which Gradle decides which projects are to participate in the build. The latter phase is important in multiproject builds.)

- First the configuration block is run, which is why it prints `configuring database connection` first. 
- Then it starts the execution phase, and the task it runs is `initializeDatabase`, so it prints `:initializeDatabase`.
- Then it runs the code in the execution phase, so it prints `connect to database` and finally `update database schema`.

Appending Configuration Blocks
------------------------------
We can append tasks in configuration blocks in the same way as we did before with `<<`. So we could have achieved the same output by replacing `initializeDatabase { println 'configuring database connection' }` with

```groovy
initializeDatabase { print 'configuring '  }
initializeDatabase { println 'database connection'  }
```

Configuration Blocks
====================
The configuration block is the place to set up variables and data structures that will be needed by the task action when (and if) it runs later on in the build. All build configuration code runs every time you run a Gradle build file, regardless of whether any given task runs during execution.
