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
  (testing "add-douat should accumulate patterns until the limit is reached."
    (let [state-0 (douat/Douat test-shape)
          state-5 (-> state-0 douat/A douat/A douat/A douat/A douat/A)]
      (is (= 5 (count (:ps state-5))))
      (is (= test-shape (:p state-5))))))

(deftest add-douat-recursion-test
  (testing "add-douat should reset and create a new pattern after 5 elements."
    (let [state-5 {:p test-shape
                   :ps (repeat 5 test-shape)}
          state-6 (douat/A state-5)]
      (is (empty? (:ps state-6)) "The :ps list should be reset to empty.")
      (is (not= test-shape (:p state-6)) "A new pattern should be generated for :p.")
      (is (coll? (:p state-6)) "The new pattern should be a collection of shapes."))))

(deftest truchet-var-test
  (testing "The truchet var should be defined and be a collection."
    (is (some? douat/truchet))
    (is (coll? douat/truchet))))
