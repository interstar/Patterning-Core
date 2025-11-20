(ns patterning.library.std-test
  (:require [clojure.test :refer :all]          
            [patterning.maths :as maths]
            [patterning.groups :refer [mol=]]
            [patterning.library.std :refer [poly bez-curve]]
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
        (is (mol=shapes expected-points (:points (first result))))))))
