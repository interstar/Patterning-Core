(defproject com.alchemyislands/patterning "0.5.6-SNAPSHOT"
  :description "Generating Patterns with Clojure"
  :url "http://alchemyislands.com/"
  :license {:name "Gnu Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl.html"}

  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/clojurescript "1.11.132"]]

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-localrepo "0.4.0"]]

  :source-paths ["src/clj" "src/cljc"]

  :cljsbuild {:builds [;; Main library build
                       {:id "main"
                        :source-paths ["src/cljc"]
                        :compiler {:output-to "browser-based/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true}}
                       ;; Pattern compilation build
                       {:id "pattern"
                        :source-paths ["src/cljc" "patterns"]
                        :compiler {:output-to ~(str "dist/patterns/" (or (System/getenv "PATTERN_NAME") "default") ".js")
                                   :optimizations :advanced
                                   :pretty-print false
                                   :output-dir "target/pattern-build"
                                   :main ~(or (System/getenv "PATTERN_NAME") "default")
                                   :externs ["externs.js"]
                                   :closure-defines {"triangles.PATTERN_NAME" "triangles"}
                                   :target :nodejs}}]}

  :aot [patterning.core]
  :main patterning.core)
