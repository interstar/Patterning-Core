# Patterning

A Clojure library for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested. It was originally written to work with [Quil](https://github.com/quil/quil), the Clojure wrapper around [Processing](https://processing.org/), but has now been broken into several separate projects.

### Patterning Core

This repository, Patterning Core, contains code to generate patterns. But is no longer referencing or dependent on Quil / Processing at all. The generated patterns are merely data-structures. This library contains a function to render them as simple SVG. But it's likely you will want to write your own renderer for the pattern in the framework / environment you are working in.

Patterning Core is now in cljc, so it can be compiled to both Clojure and ClojureScript. And it can run in the browser.

A compiled version of this library is now available on Clojars. Include in your Lein project using :

[![Clojars Project](http://clojars.org/com.alchemyislands/patterning/latest-version.svg)](http://clojars.org/com.alchemyislands/patterning)

(See below for an example of how to do this.)



### Patterning works in ClojureScript and Figwheel

The `tutorial` subdirectory of this repository is actually a separate leiningen project. It uses Patterning with Figwheel.

```
cd tutorial

lein figwheel devcards
```

### Patterning Quil 

[A separate project](https://github.com/interstar/Patterning-Quil) now shows how to use Patterning Core in a Quil / Processing project. See the example on that page.

### Patterning for Processing

Patterning was originally written to be used in all-Clojure projects. But for those who are already more familiar with, or need to work in, Java and the Processing development environment itself, there's now a standard Processing Library wrapper around Patterning.

The source for that library and examples are on [Patterning for Processing](https://github.com/interstar/Patterning-for-Processing). They are based on the standard Processing Library template.

## What Patterning looks like 

![Patterning Image](http://alchemyislands.com/blog/wp-content/uploads/2014/09/fp6.png)

See how we made this in the [Functional Patterning Tutorial](http://alchemyislands.com/bs/assets/patterning/tutorial_site/index.html#!/tutorial.fp).

## Quick Start

    git clone https://github.com/interstar/Patterning-Core.git Patterning
    cd Patterning
    mkdir outs
    lein run
    
You should see that Patterning has produced several example patterns in the outs directory.
    
Check the src/clj/core.clj for code that made these patterns.


# DEPRECATED 

[Older Patterning repo](https://github.com/interstar/patterning).


## License

Copyright © 2014-2018 Phil Jones

Distributed under the [Gnu Lesser General Public License](https://www.gnu.org/licenses/lgpl.html) 
either version 3.0 or (at your option) any later version.
