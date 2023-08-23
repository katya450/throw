### Throw?

Imagine a situation that you're starting a roleplaying session, but forgot your dice home! This tiny applicaton is here to fix it, and let you throw virtual dice. Eventually. As a hobby project this is, this mainly progresses only when I haven't had enough Clojure to write or think on the daily job.

### Development mode
```
npm install
npx shadow-cljs watch app
```

Or to start this on command line and hook it to a nREPL-port in Calva:

```
npx shadow-cljs -d cider/cider-nrepl:0.28.5 watch :app  
```

start a ClojureScript REPL
```
npx shadow-cljs browser-repl
```

How to JS-interop-REPL in Calva?

```
1. Run the command Calva: Start a Project REPL and Connect (a.k.a. Jack-in)
2. Select project type shadow-cljs
3. Select to start the build :app
4. Select to connect to the build :app
5. Wait for the build to complete
6. Open http://localhost:3003/ in the browser
7. Open core.cljs file and load it in the REPL: Calva: Load/Evaluate Current File and Dependencies

From https://calva.io/shadow-cljs/
```

### Test

```
npx shadow-cljs compile test
node target/test/test.js
```

### Building for production

```
npx shadow-cljs release app
```
