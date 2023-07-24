(ns tic-tac-toe.database-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.utility :refer :all]
            [clojure.java.jdbc :as jdbc]))

(defn grab-last-game-numb []
  (get (first (jdbc/query create-connection
                          ["select gamenumber from game order by id desc limit 1"]))
       :gamenumber))

(defn grab-last-board-numb []
  (get (first (jdbc/query create-connection
                          ["select game_id from board order by id desc limit 1"]))
       :game_id))

(describe "connects to sql lite jdbc database and sets it up"

  (it "insert game info into game table"
    (insert-game 0 1 1)
    (should= 0 (grab-last-game-numb))
    (delete-row {:table :game}))

  (it "inserts board into board table"
    (insert-board (init-board (->Three-by-three)) ":x" 1)
    (should= 1 (grab-last-board-numb))
    (delete-row {:table :board})
    )
  (it "deletes last row from table"
    (insert-game 1 1 1)
    (should= [1] (delete-row {:table :game})))

  (it "grabs the last board, player, levels"
    (insert-game 10000 1 1)
    (insert-board (init-board (->Three-by-three)) ":x" 1)
    (should=  {:board {:state [:e :e :e :e :e :e :e :e :e], :size 3, :dimension :two}, :player :x, :game-number 10000, :difficulty 1, :difficulty2 1}
             (grab-last-state))
    (delete-row {:table :game})
    (delete-row {:table :board}))

  (it "grabs the board, player, levels from a given gamenumber"
    (insert-game 2000 1 1)
    (insert-board (init-board (->Three-by-three)) ":x" 2000)
    (should= {:board {:state [:e :e :e :e :e :e :e :e :e], :size 3, :dimension :two}, :player :x, :game-number 2000, :difficulty 1, :difficulty2 1}
             (current-game-pieces 2000))
    (delete-row {:table :game})
    (delete-row {:table :board})
    (delete-row {:table :board})
    )

  )

