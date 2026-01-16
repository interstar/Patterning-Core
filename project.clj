(defproject com.alchemyislands/patterning "0.7.4"
  :description "Generating Patterns with Clojure"
  :url "http://alchemyislands.com/"
  :license {:name "Gnu Lesser Public License"
            :url "https://www.gnu.org/licenses/lgpl.html"}
  :scm {:name "git"
        :url "https://github.com/interstar/Patterning-Core"}
  :deploy-repositories [["clojars" {:url "https://clojars.org/repo"
                                     :username :env/CLOJARS_USERNAME
                                     :password :env/CLOJARS_PASSWORD
                                     :sign-releases false}]]

  :test-selectors {:default (constantly true)
                   :namespace :namespace}

  :dependencies [[org.clojure/clojure "1.11.3"]
                 [org.clojure/clojurescript "1.11.132"]
                 [org.clojure/data.xml "0.2.0-alpha8"]
                 [metosin/malli "0.14.0"]
                 [org.babashka/sci "0.10.49"]]

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
                                   :elide-asserts true}}
                       ;; Presentation pattern build
                       {:id "presentation-pattern"
                        :source-paths ["src/cljc" "src/cljs" "presentation/patterns"]
                        :compiler {:output-to "presentation/slides/{{pattern_name}}.js"
                                   :output-dir "presentation/slides/build"
                                   :main ~(symbol (or (System/getenv "PATTERN_NAME") 'default-placeholder))
                                   :optimizations :simple
                                   :pretty-print true
                                   :output-wrapper false
                                   :preloads ['patterning.canvasview]
                                   :source-map "presentation/slides/{{pattern_name}}.js.map"}}
                       ;; Workbench main build
                       {:id "workbench"
                        :source-paths ["src/cljc" "src/cljs" "workbench"]
                        :compiler {:output-to "workbench/workbench.js"
                                   :output-dir "workbench/out"
                                   :asset-path "out"
                                   :main workbench
                                   :optimizations :simple
                                   :source-map "workbench/workbench.js.map"}}
                       ;; Workbench worker build
                       {:id "workbench-worker"
                        :source-paths ["src/cljc" "src/cljs" "workbench"]
                        :compiler {:output-to "workbench/worker.js"
                                   :output-dir "workbench/out-worker"
                                   :main worker
                                   :optimizations :simple
                                   :source-map "workbench/worker.js.map"}}]}

  ;; Profile for app-development builds (optional - only active when app-development/ exists)
  ;; This allows project.clj to be in git without requiring app-development to exist
  :profiles {:app-dev {:cljsbuild {:builds [{:id "app-dev"
                                              :source-paths ["src/cljc" "src/cljs" "app-development/source"]
                                              :compiler {:output-to ~(str "app-development/dist/" (or (System/getenv "APP_NAME") "default") ".js")
                                                         :optimizations :advanced
                                                         :output-dir ~(str "app-development/dist/" (or (System/getenv "APP_NAME") "default") "/build")
                                                         :main ~(symbol (or (System/getenv "APP_NAME") "default"))
                                                         :preloads ['patterning.view 'patterning.canvasview]
                                                         :source-map false
                                                         :closure-defines {"goog.DEBUG" false}
                                                         :elide-asserts true
                                                         :pretty-print false}}]}}}

  :aot [patterning.core patterning.cli]
  :main patterning.core
  
  ;; Aliases for convenient building
  :aliases {"build-workbench" ["do" ["cljsbuild" "once" "workbench"] ["cljsbuild" "once" "workbench-worker"]]})
