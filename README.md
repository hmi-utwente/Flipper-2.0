# Flipper-2.0
Version 2.0 of the Dialogue Control system Flipper by Jan Flokstra.

# Requirements
Java 8 or higher

# Get Started
There are multiple ways you can add Flipper to your project. Flipper 2.0.0.2 first official release is now available via Jitpack.io.

Once you are done with installing, you can look at the [Wiki](https://github.com/hmi-utwente/Flipper-2.0/wiki) for more help.

## Gradle installation
For use in Gradle, add the jitpack repository and/or the dependency
```kotlin 
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
```kotlin
dependencies {
    implementation 'com.github.hmi-utwente:Flipper-2.0:0.2'
}
```
## Maven installation
For use in Maven, add the jitpack repository and/or the dependency
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.hmi-utwente</groupId>
    <artifactId>Flipper-2.0</artifactId>
    <version>0.2</version>
</dependency>
```

## hmibuild installation
You can also use HMI's own build system of Ant + Ivy, which can be seen [here](https://github.com/hmi-utwente/Flipper-2.0/tree/master/hmibuild)

## As local library
We have released a compiled .jar as well [here](https://github.com/hmi-utwente/Flipper-2.0/releases/tag/0.2).

# Publication
Jelte van Waterschoot, Merijn Bruijnes, Jan Flokstra, Dennis Reidsma, Daniel Davison, Mariët Theune, and Dirk Heylen. 2018. Flipper 2.0: A Pragmatic Dialogue Engine for Embodied Conversational Agents. In Proceedings of the 18th International Conference on Intelligent Virtual Agents (IVA '18). ACM, New York, NY, USA, 43-50. DOI: https://doi.org/10.1145/3267851.3267882 
