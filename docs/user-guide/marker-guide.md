# How to Run Simulizer #
There are a couple of useful scripts in the root of this repository: `gradlew` and `gradlew.bat`. To run the project, simply execute  

```
$ ./gradlew run
```

or, for Windows,

```
gradlew run
```

## Required folders ##
In order for the system to work correctly, there should be a `settings.json` file, and folders `layouts` and `themes`. If running from Gradle, these files should be in a folder called `work`; otherwise they should be in the same directory as the jar.

- From Gradle (`gradlew run`):
  ```
  .
  |-- gradlew
  |-- work/
      |-- layouts/
      |-- themes/
      |-- settings.json
  ...
  ```
- From the jar (`java -jar Simulizer-x.yz.jar`):
  ```
  .
  |-- Simulizer-x.yz.jar
  |-- layouts/
  |-- themes/
  |-- settings.json
  ...
  ```

### Running from the jar ###

If the Gradle script doesn't work, Simulizer is pre-packaged in the `simulizer.zip` folder. Simply extract this folder and then run the jar file, e.g.

```
java -jar Simulizer-x.yz.jar
```
