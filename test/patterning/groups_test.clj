(ns patterning.groups-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.sshapes :as sshapes]
            [patterning.groups :as groups]

            [patterning.color :refer [p-color]]
            [malli.core :as m]))

(deftest more-or-less-equal-groups
  (let [g1 (groups/APattern (sshapes/->SShape {} [[0 0] [1 1]] ))
        g2 (groups/APattern (sshapes/->SShape {} [[0 0] [2 2]]))]
    (testing "more or less equal patterns"
      (is (true? (groups/mol= (groups/empty-pattern) (groups/empty-pattern))))
      (is (false? (groups/mol= (groups/empty-pattern) g1) ))
      (is (false? (groups/mol= g1 g2)))
      (is (true? (groups/mol= g1 g1)))
      )
    )
  )


(deftest simple-transforms
  (let [g1 (groups/APattern (sshapes/->SShape {} [[0 0] [1 1]] ) (sshapes/->SShape {} [[-1 -1]] ) )
        g2 (groups/APattern (sshapes/->SShape {} [[1 1] [2 2]] ) (sshapes/->SShape {} [[0 0]] ) )
        g3 (groups/APattern (sshapes/->SShape {} [[0.5 0.5] [1 1]]) (sshapes/->SShape {} [[0 0]]))
        ]
    (testing "translate"
      (is (groups/mol= (groups/translate 1 1 g1) g2)))
    (testing "scale"
      (is (groups/mol= (groups/scale 0.5 g2) g3)))
    )

  )

(deftest flatten-pattern
  (let [s1 (sshapes/->SShape {} [[0 0] [1 1]])
        s2 (sshapes/->SShape {} [[2 2] [3 3]])
        g1 (groups/APattern s1)
        g2 (groups/APattern s1 s2)]
    (testing "extracting points"
      (is (= (groups/extract-points s1)
             [[0 0] [1 1]]  ))
      (is (= (groups/extract-points  (groups/flatten-pattern {:stroke 1} g1))
             [[0 0] [1 1]]))
      (is (= (groups/extract-points (groups/flatten-pattern {:stroke 2} g2))
             [[0 0] [1 1] [2 2] [3 3]]) )
      )))


(deftest process
  (let [s1 (sshapes/->SShape {} [[0.2 0.2] [0.4 -0.4]])
        ]
    (testing "reframing"
      (is (= (sshapes/top s1) -0.4))
      (is (= (sshapes/leftmost s1) 0.2))
      (is (= (sshapes/bottom s1) 0.2))
      (is (= (sshapes/rightmost s1) 0.4))

      (is (mol= (sshapes/width s1) 0.2))
      (is (mol= (sshapes/height s1) 0.6))
      (is (mol= (groups/reframe-scaler s1) (/ 2.0 0.6)))

     )))

(deftest filtering
  (let [inside? (fn [[ x y]] (< (+ (* y y) (* x x)) 4 ))
        points [[0 0] [1 1] [2 2] [3 3]]
        gp (groups/APattern (sshapes/->SShape {} points) (sshapes/->SShape {} points))
        gp2 (groups/APattern {:style {} :points [[(- 1) (- 1)] [0 0]]} {:style {} :points [[0 0] [2 2] [4 4]]})
        ]
    (testing "filter points from shape, sshape and groups. filtering sshapes from group"
      (is (= (sshapes/filter-shape inside? []) []))
      (is (= (sshapes/filter-shape inside? points) [[0 0] [1 1]]))
      (is (= (sshapes/ss-filter inside? (sshapes/->SShape {} points)) (sshapes/->SShape {} [[0 0] [1 1]])))
      (is (= (groups/filter-pattern inside? gp) [  (sshapes/->SShape {} [[0 0] [1 1]])
                                                 (sshapes/->SShape {} [[0 0] [1 1]])]))
      (is (= (groups/filter-sshapes-in-pattern inside? gp2)
             [{:style {} :points [[-1 -1] [0 0]]}] ))
      )))

(deftest clipping
  (let [inside? (fn [[ x y]] (< (+ (* y y) (* x x)) 4 ))
        s (sshapes/->SShape {} [[0 0] [1 1] [2 2] [1 1] [0 0]])
        g [s s]]
    (testing "clipping. always returns a pattern (sequence of sshapes)"
      (is (= (groups/clip-sshape inside? s)
             [{:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]}]))
      (is (= (groups/clip inside? g)
             [{:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]}
              {:style {} :points [[0 0] [1 1]]} {:style {} :points [[1 1] [0 0]]} ]) )
      )

    ))

(deftest group-validation-test
  (testing "valid group validation"
    (let [valid-group (groups/APattern (sshapes/->SShape {} [[0 0] [1 0] [0 1]]))]
      (is (m/validate groups/Group valid-group))
      (is (nil? (groups/explain-group valid-group)))))

  (testing "invalid group validation"
    (let [invalid-group (groups/APattern (sshapes/->SShape {} nil))]
      (is (not (m/validate groups/Group invalid-group)))
      (is (contains? (groups/explain-group invalid-group) :points)))))

(deftest sshape-validation-test
  (testing "valid sshape validation"
    (let [valid-sshape (sshapes/->SShape {} [[0 0] [1 0] [0 1]])]
      (is (m/validate groups/SShape valid-sshape))
      (is (nil? (groups/explain-sshape valid-sshape)))))

  (testing "invalid sshape validation"
    (let [invalid-sshape (sshapes/->SShape {} nil)]
      (is (not (m/validate groups/SShape invalid-sshape)))
      (is (contains? (groups/explain-sshape invalid-sshape) :points)))))
