(ns tutorial.fp

  (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]

   [cljs.js :refer [eval empty-state js-eval]]
   [patterning.sshapes :as sshapes]
   [patterning.groups :as groups]
   [patterning.layouts :as layouts]
   [patterning.library.std :as std]
   [patterning.color :refer [p-color] :as color]
   [patterning.view :refer [transformed-sshape make-txpt xml-tpl make-svg] ]
   [patterning.maths :refer [PI]]

   [tutorial.common :refer [render]]
   )
  (:require-macros [devcards.core :as dc :refer [defcard deftest]]
                   [tutorial.mymacro :refer [render2]])

  )




(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (layouts/clock-rotate n (std/poly 0 0.5 0.4 n
                                     {:stroke lc,
                                      :fill fc,
                                      :stroke-weight 3}))))


(defcard
  "# Functional Power For Patterning

Functional Programming has many tools and tricks for writing elegant code. Here we see how they can be applied to make interesting patterns.

## A round of regular polygons

Let's start with a simple function that makes a ring of n copies of an n-sided regular polygon.

We define it once and use it throughout this tutorial :

```
(defn a-round
  ([n] (a-round n (p-color 0) (p-color 255)))
  ([n lc fc]
   (layouts/clock-rotate n (std/poly 0 0.5 0.4 n
                                     {:stroke lc,
                                      :fill fc,
                                      :stroke-weight 3}))))

```
"

  (let [p
        (let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)
             ] (a-round 8 lc fc))
        code '(let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)
             ] (a-round 8 lc fc))
       ]
    (sab/html (render p code) ))
  )


(defcard
"As described [previously](/cards.html#!/tutorial.core), we can place this pattern into a grid.
"
(let [p
        (let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)]
          (layouts/grid-layout 4 (repeat (a-round 8 lc fc))))
        code '(let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)]
          (layouts/grid-layout 4 (repeat (a-round 8 lc fc))))]
    (sab/html (render p code)))

  )

(defcard
  "## The nature of `repeat`

The meaning of this code should be self-evident. But what about the call to `repeat`?

Layouts such as grid-layout take not just a single pattern to fill the grid, but a list of them. The algorithm starts by generating a sequence of positions to place each 'tile' at. And then runs through the list of positions and the list of patterns and places one pattern at one position. The number of positions is finite, so in Clojure we can pass in an infinite, lazily evaluated list of patterns.

`repeat` is a function that takes a single item and returns an infinite list of them.

But we can also use `cycle` to turn finite vector of patterns into an infinite list :

   `(cycle [(a-round 4) (a-round 8)])`

(We also note here that the grid is filled column-wise from left to right.)

 "

  (let [p
        (let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100) ]
          (layouts/grid-layout 4 (cycle [(a-round 4 lc fc) (a-round 8 lc fc)])))
        code '(let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100) ]
          (layouts/grid-layout 4 (cycle [(a-round 4 lc fc) (a-round 8 lc fc)])))]
    (sab/html (render p code)))
  )


(defcard
  "## Sequence tricks

Because the grid is filled from a sequence, we can use any functions that operate on the sequence abstraction to generate or process the patterns that feed it.

Here we generate our sequence by mapping a-round to a vector of integers.

"

    (let [p
        (let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)]
          (layouts/grid-layout 4 (cycle (map #(a-round % lc fc) [3 4 5 6]))))
        code '(let [lc (p-color 140 220 180)
              fc (p-color 190 255 200 100)]
          (layouts/grid-layout 4 (cycle (map #(a-round % lc fc) [3 4 5 6]))))]
    (sab/html (render p code)))
  )

(defcard
  "Or we can generate the sequence using Clojure's `iterate` to constantly apply a transformation to an initial pattern. Such as shrinking :

"
      (let [p
        (let [lc (p-color 220 140 180)
              fc (p-color 255 190 200 100)]
          (layouts/grid-layout 4 (iterate (partial groups/scale 0.9) (a-round 9 lc fc))))
        code '(let [lc (p-color 220 140 180)
              fc (p-color 255 190 200 100)]
          (layouts/grid-layout 4 (iterate (partial groups/scale 0.9) (a-round 9 lc fc))))]
    (sab/html (render p code)))
  )

(defcard
  "We can even add random transformations, such as assigning each pattern an arbitrary colour."

  (let [p
        (let [rand-color
              (fn [p] (let [c (color/rand-col)]
                        (groups/over-style {:fill c
                                            :stroke-weight 2
                                            :stroke (color/darker-color c)} p) ) )

             ]
          (layouts/grid-layout 4 (map rand-color (map a-round (cycle [3 4 5 6 7]) ))))
        code '(let [rand-color
              (fn [p] (let [c (color/rand-col)]
                        (groups/over-style {:fill c
                                            :stroke-weight 2
                                            :stroke (color/darker-color c)} p) ) )

             ]
          (layouts/grid-layout 4 (map rand-color (map a-round (cycle [3 4 5 6 7]) ))))]
    (sab/html (render p code)))

  )

(defcard
"## Clojure mapping can take multiple arguments

Clojure's `map` function can actually map a function across multiple lists. `(map f xs ys)` will call `(f x y)` for each corresponding
element of xs and ys.

We can use this to apply evolving transforms to a stream of evolving patterns. For example, this rotating / shrinking trianlgle.

```

``` "

    (let [p
          (let [rand-color
                (fn [p] (let [c (color/rand-col)]
                        (groups/over-style {:fill c
                                            :stroke-weight 2
                                            :stroke (color/darker-color c)} p) ) )
                t
                (layouts/stack
                 (std/poly 0 0 0.6 3 {:stroke-weight 1})
                 (std/horizontal-line 0 {:stroke-weight 2})) ]
            (layouts/grid-layout 6
                                 (map rand-color
                                  (map groups/rotate
                                       (iterate (partial + 0.2) 0)
                                       (iterate (partial groups/scale 0.97) t)
                                       )
                                  ) ))
        code '(let [rand-color
                (fn [p] (let [c (color/rand-col)]
                        (groups/over-style {:fill c
                                            :stroke-weight 2
                                            :stroke (color/darker-color c)} p) ) )
                t
                (layouts/stack
                 (std/poly 0 0 0.6 3 {:stroke-weight 1})
                 (std/horizontal-line 0 {:stroke-weight 2})) ]
            (layouts/grid-layout 6
                                 (map rand-color
                                  (map groups/rotate
                                       (iterate (partial + 0.2) 0)
                                       (iterate (partial groups/scale 0.97) t)
                                       )
                                  ) ))]
      (sab/html (render p code)))

  )
