(ns patterning.strings
  #?(:cljs (:require [goog.string :as gstring] [goog.string.format :as gformat]))
  )

;; String library to factor out Clojure / ClojureScript differences
(defn gen-format [& args] #?(:clj (apply format args) :cljs (apply gstring/format args)) )

(defn lower-case
  [s]
  #?(:clj (clojure.string/lower-case s)
     :cljs (.toLowerCase s)))

(defn strip-leading-hash
  [s]
  #?(:clj (clojure.string/replace s #"^#" "")
     :cljs (.replace s #"^#" "")))
