(ns tutorial.month

    (:require
   #_[om.core :as om :include-macros true]
   [sablono.core :as sab :include-macros true]

   [cljs.js :refer [eval empty-state js-eval]]
   [patterning.sshapes :as sshapes]
   [patterning.groups :refer [translate rotate reframe
                              stretch scale over-style]]
   [patterning.layouts :refer [clock-rotate stack grid-layout
                               checked-layout
                               four-mirror h-mirror v-mirror
                               half-drop-grid-layout framed
                               sshape-as-layout four-round]]
   [patterning.library.std :refer [poly drunk-line square diamond horizontal-line]]
   [patterning.color :refer [p-color rand-col darker-color]]
   [patterning.view :refer [transformed-sshape make-txpt xml-tpl make-svg] ]
   [patterning.maths :refer [PI]]
   [patterning.examples.tutorial :refer []]
   [patterning.examples.framedplant :refer [framed-plant]]
   [patterning.library.complex_elements :refer [petal-group petal-pair-group
                                                polyflower-group]]

   )

   (:require-macros [devcards.core :as dc :refer [defcard deftest]])
  )


(defn render [p code]
  [:div
   [:div
    {:dangerouslySetInnerHTML {:__html (make-svg 400 400  p)}}]
   [:div [:pre [:code
                {:style {:font-family   "'Lucida Console', Monaco, monospace" }}
                (with-out-str (cljs.pprint/pprint code))]]
    ]])

(defcard intro
  "This is a series of patterns for August 2018"
  )

(defcard p1
  "Chita"

  (let [verde {:fill (p-color  100 150 100) :stroke-weight 0}
        bg (square verde)
        amarelo {:fill (p-color 255 255 0) :stroke-weight 0}
        y (translate 0.3 0 (scale 0.6  (diamond amarelo)))
        azul {:fill (p-color 50 30 150) :stroke-weight 0}
        b (stretch 0.6 1
                   (let [bs (stack (square azul)
                                   (->> (poly 0 0.2 0.2 7 {:fill (p-color 255 255 255)})

                                        (#(grid-layout 3 (cycle [() %])))) )]
                     (framed 4 (repeat bs) (repeat bs) []) ))
        vermelho {:fill (p-color 250 20 60)
                  :stroke-weight 2
                  :stroke (p-color 200 0 0)}
        petal (let [p (->> (poly 0.2 0 0.4 15 vermelho)
                           (stretch 1.3 1)
                           )
                    p2 (scale 0.8 (over-style {:fill (p-color 255 240 250)} p))
                    p3 (scale 0.8 (over-style vermelho p2))]
                (stack p p2 p3                       ))
        flor (stretch 1 0.7 (clock-rotate 5 petal))
        dl (drunk-line 7 0.15 { :stroke-weight 4 :stroke (p-color 0 255 0)})
        rose-line (four-round (clock-rotate 3 (reframe (stack dl (sshape-as-layout (first dl) (cycle [() flor]) 0.08)))))
        final (stack bg y b rose-line )  ]
    (sab/html (render final '()))
    )

  )
