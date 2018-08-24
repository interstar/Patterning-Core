(ns patterning.sshapes-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.color :refer [p-color]]
            [patterning.sshapes :as sshapes]
            [patterning.sshapes :refer [mol=shapes ->SShape]]
            [clojure.spec.alpha :as s]
            [clojure.spec.test.alpha :as stest]
            ))

(deftest mol-equal-shapes
  (testing "more or less equal shapes"
    (is (true? (mol=shapes [] [])))
    (is (false? (mol=shapes [[0 0]] [])))
    (is (true? (mol=shapes [[0 0] [1 1] [2 2]] [[0 0] [1 1] [2 2]])))
    (is (true? (mol=shapes [[0 0] [1 1] [2 2]] [[0 0] [1 1] [2 2.0000001]])))
    (is (false? (mol=shapes [[0 0] [1 1] [2 2]] [[0 0] [1 1] [2 2.1]])))
    ))

(deftest mol-equal-sshapes
  (testing "more or less equal sshapes"
    (is (true? (sshapes/mol= sshapes/empty-sshape sshapes/empty-sshape)))
    (is (false? (sshapes/mol= (->SShape {} []) (->SShape {:key 1} []) )))
    (is (false? (sshapes/mol= (->SShape {} [[0 0]]) (->SShape {} [[0 1]]))))
    (is (true? (sshapes/mol= (->SShape {:key 1} [[0 0]]) (->SShape {:key 1} [[0 0]]))))
   )  )

(stest/instrument `sshapes/scale)
(stest/instrument `sshapes/translate)
(stest/instrument `sshapes/rotate)

(deftest sshape-transforms
  (testing "Sshape transforms"
    (let [nulls (sshapes/->SShape {} [])
          ss1 (sshapes/->SShape {:fill (p-color 10 10 40)} [[0 0]
                                                            [1 1] [0 0]])
          ss1scaled (sshapes/->SShape {:fill (p-color 10 10 40)}
                                      [[0 0]           [0.5 0.5] [0 0]])
          ss1shifted (sshapes/->SShape {:fill (p-color 10 10 40)}
                                       [[1 1] [2 2] [1 1]] )
          ss1rotated (sshapes/->SShape {:fill (p-color 10 10 40)}
                                       [[0 0] [-1 1] [0 0]])]
      (is (s/valid? ::sshapes/SShape nulls))
      (is (s/valid? ::sshapes/SShape ss1))
      (is (sshapes/mol= (sshapes/scale 0.5 ss1) ss1scaled))
      (is (sshapes/mol= (sshapes/translate 1 1 ss1) ss1shifted))
      (is (sshapes/mol= (sshapes/rotate maths/half-PI ss1) ss1rotated))
      )
    ))

(deftest line-to-segments
  (testing "line-to-segments"
    (is (= (maths/line-to-segments [])
           []))
    (is (= (maths/line-to-segments [[0 0]])
           []))
    (is (= (maths/line-to-segments [[0 0] [1 1]])
           [[ [0 0] [1 1] ]]))
    (is (= (maths/line-to-segments [[0 0] [1 1] [2 2]])
           [[ [0 0] [1 1]] [[1 1] [2 2]]]))
    (is (= (maths/line-to-segments [[0 0] [1 1] [2 2] [3 3]])
           [[ [0 0] [1 1]] [[1 1] [2 2]] [[2 2] [3 3]]]))
    ))

(deftest flatten-point-list
  (let [ss (sshapes/->SShape {} [[0 0] [1 1] [2 2]])  ]
    (testing "flatten-point-list"
      (is (= (sshapes/flat-point-list ss)
             (list 0 0 1 1 2 2))))))

(deftest basic-points
  (testing "basic point functions"
    (is (= (maths/diff [3 3] [5 5]) [2 2]))
    (is (= (maths/add-points [1 1] [2 2]) [3 3]))
    (is (= (maths/magnitude [3 4]) 5.0 ))
    (is (= (maths/distance [0 0] [0 10]) 10.0))
    (is (maths/p-eq (maths/unit [10 0]) [1 0] ))
    ))

(stest/instrument `sshapes/is-ear)

(deftest as-triangles
  (let [s1 (sshapes/->SShape {} [[0 0] [0 1] [1 1] [1 0]])
        tl (sshapes/triple-list (:points s1))
        tls (sshapes/triangles-in-sshape s1)
        s2 (sshapes/->SShape {} [[0 0] [0 1] [1 1] [0.5 0.8]])
        tls2 (sshapes/triangles-list (:points s2))
        ;ears (sshapes/ears s1)
       ]
    (testing "generating a list of three point triples from a shape"
      (is (= (nth tl 0)
             [[0 0] [0 1] [1 1]]))
      (is (= (nth tl 1)
             [[0 1] [1 1] [1 0]]))
      (is (= (nth tl 2)
             [[1 1] [1 0] [0 0]] )))
    (testing "generating a list of triangles from a sshape"
      (is (= (nth tls 1)
             (maths/triangle 0 1 1 1 1 0)))
      )
    (testing "generating list of 'ears' (triangles without other points inside"
      (let [t (first tls) t2 (first tls2)]
        (is (= true (sshapes/is-ear s1 t)))
        (is (= [[0 0] [0 1] [1 1]]
               (maths/triangle-points (first tls2) )))
        (is (= false (sshapes/is-ear s2 t2) ))))

    (testing "trianglization"
      (let [tp1 (maths/triangle-points (maths/triangle 0 0 0 1 1 1))
            t2 (maths/triangle 0 0 1 1 1 0)
            s1ts (sshapes/to-triangles s1)
            ]
        (is (s/valid? ::sshapes/triangle-points tp1))
        (is (s/valid? ::sshapes/triangle-points (maths/triangle-points t2)))

        (is (= tp1
               (maths/triangle-points (first s1ts))))
        (is (maths/tri= t2 (second s1ts)))
        ) )    ))
