(ns tutorial.common
  (:require
      [patterning.view :refer [make-svg] ])
  )

(defn render [p code]
  [:div
   [:div
    [:h3 "Visual"]
    [:div
     {:dangerouslySetInnerHTML {:__html (make-svg 400 400
                                                  p)}}]]
   [:div
    [:h3 "Code"]
    [:pre [:code
                (with-out-str (cljs.pprint/pprint code))]]
    ]])
