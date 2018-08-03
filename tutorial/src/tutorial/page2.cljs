(ns tutorial.page2

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

   [tutorial.common :refer [render]]
   [clojure.string :as str])

  (:require-macros [devcards.core :as dc :refer [defcard deftest]]
                   [tutorial.mymacro :refer [render2]])
  )



(defcard
  "# Tutorial Part #2

## Bezier Curves

What if you need something smoother than a line of straight segments?

Bezier curves? We got 'em


"

  (let [p (std/bez-curve [[0 1] [-1.5 -0.5] [-0.5 -1.5] [0 0]]
                     {:stroke (p-color 255 128 64) :stroke-weight 4})
        code '(std/bez-curve [[0 1] [-1.5 -0.5] [-0.5 -1.5] [0 0]]
                     {:stroke (p-color 255 128 64) :stroke-weight 4})]
    (sab/html (render p code))))


(defcard
  "Perhaps we can use this to make smoothly curved corners for a frame

Note : ``framed`` is a *Layout* that takes three arguments :

 * a list of corner pieces (which it reflects appropriately),
 * a list of edge pieces (which it rotates appropriately)
 * and a single centre (NOT a list, just a single *group* which it fills the middle.)
"
  (let [p
        (let [orange {:stroke (p-color 255 128 64) :stroke-weight 2}
              blue {:stroke (p-color 100 100 200) :stroke-weight 2} ]
          (layouts/framed 9
                  (repeat (std/bez-curve
                           [[0.9 -0.9] [-1.4 -0.9] [-0.9 -1.4] [-0.9 0.9]]
                           orange))
                  (repeat [{:style orange
                            :points [[-0.9 -1] [-0.9 -1] [-0.9 1]]}])
                  (->>
                   (std/drunk-line 10 0.1 blue)
                   (layouts/clock-rotate 7))
                  ))
        code '(let [orange {:stroke (p-color 255 128 64) :stroke-weight 2}
              blue {:stroke (p-color 100 100 200) :stroke-weight 2} ]
          (layouts/framed 9
                  (repeat (std/bez-curve
                           [[0.9 -0.9] [-1.4 -0.9] [-0.9 -1.4] [-0.9 0.9]]
                           orange))
                  (repeat [{:style orange
                            :points [[-0.9 -1] [-0.9 -1] [-0.9 1]]}])
                  (->>
                   (std/drunk-line 10 0.1 blue)
                   (layouts/clock-rotate 7))
                  ))]
    (sab/html (render p code) )))


(defcard
"## Framed can be used to generate various bordered patterns"

  (let [p (let [corner
                (layouts/stack
                 (std/square {:fill (p-color 255 0 0 100)} )
                 (std/poly 0 0 0.9 4 {:fill (p-color 240 100 240) }))
                edge
                (layouts/stack
                 (std/square {:fill (p-color 0 0 255)})
                 (std/poly 0 0 0.5 8 {:fill (p-color 150 150 255)}))
                centre (std/poly 0 0 0.9 30 {:fill (p-color 150 255 140) })
                ]
            (layouts/framed 7 (repeat corner) (repeat  edge) centre))

        code '(let [corner
                (layouts/stack
                 (std/square {:fill (p-color 255 0 0 100)} )
                 (std/poly 0 0 0.9 4 {:fill (p-color 240 100 240) }))
                edge
                (layouts/stack
                 (std/square {:fill (p-color 0 0 255)})
                 (std/poly 0 0 0.5 8 {:fill (p-color 150 150 255)}))
                centre (std/poly 0 0 0.9 30 {:fill (p-color 150 255 140) })
                ]
            (layouts/framed 7 (repeat corner) (repeat  edge) centre))]
    (sab/html (render p code)))
  )




(defcard
"## Which can be tiled together with grid-layout"

  (let [p (let [corner
                (layouts/stack
                 (std/square {:fill (p-color 255 0 0 100)} )
                 (std/poly 0 0 0.9 4 {:fill (p-color 240 100 240) }))
                edge
                (layouts/stack
                 (std/square {:fill (p-color 0 0 255)})
                 (std/poly 0 0 0.5 8 {:fill (p-color 150 150 255)}))
                centre (std/poly 0 0 0.9 30 {:fill (p-color 150 255 140) })
                ]
            (layouts/grid-layout 5 (repeat
                            (layouts/framed 7 (repeat corner) (repeat  edge) centre))))

        code '(let [corner
                (layouts/stack
                 (std/square {:fill (p-color 255 0 0 100)} )
                 (std/poly 0 0 0.9 4 {:fill (p-color 240 100 240) }))
                edge
                (layouts/stack
                 (std/square {:fill (p-color 0 0 255)})
                 (std/poly 0 0 0.5 8 {:fill (p-color 150 150 255)}))
                centre (std/poly 0 0 0.9 30 {:fill (p-color 150 255 140) })
                ]
            (layouts/grid-layout 5 (repeat
                            (layouts/framed 7 (repeat corner) (repeat  edge) centre))))]
    (sab/html (render p code)))
  )

