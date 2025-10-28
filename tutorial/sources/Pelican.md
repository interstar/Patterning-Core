### Pelican

I got ChatGPT 5 to make me the infamous [Pelican on a Bicycle](https://simonwillison.net/2024/Oct/25/pelicans-on-a-bicycle/) example by Simon Willison with Patterning.

And to be honest, it did a pretty good job. I showed it some examples of Patterning first, and the source code of the [groups.cljc file](https://github.com/interstar/Patterning-Core/blob/master/src/cljc/patterning/groups.cljc) and the maths.cljc functions.

----
:patterning


;; Palette
(def sky      (p-color 220 240 255))
(def dark     (p-color  25  30  35))
(def tire     (p-color  20  22  26))
(def rimc     (p-color 185 190 200))
(def framec   (p-color 220  60  60))
(def beakc    (p-color 255 180  40))
(def bodyc    (p-color 240 240 235))
(def wingc    (p-color 225 225 220))
(def footc    (p-color 255 170   0))
(def seatc    (p-color  40  40  50))
(def barc     (p-color  40  45  60))
(def eyew     (p-color 255 255 255))
(def pupilc   (p-color  10  10  10))

;; Helpers that return patterns
(defn circle [n r s] [(->SShape s (clock-points n r))])
(defn centered-rod [L t s] (rect (- (/ L 2)) (- (/ t 2)) L t s))

(defn wheel [cx cy r]
  (let [tire   (circle 48 r             {:fill tire})
        rim    (circle 48 (* r 0.82)    {:fill rimc})
        hub    (circle 32 (* r 0.08)    {:fill dark})
        spoke  (centered-rod (* r 0.6) (* r 0.02) {:fill rimc})
        spokes (clock-rotate 8 spoke)
        w      (stack tire rim hub spokes)]
    (translate cx cy w)))

(defn bike-frame [[ax ay] [bx by]]
  (let [L     (abs (- bx ax))
        t     (* 0.02 L)
        bbx   (+ ax (* 0.05 L))   bby (+ ay (* 0.02 L))
        seatx (+ ax (* 0.12 L))   seaty (- ay (* 0.5  L))
        headx (- bx (* 0.18 L))   heady (- by (* 0.45 L))
        seg   (fn [[x1 y1] [x2 y2] w]
                (let [len (distance [x1 y1] [x2 y2])
                      ang (atan2 (- y2 y1) (- x2 x1))
                      rod (centered-rod len w {:fill framec})
                      rodR (rotate ang rod)]
                  (translate (/ (+ x1 x2) 2.0) (/ (+ y1 y2) 2.0) rodR)))]
    (let [top-tube   (seg [seatx seaty] [headx heady] t)
          down-tube  (seg [bbx bby]     [headx heady] t)
          seat-tube  (seg [bbx bby]     [seatx seaty] t)
          chain-stay (seg [ax ay]       [bbx bby]     t)
          seat-stay  (seg [ax ay]       [seatx seaty] t)
          bar-raw    (centered-rod (* 0.35 L) (* 1.6 t) {:fill barc})
          bar-rot    (rotate (atan2 (- heady seaty) (- headx seatx)) bar-raw)
          bar        (translate headx heady bar-rot)
          seat       (translate seatx seaty (centered-rod (* 0.18 L) (* 2.0 t) {:fill seatc}))]
      (stack top-tube down-tube seat-tube chain-stay seat-stay bar seat))))

(defn pelican [base-x base-y wheel-r]
  (let [body0  (circle 48 wheel-r {:fill bodyc
                                   :stroke (p-color 210 210 210)
                                   :stroke-weight 0.01})
        body1  (stretch 1.7 1.2 body0)
        body   (translate (+ base-x (* 0.1 wheel-r)) (- base-y (* 1.5 wheel-r)) body1)

        wing0  [(->SShape {:fill wingc}
                          [[0.0 0.0] [0.6 0.15] [0.9 0.0] [0.6 -0.25] [0.15 -0.35] [-0.2 -0.2]])]
        wing1  (scale (* 1.2 wheel-r) wing0)
        wing2  (rotate -0.2 wing1)
        wing   (translate (+ base-x (* 0.15 wheel-r)) (- base-y (* 1.55 wheel-r)) wing2)

        neck0  (rect -0.03 -0.25 0.06 0.5 {:fill bodyc})
        neck1  (scale wheel-r neck0)
        neck   (translate (+ base-x (* 0.35 wheel-r)) (- base-y (* 1.9 wheel-r)) neck1)

        head   (translate (+ base-x (* 0.45 wheel-r)) (- base-y (* 2.2 wheel-r))
                          (circle 48 (* 0.35 wheel-r) {:fill bodyc
                                                       :stroke (p-color 210 210 210)
                                                       :stroke-weight 0.01}))
        eyeW   (translate (+ base-x (* 0.52 wheel-r)) (- base-y (* 2.25 wheel-r))
                          (circle 24 (* 0.08 wheel-r) {:fill eyew}))
        pupil  (translate (+ base-x (* 0.545 wheel-r)) (- base-y (* 2.25 wheel-r))
                          (circle 20 (* 0.04 wheel-r) {:fill pupilc}))

        beakU0 [(->SShape {:fill beakc}
                          [[0 0] [0.6 0.08] [0.95 0.02] [0.6 -0.02]])]
        beakL0 [(->SShape {:fill (p-color 240 160 40)}
                          [[0 0] [0.58 -0.05] [0.95 -0.09] [0.58 -0.08]])]
        beakU1 (scale (* 0.9 wheel-r) beakU0)
        beakU2 (rotate 0.03 beakU1)
        beakU  (translate (+ base-x (* 0.58 wheel-r)) (- base-y (* 2.22 wheel-r)) beakU2)
        beakL  (translate (+ base-x (* 0.58 wheel-r)) (- base-y (* 2.22 wheel-r))
                          (scale (* 0.9 wheel-r) beakL0))

        leg10  (rect -0.01 0.0 0.02 0.35 {:fill footc})
        leg11  (scale wheel-r leg10)
        leg12  (rotate 0.10 leg11)
        leg1   (translate (+ base-x (* 0.15 wheel-r)) (- base-y (* 1.05 wheel-r)) leg12)

        leg20  (rect -0.01 0.0 0.02 0.33 {:fill footc})
        leg21  (scale wheel-r leg20)
        leg22  (rotate -0.05 leg21)
        leg2   (translate (+ base-x (* 0.20 wheel-r)) (- base-y (* 1.08 wheel-r)) leg22)

        foot0  [(->SShape {:fill footc} [[-0.05 0.0] [0.08 0.03] [0.1 -0.03]])]
        foot1  (translate (+ base-x (* 0.23 wheel-r)) (- base-y (* 0.95 wheel-r))
                          (scale wheel-r foot0))
        foot2  (translate (+ base-x (* 0.18 wheel-r)) (- base-y (* 0.98 wheel-r))
                          (scale wheel-r foot0))]
    (stack body wing neck head eyeW pupil beakU beakL leg1 leg2 foot1 foot2)))

(defn pelican-riding-bike []
  (let [wheel-r 0.25
        rear    [-0.5 -0.2]
        front   [ 0.2 -0.2]
        bg      (rect -1.05 -1.05 2.1 2.1 {:fill sky})
        ground  (rect -1.2 -0.47 2.4 0.02 {:fill dark})
        w1      (wheel (first rear)  (second rear)  wheel-r)
        w2      (wheel (first front) (second front) wheel-r)
        frame   (bike-frame rear front)
        bird    (pelican (first rear) (second rear) wheel-r)]
    
    (stack
      bg ground w1 w2 frame bird
      )))

 (-> (pelican-riding-bike) reframe)

