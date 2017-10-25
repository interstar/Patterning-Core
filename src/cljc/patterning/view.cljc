(ns patterning.view
  (:require [clojure.string :as string]
            [patterning.maths :refer [tx]]
            [patterning.strings :as strings]
            [patterning.sshapes :refer []]
            [patterning.groups :refer []]
            [patterning.color :refer [stroke-gen fill-gen p-color]]) )




;; Viewing pipeline

(defn make-txpt
  "first argument is viewport (left, top, right, bottom), second is window (left, top, right, bottom)
   returns a mapping function from viewport to window"
  [[vx1 vy1 vx2 vy2] [wx1 wy1 wx2 wy2]]
  (fn [[x y]] [(tx vx1 vx2 wx1 wx2 x) (tx vy1 vy2 wy1 wy2 y)] )   )

(defn project-points
  ([txpt points] (map txpt points))
  ([viewport window points] (map (make-txpt viewport window) points) )  )


(defn transformed-sshape [txpt {:keys [style points]}]
  {:style style :points (project-points txpt points) } )


;; SVG generation


(defn sshape-to-SVG-path [txpt sshape]
  (let [linify (fn [[x y]] (str " L " x " " y))
        bezify (fn [[x y]] (str x " " y " " ))

        {:keys [style points] :as tss} (transformed-sshape txpt sshape)
        p (first points)
        s1 (str "M " (first p) " " (last p) )

        strung (if (contains? style :bezier)
                 (cons s1 (cons "C" (mapcat bezify (rest points) )))
                 (cons s1 (mapcat linify (rest points)))
                 )
        stroke (if (contains? style :stroke) (stroke-gen (get style :stroke)) (stroke-gen (p-color 0)) )
        stroke-width (if (contains? style :stroke-weight) (strings/gen-format "stroke-width='%spx'" (:stroke-weight style) ) "" )
        fill (str (if (contains? style :fill)
                    (fill-gen (get style :fill))
                    "fill='none' ") )
        ]

    (str (strings/gen-format "\n<path style='-webkit-tap-highlight-color: rgba(0, 0, 0, 0);' %s %s %s " stroke-width stroke fill )
         " d='"
         (apply str strung)
         "'></path>"
         )  ) )

(defn inner-xml-tpl [txpt width height group] (string/join (apply str (mapcat (partial sshape-to-SVG-path txpt) group))) )

(defn xml-tpl
  "svg 'template' which also flips the coordinate system via http://www.braveclojure.com/organization/"
  [txpt width height group]
  (str "<svg height=\"" height "\" width=\"" width "\">"
       (inner-xml-tpl txpt width height group)
       "</svg>"))

(defn make-svg
  ([viewport window width height group]
     (let [txpt (make-txpt viewport window)]
       (xml-tpl txpt width height group)  ))
  ([width height group] (make-svg [-1 -1 1 1] [0 0 width height] width height group )))
