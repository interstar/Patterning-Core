(ns patterning.color-test
  (:require [clojure.test :refer :all]
            [patterning.color :as color]
))

(deftest color-svg
  (testing "color for svg"
    (is (= (color/stroke-gen (color/p-color 100 200 150 150))
           "stroke='rgb(100,200,150)' stroke-opacity='0.59' "))
    (is (= (color/fill-gen (color/p-color 200 200 100 50))
           "fill='rgb(200,200,100)' fill-opacity='0.20' ")) ))

(deftest color-stuff
  (let []
    (testing "color sequence"
      (is (= (color/color-seq ["red" "blue"])
             (list {:stroke "red"} {:stroke "blue"}) ))
      )
    (testing "color to fill"
      (is (= (color/color-to-fill {:stroke "red" :other "blah"})
             {:stroke "red" :fill "red" :other "blah"} ) ))

    (testing "setup-colors"
      (is (= ( ( color/edge-col "red") {:stroke "green"})
             {:stroke "red" :fill "green"} ))

      (is (= (color/setup-colors ["red" "blue"] "black")
             (list {:fill "red" :stroke "black"} {:fill "blue" :stroke "black"}) )))
    ))
