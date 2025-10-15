(ns workbench
  (:require [patterning.dynamic :as dynamic]
            [patterning.view :as p-view]
            ))

(defonce pattern-atom (atom nil))
(defonce editor-status-atom (atom :ok))
(defonce debounce-timer (atom nil))
(defonce error-message-atom (atom nil))
(defonce data-visible-atom (atom false))
(defonce worker-ref (atom nil))
(defonce timeout-ref (atom nil))


(defn- render-pattern [p5 pattern]
  (. p5 background 255)
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
          
          (if (:bezier style)
            ;; Handle bezier curves
            (do
              (. p5 beginShape)
              (let [[start-x start-y] (first points)]
                (. p5 vertex start-x start-y))
              (doseq [[p1 p2 p3] (partition 3 (rest points))]
                (. p5 bezierVertex (first p1) (second p1) (first p2) (second p2) (first p3) (second p3)))
              (. p5 endShape (if (:closed style) js/CLOSE nil)))
            ;; Handle regular shapes
            (do
              (. p5 beginShape)
              (doseq [[px py] points]
                (. p5 vertex px py))
              (. p5 endShape (if (:closed style) js/CLOSE nil)))))))))

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

(defn- evaluate-code [editor]
  (let [code (.getValue editor)]
    (. js/console log "=== EVALUATING CODE VIA WORKER ===")

    ;; Terminate any previously running worker and timeout
    (when @worker-ref
      (.terminate @worker-ref)
      (reset! worker-ref nil))
    (when @timeout-ref
      (js/clearTimeout @timeout-ref)
      (reset! timeout-ref nil))

    (let [worker (js/Worker. "worker.js")]
      (reset! worker-ref worker)

      ;; 1. Handle successful evaluation
      (set! (.-onmessage worker)
        (fn [event]
          (js/clearTimeout @timeout-ref)
          (let [data (js->clj (. event -data) :keywordize-keys true)]
            (if (= (:status data) "ok")
              (do
                (let [deserialized-result (js->clj (js/JSON.parse (:result data)) :keywordize-keys true)]
                  (reset! pattern-atom deserialized-result))
                (reset! editor-status-atom :ok)
                (update-error-display nil)
                (.redraw js/window.p5Instance))
              (do
                (reset! editor-status-atom :runtime-error)
                (update-error-display (str "Runtime Error: " (:error-message data)))
                (. js/console error "Worker evaluation error:" (:error-message data)))))))

      ;; 2. Handle errors within the worker script itself
      (set! (.-onerror worker)
        (fn [error]
          (js/clearTimeout @timeout-ref)
          (reset! editor-status-atom :runtime-error)
          (let [msg (str "Worker Error: " (.-message error))]
            (update-error-display msg)
            (. js/console error msg error))))

      ;; 3. Set the timeout
      (reset! timeout-ref
        (js/setTimeout
          (fn []
            (.terminate worker)
            (reset! worker-ref nil)
            (reset! editor-status-atom :runtime-error)
            (let [msg "Runtime Error: Evaluation timed out (possible infinite loop)."]
              (update-error-display msg)
              (. js/console error msg)))
          5000)) ;; 5 second timeout

      ;; 4. Start evaluation
      (.postMessage worker code))))

(defn -main []
  (let [editor-host (. js/document getElementById "editor-container")
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
                           (reset! debounce-timer (js/setTimeout #(evaluate-code editor) 1000))))
    
    
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
    
    ;; Set default code - triangle and hexagon pattern
    (let [default-code "(let 
  [tri
   (poly 0 0.3 1.05 3
     {:fill (p-color 200 0 100)
      :stroke (p-color 250 50 150)})
    hex
   (poly 0 0 0.8 6 
     {:fill (p-color 100 150 100) 
      :stroke (p-color 200 255 50) 
      :stroke-weight 2})]
  (stack 
   (square {:fill (p-color 0)})
   (grid-layout 5 (cycle [hex tri]))))"
          ;; Check for code parameter in URL or localStorage
          url-params (js/URLSearchParams. (.-search js/window.location))
          code-param (.get url-params "code")
          storage-key (.get url-params "storageKey")
          initial-code (cond
                        ;; Use localStorage if storageKey is provided
                        storage-key
                        (or (.getItem js/localStorage storage-key) default-code)
                        
                        ;; Use URL parameter if code is provided
                        code-param 
                        (try 
                          (js/decodeURIComponent code-param)
                          (catch js/Error e
                            (. js/console error "Error decoding URL parameter:" e)
                            (. js/console error "Raw code parameter:" code-param)
                            default-code))
                        
                        ;; Default code
                        :else default-code)]
      (.setValue editor initial-code))
    (evaluate-code editor)
    ))

(goog/exportSymbol "workbench.-main" -main)
