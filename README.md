
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
