(defproject com.alchemyislands/patterning_quil "0.1.0-SNAPSHOT"
  :description "Quil Wrapper for Patterning library"
  :url "http://alchemyislands.com"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.11.0"]
                 [org.clojure/math.numeric-tower "0.0.4"]
                 [quil "4.3.1563"]
                 [com.alchemyislands/patterning "0.5.6-SNAPSHOT"]]
  
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.4.4"]
                                 [cider/cider-nrepl "0.43.1"]]
                   :source-paths ["src" "dev"]
                   :repl-options {:port 46657}}}
  
  :aot [patterning_quil.core patterning_quil.examples patterning_quil.quilview]
  :main patterning_quil.core)
