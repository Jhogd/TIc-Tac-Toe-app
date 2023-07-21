(ns tic-tac-toe.db-and-edn-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.db-and-edn :refer :all]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.utility :refer :all]))

(describe "a helper file that contains functions combining edn and database"

  (it "checks if the last game read in is finished or not if it is false else true"
    (should= true (last-game-not-finished? (utility/->game-state (init-board (->Three-by-three)) "X" 1 1 1))))

  (it "returns last game number or 0 if null"
    (insert-game 100000 1 2)
    (should= 100000 (get-game-number {:file-type :db}))
    (delete-row {:table :game})
    (delete-row {:table :game})
    )

  (it "decides to increase game number or not depending on age"
    (insert-game 100000 1 2)
    (should= 100000 (increase-game-number-or-no {:board {:age :old :file-type :db}}))
    (delete-row {:table :game}))

  )