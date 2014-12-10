# Patterning

A Clojure library for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested. It was originally written to work with (Quil)[https://github.com/quil/quil], the Clojure wrapper around (Processing)[https://processing.org/], but has now been broken into several separate projects.

### Patterning Core
This repository, Patterning Core, contains code to generate patterns. But is no longer referencing or dependent on Quil / Processing at all. The generated patterns are merely data-structures. This library contains a function to render them as simple SVG. But it's likely you will want to write your own renderer for the pattern in the framework / environment you are working in.

A compiled version of this library is now available on Clojars. Include in your Lein project using

    [com.alchemyislands/patterning "0.3.0-SNAPSHOT"]]

(See below for an example of how to do this.)

### Patterning Quil 

[A separate project](https://github.com/interstar/Patterning-Quil) now shows how to use Patterning Core in a Quil / Processing project. See the example on that page.

### Patterning for Processing

Patterning was originally written to be used with Quil, in all-Clojure projects. But for those who are already more familiar with, or need to work in, Java and the Processing development environment itself, there's now a standard Processing Library wrapper around Patterning.

The source for that library and examples are on (Patterning for Processing)[https://github.com/interstar/Patterning-for-Processing]. They are based on the standard Processing Library template.

## What Patterning looks like 

![Patterning Image](http://alchemyislands.com/blog/wp-content/uploads/2014/09/fp6.png)

See how we made this in the [Functional Patterning Tutorial](http://alchemyislands.com/tutorial/fp.html).

# DEPRECATED READ ME 
If you are interested in using this project, please see [the current Patterning repository](https://github.com/interstar/patterning).

## Quick Start
Make sure you have [Clojure](http://clojure.org/), [Leiningen](http://leiningen.org/) and [Quil](https://github.com/quil/) installed.

    git clone https://github.com/interstar/Patterning.git patterning
    cd patterning
    lein run

The code to generate the pattern is in src/patterning/core.clj

To run unit tests.

    lein test
   
To run the REPL.

    lein repl


## Examples
Look in the cljx-src/patterning/examples/ directory for examples

[The tutorial](http://alchemyislands.com/tutorial/tutorial.html) gives a guided tour of the basic functions of Patterning. The code for this tutorial can be found in examples/tutorial.clj

to see each of the examples in action change src/patterning/core.clj to assign the pattern that's created to "final-pattern".

For example : 

    (def final-pattern tutorial/triangles)


Then re-run with 

    lein run


See [Alchemy Islands](http://alchemyislands.com) for more examples and discussion of Patterning.

## License

Copyright © 2014 Phil Jones

Distributed under the [Gnu Lesser General Public License](https://www.gnu.org/licenses/lgpl.html) 
either version 3.0 or (at your option) any later version.
