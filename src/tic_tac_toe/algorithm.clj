(ns tic-tac-toe.algorithm
  (:require [tic-tac-toe.utility :refer :all])
  )

(defn check-initial-moves [player sub-board]
  (boolean (and (some #(= (switch-player player) %) sub-board) (every? #(not= player %) sub-board))))

(defn first-moves [board player]
  (let [first-row (subvec board 0 4)
        second-row (subvec board 4 8)
        third-row (subvec board 8 12)
        fourth-row (subvec board 12 16)
        first-col (mapv #(nth board %) [0 4 8 12])
        second-col (mapv #(nth board %) [1 5 9 13])
        third-col (mapv #(nth board %) [2 6 10 14])
        fourth-col (mapv #(nth board %) [3 7 11 15])
        ]
    (cond
      (check-initial-moves player first-row) (rand-nth [0 1 2 3])
      (check-initial-moves player first-col) (rand-nth [0 4 8 12])
      (check-initial-moves player second-row) (rand-nth [4 5 6 7])
      (check-initial-moves player second-col) (rand-nth [1 5 9 13])
      (check-initial-moves player third-row) (rand-nth [8 9 10 11])
      (check-initial-moves player third-col) (rand-nth [2 6 10 14])
      (check-initial-moves player fourth-row) (rand-nth [12 13 14 15])
      (check-initial-moves player fourth-col) (rand-nth [3 7 11 15])
      :else
      0)))


(declare min-value)

(defn max-value [board player depth]
  (let [depth-limit (if (= (:dimension board) :three) 4 30)]
  (if (or (terminal? board) (= depth depth-limit))
    (* (terminal-state board) (/ 1 depth))
    (loop [moves (list-empties (:state board))
           eval -1000]
      (if (empty? moves)
        eval
        (let [move (first moves)
              new-board (player-move board player move)
              new-eval (min-value new-board (switch-player player) (inc depth))]
          ;(prn "new-eval" new-eval)
          (recur (rest moves) (max eval new-eval))
          ))))))

(def max-value (memoize max-value))

(defn min-value [board player depth]
  (let [depth-limit (if (= (:dimension board) :three) 4 30)]
  (if (or (terminal? board) (= depth depth-limit))
    (* (terminal-state board) (/ 1 depth))
    (loop [moves (list-empties (:state board))
           eval 1000]
      (if (empty? moves)
        eval
        (let [move (first moves)
              new-board (player-move board player move)
              new-eval (max-value new-board (switch-player player) (inc depth))]
          (recur (rest moves) (min eval new-eval)))
        )))))

(def min-value (memoize min-value))

(defn minimax [board player]
  (loop [[move & moves] (list-empties (:state board))
         best-move -1
         best-val (if (= player X) -1000 1000)]
    (if move
      (let [new-board (player-move board player move)
            eval (min-value new-board (switch-player player) 1)]
        (if (and (= player O) (< eval best-val))
          (recur moves move eval)
          (if (and (= player X) (> eval best-val))
            (recur moves move eval)
            (recur moves best-move best-val))))
      [best-move best-val])))

(defn best-move [board player]
  (if (and (= (:size board) 4) (> (count (list-empties (:state board))) 12))
    (loop [initial-move (first-moves (:state board) player)]
      (if (is-empty? (:state board) initial-move)
        initial-move
        (recur (first-moves (:state board) player))))
    (first (minimax board player))))

(defn level-decision? [standard]
  (let [random (rand)]
    (< random standard)))

(defmulti Ai-level? :level)

(defmethod Ai-level? :easy [_]
  (level-decision? 0.8))

(defmethod Ai-level? :medium [_]
  (level-decision? 0.3))

(defmethod Ai-level? :unbeatable [_]
  (level-decision? -1.0))

(defn ai-standard [level board player]
  (if (Ai-level? level)
    (rand-nth (list-empties (:state board)))
    (best-move board player)))


