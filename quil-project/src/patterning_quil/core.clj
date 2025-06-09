(ns patterning_quil.core
  (:require [quil.core :refer :all])
  (:require [patterning_quil.quilview :refer :all])
  (:require [patterning_quil.examples :as ex])
  (:require [patterning.view :refer [make-txpt]]))

;; Window dimensions
(def my-width 700)
(def my-height 700)

;; Pattern definition atom for live coding
(def current-pattern (atom nil))

;; Initialize with default pattern
(reset! current-pattern (ex/default-pattern))

(defn setup []
  (frame-rate 1)
  (stroke-weight 1)
  (color 0)
  (no-fill))

(defn draw []
  (background 0)
  (draw-group (make-txpt [-1 -1 1 1] [0 0 my-width my-height])
              @current-pattern)
  (save "out.png")
  (spit "out.patdat" (into [] @current-pattern))
  (write-svg 800 800 @current-pattern)
  (reset! current-pattern (ex/default-pattern)))

(defn -main [& args]
  (when (= *file* (System/getProperty "clojure.main.script"))
    (sketch
     :title "Patterning"
     :setup setup
     :draw draw
     :size [my-width my-height]
     :on-close #(System/exit 0))))
