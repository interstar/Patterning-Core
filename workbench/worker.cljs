(ns worker
  (:require [sci.core :as sci]
            [patterning.dynamic :as dynamic]))

(defn- get-sci-ctx []
  (let [config (dynamic/get-sci-context-config)
        ns-map (assoc (:namespaces config) 'cljs.core (ns-publics 'cljs.core))
        bindings (apply merge (vals ns-map))
        all-bindings (merge bindings (:key-bindings config))]
    (sci/init {:namespaces ns-map
               :bindings all-bindings
               :allow (set (concat (keys all-bindings)
                                  (dynamic/get-core-allow-list)))})))

(defonce sci-ctx (get-sci-ctx))

;; Main worker entry point
(set! js/self.onmessage
      (fn [event]
        (let [code (.-data event)]
          (try
            (let [result (sci/eval-string code sci-ctx)
                  serialized-result (js/JSON.stringify (clj->js result))]
              (.postMessage js/self #js {:status "ok" :result serialized-result}))
            (catch :default e
              ;; Errors during evaluation are caught and sent back to the main thread
              (.postMessage js/self #js {:status "error" :error-message (str (.-message e))}))
            ))))
