(ns database-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.utility :refer :all]))

(describe "connects to sql lite jdbc database and sets it up"

  #_(focus-it "connects to the database"
    (should= nil (create-connection)))

  #_(focus-it "insert game info into game table"
    (should= nil (insert-game create-connection 0  1 1)))

  (focus-it "inserts board into board table"
    (should= nil (insert-board create-connection (init-board(->Three-by-three)) ":x" (grab-last-id-game create-connection))))

  (focus-it "grabs last id from game table"
    (should= 14 (grab-last-id-game create-connection)))

  (focus-it "grabs the last board, player, levels"
    (should= nil))

  )

