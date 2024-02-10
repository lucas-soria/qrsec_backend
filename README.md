# qrsec_backend

> Thesis backend project for the University of Mendoza's Information Technologies Engineering program.
 
- CRUD:
  - Address
  - Guest
  - Invite
  - User

---

# Getting Started

## Running the project
### Normal run

```shell
./gradlew run
```

### Run skipping test execution[^1]

```shell
./gradlew run --exclude-task test
```

### Clean run[^2]

```shell
./gradlew clean run --exclude-task test
```

## Updating dependencies

```shell
./gradlew dependencyUpdates -Drevision=release -DoutputFormatter=plain
```

## Building .jar file
### Normal build

```shell
./gradlew build
```

### Build with refreshing dependencies[^3]

```shell
./gradlew build --refresh-dependencies
```

### Build skipping test execution[^1]

```shell
./gradlew build --refresh-dependencies --exclude-task test
```

### Clean build[^2]

```shell
./gradlew clean build --refresh-dependencies --exclude-task test
```


[^1]: Or any defined gradle task.
[^2]: This gradle task ensures a clean build environment before starting the build process by cleaning the build directory of the project.
[^3]: Gradle forces all dependencies declared in your project to be re-resolved and potentially re-downloaded. It ensures that you have the latest versions of all dependencies specified in your build.gradle file.
