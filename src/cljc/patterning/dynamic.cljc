(ns patterning.dynamic
  (:require [sci.core :as sci]
            [patterning.groups :as p-groups]
            [patterning.layouts :as p-layouts]
            [patterning.sshapes :as p-sshapes]
            [patterning.color :as p-color]
            [patterning.maths :as p-maths]
            [patterning.view :as p-view]
            [patterning.macros :as p-macros]
            [patterning.library.std :as p-lib-std]
            [patterning.library.turtle :as p-lib-turtle]
            [patterning.library.l_systems :as p-lib-lsystems]
            [patterning.library.complex-elements :as p-lib-complex]
            [patterning.library.machines :as p-lib-machines]
            [patterning.library.symbols :as p-lib-symbols]
            [patterning.library.douat :as p-lib-douat]))

(defn get-patterning-namespaces
  "Returns the mapping of SCI namespace names to their public functions.
   This is shared between both SCI context functions."
  []
  {'p-groups (ns-publics 'patterning.groups)
   'p-layouts (ns-publics 'patterning.layouts)
   'p-sshapes (ns-publics 'patterning.sshapes)
   'p-color (ns-publics 'patterning.color)
   'p-maths (ns-publics 'patterning.maths)
   'p-view (ns-publics 'patterning.view)
   'p-macros (ns-publics 'patterning.macros)
   'p-lib-std (ns-publics 'patterning.library.std)
   'p-lib-turtle (ns-publics 'patterning.library.turtle)
   'p-lib-lsystems (ns-publics 'patterning.library.l_systems)
   'p-lib-complex (ns-publics 'patterning.library.complex-elements)
   'p-lib-machines (ns-publics 'patterning.library.machines)
   'p-lib-symbols (ns-publics 'patterning.library.symbols)
   'p-lib-douat (ns-publics 'patterning.library.douat)})

(defn get-key-bindings
  "Returns the key bindings that patterns expect to be available.
   This is shared between both SCI context functions."
  []
  {'basic-turtle #'p-lib-turtle/basic-turtle
   'l-system #'p-lib-lsystems/l-system
   'PI p-maths/PI
   'p-color #'p-color/p-color
   'hex-color #'p-color/hex-color
   'defcolor #'p-macros/defcolor
   'poly #'p-lib-std/poly
   'arc #'p-lib-std/arc
   'stack #'p-layouts/stack
   'nested-stack #'p-layouts/nested-stack
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
   'drunk-line #'p-lib-std/drunk-line
   ;; Additional functions needed for complex patterns like Pelican
   'translate #'p-groups/translate
   'scale #'p-groups/scale
   'rotate #'p-groups/rotate
   'stretch #'p-groups/stretch
   'reframe #'p-groups/reframe
   'h-reflect #'p-groups/h-reflect
   'v-reflect #'p-groups/v-reflect
   'rotate-tile-set #'p-groups/rotate-tile-set
   'reflect-tile-set #'p-groups/reflect-tile-set
   'on-background #'p-lib-std/on-background
   'four-round #'p-layouts/four-round
   'Douat #'p-lib-douat/Douat
   'clock-points #'p-maths/clock-points
   'distance #'p-maths/distance
   'atan2 #'p-maths/atan2
   'rect #'p-lib-std/rect
   'rect-std #'p-lib-std/rect
   ;; Additional core functions needed for complex patterns
   'abs #'p-maths/abs
   'deg-to-rad #'p-maths/deg-to-rad
   'rad-to-deg #'p-maths/rad-to-deg
   ;; Core sequence functions needed for lazy sequences
   'seq #'clojure.core/seq
   'first #'clojure.core/first
   'iterate #'clojure.core/iterate
   'take #'clojure.core/take
   'cycle #'clojure.core/cycle
   'rand-nth #'clojure.core/rand-nth
   'repeat #'clojure.core/repeat})

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
   '<
   '>
   '<=
   '>=
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
   'and
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
   'clojure.core/<
   'clojure.core/>
   'clojure.core/<=
   'clojure.core/>=
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
   'clojure.core/and
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
   'clojure.core/vderef
   ;; Additional functions needed for complex patterns
   'first
   'second
   'clojure.core/first
   'clojure.core/second])


