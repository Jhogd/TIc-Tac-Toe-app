(ns tic-tac-toe.db-and-edn
  (:require [tic-tac-toe.file-persistence :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.utility :refer :all]))


(defmulti last-game :file-type)

(defmethod last-game :edn [_]
  (grab-last-game edn-file))

(defmethod last-game :db [_]
  (grab-last-state))

(defn last-game-not-finished? [last-game-state]
  (not (terminal? (:board last-game-state))))

(defn get-game-number [per-choice]
  (let [check-numb (:game-number (last-game per-choice))]
    (if (nil? check-numb)
      0
      check-numb)))

(defn save-current-board [board player number diff1 diff2]
  (insert-board board player number)
  (save-board board player number diff1 diff2 edn-file))

(defn increase-game-number-or-no [game-map]
  (if (= (:age (:board game-map)) :old)
    (get-game-number (:board game-map))
    (inc (get-game-number (:board game-map)))))