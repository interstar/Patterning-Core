(ns tutorial.core
  (:require
   #_[om.core :as om :include-macros true]
   [devcards.core :as dc]

   [sablono.core :as sab :include-macros true]

   [cljs.js :refer [eval empty-state js-eval]]
   [patterning.sshapes :as sshapes]
   [patterning.groups :as groups]
   [patterning.layouts :as layouts]
   [patterning.library.std :as std]
   [patterning.color :refer [p-color]]
   [patterning.view :refer [make-svg] ]
;   [patterning.maths :refer [PI]]
;  [patterning.examples.framedplant :refer [framed-plant]]

   [tutorial.common :refer [render]]
   [tutorial.fp :as fp]
   [tutorial.page2 :as page2]
   [tutorial.download :as download]
   [tutorial.turtorial :as turtorial]

   ;[tutorial.month :as month]

   )
  (:require-macros
   [devcards.core :as dc :refer [defcard deftest]]))

(enable-console-print!)
(devcards.core/start-devcard-ui!)




(defcard
  "## Introduction to Patterning

Some examples to get you started with Patterning.

*Please make sure you have the most up-to-date version of Patterning from the repository. The library is changing very rapidly and the API isn't at all stable yet. I'm making an effort to keep this tutorial up-to-date with the latest code-base so you may find these examples break with older versions of the code.*

These examples are based on the following required dependencies :
```
 (:require
   [patterning.sshapes :as sshapes]
   [patterning.groups :as groups]
   [patterning.layouts :as layouts]
   [patterning.library.std :as std]
   [patterning.color :refer [p-color]]
   [patterning.view :refer [make-svg] ]
   )
```

This tutorial is made with ClojureScript, FigWheel and Devcards.
All the patterns you are seeing in these pages are being generated by code running live in the page.
")

(defcard
  "## Basic Polygon"
  (let [p (std/poly 0 0 0.7 5 {:stroke (p-color 0 255 0)
                           :fill (p-color 150 150 255)
                           :stroke-weight 4})
        code '(std/poly 0 0 0.7 5 {:stroke (p-color 0 255 0)
                           :fill (p-color 150 150 255)
                           :stroke-weight 4})
         ]
    (sab/html (render p code))))


(defcard
  "## Five red triangles in a ring

Note the definition of style. A style is represented as a mapping with keyword keys. For example :

  `  {:stroke (p-color 255 100 100) :stroke-weight 2 }`

Commonly used entries are `:stroke`, `:stroke-weight` and `:fill`. :stroke and :fill are colours defined with `(p-color r g b)`.


Now let's make a simple pattern.

  `  (def triangles (clock-rotate 5 (poly 0.5 0.5 0.3 3 {:stroke (p-color 255 100 100) :stroke-weight 2 }) ) )`

`poly` creates a regular polygon. Its arguments are x-centre, y-centre, radius, number-of-sides and, optionally, style.

`clock-rotate` is a *Layout*, a function which takes an existing pattern and returns a new one.

In this case, `clock-rotate` takes a number, `n`, and a pattern, and makes the new pattern by rotating n copies of the input around the centre point.
"
  (let [p (layouts/clock-rotate
           5 (std/poly 0.5 0.5 0.3 3 {:stroke (p-color 255 100 100)
                                      :stroke-weight 2 }))
        code '(layouts/clock-rotate
           5 (std/poly 0.5 0.5 0.3 3 {:stroke (p-color 255 100 100)
                                      :stroke-weight 2 }))
        ]
    (sab/html (render p code) )))

(defcard
  "## Stack the five triangles on a blue pentagon"
  (let [p (layouts/stack
            (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2})
            (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                              {:stroke (p-color 255 100 100)
                                               :stroke-weight 2 })))

        code '(layouts/stack
            (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2})
            (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                              {:stroke (p-color 255 100 100)
                                               :stroke-weight 2 })))

        ]
    (sab/html (render p code)))
  )


(defcard
  "## Let's make some grids of these

Note that `grid-layout` takes a **list** of the patterns we want to lay out on it.

Here we just use (repeat pattern) to make an infinite lazy list of them.

    ` (grid-layout 8 (repeat a-pat)) `"

  (let [p (layouts/grid-layout 6 (repeat (layouts/stack
            (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2})
            (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                              {:stroke (p-color 255 100 100)
                                               :stroke-weight 2 })))))
        code '(layouts/grid-layout 6 (repeat (layouts/stack
            (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2})
            (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                              {:stroke (p-color 255 100 100)
                                               :stroke-weight 2 })))))
        ]
    (sab/html (render p code))) )

