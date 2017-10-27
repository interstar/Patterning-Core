(defproject com.alchemyislands/patterning "0.5.3-SNAPSHOT"
  :description "Generating Patterns with Clojure"
  :url "http://alchemyislands.com/"
  :license {:name "Gnu Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.28"]
                ]

  :plugins [[lein-cljsbuild "1.0.6"]
            [lein-localrepo "0.4.0"]
            ]

  :source-paths ["src/clj" "src/cljc" ]

  :cljsbuild {:builds [{
                        :source-paths ["src/cljc" ]
                        :compiler {
                                   :output-to "browser-based/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true }
                        } ]}

  :aot [patterning.core]
  :main patterning.core)
