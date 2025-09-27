(ns patterning.maths-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [malli.core :as m]))

(deftest triangle-validation-test
  (testing "valid triangle validation"
    (let [valid-triangle (maths/triangle 0 0 1 0 0 1)]
      (is (maths/validate-triangle valid-triangle))
      (is (nil? (maths/explain-triangle valid-triangle)))))

  (testing "invalid triangle validation"
    (let [invalid-triangle {}]  ; Missing everything
      (is (not (maths/validate-triangle invalid-triangle)))
      (is (contains? (maths/explain-triangle invalid-triangle) :ax))
      (is (contains? (maths/explain-triangle invalid-triangle) :ay))
      (is (contains? (maths/explain-triangle invalid-triangle) :bx))
      (is (contains? (maths/explain-triangle invalid-triangle) :by))
      (is (contains? (maths/explain-triangle invalid-triangle) :cx))
      (is (contains? (maths/explain-triangle invalid-triangle) :cy)))))

(deftest triangle-area-test
  (testing "triangle area calculation"
    (let [t1 (maths/triangle [0 0] [1 0] [0 1])  ; Right triangle
          t2 (maths/triangle [0 0] [2 0] [0 2])] ; Larger right triangle
      (is (maths/mol= (maths/area t1) 0.5))
      (is (maths/mol= (maths/area t2) 2.0)))))

(deftest triangle-contains-point-test
  (testing "point containment in triangle"
    (let [t (maths/triangle [0 0] [1 0] [0 1])]  ; Right triangle
      (is (maths/contains-point t [0.25 0.25]))  ; Inside
      (is (maths/contains-point t [0 0]))        ; On vertex
      (is (maths/contains-point t [0.5 0]))      ; On edge
      (is (not (maths/contains-point t [1 1])))  ; Outside
      (is (not (maths/contains-point t [-1 -1]))))))

(deftest triangle-points-test
  (testing "triangle points extraction"
    (let [t (maths/triangle [0 0] [1 0] [0 1])
          points (maths/triangle-points t)]
      (is (= (count points) 3))
      (is (maths/molp= (first points) [0 0]))
      (is (maths/molp= (second points) [1 0]))
      (is (maths/molp= (nth points 2) [0 1]))))) 