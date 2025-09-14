(ns workbench
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
            [patterning.library.douat :as p-douat]
            ))

(defonce pattern-atom (atom nil))
(defonce editor-status-atom (atom :ok))
(defonce debounce-timer (atom nil))
(defonce error-message-atom (atom nil))

(defn- render-pattern [p5 pattern]
  (. p5 background 50)
  (let [tx (p-view/make-txpt [-1 -1 1 1] [0 0 (. p5 -width) (. p5 -height)])]
    (doseq [sshape pattern]
      (when-not (or (empty? (:points sshape)) (:hidden (:style sshape)))
        (let [style (:style sshape)
              points (p-view/project-points tx (:points sshape))]
          (if-let [[r g b a] (:fill style)]
            (.fill p5 r g b (or a 255))
            (. p5 noFill))
          (if-let [[r g b a] (:stroke style)]
            (.stroke p5 r g b (or a 255))
            (. p5 noStroke))
          (when-let [stroke-weight (:stroke-weight style)]
            (. p5 strokeWeight stroke-weight))
          (. p5 beginShape)
          (doseq [[px py] points]
            (. p5 vertex px py))
          (. p5 endShape (if (:closed style) js/CLOSE nil)))))))

(defn- sketch [p5]
  (set! (.-setup p5)
        (fn []
          (let [container (. js/document getElementById "canvas-container")
                width (.-clientWidth container)
                height (.-clientHeight container)
                ;; Ensure square aspect ratio
                size (min width height)]
            (. p5 createCanvas size size)
            (. p5 noLoop))))
  (set! (.-draw p5)
        (fn []
          (when-let [pattern @pattern-atom]
            (render-pattern p5 pattern)))))

(defn- get-sci-ctx []
  (let [;; By including cljs.core, we get most standard functions automatically.
        ns-map {'cljs.core (ns-publics 'cljs.core)
                'p-groups (ns-publics 'patterning.groups)
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
                'p-lib-douat (ns-publics 'patterning.library.douat)
                }
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
               ;; Most cljs.core functions are now covered by the ns-map and all-bindings.
               :allow (set (concat (keys all-bindings)
                                  ['let 'let* 'def 'defn 'fn 'fn* 'if 'when 'cond 'case 'do '-> '->>
                                   'loop 'recur 'throw 'try 'catch 'finally
                                   'quote 'syntax-quote 'unquote 'unquote-splicing]))})))

(defn- update-editor-status-display [editor new-status]
  (. js/console log (str "Updating editor status to: " new-status))
  (let [wrapper (.getWrapperElement editor)
        class-list (.-classList wrapper)]
    (.remove class-list "status-ok" "status-syntax-error" "status-runtime-error")
    (.add class-list (str "status-" (name new-status)))
    (. js/console log (str "Added class: " (str "status-" (name new-status))))))

(defn- update-error-display [error-message]
  (let [error-div (. js/document getElementById "error-display")]
    (if error-message
      (do
        (set! (.-textContent error-div) error-message)
        (.add (.-classList error-div) "show"))
      (.remove (.-classList error-div) "show"))))

