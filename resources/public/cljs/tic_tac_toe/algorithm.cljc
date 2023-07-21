(ns tic-tac-toe.algorithm
  (:require [tic-tac-toe.utility :refer [player-move switch-player terminal? terminal-state
                                         list-empties all-empty-space? human-turn]]))

(defn determine-depth [board]
  (cond
    (= (:size board) 4) 4
    (= (:dimension board) :three) 4
    (and (= (:dimension board) :two) (= (:size board))) 362880
  ))

(defn new-board-minimax [board player move]
  (if (= player X)
    (player-move board player move)
    (player-move board (switch-player player) move)))



(declare min-value)

(defn max-value [board player depth]
  (let [depth-limit (determine-depth board)]
  (if (or (terminal? board) (= depth depth-limit))
    (* (terminal-state board) (/ 1 depth))
    (loop [moves (list-empties (:state board))
           eval1 -1000]
      (if (empty? moves)
        eval1
        (let [move (first moves)
              new-board (player-move board player move)
              new-eval (min-value new-board (switch-player player) (inc depth))]
          (recur (rest moves) (max eval1 new-eval))
          ))))))

(defn new-board-minimax [board player move]
  (if (= player X)
    (player-move board player move)
    (player-move board (switch-player player) move)))

(def max-value (memoize max-value))

(defn min-value [board player depth]
  (let [depth-limit (determine-depth board)]
  (if (or (terminal? board) (= depth depth-limit))
    (* (terminal-state board) (/ 1 depth))
    (loop [moves (list-empties (:state board))
           eval3 1000]
      (if (empty? moves)
        eval3
        (let [move (first moves)
              new-board (player-move board player move)
              new-eval (max-value new-board (switch-player player) (inc depth))]
          (recur (rest moves) (min eval3 new-eval)))
        )))))

(def min-value (memoize min-value))

(defn minimax [board player]
  (loop [[move & moves] (list-empties (:state board))
         best-move -1
         best-val (if (= player X) -1000 1000)
         best-coll {best-move best-val}]
    (if move
      (let [new-board (player-move board player move)
            eval2 (min-value new-board (switch-player player) 1)]
        (recur moves move eval2 (conj best-coll {move eval2})))
      best-coll)))

(defn filter-greatest-vals [move-eval-map]
  (into {} (filter #(= (val %) (apply max (vals move-eval-map))) move-eval-map)))

(defn filter-smallest-vals [move-eval-map]
  (into {} (filter #(= (val %) (apply min (vals move-eval-map))) move-eval-map)))

(defn contains-neg? [best-map]
  (boolean (some #(neg? (val %)) best-map)))

(defn get-neg-key [best-map]
  (some (fn [[k v]] (when (neg? v) k)) best-map))

(defmulti best-first :dimension)

(defmethod best-first :two [board]
 0)

(defmethod best-first :three [board]
  13)

(defn best-move-win [board player best-moves-map]
  (let [moves (keys best-moves-map)]
    (let [check-move-win? (fn [player move]
                            (terminal? (player-move board player move)))]
      (first (filter #(or (check-move-win? player %) (check-move-win? (switch-player player) %)) moves)))))

(defn best-move [board player]
  (let [best-moves-map (dissoc (minimax board player) -1)
        check-best-moves (best-move-win board player best-moves-map)]
    (cond
      (all-empty-space? (:state board)) (best-first board)
      (not (nil? check-best-moves)) check-best-moves
      (= player X) (first (keys (filter-greatest-vals best-moves-map)))
      (= player O) (first (keys (filter-smallest-vals best-moves-map)))
      )))

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

(defn difficulty [level]
  (cond
    (= level 1) {:level :easy}
    (= level 2) {:level :medium}
    (= level 3) {:level :unbeatable}
    ))

(defmulti ai-turn (fn [x & args] (:display x)))

(defmethod ai-turn :print [board level player]
  (let [move (ai-standard (difficulty level) board player)]
    (println (str "AI " player " has chose move:" move))
    (player-move board player move)))

(defmethod ai-turn :gui [board level player]
  (let [move (ai-standard (difficulty level) board player)]
    (player-move board player move)))

(defn human-turn? [board current-player player]
  (or (and (= (:game-type board) :ai-vs-human) (= current-player player))
      (= (:game-type board) :human-vs-human)))


(defmulti process-game-board (fn [x & args] (:game-type x)))

(defmethod process-game-board :ai-vs-human [game-board current-player player difficulty difficulty2]
(if (= current-player player)
  (human-turn game-board current-player)
  (ai-turn game-board difficulty current-player)))

(defmethod process-game-board :ai-vs-ai [game-board current-player player difficulty difficulty2]
  (if (= current-player player)
    (ai-turn game-board difficulty current-player)
    (ai-turn game-board difficulty2 current-player)))

(defmethod process-game-board :human-vs-human [game-board current-player player difficulty difficulty1]
  (human-turn game-board current-player))
