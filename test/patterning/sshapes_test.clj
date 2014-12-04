(ns patterning.sshapes-test
  (:require [clojure.test :refer :all]
            [patterning.maths :as maths]
            [patterning.maths :refer [mol= molp=]]
            [patterning.sshapes :as sshapes]
            [patterning.sshapes :refer [mol=shapes ->SShape]]))

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
      (let [out1 [(maths/triangle-points (maths/triangle 0 0 0 1 1 1))
                  (maths/triangle-points (maths/triangle 0 1 1 1 1 0))]]
        (is (= (first out1)
               (maths/triangle-points (first (sshapes/to-triangles s1)))))
        (is (= (get out1 1)
               (maths/triangle-points (get (sshapes/to-triangles s1) 1 ))))
        ) )    ))
