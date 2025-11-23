;; Title: The City We Invent
;; Description: A Stylized City Made of Reusable Parts

(ns city
  (:require [patterning.layouts :as l :refer [stack clock-rotate h-mirror grid checkered-grid framed h-reflect]]
            [patterning.groups :as p :refer [reframe translate scale rotate stretch rect]]
            [patterning.library.turtle :refer [basic-turtle]]
            [patterning.sshapes :as sshape :refer [->SShape]]
            [patterning.library.l_systems :refer [l-system]]
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

(def engrave {:stroke-weight 1 :stroke (p-color 0 0 0)})

(def roof-a
  [(->SShape engrave
             [[-0.9 1] [0 -0.4] [0.9 1]])])

(def roof-b
  [(->SShape engrave
             [[-0.9 1] [0 0.2] [0.9 1]])])

(def roof-c
  [(->SShape engrave
             [[-0.9 1]
              [-0.6 0.78] [-0.6 0.3]
              [-0.4 0.3] [-0.4 0.62]
              [0 0.2] [0.9 1]])])

(def roof1
  (fn []
    (let [r (rand-nth [roof-a roof-b roof-c])]
      (rand-nth [r (h-reflect r)]))))

(def roof2
  (stack
   (roof1)
   [(->SShape engrave [[-0.9 -1] [-0.9 0.4]])]
   [(->SShape engrave [[0.9 -1] [0.9 0.4]])]))

(defn narrow_ [pat]
  (stack
   (h-mirror [(->SShape engrave [[-0.8 -1] [-0.4 -0.8] [-0.4 0.8] [-0.8 1]])])
   (scale 0.8 pat)))

(defn block_ [p]
  (stack
   [(->SShape engrave [[-0.9 -1] [-0.9 1]])]
   [(->SShape engrave [[0.9 -1] [0.9 1]])]
   p))

(defn block [p]
  #((rand-nth [block_ block_ block_ narrow_ narrow_]) p))

(def blank [])

(defn person []
  (let [x (rand-nth [-0.5 -0.1 0 0.2 0.6])
        p (stack
           (poly x -0.3 0.1 10 engrave)
           [(->SShape engrave [[x -0.2] [x 0.1] [(- x 0.1) 0.6]])
            (->SShape engrave [[x 0.1] [(+ x 0.1) 0.6]])
            (->SShape engrave [[(- x 0.1) -0.1] [(+ x 0.1) 0]])])]
    (rand-nth [p (h-reflect p)])))

(defn street []
  (fn []
    (rand-nth [blank blank (person)])))

(def bottom
  (stack
   [(->SShape engrave [[-0.9 0.8] [0.9 0.8]])]
   (rect -0.3 -0.2 0.6 1 engrave)
   [(->SShape engrave [[-0.9 -1] [-0.9 0.8]])]
   [(->SShape engrave [[0.9 -1] [0.9 0.8]])]))

(def round-window
  (poly 0 0 0.65 50 engrave))

(def arched-window
  (let [half
        [(->SShape
          engrave
          [[0 0.4]
           [-0.6 0.4] [-0.6 -0.2]
           [-0.5 -0.27] [-0.4 -0.34] [-0.3 -0.4]
           [-0.2 -0.42] [-0.1 -0.43] [0 -0.435]])]]
    (stack
     half
     (h-reflect half))))

(def thin-arched   (stretch 0.4 1 arched-window))

(def four-arched
  (translate
   0 0.5
   (stretch
    0.7 1.2
    (reframe
     (stack
      (translate -0.9 0 thin-arched)
      (translate -0.3 0 thin-arched)
      (translate  0.3 0 thin-arched)
      (translate  0.9 0 thin-arched))))))

(def clock
  (stack
   (poly 0 0 0.65 50 engrave)
   (clock-rotate 12 [(->SShape engrave [[0.5 0] [0.6 0]])])
   [(->SShape engrave [[0 0.35] [0 0] [0.3 -0.3]])]))

(def segmented-window
  (stack
   (poly 0 0 0.65 50 engrave)
   (clock-rotate
    8
    [(->SShape engrave [[0 0] [0 0.7]])])))

(def three-windows
  (let [h 0.9 w 0.3 l -0.8]
    (apply stack
           (map #(rect (- (* 0.5 %) 0.65) -0.6 w h) (range 3)))))

(def bars
  (let [bar (fn [y] [(->SShape engrave [[-0.4 y] [0.4 y]])])]
    (apply stack (map bar [-0.5 -0.2 0.1]))))


(def grid-windows
  (scale 0.8
         (grid-layout 3 (repeat (rect -0.6 -0.6 1.2 1.2 engrave)))))


(def tiles
  {:street (street)
   :roof1  roof1
   :roof2 roof2
   :bottom bottom
   :round-window (block round-window)
   :clock  (block clock)
   :segmented-window (block segmented-window)
   :three-windows (block three-windows)
   :grid-windows (block grid-windows)
   :bars (block bars)
   :arched-window (block arched-window)
   :thin-arched (block thin-arched)
   :four-arched (block_ four-arched)})



(def ok-tops (into []
                   (remove #(condp = % :street true :roof1 :roof2 true true false)) (keys tiles)))

(def ok-bottoms (into []
                      (remove #(condp = % :street true :bottom true false)) (keys tiles)))


(defn random-key [previous]
  (let [rk (rand-nth (filter #(not (some #{%} [:roof1 :street])) (keys tiles)))]
    (cond
      (= previous rk) (random-key previous)
      :else rk)))

(defn good-random-key [previous]
  (condp = previous
    :bottom  :street
    :street (rand-nth [:roof1 :roof1 :street])
      ; otherwise
    (random-key previous)))


(defn process-key [k]
  (let [v (get tiles k)]
    (if (fn? v) (v) v)))



(defn make-batch [size dummy]
  (let [r2 (- size 2)
        primo (rand-nth ok-tops)

        mid (loop [count r2 previous primo build []]
              (if (= 0 count) build
                  (let [rk (good-random-key previous)]

                    (recur (- count 1) rk (conj build rk)))))

        fin (if (= (last mid) :street) :roof1
                (rand-nth ok-bottoms))]
    (concat [primo] mid [fin])))

(defn tile-stream [rows]
  (map process-key
       (apply concat
              (iterate #(make-batch rows %) []))))


(defn the-pattern [params]
  (stack
   (rect -1 -1 2 2)
   (grid-layout 11
    (tile-stream 11))))

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