(defn get-sci-context
  "Create a shared SCI context for pattern evaluation.
   This function provides the same evaluation environment for both CLI and workbench."
  []
  (let [;; Use shared configuration
        ns-map (get-patterning-namespaces)
        bindings (apply merge (vals ns-map))
        key-bindings (get-key-bindings)
        ;; Add angle constants from p-maths namespace at runtime
        ;; Need to dereference Vars to get their actual numeric values
        p-maths-publics (get ns-map 'p-maths)
        angle-constants (when p-maths-publics
                          (into {}
                                (keep (fn [[k v]]
                                        (when (contains? #{'d360 'd270 'd180 'd90 'd45 'd60 'd30 'd10 'd36} k)
                                          [k (deref v)])))
                                p-maths-publics))
        all-bindings (merge bindings key-bindings (or angle-constants {}))]
    (sci/init {:namespaces ns-map
               :bindings all-bindings
               ;; We still need to allow the bindings themselves, plus core macros and special forms.
               ;; Most clojure.core/cljs.core functions are now covered by the ns-map and all-bindings.
               :allow (set (concat (keys all-bindings)
                                  ['let 'let* 'def 'defn 'fn 'fn* 'if 'when 'cond 'case 'do '-> '->>
                                   'clojure.core/->
                                   'clojure.core/->>
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
                                   '<
                                   '>
                                   '<=
                                   '>=
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
                                   'and
                                   ;; Additional functions needed for complex patterns
                                   'first
                                   'second
                                   'seq
                                   'rest
                                   'next
                                   'cons
                                   'empty?
                                   'count
                                   'clojure.core/first
                                   'clojure.core/second
                                   'clojure.core/seq
                                   'clojure.core/rest
                                   'clojure.core/next
                                   'clojure.core/cons
                                   'clojure.core/empty?
                                   'clojure.core/count]))})))

(defn evaluate-pattern
  "Evaluate a pattern string using the shared SCI context.
   Returns the evaluated result."
  [sci-ctx pattern-code]
  (sci/eval-string pattern-code sci-ctx))

(defn get-sci-namespace-config
  "Returns the SCI namespace configuration for the second SCI context function.
   This creates SCI namespaces and copies vars from the patterning namespaces."
  []
  (let [;; Create SCI namespaces for each patterning namespace
        p-groups-ns (sci/create-ns 'p-groups nil)
        p-layouts-ns (sci/create-ns 'p-layouts nil)
        p-sshapes-ns (sci/create-ns 'p-sshapes nil)
        p-color-ns (sci/create-ns 'p-color nil)
        p-maths-ns (sci/create-ns 'p-maths nil)
        p-view-ns (sci/create-ns 'p-view nil)
        p-macros-ns (sci/create-ns 'p-macros nil)
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
        p-macros-sci (update-vals (ns-publics 'patterning.macros) #(sci/copy-var* % p-macros-ns))
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
                  'p-macros p-macros-sci
                  'p-lib-std p-lib-std-sci
                  'p-lib-turtle p-lib-turtle-sci
                  'p-lib-lsystems p-lib-lsystems-sci
                  'p-lib-complex p-lib-complex-sci
                  'p-lib-machines p-lib-machines-sci
                  'p-lib-symbols p-lib-symbols-sci
                  'p-lib-douat p-lib-douat-sci}
     :sci-vars {'p-groups-sci p-groups-sci
                'p-layouts-sci p-layouts-sci
                'p-sshapes-sci p-sshapes-sci
                'p-color-sci p-color-sci
                'p-maths-sci p-maths-sci
                'p-view-sci p-view-sci
                'p-macros-sci p-macros-sci
                'p-lib-std-sci p-lib-std-sci
                'p-lib-turtle-sci p-lib-turtle-sci
                'p-lib-lsystems-sci p-lib-lsystems-sci
                'p-lib-complex-sci p-lib-complex-sci
                'p-lib-machines-sci p-lib-machines-sci
                'p-lib-symbols-sci p-lib-symbols-sci
                'p-lib-douat-sci p-lib-douat-sci}}))

(defn include-from
  "Helper function to extract SCI vars from a namespace map.
   Usage: (include-from sci-vars 'p-color-sci ['p-color 'hex-color 'paint])
   Returns a map of function names to their SCI vars."
  [sci-vars namespace-sym function-syms]
  (let [namespace-map (get sci-vars namespace-sym)]
    (into {}
          (map (fn [fn-sym]
                 [fn-sym (get namespace-map fn-sym)]))
          function-syms)))

(defn get-sci-context-config
  "Shared configuration for SCI contexts across platforms.
   Returns the common parts that both Clojure and ClojureScript can use."
  []
  (let [sci-config (get-sci-namespace-config)
        sci-vars (:sci-vars sci-config)]
    
    (merge sci-config
           {:key-bindings (merge (:key-bindings sci-config {})
                                ;; Convert key bindings to use SCI vars
                                (merge
                                 ;; Color functions
                                 (include-from sci-vars 'p-color-sci ['p-color 'hex-color 'paint 'darker-color])
                                 ;; Macro functions
                                 (include-from sci-vars 'p-macros-sci ['defcolor])
                                 ;; Standard library shapes
                                 (include-from sci-vars 'p-lib-std-sci ['poly 'arc 'rect 'star 'nangle 'spiral 'diamond 'horizontal-line 'square 'drunk-line 'on-background])
                                 ;; Layout functions
                                 (include-from sci-vars 'p-layouts-sci ['stack 'nested-stack 'clock-rotate 'grid-layout 'checked-layout 'framed 'aspect-ratio-framed 'aspect-ratio-frame 'inner-stretch 'inner-min 'inner-max 'q1-rot-group 'q2-rot-group 'q3-rot-group 'four-round])
                                 ;; Group/transform functions
                                 (include-from sci-vars 'p-groups-sci ['APattern 'translate 'scale 'rotate 'stretch 'reframe 'h-reflect 'v-reflect 'rotate-tile-set 'reflect-tile-set])
                                 ;; Shape construction
                                 (include-from sci-vars 'p-sshapes-sci ['->SShape])
                                 ;; Maths functions
                                 (include-from sci-vars 'p-maths-sci ['clock-points 'distance 'atan2 'abs 'deg-to-rad 'rad-to-deg])
                                 ;; Turtle functions
                                 (include-from sci-vars 'p-lib-turtle-sci ['basic-turtle])
                                 ;; L-systems
                                 (include-from sci-vars 'p-lib-lsystems-sci ['l-system])
                                 ;; Douat functions
                                 (include-from sci-vars 'p-lib-douat-sci ['Douat])
                                 ;; Special cases: constants, not functions
                                 {'PI (get-in sci-vars ['p-maths-sci 'PI])
                                  'd360 (get-in sci-vars ['p-maths-sci 'd360])
                                  'd270 (get-in sci-vars ['p-maths-sci 'd270])
                                  'd180 (get-in sci-vars ['p-maths-sci 'd180])
                                  'd90 (get-in sci-vars ['p-maths-sci 'd90])
                                  'd45 (get-in sci-vars ['p-maths-sci 'd45])
                                  'd60 (get-in sci-vars ['p-maths-sci 'd60])
                                  'd30 (get-in sci-vars ['p-maths-sci 'd30])
                                  'd10 (get-in sci-vars ['p-maths-sci 'd10])
                                  'd36 (get-in sci-vars ['p-maths-sci 'd36])}))})))


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
