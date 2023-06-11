(ns tic-tac-toe.database
  (:require [clojure.java.jdbc :as jdbc]
            [tic-tac-toe.utility :refer :all]))


(def create-connection
  {:dbtype "sqlite"
   :dbname "/Users/jakeogden/cleancoders/tic-tac-toe/tic-tac-toe.db"})


(defn insert-game [conn game-number difficulty1 difficulty2]
   (jdbc/insert! conn
                 :game
                 {:gamenumber game-number
                  :difficulty1 difficulty1
                  :difficulty2 difficulty2}))

(defn grab-last-id-game [conn]
  (get (last (jdbc/query conn  ["SELECT id AS id FROM game"])
  ) :id ))

(defn insert-board [conn board player game-id]
  (jdbc/insert! conn
                :board
                {:state (str board)
                 :game_id game-id
                 :player player}))


