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

### Test

```
npx shadow-cljs compile test
node target/test/test.js
```

### Building for production

```
npx shadow-cljs release app
```
