(ns tutorial.inter
  (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]

   [cljs.js :refer [eval empty-state js-eval]]
   [patterning.sshapes :as sshapes]
   [patterning.groups :as groups]
   [patterning.layouts :as layouts]
   [patterning.library.std :as std]
   [patterning.color :refer [p-color] :as color]
   [patterning.view :refer [transformed-sshape make-txpt xml-tpl make-svg] ]
   [patterning.library.turtle :refer [basic-turtle]]
   [patterning.library.l_systems :refer [l-system]]
   [patterning.maths :refer [PI]]

   [tutorial.common :refer [render]]
   [clojure.string :as str]
   [reagent.core :as r]

   )
  (:require-macros [devcards.core :as dc :refer [defcard deftest]])

  )





  (defcard
  "## Interactive Patterns"

    (defn inter-component [params]
      (r/with-let [rota (-> @params :rotations)
                   grida (-> @params :grid)
                   rot (if (nil? rota) 3 rota)
                   grid (if (nil? grida) 3 grida)]

        (sab/html
         [:div
          (let [p (-> (std/poly 0 0.6 0.3 5 {:stroke-weight 2
                                             :stroke (p-color 200 150 200)
                                             :fill (p-color 100 50 100
                                                            200)} )
                      (#(layouts/clock-rotate rot %))
                      repeat
                      (#(layouts/grid-layout grid %))
                      (#(layouts/stack (std/square {:fill (p-color 255 255 150)}) % ))
                      )
                svg (make-svg 500 500 p)
                blob (js/Blob. (seq (str svg)) {:type "image/svg+xml"})
                blob-url (.createObjectURL js/URL blob)]
            [:div
             [:div
              [:div
               [:label "Rotation : "]
               [:input {:type "range" :value rot :min 1 :max 14
                        :on-change #(reset! params (conj @params {:rotations (-> % .-target .-value (* 1) )  })) }]

               ]
              [:div
               [:label "Grid : "
                [:input {:type "range" :value grid :min 2 :max 10
                         :on-change #(reset! params (conj @params {:grid (-> % .-target .-value (* 1))}))}]]]
             ]
             [:div
              {:dangerouslySetInnerHTML {:__html svg}}]

             [:div [:a {:download "pattern.svg"
                        :href blob-url} "Download" ] ]
             ])]))))
