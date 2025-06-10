;; Title: SShape as Layout
;; Description: An SShape is just a sequence of points. So we can use one as a layout

(ns sshapelayout
  (:require [patterning.layouts :as l :refer [stack clock-rotate grid-layout checked-layout framed]]
            [patterning.groups :as p]
            [patterning.library.std :as std :refer [poly drunk-line bez-curve]]
            [patterning.library.complex_elements :as complex]
            [patterning.color :as color :refer [p-color]]
            [patterning.maths :as maths :refer [PI]]
            [patterning.canvasview :as canvasview]))

;; Define your pattern parameters here
(def default-params
  {:width 600
   :height 600
   ;; Add your pattern-specific parameters here
   })

;; PATTERN START

(defn sshape-to-positions [{:keys [style points]}] points)

(defn sshape-as-layout [sshape group-stream scalar]
  (l/place-groups-at-positions (map #(p/scale scalar %) group-stream) (sshape-to-positions sshape)))


(defn a-nangle [n] (std/nangle 0 0 0.6 n {:stroke (p-color 0 0 0) :stroke-weight 2}))
(defn randomize-color [p]
  (let [c (color/rand-col)]
    (p/over-style {:fill (color/darker-color c)
                   :stroke c} p)))


(defn the-pattern [params]
  (let [l (drunk-line 10 0.1)
        s (map randomize-color
               (cycle (map a-nangle [5 7 9])))]
    (clock-rotate 8
     (stack l (sshape-as-layout (first l) s 0.1)))))

;; PATTERN END

;; Define your pattern generation function
(defn ^:export main [params]
  (let [merged-params (merge default-params (js->clj params :keywordize-keys true))
        canvas (:canvas merged-params)
        pattern (the-pattern merged-params)]
    
    (when canvas
      (js/console.log "Setting up responsive canvas...")
      (canvasview/setupResponsiveCanvas canvas pattern))
    
    pattern)) 