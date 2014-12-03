# Patterning

A Clojure library for generating patterns, both at the small scale and the "layout" of smaller units. Units can be recursively nested.

*Please note that Patterning is in the process of being broken into several separate projects. This repository, Patterning Core, contains code to generate patterns, but is no longer referencing or dependent on Quil / Processing. The generated patterns are merely data-structures, although this library contains a function to render as simple SVG.*

*This refactoring is work in progress and right now, you'd be better off looking at [the old repository](https://github.com/interstar/patterning) unless you know you are meant to be here. That one actually **does** something.* 

* **Eventually** this repository **will** be the official Patterning Core. And new development on the core is now taking place here.*

## What Patterning looks like 

![Patterning Image](http://alchemyislands.com/blog/wp-content/uploads/2014/09/fp6.png)

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

Distributed under the [Gnu Affero General Public License](http://www.gnu.org/licenses/agpl.html) 
either version 3.0 or (at your option) any later version.
