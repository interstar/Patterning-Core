(ns patterning.color
  (:require [patterning.strings :as strings]
            [patterning.maths :as maths]))

;; Now we'll use a custom vector as a color
(defn p-color
  ([r g b a] [r g b a])
  ([r g b] (p-color r g b 255))
  ([x] (p-color x x x 255))  )

;; Color helpers
(defn color-seq "handy for creating sequences of color changes"
  [colors] (into [] (map (fn [c] {:stroke c}) colors )))


(defn lightened-color [c]
  (let [light (fn [c] (* 1.2 c))]
    (p-color (light (get c 0))  (light (get c 1))  (light (get c 2))  (get c 3)) ))

(defn color-to-fill [style] (conj  {:fill (get style :stroke)} style))

(defn lighten [style] (conj {:stroke (lightened-color (get style :stroke))} style))

(defn mod-styles [f styles] (into [] (map f styles)))


(defn edge-col [c] (comp #(conj % {:stroke c}) color-to-fill) )

(defn setup-colors [colors c] (map (edge-col c) (color-seq colors)  ))


(defn remove-transparency [c] (conj (take 3 c) 255))

(defn rand-col [] (p-color (rand-int 255) (rand-int 255) (rand-int 255) ))
(defn darker-color [c] (apply p-color (map (partial * 0.7) c)))



;; Colours for SVG
(defn transparent-gen [name [r g b a]]
  (let [tx (fn [x] (maths/tx 0 255 0 1 x))]
    (if (= a 255) (strings/gen-format "%s='rgb(%s,%s,%s)'" name (int r) (int g) (int b))
        (strings/gen-format "%s='rgb(%s,%s,%s)' %s-opacity='%.2f' "name (int r) (int g) (int b)  name (tx a) )
        )) )

(defn stroke-gen [c] (transparent-gen "stroke" c))
(defn fill-gen [c] (transparent-gen "fill" c))
