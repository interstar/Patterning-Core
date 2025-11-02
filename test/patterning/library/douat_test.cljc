(ns patterning.library.douat-test
  (:require [clojure.test :refer :all]
            [patterning.library.douat :as douat]
            [patterning.sshapes :refer [->SShape]]
            [patterning.library.std :refer [square]]
            [patterning.color :refer [p-color]]
            [patterning.groups :refer [scale translate]]
            [patterning.layouts :refer [stack]]))

;; A simple shape to use for testing.
;; NOTE: We are NOT wrapping this in a vector, assuming the cleaner fix
;; for the original bug. If `p` is intended to be a collection, the
;; functions in `add-douat` (scale, translate) must be able to handle collections.
(def test-shape (->SShape {:fill (p-color 0)} [[0 0] [1 1]]))

(deftest douat-initialization-test
  (testing "Douat function should create the initial state correctly."
    (let [initial-state (douat/Douat test-shape)]
      (is (map? initial-state))
      (is (= test-shape (:p initial-state)))
      (is (empty? (:ps initial-state))))))

(deftest add-douat-accumulation-test
  (testing "add-douat should accumulate patterns until the limit (3) is reached."
    (let [state-0 (douat/Douat test-shape)
          state-2 (-> state-0 douat/A douat/A)]
      (is (= 2 (count (:ps state-2))) "Should accumulate 2 patterns before reset")
      (is (= test-shape (:p state-2)) "Original pattern should be preserved"))))

(deftest add-douat-recursion-test
  (testing "add-douat should reset and create a new pattern after 3 elements."
    (let [state-2 {:p test-shape
                   :ps (repeat 2 test-shape)
                   :depth 0}
          state-3 (douat/A state-2)]
      (is (= 3 (count (:ps state-3))) "Should have 3 elements before reset")
      (let [state-4 (douat/A state-3)]
        (is (empty? (:ps state-4)) "The :ps list should be reset to empty after 3 elements.")
        (is (not= test-shape (:p state-4)) "A new pattern should be generated for :p.")
        (is (coll? (:p state-4)) "The new pattern should be a collection of shapes.")))))

(deftest truchet-var-test
  (testing "The truchet function should be defined and return a collection."
    (is (some? douat/truchet))
    (is (fn? douat/truchet))
    (is (coll? (douat/truchet)))))
