(ns patterning-demo.prelude
  {:kindly/options {:kinds-that-hide-code #{:kind/md}}}
  (:require [patterning.prelude :refer :all]
            [scicloj.kindly.v4.kind :as kind]))

(defn render
  ([pattern] (render 360 pattern))
  ([size pattern]
   (let [svg (make-svg size size pattern)]
     (kind/html
      (str
       "<div style='display:inline-block;border:1px solid #ddd;background:#fff;padding:6px'>"
       svg
       "</div>")))))

^:kindly/hide-code
(kind/md
 "# Patterning in Clay\n\nThis notebook is a short narrative tour through Patterning. Each section defines a pattern as data, then renders it directly as SVG.")

^:kindly/hide-code
(kind/md "## 1. A simple seed + symmetry")

(defn ring-of-polys [n r]
  (->>
   (poly n 0.12 0 r
         (paint
          (p-color 30 30 30)
          (p-color 240 160 80)
          2))
   (clock-rotate n)))

(def ring-pattern
  (stack
   (ring-of-polys 24 0.8)
   (ring-of-polys 5 0.4)))

(render ring-pattern)

^:kindly/hide-code
(kind/md
 "Patterning is just data transforms: make a base shape, then compose layouts like `clock-rotate` and `stack` to build structure.")

^:kindly/hide-code
(kind/md "## 2. Pixel art as pattern data")

(def invader1
  (pixel 13
         [1 1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 2 1 1 1 1 1 2 1 1 1
          1 1 1 1 2 1 1 1 2 1 1 1 1
          1 1 1 2 2 2 2 2 2 2 1 1 1
          1 1 2 2 1 2 2 2 1 2 2 1 1
          1 2 2 2 2 2 2 2 2 2 2 2 1
          1 2 1 2 2 2 2 2 2 2 1 2 1
          1 1 1 2 1 1 1 1 1 2 1 1 1
          1 1 1 1 2 2 1 2 2 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1 1]
         {1 (p-color 20 20 20)
          2 (p-color 40 190 80)}))

(def invader2
  (pixel 12
         [1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 2 2 1 1 1 1 1
          1 1 1 1 2 2 2 2 1 1 1 1
          1 1 1 2 2 2 2 2 2 1 1 1
          1 1 2 2 1 2 2 1 2 2 1 1
          1 1 2 2 2 2 2 2 2 2 1 1
          1 1 1 1 2 1 1 2 1 1 1 1
          1 1 1 2 1 2 2 1 2 1 1 1
          1 1 2 1 2 1 1 2 1 2 1 1
          1 1 1 1 1 1 1 1 1 1 1 1
          1 1 1 1 1 1 1 1 1 1 1 1]
         {1 (p-color 20 20 20)
          2 (p-color 40 190 80)}))

(def pixel-pattern
  (grid 6 (cycle [invader1 invader2])))

(render pixel-pattern)

^:kindly/hide-code
(kind/md
 "Pixel art is just another pattern: a grid of tiny rects. Because Patterning is data-first, that pixel grid can be repeated, rotated, or mixed with other layouts.")

^:kindly/hide-code
(kind/md "## 3. Douat-style recursive tiling")

(def base-tile
  (stack
   (square {:fill (p-color 240 240 240)})
   [(->SShape
     {:fill (p-color 30 30 30)
      :stroke (p-color 30 30 30)}
     [[-1 1] [-1 -1] [1 1]])]))

(def douat-pattern
  (-> (Douat base-tile)
      A E E A
      A B C D
      A E E A
      B E E B
      (:p)))

(render douat-pattern)

^:kindly/hide-code
(kind/md
 "Douat patterns show how a small rule set can grow into a complex image. The pattern evolves through a pipeline of transformations, and the result is still just a data structure that can be rendered as SVG.")