(defcard
  "## Chequered Grid

Not massively exciting, instead let's do a chequered pattern.

The `checked-layout` takes two streams of patterns and interpolates between them when laying on a grid

`    (checked-layout 8 (repeat pentagon) (repeat triangles))`
"
  (let [p (let [triangles
                (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                                  {:stroke (p-color 255 100 100)
                                                   :stroke-weight 2 }))
                pentagon (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2}) ]
            (layouts/checked-layout 6 (repeat pentagon) (repeat triangles)))
        code '(let [triangles
                (layouts/clock-rotate 5 (std/poly 0.5 0.5 0.3 3
                                                  {:stroke (p-color 255 100 100)
                                                   :stroke-weight 2 }))
                pentagon (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                             :stroke-weight 2}) ]
            (layouts/checked-layout 6 (repeat pentagon) (repeat triangles)))]
    (sab/html (render p code ))))


(defcard
  "## A Drunk Line

OK. change of direction, a ''drunkards walk'' is a series of points each of which is a move in a random direction from the previous one.

Patterning has a function for that, `drunk-line`, which takes a number of steps, a step-length and an option style

`  (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) `
"
  (let [p (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3})
        code '(std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3})]
    (sab/html
     (render p code))))

(defcard
         "Why do we want a random wiggle? Well, they look ''a lot'' cooler when we do some more things to them.

Like clock-rotate them

  `  (clock-rotate 12 (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) )` "
  (let [p  (layouts/clock-rotate 12 (std/drunk-line 10 0.1
                                                    {:stroke (p-color 100 255 100)
                                                     :stroke-weight 3}) )
        code '(layouts/clock-rotate 12 (std/drunk-line 10 0.1
                                                    {:stroke (p-color 100 255 100)
                                                     :stroke-weight 3}) )]
    (sab/html (render p code))
    )
  )

(defcard

  "Or mirror them

` (four-mirror (drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}))`
"
  (let [p (layouts/four-mirror
           (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) )
        code '(layouts/four-mirror
           (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3}) )]
    (sab/html (render p code)))
  )


(defcard
  "Or both. "
  (let [p (->> (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3})
               (layouts/four-mirror)
               (layouts/clock-rotate 12)  )
        code '(->> (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100) :stroke-weight 3})
               (layouts/four-mirror)
               (layouts/clock-rotate 12)  )]
    (sab/html (render p code)))
  )

(defcard
  "And did you want that mixed with our other shapes?"

  (let [p (let [dline (std/drunk-line 10 0.1
                                      {:stroke (p-color 100 255 100)
                                       :stroke-weight 3})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 2 }))
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)) )
        code '(let [dline (std/drunk-line 10 0.1
                                      {:stroke (p-color 100 255 100)
                                       :stroke-weight 3})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 2 }))
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)) )]
    (sab/html (render p code)))
  )

(defcard
  "And perhaps on a staggered grid? The `half-drop-grid-layout` gives us that.
"
  (let [p (let [dline (std/drunk-line 10 0.1
                                      {:stroke (p-color 100 255 100)
                                       :stroke-weight 2})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 1 }))
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)
                     (repeat)
                     (layouts/half-drop-grid-layout 3)) )
        code '(let [dline (std/drunk-line 10 0.1
                                      {:stroke (p-color 100 255 100)
                                       :stroke-weight 2})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 1 }))
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)
                     (repeat)
                     (layouts/half-drop-grid-layout 3)) )]
    (sab/html (render p code)))
  )

(defcard
  "Maybe bring back a bit of blue, every other."
  (let [p (let [dline (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100)
                                              :stroke-weight 2})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 1 }))
                pentagon (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                                              :fill (p-color 100 100 255 100)
                                              :stroke-weight 1})
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)
                     ( #(cycle [pentagon %] ))
                     (layouts/half-drop-grid-layout 6)) )
        code '(let [dline (std/drunk-line 10 0.1 {:stroke (p-color 100 255 100)
                                              :stroke-weight 2})
                triangles (layouts/clock-rotate 5
                                                (std/poly 0.5 0.5 0.3 3
                                                          {:stroke (p-color 255 100 100)
                                                           :stroke-weight 1 }))
                pentagon (std/poly 0 0 0.7 5 {:stroke (p-color 0 0 255)
                                              :fill (p-color 100 100 255 100)
                                              :stroke-weight 1})
              ] (->> (layouts/stack dline (groups/scale 0.4 triangles))
                     (layouts/four-mirror)
                     (layouts/clock-rotate 5)
                     ( #(cycle [pentagon %] ))
                     (layouts/half-drop-grid-layout 6)) )]
    (sab/html (render p code)))
  )

(defcard
"Continue to [Page 2](#!/tutorial.page2)"
  )


(defn main []
  ;; conditionally start the app based on whether the #main-aptp-area
  ;; node is on the page
  (if-let [node (.getElementById js/document "main-app-area")]
    (.render js/ReactDOM (sab/html [:div ""]) node)))

(main)

;; remember to run lein figwheel and then browse to
;; http://localhost:3449/cards.html
