(ns tic-tac-toe.web-page-spec
  (:require-macros [speclj.core :refer [describe it should= should-contain should-have-invoked with-stubs stub should-not-contain should-invoke]])
  (:require [speclj.core]
            [tic-tac-toe.play-game-web :as play]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.algorithm :as alg]
            [reagent.core :as r]))


(def test-atom (r/atom (conj (utility/->game-state nil nil 0 nil nil) {:playing? false})))

(describe "generates tic-tac-toe webpage"
  (with-stubs)
  (it "updates a piece of the game-map"
    (play/update-map test-atom :board (utility/init-board (utility/->Three-by-three)))
    (should= (utility/init-board (utility/->Three-by-three))
             (:board @test-atom)))

  (it "updates board by conjoining key and value"
    (play/update-board test-atom :display :gui)
    (should= (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})
             (:board @test-atom))
    )

  (it "adds to map"
    (play/update-map test-atom :board (utility/init-board (utility/->Three-by-three)))
    (should= {:board {:state [:e :e :e :e :e :e :e :e :e], :size 3, :dimension :two}, :player nil, :game-number 0, :difficulty nil, :difficulty2 nil, :playing? false, :hi :bob}
             (play/add-to-map test-atom :hi :bob))
    )

  (it "converts keyword :x and :o to X and O and :e to " ""
    (should= "X" (play/key-to-string :x))
    (should= "O" (play/key-to-string :o))
    (should= "~" (play/key-to-string :e))
    )

  (it "plays human turn general"
    (with-redefs [play/update-map (stub :update)
                  utility/player-move (stub :player-move)]
                 (play/play-human-turn (utility/init-board (utility/->Three-by-three)) :x 3))
    (should-have-invoked :update {:times 1})
    (should= {:state [:e :e :e :x :e :e :e :e :e], :size 3, :dimension :two} (play/play-human-turn (utility/init-board (utility/->Three-by-three)) :x 3))
  (should-have-invoked :player-move {:times 1}))

  (it "returns board when it is not an empty space"
    (should= (utility/player-move (utility/init-board (utility/->Three-by-three)) :x 0) (play/play-human-turn (utility/player-move (utility/init-board (utility/->Three-by-three)) :x 0) :x 0))
    )

  (it "plays AI turn general"
    (with-redefs [play/update-map (stub :update)
                  alg/best-move (constantly 0)]
    (should= {:state [:x :e :e :e :e :e :e :e :e], :size 3, :dimension :two, :game-type :ai-vs-human, :display :gui}
             (play/play-ai-turn (conj (conj (utility/init-board (utility/->Three-by-three)) {:game-type :ai-vs-human}) {:display :gui}) :x :o 3 3))
    (should-have-invoked :update {:times 1})))

  (it "generates html for selecting a board"
    (let [board-menu (play/select-board-menu)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain "Select a Board: " (str board-menu))
                   (should-contain "3 by 3" (str board-menu))
                   (play/select-board-menu)
                   (should-have-invoked :input-field {:times 2})
                   ))
    )

  (it "generates html for selecting a game-mode"
    (let [game-menu (play/select-game-mode)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain "Select a Game type: " (str game-menu))
                   (should-contain "Player vs Computer" (str game-menu))
                   (should-contain "Player vs Player" (str game-menu))
                   (should-contain "Computer vs Computer" (str game-menu))
                   (play/select-game-mode)
                   (should-have-invoked :input-field {:times 3})
                   ))
    )


  (it "generates html for selecting difficulty"
    (let [diff (play/select-difficulty "Ai 1" :difficulty "diff1")]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain "Select a difficulty for: Ai 1" (str diff))
                   (should-contain "easy" (str diff))
                   (should-contain "medium" (str diff))
                   (should-contain "unbeatable" (str diff))
                   (play/select-difficulty "Ai 2" :difficulty "diff2")
                   (should-have-invoked :input-field {:times 4})
                   ))
    )

  (it "generates html for selecting player"
    (let [player (play/select-player-menu)]
      (with-redefs [play/input-field (stub :input-field)]
                   (should-contain "Select a player" (str player))
                   (should-contain "X" (str player))
                   (should-contain "O" (str player))
                   (should-contain "NA" (str player))
                   (play/select-player-menu)
                   (should-have-invoked :input-field {:times 3})
                   ))
    )

  (it "creates a square"
                 (play/update-map test-atom :board (utility/init-board (utility/->Three-by-three)))
                 (play/add-to-map test-atom :human-turn? true)
    (should-contain "[:td [:button {:id 3, :style {:color \"blue\", :font-size \"30px\", :display \"inline-block\", :background-color \"black\", :padding \"50px 50px\"}"
                    (str (play/create-square @test-atom 3)))

    (should-contain ":on-click #object[Function]} nil]]"
                    (str (play/create-square @test-atom 3))))

  (it "creates a row of squares"
    (with-redefs [play/create-square (stub :square)]
                 (play/update-map test-atom :board (utility/init-board (utility/->Three-by-three)))
                 (play/add-to-map test-atom :human-turn? true)
                 (play/create-row (:board @test-atom) 1)
                 (should= "[:tr (nil nil nil)]" (str (play/create-row (:board @test-atom) 1)))
                 ))


  (it "starts game"
    (with-redefs [play/update-map (stub :update-map)
                  play/add-to-map (stub :add)]
                 (play/start-game)
                 (should-have-invoked :update-map {:times 2})
                 (should-have-invoked :add {:times 1})
                 )

    )

  (it "resets all pieces to empty"
    (should= [:e :e :e :e :e :e :e :e :e] (play/return-beginning-board [:x :x :e :e :e :e :e :e :e])))

  (it " generates html for reset button"
    (should= "[:button {:style {:color \"blue\"}, :on-click #object[Function]} \"restart\"]"
             (str (play/restart-button))))

  (it "restarts game"
    (with-redefs [play/update-state-only (stub :beg)
                  play/update-map (stub :map)]
                 (play/restart-game)
                 (should-have-invoked :beg {:times 1})
                 (should-have-invoked :map {:times 1})
                 ))

  (it "renders entire menu"
    (with-redefs [play/select-board-menu (stub :board)
                  play/select-game-mode (stub :game-mode)
                  play/select-difficulty (stub :diff)
                  play/select-player-menu (stub :player)]
                 (play/render-menu)
                 (should-have-invoked :board {:times 1})
                 (should-have-invoked :game-mode {:times 1})
                 (should-have-invoked :diff {:times 2})
                 (should-have-invoked :player {:times 1})
                 (should-contain "[:div nil nil nil nil nil [:button {:on-click #object[Function]} \"Start Game\"]]"
                                 (str (play/render-menu)))
                 ))

  )

