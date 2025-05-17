# Patterning

A Clojure library for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested. It was originally written to work with [Quil](https://github.com/quil/quil), the Clojure wrapper around [Processing](https://processing.org/), but has now been broken into several separate projects.

### Patterning Core

This repository, Patterning Core, contains code to generate patterns. But is no longer referencing or dependent on Quil / Processing at all. The generated patterns are merely data-structures. This library contains a function to render them as simple SVG. But it's likely you will want to write your own renderer for the pattern in the framework / environment you are working in.

Patterning Core is now in cljc, so it can be compiled to both Clojure and ClojureScript. And it can run in the browser.

A compiled version of this library is now available on Clojars. Include in your Lein project using :

[![Clojars Project](http://clojars.org/com.alchemyislands/patterning/latest-version.svg)](http://clojars.org/com.alchemyislands/patterning)

(See below for an example of how to do this.)

## How (and Where) to Use It

### Patterning works in ClojureScript

The easiest way, today, to just play around with Patterning to see what you can do with it is to try it in the online tutorial. Previously "Project RoseEngine" was a browser-based Patterning editor. This has now been replaced by the ability to edit and update Patterning patterns on many sites. Eg. in the tutorial at [https://alchemyislands.com/assets/patterning-tutorial/Getting%20Started%20with%20Patterning.html](https://alchemyislands.com/assets/patterning-tutorial/Getting%20Started%20with%20Patterning.html)

### Patterning in Cardigan Bay

[Cardigan Bay](https://github.com/interstar/cardigan-bay), a wiki / note-taking / digital gardening engine written in Clojure, also has Patterning built in. That means you can add patterns to your wiki in the form of short embedded scripts.

Cardigan Bay is another easy way to play with Patterning without writing your own application. It's distributed as a Java app that will run on any desktop OS. You don't need a Clojure environment installed. 
 
### Other Documentation

As of writing, https://github.com/nataliefreed also has some nice documentation of the different functions in Patterning at : https://docs.google.com/document/d/1kKsBw3C4jrPGtr7BRYbmcbid4FgWn0_CzdNy1bQ60b8/edit?pli=1

### Patterning Quil 

[A separate project](https://github.com/interstar/Patterning-Quil) now shows how to use Patterning Core in a Quil / Processing project. See the example on that page.

### Patterning for Processing

Patterning was originally written to be used in all-Clojure projects. But for those who are already more familiar with, or need to work in, Java and the Processing development environment itself, there's now a standard Processing Library wrapper around Patterning.

The source for that library and examples are on [Patterning for Processing](https://github.com/interstar/Patterning-for-Processing). They are based on the standard Processing Library template.

## What Patterning looks like 

![Patterning Image](http://alchemyislands.com/bs/assets/patterning/p1.png)

![Patterning Image2](http://alchemyislands.com/bs/assets/patterning/p3.png)

![Patterning Image3](http://alchemyislands.com/bs/assets/patterning/p4.png)

See more examples in the [Patterning Tutorial](http://alchemyislands.com/tutorials/HelloWorld.html)

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

Copyright © 2014-2025 Phil Jones

Distributed under the [Gnu Lesser General Public License](https://www.gnu.org/licenses/lgpl.html) 
either version 3.0 or (at your option) any later version.
