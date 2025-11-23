(ns patterning.quilview
  (:require [patterning.maths :refer [tx]]
            [patterning.color :refer [p-color]]
            [patterning.view :refer [make-txpt transformed-sshape]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]
            [patterning.layouts :as l]
            [patterning.library.std :as std]))

;; Convert Patterning color to Quil color
(defn patterning-color-to-quil [p color]
  (if (nil? color)
    (p/color 0)
    (if (string? color)
      (p/color color)
      (let [[r g b a] (if (= (count color) 3)
                        (conj color 255)  ; Add alpha if not present
                        color)]          ; Use existing alpha
        (p/color r g b a)))))

;; Draw a single shape using Quil
(defn draw-sshape [p txpt {:keys [style points] :as sshape}]
  (when-not (sshapes/hidden? sshape)
    (let [tsshape (transformed-sshape txpt sshape)]
      (p/push-style)
      
      ;; Set style properties
      (when (contains? style :stroke)
        (p/stroke (patterning-color-to-quil p (get style :stroke))))
      (when (contains? style :fill)
        (p/fill (patterning-color-to-quil p (get style :fill))))
      (when (contains? style :stroke-weight)
        (p/stroke-weight (get style :stroke-weight)))
      
      ;; Draw the shape
      (if (contains? style :bezier)
        ;; Handle bezier curves
        (let [ls (sshapes/flat-point-list tsshape)]
          (apply p/bezier ls))
        ;; Handle regular shapes
        (do
          (p/begin-shape)
          (doseq [[x y] (get tsshape :points)]
            (p/vertex x y))
          (p/end-shape)))
      
      (p/pop-style))))

;; Draw a group of shapes
(defn draw-group [p txpt group]
  (doseq [shape group]
    (draw-sshape p txpt shape)))

;; Initialize all pattern sketches on the page
(defn init-patterns []
  (let [containers (.querySelectorAll js/document ".pattern-container")]
    (doseq [container containers]
      (let [container-id (.-id container)
            code-block (.querySelector (.-previousElementSibling container) "code")
            pattern-code (.-textContent code-block)]
        (create-pattern-sketch container-id pattern-code)))))

;; Create a Quil sketch for a pattern
(defn create-pattern-sketch [container-id pattern-code]
  (let [sketch (p5.
                (fn [p]
                  (let [state (atom {:pattern nil
                                    :mouse {:x 0 :y 0
                                           :x01 0 :y01 0}
                                    :frame-count 0})]
                    
                    (p/setup
                     (fn []
                       (let [container (js/document.getElementById container-id)
                             canvas (p/createCanvas (.-clientWidth container)
                                                  (.-clientHeight container))]
                         (set! (.-parent canvas) container))))
                    
                    (p/draw
                     (fn []
                       (p/background 0)
                       
                       ;; Update mouse position
                       (let [w (p/width)
                             h (p/height)
                             x (p/mouse-x)
                             y (p/mouse-y)]
                         (swap! state assoc
                                :mouse {:x (p/map-range x 0 w -1 1)
                                       :y (p/map-range y 0 h -1 1)
                                       :x01 (p/map-range x 0 w 0 1)
                                       :y01 (p/map-range y 0 h 0 1)})
                         (swap! state update :frame-count inc))
                       
                       ;; Evaluate and draw pattern
                       (let [pattern (cljs.core.eval_string pattern-code)]
                         (draw-group p
                                    (make-txpt [-1 -1 1 1]
                                             [0 0 (p/width) (p/height)])
                                    pattern)))))))]
    sketch))

;; Initialize patterns when the page loads
(.addEventListener js/window "load" init-patterns)

;; Example interactive pattern
(defn example-pattern [{:keys [mouse frame-count]}]
  (let [max-size 24]
    (groups/scale
     0.9
     (l/checkered-grid
      9
      (repeat (std/poly 0 0 (:x01 mouse) 4
                       {:fill (p-color 0 255 255)}))
      (cycle
       (map #(std/poly 0 0
                      (p/map-range
                       (js/Math.log
                        (+ 1 (mod (+ frame-count %1) max-size)))
                       0
                       (js/Math.log max-size)
                       0 1)
                      9
                      {:fill (p-color 255 0 0)})
            (range max-size))))))) 