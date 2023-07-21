(ns tic-tac-toe.web-page-spec
  (:require-macros [speclj.core :refer [describe it should= should-contain should-have-invoked with-stubs stub should-not-contain should-invoke]])
  (:require [speclj.core]
            [tic-tac-toe.play-game-web :as play]
            [tic-tac-toe.utility :as utility]
            [reagent.core :as r]))



(describe "generates tic-tac-toe webpage"
  (with-stubs)
  (it "updates a piece of the game-map"
    (play/update-map :board (utility/init-board (utility/->Three-by-three)))
    (should= (utility/init-board (utility/->Three-by-three))
             (:board @play/game-map)))

  (it "updates board by conjoining key and value"
    (play/update-board :display :gui)
    (should= (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})
             (:board @play/game-map))
    )

  (it "converts keyword :x and :o to X and O and :e to " ""
    (should= "X" (play/key-to-string :x))
    (should= "O" (play/key-to-string :o))
    (should= "" (play/key-to-string :e))
    )

  (it "generates html for selecting a board"
    (let [board-menu (play/select-board-menu)]
      (with-redefs [play/input-field (stub :input-field)]
      (should-contain "Select a Board: " (str board-menu))
      (should-contain  "3 by 3" (str board-menu))
      (play/select-board-menu)
      (should-have-invoked :input-field {:times 2})
      ))
    )

  (it "generates html for selecting a game-mode"
    (let [game-menu (play/select-game-mode)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain  "Select a Game type: "  (str game-menu))
                   (should-contain  "Player vs Computer" (str game-menu))
                   (should-contain  "Player vs Player" (str game-menu))
                   (should-contain  "Computer vs Computer" (str game-menu))
                   (play/select-game-mode)
                   (should-have-invoked :input-field {:times 3})
                   ))
    )


  (it "generates html for selecting difficulty"
    (let [diff (play/select-difficulty "Ai 1" :difficulty)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain  "Select a difficulty for: Ai 1"  (str diff))
                   (should-contain  "easy" (str diff))
                   (should-contain  "medium" (str diff))
                   (should-contain  "unbeatable" (str diff))
                   (play/select-difficulty "Ai 1" :difficulty)
                   (should-have-invoked :input-field {:times 4})
                   ))
    )

  (it "generates html for selecting player"
    (let [player (play/select-player-menu)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain  "Select a player"  (str player))
                   (should-contain  "X" (str player))
                   (should-contain  "O" (str player))
                   (should-contain  "NA" (str player))
                   (play/select-player-menu)
                   (should-have-invoked :input-field {:times 3})
                   ))
    )

  (it "creates a square"
    (with-redefs [play/key-to-string (stub :key2string)
                  play/update-game-state (stub :game-state)]
                 (should-contain "[:button {:id 3" (str (play/create-square 3)))
                 (play/create-square 3)
                 (should-have-invoked :game-state {:times 2})
                 (should-have-invoked :key2string {:times 2})
                 ))

  (it "creates a row of squares"
    (with-redefs [play/create-square (stub :square)]
                 ;(play/create-row (utility/init-board (utility/->Three-by-three)))
                 ;(should-have-invoked :square {:times 3})
                 (should= "[:tr (nil nil nil)]" (str  (play/create-row (utility/init-board (utility/->Three-by-three)))))
                 ))

  (it "starts game"
    (with-redefs [utility/terminal? (stub :terminal {:return true})
                  play/update-game-state (stub :game-state)
                  utility/terminal-state (stub :terminal-state {:return 10})]
                 (play/start-game (utility/init-board (utility/->Three-by-three)) )
                 (should-have-invoked :terminal-state {:times 1})
                 (should-have-invoked :terminal {:times 1})
                 (should-contain  "Player X has won!" (str (play/start-game  (utility/init-board (utility/->Three-by-three)))))
                 )
    )











  )

