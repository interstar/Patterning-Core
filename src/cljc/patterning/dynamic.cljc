(ns patterning.dynamic
  (:require [sci.core :as sci]
            [patterning.groups :as p-groups]
            [patterning.layouts :as p-layouts]
            [patterning.sshapes :as p-sshapes]
            [patterning.color :as p-color]
            [patterning.maths :as p-maths]
            [patterning.library.std :as p-lib-std]
            [patterning.library.turtle :as p-lib-turtle]
            [patterning.library.l_systems :as p-lib-lsystems]
            [patterning.library.complex-elements :as p-lib-complex]
            [patterning.library.machines :as p-lib-machines]
            [patterning.library.symbols :as p-lib-symbols]
            [patterning.library.douat :as p-lib-douat]))

  (defn get-core-allow-list
  "Returns the core Clojure/ClojureScript functions that need to be explicitly allowed in SCI.
   This centralizes the :allow list to avoid duplication."
  []
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
   'or
   ;; Additional functions needed for threading macros
   'assoc
   'get
   'first
   'rest
   'next
   'seq
   'cons
   'empty?
   'count
   'vec
   'set
   'hash-map
   'array-map
   'vector
   'list*
   'seq?
   'coll?
   'associative?
   'sequential?
   'counted?
   'reduced?
   'reduced
   ;; More fundamental functions for threading
   'clojure.core/assoc
   'clojure.core/get
   'clojure.core/first
   'clojure.core/rest
   'clojure.core/next
   'clojure.core/seq
   'clojure.core/cons
   'clojure.core/empty?
   'clojure.core/count
   'clojure.core/vec
   'clojure.core/set
   'clojure.core/hash-map
   'clojure.core/array-map
   'clojure.core/vector
   'clojure.core/list*
   'clojure.core/seq?
   'clojure.core/coll?
   'clojure.core/associative?
   'clojure.core/sequential?
   'clojure.core/counted?
   'clojure.core/reduced?
   'clojure.core/reduced
   ;; Additional functions for threading macro expansion
   'clojure.core/->>
   'clojure.core/->
   'clojure.core/let
   'clojure.core/let*
   'clojure.core/def
   'clojure.core/defn
   'clojure.core/fn
   'clojure.core/fn*
   'clojure.core/if
   'clojure.core/when
   'clojure.core/cond
   'clojure.core/case
   'clojure.core/do
   'clojure.core/loop
   'clojure.core/loop*
   'clojure.core/recur
   'clojure.core/throw
   'clojure.core/try
   'clojure.core/catch
   'clojure.core/finally
   'clojure.core/quote
   'clojure.core/syntax-quote
   'clojure.core/unquote
   'clojure.core/unquote-splicing
   'clojure.core/cycle
   'clojure.core/take
   'clojure.core/repeat
   'clojure.core/rand-nth
   'clojure.core/rand-int
   'clojure.core//
   'clojure.core/condp
   'clojure.core/get
   'clojure.core/-
   'clojure.core/map
   'clojure.core/conj
   'clojure.core/=
   'clojure.core/*
   'clojure.core/+
   'clojure.core/apply
   'clojure.core/range
   'clojure.core/new
   'clojure.core/mod
   'clojure.core/dissoc
   'clojure.core/into
   'clojure.core/iterate
   'clojure.core/str
   'clojure.core/list
   'clojure.core/shuffle
   'clojure.core/partial
   'clojure.core/remove
   'clojure.core/nth
   'clojure.core/keys
   'clojure.core/drop
   'clojure.core/filter
   'clojure.core/not
   'clojure.core/some
   'clojure.core/fn?
   'clojure.core/last
   'clojure.core/concat
   'clojure.core/or
   ;; Additional functions that might be needed
   'clojure.core/identity
   'clojure.core/constantly
   'clojure.core/comp
   'clojure.core/comp
   'clojure.core/partial
   'clojure.core/complement
   'clojure.core/always
   'clojure.core/never
   'clojure.core/true?
   'clojure.core/false?
   'clojure.core/nil?
   'clojure.core/zero?
   'clojure.core/pos?
   'clojure.core/neg?
   'clojure.core/even?
   'clojure.core/odd?
   'clojure.core/inc
   'clojure.core/dec
   'clojure.core/max
   'clojure.core/min
   'clojure.core/quot
   'clojure.core/rem
   'clojure.core/bit-and
   'clojure.core/bit-or
   'clojure.core/bit-xor
   'clojure.core/bit-not
   'clojure.core/bit-shift-left
   'clojure.core/bit-shift-right
   'clojure.core/bit-shift-right-logical
   'clojure.core/bit-and-not
   'clojure.core/bit-clear
   'clojure.core/bit-flip
   'clojure.core/bit-set
   'clojure.core/bit-test
   'clojure.core/bit-shift-left
   'clojure.core/bit-shift-right
   'clojure.core/bit-shift-right-logical
   'clojure.core/bit-and-not
   'clojure.core/bit-clear
   'clojure.core/bit-flip
   'clojure.core/bit-set
   'clojure.core/bit-test
   ;; Additional core functions that might be needed for map construction
   'clojure.core/merge
   'clojure.core/merge-with
   'clojure.core/select-keys
   'clojure.core/update
   'clojure.core/update-in
   'clojure.core/assoc-in
   'clojure.core/dissoc
   'clojure.core/merge
   'clojure.core/zipmap
   'clojure.core/group-by
   'clojure.core/partition-by
   'clojure.core/sort
   'clojure.core/sort-by
   'clojure.core/compare
   'clojure.core/compare-and-set
   'clojure.core/reset!
   'clojure.core/swap!
   'clojure.core/alter-meta!
   'clojure.core/reset-meta!
   'clojure.core/vary-meta
   'clojure.core/with-meta
   'clojure.core/meta
   'clojure.core/deref
   'clojure.core/ref
   'clojure.core/atom
   'clojure.core/volatile!
   'clojure.core/vswap!
   'clojure.core/vreset!
   'clojure.core/vderef])


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

(defn get-sci-context-config
  "Shared configuration for SCI contexts across platforms.
   Returns the common parts that both Clojure and ClojureScript can use."
  []
  (let [;; Create SCI namespaces for each patterning namespace
        p-groups-ns (sci/create-ns 'p-groups nil)
        p-layouts-ns (sci/create-ns 'p-layouts nil)
        p-sshapes-ns (sci/create-ns 'p-sshapes nil)
        p-color-ns (sci/create-ns 'p-color nil)
        p-maths-ns (sci/create-ns 'p-maths nil)
        p-view-ns (sci/create-ns 'p-view nil)
        p-lib-std-ns (sci/create-ns 'p-lib-std nil)
        p-lib-turtle-ns (sci/create-ns 'p-lib-turtle nil)
        p-lib-lsystems-ns (sci/create-ns 'p-lib-lsystems nil)
        p-lib-complex-ns (sci/create-ns 'p-lib-complex nil)
        p-lib-machines-ns (sci/create-ns 'p-lib-machines nil)
        p-lib-symbols-ns (sci/create-ns 'p-lib-symbols nil)
        p-lib-douat-ns (sci/create-ns 'p-lib-douat nil)
        
        ;; Copy all public vars from each namespace
        p-groups-sci (update-vals (ns-publics 'patterning.groups) #(sci/copy-var* % p-groups-ns))
        p-layouts-sci (update-vals (ns-publics 'patterning.layouts) #(sci/copy-var* % p-layouts-ns))
        p-sshapes-sci (update-vals (ns-publics 'patterning.sshapes) #(sci/copy-var* % p-sshapes-ns))
        p-color-sci (update-vals (ns-publics 'patterning.color) #(sci/copy-var* % p-color-ns))
        p-maths-sci (update-vals (ns-publics 'patterning.maths) #(sci/copy-var* % p-maths-ns))
        p-view-sci (update-vals (ns-publics 'patterning.view) #(sci/copy-var* % p-view-ns))
        p-lib-std-sci (update-vals (ns-publics 'patterning.library.std) #(sci/copy-var* % p-lib-std-ns))
        p-lib-turtle-sci (update-vals (ns-publics 'patterning.library.turtle) #(sci/copy-var* % p-lib-turtle-ns))
        p-lib-lsystems-sci (update-vals (ns-publics 'patterning.library.l_systems) #(sci/copy-var* % p-lib-lsystems-ns))
        p-lib-complex-sci (update-vals (ns-publics 'patterning.library.complex-elements) #(sci/copy-var* % p-lib-complex-ns))
        p-lib-machines-sci (update-vals (ns-publics 'patterning.library.machines) #(sci/copy-var* % p-lib-machines-ns))
        p-lib-symbols-sci (update-vals (ns-publics 'patterning.library.symbols) #(sci/copy-var* % p-lib-symbols-ns))
        p-lib-douat-sci (update-vals (ns-publics 'patterning.library.douat) #(sci/copy-var* % p-lib-douat-ns))]
    
    {:namespaces {'p-groups p-groups-sci
                  'p-layouts p-layouts-sci
                  'p-sshapes p-sshapes-sci
                  'p-color p-color-sci
                  'p-maths p-maths-sci
                  'p-view p-view-sci
                  'p-lib-std p-lib-std-sci
                  'p-lib-turtle p-lib-turtle-sci
                  'p-lib-lsystems p-lib-lsystems-sci
                  'p-lib-complex p-lib-complex-sci
                  'p-lib-machines p-lib-machines-sci
                  'p-lib-symbols p-lib-symbols-sci
                  'p-lib-douat p-lib-douat-sci}
     :key-bindings {'basic-turtle (get p-lib-turtle-sci 'basic-turtle)
                    'l-system (get p-lib-lsystems-sci 'l-system)
                    'PI (get p-maths-sci 'PI)
                    'p-color (get p-color-sci 'p-color)
                    'hex-color (get p-color-sci 'hex-color)
                    'paint (get p-color-sci 'paint)
                    'darker-color (get p-color-sci 'darker-color)
                    'poly (get p-lib-std-sci 'poly)
                    'stack (get p-layouts-sci 'stack)
                    'clock-rotate (get p-layouts-sci 'clock-rotate)
                    'grid-layout (get p-layouts-sci 'grid-layout)
                    'checked-layout (get p-layouts-sci 'checked-layout)
                    'framed (get p-layouts-sci 'framed)
                    'aspect-ratio-framed (get p-layouts-sci 'aspect-ratio-framed)
                    'aspect-ratio-frame (get p-layouts-sci 'aspect-ratio-frame)
                    'inner-stretch (get p-layouts-sci 'inner-stretch)
                    'inner-min (get p-layouts-sci 'inner-min)
                    'inner-max (get p-layouts-sci 'inner-max)
                    'q1-rot-group (get p-layouts-sci 'q1-rot-group)
                    'q2-rot-group (get p-layouts-sci 'q2-rot-group)
                    'q3-rot-group (get p-layouts-sci 'q3-rot-group)
                    '->SShape (get p-sshapes-sci '->SShape)
                    'APattern (get p-groups-sci 'APattern)
                    'star (get p-lib-std-sci 'star)
                    'nangle (get p-lib-std-sci 'nangle)
                    'spiral (get p-lib-std-sci 'spiral)
                    'diamond (get p-lib-std-sci 'diamond)
                    'horizontal-line (get p-lib-std-sci 'horizontal-line)
                    'square (get p-lib-std-sci 'square)
                    'drunk-line (get p-lib-std-sci 'drunk-line)}}))

(defn evaluate-pattern-with-error-handling
  "Evaluate a pattern string with comprehensive error handling.
   Returns a map with :success, :result, and :error keys."
  [sci-ctx pattern-code]
  (try
    (let [result (evaluate-pattern sci-ctx pattern-code)]
      {:success true :result result :error nil})
    #?(:clj (catch Exception e
              {:success false :result nil :error e})
        :cljs (catch :default e
                {:success false :result nil :error e}))))
