(ns patterning.sci-test
  (:require [clojure.test :refer :all]
            [patterning.dynamic :as dynamic]
            [patterning.groups :as groups]
            [patterning.color :as color]
            [patterning.layouts :as layouts]
            [patterning.library.std :as std]))

;; Create SCI context once for all tests
(def sci-ctx (dynamic/get-sci-context))

;; Helper function to evaluate code in SCI context
(defn eval-sci [code]
  (dynamic/evaluate-pattern sci-ctx code))

(deftest test-sci-context-created
  "Test that SCI context is successfully created"
  (testing "SCI context exists"
    (is (some? sci-ctx))))

(deftest test-core-functions-available
  "Test that core functions are available in SCI context"
  (testing "p-color function"
    (is (= [255 0 0 255] (eval-sci "(p-color 255 0 0)")))
    (is (= [255 0 0 128] (eval-sci "(p-color 255 0 0 128)"))))
  
  (testing "hex-color function"
    (is (= [255 0 0 255] (eval-sci "(hex-color \"#ff0000\")")))
    (is (= [255 0 0 221] (eval-sci "(hex-color \"ff0000dd\")"))))
  
  (testing "poly function"
    (let [result (eval-sci "(poly 0 0 0.5 6 {:fill (p-color 255 0 0)})")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "square function"
    (let [result (eval-sci "(square {:fill (p-color 0 255 0)})")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "stack function"
    (let [result (eval-sci "(stack (square) (poly 0 0 0.3 3))")]
      (is (seq? result))
      (is (groups/validate-group result))
      (is (> (count result) 1))))
  
  (testing "grid-layout function"
    (let [result (eval-sci "(grid-layout 2 (repeat (square)))")]
      (is (seq? result))
      (is (groups/validate-group result))
      (is (= 4 (count result)))))
  
  (testing "clock-rotate function"
    (let [result (eval-sci "(clock-rotate 4 (square))")]
      (is (seq? result))
      (is (groups/validate-group result))
      (is (= 4 (count result))))))

(deftest test-macro-available
  "Test that defcolor macro is available and works"
  (testing "defcolor with hex string"
    (eval-sci "(defcolor my-test-red \"#ff0000\")")
    (is (= [255 0 0 255] (eval-sci "my-test-red"))))
  
  (testing "defcolor with RGB numbers"
    (eval-sci "(defcolor my-test-blue 0 0 255)")
    (is (= [0 0 255 255] (eval-sci "my-test-blue"))))
  
  (testing "defcolor with RGBA numbers"
    (eval-sci "(defcolor my-test-transparent 255 0 0 128)")
    (is (= [255 0 0 128] (eval-sci "my-test-transparent"))))
  
  (testing "defcolor with hex string including alpha"
    (eval-sci "(defcolor my-test-alpha \"ff0000dd\")")
    (is (= [255 0 0 221] (eval-sci "my-test-alpha")))))

(deftest test-pattern-evaluation
  "Test evaluating complete patterns in SCI"
  (testing "Simple pattern with stack"
    (let [code "(stack 
                  (square {:fill (p-color 255 0 0)})
                  (poly 0 0 0.5 6 {:fill (p-color 0 255 0)}))"
          result (eval-sci code)]
      (is (groups/validate-group result))
      (is (> (count result) 0))))
  
  (testing "Pattern with grid layout"
    (let [code "(grid-layout 3 (repeat (square {:fill (p-color 100 100 200)})))"
          result (eval-sci code)]
      (is (groups/validate-group result))
      (is (= 9 (count result)))))
  
  (testing "Pattern using defcolor macro"
    (let [code "(do
                  (defcolor bg-color \"#ffffff\")
                  (defcolor shape-color 255 0 0)
                  (stack
                    (square {:fill bg-color})
                    (poly 0 0 0.4 5 {:fill shape-color})))"
          result (eval-sci code)]
      (is (groups/validate-group result))
      (is (> (count result) 0)))))

(deftest test-key-bindings-available
  "Test that important key-bindings are available"
  (testing "PI constant"
    (is (number? (eval-sci "PI")))
    (is (> (eval-sci "PI") 3)))
  
  (testing "->SShape constructor"
    (let [result (eval-sci "(->SShape {:fill (p-color 255 0 0)} [[0 0] [1 0] [1 1] [0 1]])")]
      (is (map? result))
      (is (contains? result :style))
      (is (contains? result :points))))
  
  (testing "translate function"
    (let [result (eval-sci "(translate 0.5 0.5 (square))")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "scale function"
    (let [result (eval-sci "(scale 0.5 (square))")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "rotate function"
    (let [result (eval-sci "(rotate 0.5 (square))")]
      (is (seq? result))
      (is (groups/validate-group result)))))

(deftest test-library-functions-available
  "Test that library functions are available"
  (testing "star function"
    (let [result (eval-sci "(star 0 0 [0.3 0.5] 5 {:fill (p-color 255 255 0)})")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "nangle function"
    (let [result (eval-sci "(nangle 0 0 0.5 7 {:stroke (p-color 0 0 255)})")]
      (is (seq? result))
      (is (groups/validate-group result))))
  
  (testing "diamond function"
    (let [result (eval-sci "(diamond {:fill (p-color 128 0 128)})")]
      (is (seq? result))
      (is (groups/validate-group result)))))

(deftest test-error-handling
  "Test that errors are handled appropriately"
  (testing "Invalid function call"
    (is (thrown? Exception (eval-sci "(non-existent-function 1 2 3)"))))
  
  (testing "Wrong number of arguments"
    (is (thrown? Exception (eval-sci "(p-color)")))))
