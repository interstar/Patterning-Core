There are three recommended ways to play with Patterning today:

* **Project RoseEngine**, the in-browser Patterning "workbench" is back. Try it at [https://alchemyislands.com/assets/patterning-tutorial/workbench/index.html](https://alchemyislands.com/assets/patterning-tutorial/workbench/index.html).

* Use the [Cardigan Bay](https://github.com/interstar/cardigan-bay) wiki-engine / PKM tool. This is a personal wiki-like notebook which allows you to embed Patterning patterns into pages, and also to export flat HTML sites that include the embedded patterns.

* Write a Clojure or ClojureScript project that uses the [Patterning Core Library](https://github.com/interstar/Patterning-Core). This library can be included directly from Clojars : [Patterning](https://clojars.org/com.alchemyislands/patterning)

* [Patterning for Processing](https://github.com/interstar/Patterning-for-Processing) is a version of the Patterning library packaged for use within Processing.

The first is the easiest way to play with Patterning and to start making patterns. The second is useful if you want to publish a site containing those patterns. And the third is the most flexible but is most likely to be used by people who are already familiar with Clojure programming and projects.

Clojure programmers should note that the various functions in Patterning come from a number of namespaces. In both RoseEngine and Cardigan Bay you are, in fact, using the [Small Clojure Interpreter](https://github.com/babashka/sci) and a subset of Patterning functions has already been made available within the namespace. You don't have to worry about where these functions come from.

If you are writing your own software you'll need to know where to find different functions.

For example ... 

<pre class="code-preview">
<code>

(:require [patterning.maths :as maths :refer [PI]]
          [patterning.sshapes
             :refer [->SShape to-triangles ]
             :as sshapes]
          [patterning.strings :as strings]
          [patterning.groups :as groups]
          [patterning.layouts
             :refer [framed clock-rotate stack grid-layout diamond-layout
                     four-mirror four-round nested-stack checked-layout
                     half-drop-grid-layout random-turn-groups h-mirror ring
                     sshape-as-layout]]
          [patterning.library.std
             :refer [poly star nangle spiral diamond
                     horizontal-line square drunk-line]]
          [patterning.library.turtle :refer [basic-turtle]]
          [patterning.library.l_systems :refer [l-system]]
          [patterning.library.complex_elements :refer [vase zig-zag]]
          [patterning.view :refer [make-txpt make-svg]]
          [patterning.color :refer [p-color remove-transparency] ]
          [patterning.examples.framedplant :as framedplant]
          [patterning.examples.design_language1 :as design-language]
          [patterning.library.symbols :as symbols]
          [patterning.library.complex_elements 
             :refer [petal-pair-group petal-group]]
          [patterning.api :refer :all]
          [clojure.spec.alpha :as s]
)

</code>
</pre>
