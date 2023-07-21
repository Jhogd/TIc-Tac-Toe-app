(ns tic-tac-toe.database
  (:require [clojure.java.jdbc :as jdbc]
            [tic-tac-toe.utility :refer :all]))


(def create-connection
  {:dbtype "sqlite"
   :dbname "/Users/jakeogden/cleancoders/tic-tac-toe/tic-tac-toe.db"})


(defn insert-game [game-number difficulty1 difficulty2]
   (jdbc/insert! create-connection
                 :game
                 {:gamenumber game-number
                  :difficulty1 difficulty1
                  :difficulty2 difficulty2}))

(defn grab-last-id-game []
  (get (first (jdbc/query create-connection  ["select id from game order by id desc limit 1"]))
   :id))

(defn grab-last-id-board []
  (get (first (jdbc/query create-connection  ["select id from board order by id desc limit 1"]))
       :id))

(defn insert-board  [board player game-id]
  (jdbc/insert! create-connection
                :board
                {:state (str board)
                 :game_id game-id
                 :player player}))

(defmulti delete-row :table)

(defmethod delete-row :game [_]
  (if (nil? (grab-last-id-game))
    nil
  (jdbc/delete! create-connection
                :game
                [(str "id = " (grab-last-id-game))])))

(defmethod delete-row :board [_]
  (if (nil? (grab-last-id-board))
    nil
  (jdbc/delete! create-connection
                :board
                [(str "id = " (grab-last-id-board))])))

(defn grab-last-state []
  (let [board-player (jdbc/query create-connection
                                    ["select state, player from board order by id DESC limit 1"])
        game-difficulty1-2 (jdbc/query create-connection
                                  ["select gamenumber, difficulty1, difficulty2 from game order by gamenumber desc limit 1"])
        board (:state (first board-player))
        player (:player (first board-player))
        game-number (:gamenumber (first game-difficulty1-2))
        difficulty1 (:difficulty1 (first game-difficulty1-2))
        difficulty2 (:difficulty2 (first game-difficulty1-2))]
    (->game-state (if (nil? board) board (read-string board)) (if (nil? player) player (read-string player)) game-number difficulty1 difficulty2)))

(defn current-game-pieces [gameId]
  (let [board-player (jdbc/query create-connection
                                 ["select state, player from board where game_id = ?" gameId])
        game-difficulty1-2 (jdbc/query create-connection
                                       ["select gamenumber, difficulty1, difficulty2 from game where gamenumber = ?"  gameId])
        board (:state (first board-player))
        player (:player (first board-player))
        game-number (:gamenumber (first game-difficulty1-2))
        difficulty1 (:difficulty1 (first game-difficulty1-2))
        difficulty2 (:difficulty2 (first game-difficulty1-2))]
    (->game-state (if (nil? board) board (read-string board)) (if (nil? player) player (read-string player)) game-number difficulty1 difficulty2)))