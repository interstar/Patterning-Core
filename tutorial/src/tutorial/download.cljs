(ns tutorial.download

    (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]

   [cljs.js :refer [eval empty-state js-eval]]

   )

   (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  )

(defcard getting-patterning
  "
# Get Patterning

Patterning can now be used in one of several ways.

## With Clojure

Make sure you have [Clojure](http://clojure.org/) and [Leiningen](http://leiningen.org/) installed.

### Including the library as a Clojar.

You can *use* Patterning as a pre-compiled version of the library. (It is now available on Clojars.) Include it in your Lein project using


`[com.alchemyislands/patterning \"0.5.4-SNAPSHOT\"]`



### With Quil
An example of a Quil project that uses this Clojar is [available on GitHub](https://github.com/interstar/Patterning-Quil).

Look at the [project.clj](https://github.com/interstar/Patterning-Quil/blob/master/project.clj) to see how to add both Quil and Patterning to your project's dependencies.

### Without Quil
If you want to use Patterning in your Clojure project but NOT use Quil/Processing, you can call the `makeSVG` function in the api to get an SVG format string. Or you'll have to write your own equivalent to `draw-group` ([see quilview.clj](https://github.com/interstar/Patterning-Quil/blob/master/src/patterning_quil/quilview.clj)) for the specific graphic toolkit you want to use.


makeSVG is declared as :

`    (defn -makeSVG [viewport width height group] (...))`

where viewport is list of `[minx, miny, maxx, maxy]`, width and height are floats and group is a pattern (ie. list of sshapes).


## Working with the core source

Patterning Core ([on GitHub](https://github.com/interstar/Patterning-Core)) is the pure Clojure (cljx) core of the library. It's the code that does all the work, and is used to generate the Clojar described previously. It is not dependent on Quil / Processing or any other graphic toolkit (apart from creating SVGs). You can obviously download the source and compile it yourself.

### Quick Start
```
    git clone https://github.com/interstar/Patterning-Core.git patterning
    cd patterning
    lein run
```
Note that the program will not display anything on the screen, it will just create an out.svg file in the same directory, with the pattern.

The code to generate the pattern is in src/patterning/core.clj

To run unit tests.
```
    lein test
```
To run the REPL.
```
    lein repl
```
## With Java or Processing

If you are not a Clojure programmer, you can still use the library from Java / Processing.

## Using with Processing

The library for Processing is available [on GitHub](https://github.com/interstar/Patterning-for-Processing-Examples).

Go to that page, download the zip archive and copy the PatterningForProcessing directory into the libraries directory of your Processing sketchbook. You can now open Processing and import the library into your sketch normally. The PatterningForProcessing directory contains examples of calling Patterning from your Processing sketch.

The [Patterning For Processing](https://github.com/interstar/Patterning-for-Processing) repository contains the source-code for the Processing library.

## Using in another Java program
Currently, the best way to learn about this is to look at the [source](https://github.com/interstar/Patterning-for-Processing) for the Patterning For Processing library. [This directory](https://github.com/interstar/Patterning-for-Processing/tree/master/src/patterning/library) gives examples of classes that wrap specific functionality from Patterning. Patterning.java is the main wrapper that you will deal with. IPattern.java is the common interface to keep a handle on a pattern.

All this Java code calls the patterning.api in the patterning.jar.

The `Patterning.java` class also contains the `draw` and `drawSShape` methods that render the pattern (in this case, using Processing). If you want to adapt this code for another graphics toolkit you will need to write a version of these functions for that toolkit.



")
