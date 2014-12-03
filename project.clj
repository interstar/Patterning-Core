(defproject com.alchemyislands/patterning "0.3.0-SNAPSHOT"
  :description "Generating Patterns with Clojure"
  :url "http://alchemyislands.com/"
  :license {:name "Gnu Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl.html"}

  :dependencies [[org.clojure/clojure "1.6.0"]]

  :plugins [[com.keminglabs/cljx "0.4.0"]
            [lein-cljsbuild "1.0.3"]
            [lein-localrepo "0.4.0"]
            ]

  :cljx {:builds [{:source-paths ["cljx-src"]
                 :output-path "target/classes"
                 :rules :clj}

                {:source-paths ["cljx-src"]
                 :output-path "src-cljs"
                 :rules :cljs}]}

  :cljsbuild {:builds [{
                        :source-paths ["target/classes" "src-cljs" ]
                        :compiler {
                                   :output-to "browser-based/js/main.js"
                                   :optimizations :whitespace
                                   :pretty-print true }
                        } ]}

  :hooks [cljx.hooks leiningen.cljsbuild]


  :aot [patterning.core]
  :main patterning.core)
