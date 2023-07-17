(ns tic-tac-toe.gui-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.gui :refer :all]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.algorithm :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            [tic-tac-toe.database :refer :all]
            [quil.core :as q :include-macros true]
            [quil.middleware :as m]))

(describe "main file to run gui and game"
  (with-stubs)

  (it "retrieves the square size for the gui"
    (with-redefs [q/width (constantly 300)]
      (should= 100 (square-size (init-board (->Three-by-three))))))


  (it "draws x and o on the gui"
    (with-redefs [q/stroke (stub :stroke)
                  q/fill (stub :fill)
                  q/stroke-weight (stub :stroke-weight)
                  q/text (stub :text)
                  q/width (constantly 300)]
      (draw-move {:state [:x :e :o :e :e :e :e :e :e], :size 3, :display :gui :dimension :two})
      (should-have-invoked :stroke {:with [0] :times 9})))

  (it "draw-grid"
    (with-redefs [q/stroke (stub :stroke)
                  q/fill (stub :fill)
                  q/width (constantly 300)
                  q/rect (stub :rect)]
      (should= nil (draw-grid {:state [:x :e :o :e :e :e :e :e :e], :size 3, :dimension :two :display :gui}))))


  )