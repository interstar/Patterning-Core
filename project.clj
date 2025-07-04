(defproject com.alchemyislands/patterning "0.5.6-SNAPSHOT"
  :description "Generating Patterns with Clojure"
  :url "http://alchemyislands.com/"
  :license {:name "Gnu Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl.html"}

  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/clojurescript "1.11.132"]
                 [metosin/malli "0.14.0"]]

  :plugins [[lein-cljsbuild "1.1.8"]
            [lein-localrepo "0.4.0"]]

  :source-paths ["src/clj" "src/cljc"]

  :cljsbuild {:builds [;; Main library build
                       {:id "main"
                        :source-paths ["src/cljc" "src/cljs"]
                        :compiler {:output-to "browser-based/js/main.js"
                                   :optimizations :simple
                                   :pretty-print true}}
                       ;; Pattern compilation build
                       {:id "pattern"
                        :source-paths ["src/cljc" "src/cljs" "NFTmaker/patterns"]
                        :compiler {:output-to ~(str "NFTmaker/dist/patterns/" (or (System/getenv "PATTERN_NAME") "default") "/" (or (System/getenv "PATTERN_NAME") "default") ".js")
                                   :optimizations :simple
                                   :output-dir ~(str "NFTmaker/dist/patterns/" (or (System/getenv "PATTERN_NAME") "default") "/build")
                                   :main ~(or (System/getenv "PATTERN_NAME") "default")
                                   :preloads ['patterning.canvasview]
                                   :source-map false
                                   :closure-defines {"goog.DEBUG" false}
                                   :elide-asserts true}}]}

  :aot [patterning.core]
  :main patterning.core)
