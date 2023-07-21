(ns tic-tac-toe.main-menu-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.db-and-edn :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.main-menu :refer :all]
            [quil.core :as q :include-macros true]))


(describe "handles menu selections for gui"

  (with-stubs)

  (it "checks if user selected button"
  (with-redefs [q/width (constantly 300)
                q/height (constantly 300)]
    (should= true (button-selected? 90 80 250 0))
    (should= false (button-selected? 40 240 250 0))
    (should= true (button-selected? 90 200 250 100))
    ))

  (it "creates rectangle with offset for button"
    (with-redefs [q/rect (stub :rect)]
      (should= nil (rect 40 50 20 20 5))))

  (it "creates rectangle for button"
    (with-redefs [q/text (stub :text)]
      (should= nil (create-two-text 0 "hi" "bye" 50 20 20 5))))

  (it "creates rectangle for button"
    (with-redefs [q/rect (stub :rect)]
      (should= nil (create-rect 0 50 20 20 5))))

  (it "creates rectangle for button"
    (with-redefs [q/rect (stub :rect)]
      (should= nil (create-three-rect 40 50 20 20 5))))

  (it "creates rectangle for button"
  (with-redefs [q/text (stub :text)]
    (should= nil (create-three-text 0 "hi" "bye" "cool" 50 20 20 5))))

  (it "draws two button menu"
    (with-redefs [q/background (stub :background)
                  q/width  (constantly 300)
                  q/height (constantly 300)
                  q/rect (stub :rect)
                  q/text (stub :text)
                  q/stroke (stub :stroke)
                  q/fill (stub :fill)]
      (should= nil (draw-two-button-menu "hi" "bye"))))

  (it "draws three button menu"
    (with-redefs [q/background (stub :background)
                  q/width  (constantly 300)
                  q/height (constantly 300)
                  q/rect (stub :rect)
                  q/text (stub :text)
                  q/stroke (stub :stroke)
                  q/fill (stub :fill)]
      (should= nil (draw-three-button-menu "hi" "bye" "sigh"))))

  (it "selects player for gui"
    (should= X (select-player-gui {:player X}
                                  {:board {:age :old :game-type :ai-vs-ai}
                                   :player O})))

  (it "checks if user selected button"
  (with-redefs [q/width (constantly 300)
                q/height (constantly 300)]
    (should= true (button-selected? 90 80 250 0))
    (should= false (button-selected? 40 240 250 0))
    (should= true (button-selected? 90 200 250 100))
    ))

  (it "decides to move to difficulty screen or player screen"
    (should= :ask-player (ask-difficulty-or-board {:board {:game-type :ai-vs-human}}))
    (should= :ask-difficulty2 (ask-difficulty-or-board {:board {:game-type :ai-vs-ai}})))

  (it "returns game-mode screen or new-old if last-game finished"
    (with-redefs [last-game-not-finished? (constantly false)]
      (should= :game-mode (check-last-game-state {:file-type :db}))))

  (it "conjoins new map and updates :board map"
    (should= {:screen :new-old :board {:file-type :db}} (update-menu-board {:screen :persistence :board {}} {:file-type :db} :new-old)))

  (it "checks if a button was clicked by user and adds to the game-map"
    (with-redefs [button-selected? (constantly true)]
      (should= {:difficulty2 1 :screen :ask-board} (handle-mouse {:screen :ask-difficulty2 :difficulty2 0} {:x 61, :y 65, :button nil}))))

  )