(defn download-edn []
  (. js/console log "=== DOWNLOAD EDN FUNCTION CALLED ===")
  (. js/console log "Pattern atom value:" @pattern-atom)
  (. js/console log "Pattern atom type:" (type @pattern-atom))
  (if @pattern-atom
    (do
      (. js/console log "Pattern found, creating download")
      (try
        (let [edn-string (pr-str @pattern-atom)
              _ (. js/console log "EDN string created:" edn-string)
              blob (js/Blob. [edn-string] #js{:type "application/edn"})
              _ (. js/console log "Blob created:" blob)
              url (.createObjectURL js/URL blob)
              _ (. js/console log "Object URL created:" url)
              link (.createElement js/document "a")
              _ (. js/console log "Link element created:" link)]
          (set! (.-href link) url)
          (set! (.-download link) "pattern.edn")
          (. js/console log "Link properties set, href:" (.-href link) "download:" (.-download link))
          (.appendChild (.-body js/document) link)
          (. js/console log "Link appended to body")
          (.click link)
          (. js/console log "Link clicked")
          (.removeChild (.-body js/document) link)
          (. js/console log "Link removed from body")
          (.revokeObjectURL js/URL url)
          (. js/console log "Object URL revoked"))
        (catch :default e
          (. js/console error "Error in EDN download:" e)))
      (. js/console log "EDN download process completed"))
    (. js/console log "No pattern to download - pattern-atom is nil")))

(defn download-svg []
  (. js/console log "=== DOWNLOAD SVG FUNCTION CALLED ===")
  (. js/console log "Pattern atom value:" @pattern-atom)
  (. js/console log "Pattern atom type:" (type @pattern-atom))
  (if @pattern-atom
    (do
      (. js/console log "Pattern found, creating SVG")
      (try
        (let [svg-string (p-view/make-svg 800 800 @pattern-atom)
              _ (. js/console log "SVG string created, length:" (count svg-string))
              blob (js/Blob. [svg-string] #js{:type "image/svg+xml"})
              _ (. js/console log "Blob created:" blob)
              url (.createObjectURL js/URL blob)
              _ (. js/console log "Object URL created:" url)
              link (.createElement js/document "a")
              _ (. js/console log "Link element created:" link)]
          (set! (.-href link) url)
          (set! (.-download link) "pattern.svg")
          (. js/console log "Link properties set, href:" (.-href link) "download:" (.-download link))
          (.appendChild (.-body js/document) link)
          (. js/console log "Link appended to body")
          (.click link)
          (. js/console log "Link clicked")
          (.removeChild (.-body js/document) link)
          (. js/console log "Link removed from body")
          (.revokeObjectURL js/URL url)
          (. js/console log "Object URL revoked"))
        (catch :default e
          (. js/console error "Error in SVG download:" e)))
      (. js/console log "SVG download process completed"))
    (. js/console log "No pattern to download - pattern-atom is nil")))

(defn- evaluate-code [editor sci-ctx]
  (let [code (.getValue editor)]
    (. js/console log "=== EVALUATING CODE ===")
    (. js/console log "Code to evaluate:" code)
    (. js/console log "SCI context:" sci-ctx)
    (try
      (sci/parse-next sci-ctx (sci/reader code))
      (try
        (let [result (sci/eval-string code sci-ctx)]
          (. js/console log "SCI evaluation result:" result)
          (. js/console log "Result type:" (type result))
          (. js/console log "Result count:" (if (sequential? result) (count result) "not sequential"))
          (when (sequential? result)
            (. js/console log "First item:" (first result))
            (. js/console log "First item type:" (type (first result)))
            (when (first result)
              (. js/console log "First item keys:" (keys (first result)))
              (. js/console log "First item points:" (:points (first result)))
              (. js/console log "First item style:" (:style (first result)))))
          (reset! pattern-atom result)
          (. js/console log "Pattern atom updated, new value:" @pattern-atom)
          (reset! editor-status-atom :ok)
          (reset! error-message-atom nil)
          (update-error-display nil)
          
          (.redraw js/window.p5Instance))
        (catch :default e
          (reset! editor-status-atom :runtime-error)
          (reset! error-message-atom (str "Runtime Error: " (.-message e)))
          (update-error-display @error-message-atom)
          (. js/console error "Runtime Error:" e)
          (. js/console error "Error stack:" (.-stack e))
          (. js/console error "Error data:" (.-data e))
          (. js/console error "Error cause:" (.-cause e))))
      (catch :default e
        (reset! editor-status-atom :syntax-error)
        (reset! error-message-atom (str "Syntax Error: " (.-message e)))
        (update-error-display @error-message-atom)
        (. js/console error "Syntax Error:" e)))))

(defn -main []
  (let [editor-host (. js/document getElementById "editor-container")
        sci-ctx (get-sci-ctx)
        editor (js/CodeMirror editor-host #js{:mode "clojure"
                                              :theme "material-darker"
                                              :lineNumbers true})]

    (set! js/window.p5Instance (new js/p5 sketch "canvas-container"))
    
    ;; Only resize canvas on actual window resize, not zoom
    (let [resize-timer (atom nil)
          last-container-size (atom nil)]
      (.addEventListener js/window "resize"
        (fn []
          (js/clearTimeout @resize-timer)
          (reset! resize-timer 
            (js/setTimeout 
              (fn []
                (when-let [p5-instance js/window.p5Instance]
                  (let [container (. js/document getElementById "canvas-container")
                        container-width (.-clientWidth container)
                        container-height (.-clientHeight container)
                        container-size [container-width container-height]
                        ;; Only resize if container dimensions actually changed (not just zoom)
                        size-changed (or (nil? @last-container-size)
                                         (not= container-size @last-container-size))]
                    (when size-changed
                      (reset! last-container-size container-size)
                      (let [size (min container-width container-height)]
                        (.resizeCanvas p5-instance size size)
                        (.redraw p5-instance))))))
              300)))))

    (add-watch editor-status-atom :editor-border-watch
               (fn [_key _atom old-state new-state]
                 (. js/console log (str "Status atom changed from " old-state " to " new-state))
                 (update-editor-status-display editor new-state)))

    (.on editor "change" (fn []
                           (js/clearTimeout @debounce-timer)
                           (reset! debounce-timer (js/setTimeout #(evaluate-code editor sci-ctx) 1000))))
    
    
    ;; Setup for the toggle data button
    (let [toggle-btn (. js/document getElementById "toggle-data-btn")
          data-container (. js/document getElementById "pattern-data-container")]
      (when (and toggle-btn data-container)
        (.addEventListener toggle-btn "click"
          (fn [event]
            (let [is-visible (swap! data-visible-atom not)]
              (if is-visible
                (do
                  (.add (.-classList data-container) "visible")
                  (set! (.-textContent toggle-btn) "Hide Pattern Data"))
                (do
                  (.remove (.-classList data-container) "visible")
                  (set! (.-textContent toggle-btn) "Show Pattern Data"))))))))

    ;; Add download button event listeners with retry mechanism
    (letfn [(setup-download-buttons []
              (let [download-edn-btn (. js/document getElementById "download-edn")
                    download-svg-btn (. js/document getElementById "download-svg")]
                (. js/console log "=== SETTING UP DOWNLOAD BUTTONS ===")
                (. js/console log "Document ready state:" (.-readyState js/document))
                (. js/console log "EDN button element:" download-edn-btn)
                (. js/console log "SVG button element:" download-svg-btn)
                (. js/console log "EDN button exists?" (boolean download-edn-btn))
                (. js/console log "SVG button exists?" (boolean download-svg-btn))
                
                (if (and download-edn-btn download-svg-btn)
                  (do
                    (. js/console log "Both buttons found, setting up listeners")
                    (.addEventListener download-edn-btn "click" 
                                      (fn [event]
                                        (. js/console log "EDN button clicked via event listener")
                                        (download-edn)))
                    (.addEventListener download-svg-btn "click" 
                                      (fn [event]
                                        (. js/console log "SVG button clicked via event listener")
                                        (download-svg)))
                    (. js/console log "Event listeners added successfully"))
                  (do
                    (. js/console log "Buttons not found, retrying in 100ms...")
                    (js/setTimeout setup-download-buttons 100)))))]
      
      ;; Try to set up buttons immediately
      (setup-download-buttons)
      
      ;; Also set up a global fallback - attach functions to window for manual testing
      (set! js/window.downloadEdn #(download-edn))
      (set! js/window.downloadSvg #(download-svg))
      
      ;; Also create JavaScript-compatible functions
      (set! js/window.testDownloadEdn 
            (fn []
              (. js/console log "JavaScript wrapper called")
              (download-edn)))
      (set! js/window.testDownloadSvg 
            (fn []
              (. js/console log "JavaScript wrapper called")
              (download-svg)))
      
      (. js/console log "Download functions attached to window")
      (. js/console log "Try: window.downloadEdn(), window.downloadSvg(), window.testDownloadEdn(), window.testDownloadSvg()")
      
      ;; Test the attachment
      (. js/console log "Testing function attachment...")
      (. js/console log "window.downloadEdn exists?" (boolean js/window.downloadEdn))
      (. js/console log "window.downloadSvg exists?" (boolean js/window.downloadSvg))
      (. js/console log "window.testDownloadEdn exists?" (boolean js/window.testDownloadEdn))
      (. js/console log "window.testDownloadSvg exists?" (boolean js/window.testDownloadSvg)))
    
    (.setValue editor ";; Test the full aspect-ratio-framed function
  (let [corner-pattern (APattern (->SShape {:fill (p-color 200 100 100) :stroke (p-color 100 50 50) :stroke-weight 2}
                                           [[0 0] [0.3 0] [0.2 0.1] [0.2 0.2] [0 0.2] [0 0]]))
        edge-pattern (APattern (->SShape {:fill (p-color 100 200 100) :stroke (p-color 50 100 50) :stroke-weight 2}
                                         [[-0.2 0] [0.2 0] [0.1 0.05] [0.1 -0.05] [-0.2 0]]))
        inner-content (APattern (->SShape {:fill (p-color 100 100 200) :stroke (p-color 50 50 100) :stroke-weight 2}
                                          [[0 0] [0.4 0] [0.4 0.4] [0 0.4] [0 0]]))]
    (aspect-ratio-framed 4 4 corner-pattern edge-pattern inner-content inner-min))")
    (evaluate-code editor sci-ctx)
    ))

(goog/exportSymbol "workbench.-main" -main)
