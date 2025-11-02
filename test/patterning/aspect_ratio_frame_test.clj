(ns patterning.aspect-ratio-frame-test
  (:require [clojure.test :refer :all]
            [patterning.sshapes :refer [->SShape]]
            [patterning.groups :refer [APattern empty-pattern validate-group explain-group]]
            [patterning.layouts :refer [aspect-ratio-frame aspect-ratio-framed inner-stretch inner-min inner-max]]
            [patterning.color :refer [p-color]]))

(defn create-test-corner-pattern []
  (APattern (->SShape {:fill (p-color 200 100 100) 
                       :stroke (p-color 100 50 50) 
                       :stroke-weight 2}
                      [[0 0] [0.3 0] [0.2 0.1] [0.2 0.2] [0 0.2] [0 0]])))

(defn create-test-edge-pattern []
  (APattern (->SShape {:fill (p-color 100 200 100) 
                       :stroke (p-color 50 100 50) 
                       :stroke-weight 2}
                      [[-0.2 0] [0.2 0] [0.1 0.05] [0.1 -0.05] [-0.2 0]])))

(deftest test-basic-pattern-validation
  (testing "Basic patterns should validate correctly"
    (is (validate-group (create-test-corner-pattern)))
    (is (validate-group (create-test-edge-pattern)))
    (is (validate-group (empty-pattern)))))

(deftest test-aspect-ratio-frame
  (testing "aspect-ratio-frame should produce valid groups"
    (let [corner-pattern (create-test-corner-pattern)
          edge-pattern (create-test-edge-pattern)
          result (aspect-ratio-frame 4 4 corner-pattern edge-pattern)]
      (is (validate-group result) 
          (str "Result should be valid group: " (explain-group result)))
      (is (pos? (count result)) "Result should not be empty")
      ;; Check that styles are preserved
      (doseq [sshape result]
        (when (seq (:points sshape))
          (is (map? (:style sshape)) "Style should be a map")
          (is (not= (:style sshape) ["invalid type"]) "Style should not be corrupted"))))))

(deftest test-aspect-ratio-framed
  (testing "aspect-ratio-framed should produce valid groups with inner content"
    (let [corner-pattern (create-test-corner-pattern)
          edge-pattern (create-test-edge-pattern)
          inner-content (APattern (->SShape {:fill (p-color 100 100 200) 
                                             :stroke (p-color 50 50 100) 
                                             :stroke-weight 2}
                                            [[0 0] [0.4 0] [0.4 0.4] [0 0.4] [0 0]]))
          result (aspect-ratio-framed 4 4 corner-pattern edge-pattern inner-content inner-min)]
      (is (validate-group result) 
          (str "Result should be valid group: " (explain-group result)))
      (is (pos? (count result)) "Result should not be empty")
      ;; Check that styles are preserved
      (doseq [sshape result]
        (when (seq (:points sshape))
          (is (map? (:style sshape)) "Style should be a map")
          (is (not= (:style sshape) ["invalid type"]) "Style should not be corrupted"))))))

(deftest test-fit-functions
  (testing "Fit functions should produce valid groups"
    (let [inner-content (APattern (->SShape {:fill (p-color 100 100 200) 
                                             :stroke (p-color 50 50 100) 
                                             :stroke-weight 2}
                                            [[0 0] [0.4 0] [0.4 0.4] [0 0.4] [0 0]]))
          stretch-result (inner-stretch 2 2 inner-content)
          min-result (inner-min 2 2 inner-content)
          max-result (inner-max 2 2 inner-content)]
      (is (validate-group stretch-result) 
          (str "inner-stretch should produce valid group: " (explain-group stretch-result)))
      (is (validate-group min-result) 
          (str "inner-min should produce valid group: " (explain-group min-result)))
      (is (validate-group max-result) 
          (str "inner-max should produce valid group: " (explain-group max-result))))))

(deftest test-invalid-inputs
  (testing "Should throw exceptions for invalid dimensions"
    (let [corner-pattern (create-test-corner-pattern)
          edge-pattern (create-test-edge-pattern)]
      ;; Test invalid dimensions that would cause calculation errors
      (is (thrown? Exception (aspect-ratio-frame 0 4 corner-pattern edge-pattern)))
      (is (thrown? Exception (aspect-ratio-frame 4 -1 corner-pattern edge-pattern))))))
