(ns file-persistance-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.file-persistence :refer :all]))


(describe "this file will control the saving and extracting of game states"
  (with-stubs)

  (it "saves the current state of the game"
    (should= nil (save-board (init-board (->Three-by-three)) X 1 nil nil "games-state.txt"))
    (should= nil (save-board (init-board (->Three-by-three)) X 1 nil nil "games-state.txt"))
    (should= nil (save-board (init-board (->Three-by-three)) X 1 nil nil "games-state.txt"))
    (should= nil (save-board (init-board (->Three-by-three)) O 1 2 nil "games-state.txt")))

  (it "retrieves last played game"
    (spit "games-state.txt" "{:board {:state [:e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e], :size 3, :dimension :three}, :player :x, :game-number 1, :difficulty 3, :difficulty2 3}")
    (should= {:board {:state [:e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e], :size 3, :dimension :three}, :player :x, :game-number 1, :difficulty 3, :difficulty2 3}
             (grab-last-game "games-state.txt")))

  (it "ignores blank lines"
    (spit "game-state.txt" "\n{:board {:state [:e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e], :size 3, :dimension :three}, :player :x, :game-number 1, :difficulty 3, :difficulty2 3}\n\n\n\n\n\n\n\n")
    (should= {:board {:state [:e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e], :size 3, :dimension :three}, :player :x, :game-number 1, :difficulty 3, :difficulty2 3}
             (grab-last-game "game-state.txt")))


  (it "grabs every game that has been played"
    (should= ""  (with-out-str (grab-games "game-state.txt"))))


  )