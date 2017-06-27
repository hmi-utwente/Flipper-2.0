# Build Flipper-2.0 with hmibuild

Clone hmibuild from [the ASAP GitHub](http://github.com/articulatedSocialAgentsPlatform/hmibuild), (requires apache ant + python 2.7.x).
It needs to be placed next to the Flipper-2.0 folder.

Go to the Flipper-2.0/hmibuild and run:

```
ant resolve
ant compile
```

Use `ant main` to select a main class and `ant run` to run it.
To generate an eclipse project, run `ant eclipsesourceproject`.
