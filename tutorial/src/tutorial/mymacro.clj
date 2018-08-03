(ns tutorial.mymacro
  (:require [patterning.view :refer [make-svg] ])
  )


(defmacro render2 [p code]
  `[:div
    [:div
     {:dangerouslySetInnerHTML
      {:__html (make-svg 300 300 ~code)}}]
    [:div "P" (str ~p)]
    [:div "CODE CODE CODE " ~code]
    [:div [:pre [:code
                 {:style {:font-family   "'Lucida Console', Monaco, monospace" }}
                 (with-out-str (cljs.pprint/pprint ~code))
                 ]]
     ]])
