(ns patterning.library.std-test
  (:require [clojure.test :refer :all]          
            [patterning.maths :as maths]
            [patterning.groups :refer [mol=]]
            [patterning.library.std :refer [poly bez-curve arc]]
            [patterning.sshapes :refer [mol=shapes]]))

(deftest test-std-library
  (let []
    (testing "bezier"
      (is (= 
           (bez-curve [[-0.9 0] [0.8 0.8] [-0.5 -0.8] [0.6 -0.5]] {} )
           [{:points [[-0.9 0] [0.8 0.8] [-0.5 -0.8] [0.6 -0.5]] 
             :style {:bezier true}}]
           ))
      )
    (testing "primitives"               
      (let [result (poly 4 0.5 0 0 {})
            expected-points [[0 -0.5] [0.5 0] [0 0.5] [-0.5 0] [0 -0.5]]]
        (is (= 1 (count result)))
        (is (= {} (:style (first result))))
        (is (mol=shapes expected-points (:points (first result))))))
    
    (testing "arc"
      ;; Test a quarter circle arc (from 0 with offset PI/2)
      (let [result (arc 0.5 0 (/ maths/PI 2) 8)
            shape (first result)
            points (:points shape)]
        (is (= 1 (count result)) "arc should return a single shape")
        (is (= {} (:style shape)) "arc should have default empty style")
        ;; With resolution 8, we should get exactly 8 points for the arc
        (is (= 8 (count points)) "arc should have exactly resolution number of points")
        ;; First point should be at angle 0: [radius, 0] = [0.5, 0]
        (let [first-point (first points)]
          (is (< (maths/abs (- (first first-point) 0.5)) 0.01) "first point x should be 0.5")
          (is (< (maths/abs (second first-point)) 0.01) "first point y should be 0")
          ;; Last point should be approximately at angle PI/2: [0, radius] = [0, 0.5]
          (let [last-point (last points)]
            (is (< (maths/abs (first last-point)) 0.1) "last point x should be near 0")
            (is (< (maths/abs (- (second last-point) 0.5)) 0.1) "last point y should be near 0.5")))))
      ;; Test arc with style (using 4-arity where 4th arg is style)
      (let [result (arc 0.5 0 maths/PI {:stroke :red})]
        (is (= {:stroke :red} (:style (first result))) "arc should preserve style"))
      ;; Test arc with explicit resolution and style
      (let [result (arc 0.5 0 (/ maths/PI 2) 8 {:stroke :blue})]
        (is (= {:stroke :blue} (:style (first result))) "arc should preserve style with explicit resolution"))
      ;; Test arc with negative offset (going backwards)
      (let [result (arc 0.5 (/ maths/PI 2) (- (/ maths/PI 2)) 8)
            shape (first result)
            points (:points shape)
            first-point (first points)
            last-point (last points)]
        ;; First point should be at angle PI/2: [0, radius] = [0, 0.5]
        (is (< (maths/abs (first first-point)) 0.1) "first point x should be near 0")
        (is (< (maths/abs (- (second first-point) 0.5)) 0.1) "first point y should be near 0.5")
        ;; Last point should be at angle 0: [radius, 0] = [0.5, 0]
        (is (< (maths/abs (- (first last-point) 0.5)) 0.1) "last point x should be near 0.5")
        (is (< (maths/abs (second last-point)) 0.1) "last point y should be near 0"))))