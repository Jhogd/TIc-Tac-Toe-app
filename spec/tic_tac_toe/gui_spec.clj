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
      (should= 100 (square-size (init-board(->Three-by-three))))))


(focus-it "should invoke play-game without caring what
       what the position values are"
  (let [moves (atom (range 10))
        next-move (fn [& _] (let [move (first @moves)]
                              (swap! moves rest)
                              move))]
    (with-redefs [best-move next-move
                  read next-move
                  save-board (constantly nil)
                  println (constantly nil)]
      (should= nil (play-game (conj (conj (init-board (->Three-by-three))
                                          {:game-type :human-vs-human})
                                    {:display :print}) X X
                                1  3 0)))
    )(doseq [i (range 10)] (delete-row {:table :board})))


  (focus-it "simulates human move on gui"
    (with-redefs [q/mouse-pressed? (constantly true)
                  q/mouse-x (constantly 270)
                  q/mouse-y (constantly 50)
                  q/width (constantly 300)
                  println (constantly nil)
                  ]
      (should= {:state [:x :e :e :e :e :e :e :e :e], :size 3, :display :gui, :current-player 0, :player :o, :game-number 1, :difficulty 1, :difficulty2 2} (handle-mouse {:state [:x :e :e :e :e :e :e :e :e] :size 3 :display :gui :current-player 0 :player O :game-number 1 :difficulty 1 :difficulty2 2}  {:x 61, :y 65, :button nil}
                                                                                           ))))

  (it "draws x and o on the gui"
    (with-redefs [q/stroke (stub :stroke)
                  q/fill (stub :fill)
                  q/stroke-weight (stub :stroke-weight)
                  q/text (stub :text)
                  q/width (constantly 300)]
      (should= nil (draw-move {:state [:x :e :o :e :e :e :e :e :e], :size 3, :display :gui}))))

  (it "draw-grid"
    (with-redefs [q/stroke (stub :stroke)
                  q/fill (stub :fill)
                  q/stroke-weight (stub :stroke-weight)
                  q/width (constantly 300)])
    (should= nil (draw-grid {:state [:x :e :o :e :e :e :e :e :e], :size 3, :dimension :two :display :gui} )))

  )