(ns patterning.dynamic
  (:require [sci.core :as sci]
            [patterning.groups :as p-groups]
            [patterning.layouts :as p-layouts]
            [patterning.sshapes :as p-sshapes]
            [patterning.color :as p-color]
            [patterning.maths :as p-maths]
            [patterning.view :as p-view]
            [patterning.library.std :as p-lib-std]
            [patterning.library.turtle :as p-lib-turtle]
            [patterning.library.l_systems :as p-lib-lsystems]
            [patterning.library.complex-elements :as p-lib-complex]
            [patterning.library.machines :as p-lib-machines]
            [patterning.library.symbols :as p-lib-symbols]
            [patterning.library.douat :as p-lib-douat]))

(defn get-sci-context
  "Create a shared SCI context for pattern evaluation.
   This function provides the same evaluation environment for both CLI and workbench."
  []
  (let [;; By including clojure.core/cljs.core, we get most standard functions automatically.
        ns-map {'p-groups (ns-publics 'patterning.groups)
                'p-layouts (ns-publics 'patterning.layouts)
                'p-sshapes (ns-publics 'patterning.sshapes)
                'p-color (ns-publics 'patterning.color)
                'p-maths (ns-publics 'patterning.maths)
                'p-view (ns-publics 'patterning.view)
                'p-lib-std (ns-publics 'patterning.library.std)
                'p-lib-turtle (ns-publics 'patterning.library.turtle)
                'p-lib-lsystems (ns-publics 'patterning.library.l_systems)
                'p-lib-complex (ns-publics 'patterning.library.complex-elements)
                'p-lib-machines (ns-publics 'patterning.library.machines)
                'p-lib-symbols (ns-publics 'patterning.library.symbols)
                'p-lib-douat (ns-publics 'patterning.library.douat)}
        bindings (apply merge (vals ns-map))
        ;; Add key functions that patterns expect - these are the main entry points
        key-bindings {'basic-turtle #'p-lib-turtle/basic-turtle
                     'l-system #'p-lib-lsystems/l-system
                     'PI p-maths/PI
                     'p-color #'p-color/p-color
                     'poly #'p-lib-std/poly
                     'stack #'p-layouts/stack
                     'clock-rotate #'p-layouts/clock-rotate
                     'grid-layout #'p-layouts/grid-layout
                     'checked-layout #'p-layouts/checked-layout
                     'framed #'p-layouts/framed
                     'aspect-ratio-framed #'p-layouts/aspect-ratio-framed
                     'aspect-ratio-frame #'p-layouts/aspect-ratio-frame
                     'inner-stretch #'p-layouts/inner-stretch
                     'inner-min #'p-layouts/inner-min
                     'inner-max #'p-layouts/inner-max
                     'q1-rot-group #'p-layouts/q1-rot-group
                     'q2-rot-group #'p-layouts/q2-rot-group
                     'q3-rot-group #'p-layouts/q3-rot-group
                     '->SShape #'p-sshapes/->SShape
                     'APattern #'p-groups/APattern
                     'star #'p-lib-std/star
                     'nangle #'p-lib-std/nangle
                     'spiral #'p-lib-std/spiral
                     'diamond #'p-lib-std/diamond
                     'horizontal-line #'p-lib-std/horizontal-line
                     'square #'p-lib-std/square
                     'drunk-line #'p-lib-std/drunk-line}
        all-bindings (merge bindings key-bindings)]
    (sci/init {:namespaces ns-map
               :bindings all-bindings
               ;; We still need to allow the bindings themselves, plus core macros and special forms.
               ;; Most clojure.core/cljs.core functions are now covered by the ns-map and all-bindings.
               :allow (set (concat (keys all-bindings)
                                  ['let 'let* 'def 'defn 'fn 'fn* 'if 'when 'cond 'case 'do '-> '->>
                                   'loop 'loop* 'clojure.core/loop* 'recur 'throw 'try 'catch 'finally
                                   'quote 'syntax-quote 'unquote 'unquote-splicing
                                   'cycle
                                   'clojure.core/seq-to-map-for-destructuring
                                   'take
                                   'repeat
                                   'rand-nth
                                   'rand-int
                                   '/
                                   'condp
                                   'clojure.core/get
                                   '-
                                   'map
                                   'conj
                                   '=
                                   '*
                                   '+
                                   'apply
                                   'range
                                   'new
                                   'mod
                                   'dissoc
                                   'into
                                   'iterate
                                   'clojure.core/str
                                   'list
                                   'shuffle
                                   'partial
                                   'remove
                                   'nth
                                   'keys
                                   'drop
                                   'filter
                                   'not
                                   'some
                                   'fn?
                                   'last
                                   'concat
                                   'or]))})))

(defn evaluate-pattern
  "Evaluate a pattern string using the shared SCI context.
   Returns the evaluated result."
  [sci-ctx pattern-code]
  (sci/eval-string pattern-code sci-ctx))

(defn evaluate-pattern-with-error-handling
  "Evaluate a pattern string with comprehensive error handling.
   Returns a map with :success, :result, and :error keys."
  [sci-ctx pattern-code]
  (try
    (let [result (evaluate-pattern sci-ctx pattern-code)]
      {:success true :result result :error nil})
    (catch Exception e
      {:success false :result nil :error e})))