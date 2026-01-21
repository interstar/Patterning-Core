(ns patterning.library.spiro
  (:require [patterning.maths :as maths]
            [patterning.sshapes :refer [->SShape]]
            [patterning.groups :refer [APattern]]
            [patterning.color :refer [default-style]]))

(defn- hypotrochoid-point [r1 r2 d t]
  (let [k (- r1 r2)
        k-ratio (/ k r2)]
    [(+ (* k (maths/cos t))
        (* d (maths/cos (* k-ratio t))))
     (- (* k (maths/sin t))
        (* d (maths/sin (* k-ratio t))))]))

(defn- epitrochoid-point [r1 r2 d t]
  (let [k (+ r1 r2)
        k-ratio (/ k r2)]
    [(- (* k (maths/cos t))
        (* d (maths/cos (* k-ratio t))))
     (- (* k (maths/sin t))
        (* d (maths/sin (* k-ratio t))))]))

(defn hypotrochoid-points
  "General hypotrochoid point generator.
   r1: radius of the fixed circle
   r2: radius of the rolling circle
   d: distance from the rolling circle center to the tracing point
   t0: start parameter in radians
   dt: step size in radians
   n: number of points"
  [r1 r2 d t0 dt n]
  (take n (map (partial hypotrochoid-point r1 r2 d)
               (iterate #(+ % dt) t0))))

(defn epitrochoid-points
  "General epitrochoid point generator.
   r1: radius of the fixed circle
   r2: radius of the rolling circle
   d: distance from the rolling circle center to the tracing point
   t0: start parameter in radians
   dt: step size in radians
   n: number of points"
  [r1 r2 d t0 dt n]
  (take n (map (partial epitrochoid-point r1 r2 d)
               (iterate #(+ % dt) t0))))

(defn hypotrochoid
  "Generate a hypotrochoid pattern.
   Arity 1: ratio (r1/r2), with defaults r1=0.5, d=r2, nopoints=200, num-cycles=1
   Arity 3: ratio, nopoints, num-cycles
   Arity 4: ratio, d, nopoints, num-cycles
   Arity 5: r1, r2, d, nopoints, num-cycles
   Arity 6: r1, r2, d, nopoints, num-cycles, style"
  ([ratio] (hypotrochoid ratio 200 1))
  ([ratio nopoints num-cycles]
   (let [r1 0.5
         r2 (/ r1 ratio)
         d r2]
     (hypotrochoid r1 r2 d nopoints num-cycles default-style)))
  ([ratio d nopoints num-cycles]
   (let [r1 0.5
         r2 (/ r1 ratio)]
     (hypotrochoid r1 r2 d nopoints num-cycles default-style)))
  ([r1 r2 d nopoints num-cycles]
   (hypotrochoid r1 r2 d nopoints num-cycles default-style))
  ([r1 r2 d nopoints num-cycles style]
   (let [dt (/ maths/TwoPI nopoints)
         n (* num-cycles nopoints)]
     (APattern (->SShape style (hypotrochoid-points r1 r2 d 0 dt n))))))

(defn epitrochoid
  "Generate an epitrochoid pattern.
   Arity 1: ratio (r1/r2), with defaults r1=0.5, d=r2, nopoints=200, num-cycles=1
   Arity 3: ratio, nopoints, num-cycles
   Arity 4: ratio, d, nopoints, num-cycles
   Arity 5: r1, r2, d, nopoints, num-cycles
   Arity 6: r1, r2, d, nopoints, num-cycles, style"
  ([ratio] (epitrochoid ratio 200 1))
  ([ratio nopoints num-cycles]
   (let [r1 0.5
         r2 (/ r1 ratio)
         d r2]
     (epitrochoid r1 r2 d nopoints num-cycles default-style)))
  ([ratio d nopoints num-cycles]
   (let [r1 0.5
         r2 (/ r1 ratio)]
     (epitrochoid r1 r2 d nopoints num-cycles default-style)))
  ([r1 r2 d nopoints num-cycles]
   (epitrochoid r1 r2 d nopoints num-cycles default-style))
  ([r1 r2 d nopoints num-cycles style]
   (let [dt (/ maths/TwoPI nopoints)
         n (* num-cycles nopoints)]
     (APattern (->SShape style (epitrochoid-points r1 r2 d 0 dt n))))))
