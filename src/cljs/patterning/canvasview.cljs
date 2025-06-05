(ns patterning.canvasview
  (:require [patterning.maths :refer [tx]]
            [patterning.color :refer [p-color]]
            [patterning.view :refer [make-txpt transformed-sshape]]))

(defn patterning-color-to-css [color]
  (if (nil? color)
    "black"
    (if (string? color)
      color
      (let [[r g b a] (if (= (count color) 3)
                        (conj color 255)  ; Add alpha if not present
                        color)]          ; Use existing alpha
        (if (= a 255)
          (str "rgb(" (Math/round r) "," (Math/round g) "," (Math/round b) ")")
          (str "rgba(" (Math/round r) "," (Math/round g) "," (Math/round b) "," (/ a 255) ")"))))))

(defn renderToCanvas [pattern canvas viewport window]
  (let [ctx (.getContext canvas "2d")
        txpt (make-txpt viewport window)]
    
    ;; Clear the canvas
    (.clearRect ctx 0 0 (.-width canvas) (.-height canvas))
    
    ;; Render each SShape
    (doseq [sshape pattern]
      (let [{:keys [style points]} (transformed-sshape txpt sshape)]
        ;; Set style properties
        (set! (.-strokeStyle ctx) (patterning-color-to-css (:stroke style)))
        (set! (.-fillStyle ctx) (patterning-color-to-css (:fill style)))
        (set! (.-lineWidth ctx) (get style :stroke-weight 1))
        
        ;; Draw the shape
        (.beginPath ctx)
        (let [[x y] (first points)]
          (.moveTo ctx x y))
        
        (if (:bezier style)
          ;; Handle bezier curves
          (doseq [[p1 p2 p3] (partition 3 (rest points))]
            (.bezierCurveTo ctx 
                           (first p1) (second p1)
                           (first p2) (second p2)
                           (first p3) (second p3)))
          ;; Handle regular lines
          (doseq [[x y] (rest points)]
            (.lineTo ctx x y)))
        
        ;; Fill and stroke
        (when (and (:fill style) (not= (:fill style) "none"))
          (.fill ctx))
        (.stroke ctx)))))

(defn setupResponsiveCanvas [canvas pattern]
  (let [resizeCanvas (fn []
                      (let [container (.-parentElement canvas)
                            width (.-clientWidth container)
                            height (.-clientHeight container)
                            viewport [-1 -1 1 1]
                            window [0 0 width height]]
                        (set! (.-width canvas) width)
                        (set! (.-height canvas) height)
                        (renderToCanvas pattern canvas viewport window)))]
    
    ;; Initial setup
    (resizeCanvas)
    
    ;; Handle window resize
    (.addEventListener js/window "resize" resizeCanvas)))