(defcard

  "
## Random transformations

Note how we fill grids from lazy, infinite lists of shapes made with `repeat`.

We can map other functions to these streams, for example to randomly rotate them."

  (let [p
        (let [orange (p-color 254 129 64) ]
          (layouts/stack
           (std/square {:fill (p-color 50 80 100)})
           (layouts/grid-layout 10
                        (layouts/random-turn-groups
                         (repeat [(sshapes/->SShape {:fill (p-color 230 180 90)
                                                     :stroke orange}
                                                    [[-1 -1] [1 -1] [-1 1]])]))
                           )))

        code '(let [orange (p-color 254 129 64) ]
          (layouts/stack
           (std/square {:fill (p-color 50 80 100)})
           (layouts/grid-layout 10
                        (layouts/random-turn-groups
                         (repeat [(sshapes/->SShape {:fill (p-color 230 180 90)
                                                     :stroke orange}
                                                    [[-1 -1] [1 -1] [-1 1]])]))
                           )))]
    (sab/html (render p code)))
  )

(defcard
  "
We can increase the complexity of the pattern, mixing streams of various sub-patterns.

For more information on tansforming streams of subpatterns with functional programming tools, see [FP Tricks](#!/tutorial.fp)"

  (let [p
        (let [orange (p-color 254 129 64) ]
          (layouts/stack
           (std/square {:fill (p-color 50 80 100)})
           (layouts/checked-layout 18
                           (cycle [
                                   (layouts/clock-rotate 8 (std/drunk-line 10 0.1
                                                               {:stroke orange
                                                                :stroke-weight 1} ))
                                   (layouts/clock-rotate 5 (std/drunk-line 10 0.1
                                                               {:stroke orange
                                                                :stroke-weight 2} ))])
                           (layouts/random-turn-groups
                            (repeat [(sshapes/->SShape {:fill (p-color 230 180 90)
                                                        :stroke orange}
                                                       [[-1 -1] [1 -1] [-1 1]])]))
                           )))

        code
        '(let [orange (p-color 254 129 64) ]
          (layouts/stack
           (std/square {:fill (p-color 50 80 100)})
           (layouts/checked-layout 18
                           (cycle [
                                   (layouts/clock-rotate 8 (std/drunk-line 10 0.1
                                                               {:stroke orange
                                                                :stroke-weight 1} ))
                                   (layouts/clock-rotate 5 (std/drunk-line 10 0.1
                                                               {:stroke orange
                                                                :stroke-weight 2} ))])
                           (layouts/random-turn-groups
                            (repeat [(sshapes/->SShape {:fill (p-color 230 180 90)
                                                        :stroke orange}
                                                       [[-1 -1] [1 -1] [-1 1]])]))
                           )))]
    (sab/html (render p code)))
  )


(defcard
  "
## A word about colour.

Colours can be defined with the function `p-color` which takes 1, 3 or 4 arguments. A single argument will give you a shade of grey between black (0) and white (255). Three arguments will get mapped to red, green and blue components. Four arguments will get mapped to red, green, blue and alpha (transparency) components.

A transparent green :
```
 (p-color 150 255 150 150)
```
We can over-ride or supplement the styling of an existing group using the `over-style` function. Here we're going to supplement an already bright green `clock-rated drunk-line` with a transparent green fill. Note that the `drunk-line` is not closed but can still be filled.

And to see the transparency we can stack it on top of a basic grid of squares.
"
  (let [p (layouts/stack
           (std/square {:fill (p-color 0)})
           (layouts/grid-layout 10 (repeat (std/square {:stroke-weight 2
                                            :stroke (p-color 100 50 100)})))
           (groups/over-style {:fill  (p-color 150 255 150 150)}
                       (layouts/clock-rotate 12 (std/drunk-line 20 0.05
                                                    {:stroke (p-color 100 255 100)
                                                     :stroke-weight 3})))
           )

        code '(layouts/stack
           (std/square {:fill (p-color 0)})
           (layouts/grid-layout 10 (repeat (std/square {:stroke-weight 2
                                            :stroke (p-color 100 50 100)})))
           (groups/over-style {:fill  (p-color 150 255 150 150)}
                       (layouts/clock-rotate 12 (std/drunk-line 20 0.05
                                                    {:stroke (p-color 100 255 100)
                                                     :stroke-weight 3})))
           )]
    (sab/html (render p code))))
