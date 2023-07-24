(ns tic-tac-toe.utility
  (:require [clojure.string :as str]))


(def X :x)
(def O :o)
(def EMPTY :e)

(def color {:cyan [0 204 204] :pink [255 0 127] :black [0 0 0]})

(def game-state {:board nil :player nil :game-number 0 :difficulty nil :difficulty2 nil})

(defn ->game-state [board current-player game-number level level-two]
  (assoc game-state :board board :player current-player :game-number game-number :difficulty level :difficulty2 level-two))

(defprotocol Board
  (init-board [this])

  )

(defrecord Four-by-four []
  Board
  (init-board [this] {:state (vec (repeat (* 4 4) EMPTY)) :size 4 :dimension :two})

  )

(defrecord Three-by-three []
  Board
  (init-board [this] {:state (vec (repeat (* 3 3) EMPTY)) :size 3 :dimension :two})
  )

(defrecord Three-dimension []
  Board
  (init-board [this] {:state (vec (repeat (* 3 3 3) EMPTY)) :size 3 :dimension :three})
  )

(defn has-empty-space? [board]
  (boolean (some #(= EMPTY %) board)))

(defn all-empty-space? [board]
  (boolean (not (some #(= X %) board))))

(defn is-empty? [board position]
  (= (nth board position) EMPTY))

(defn player-move [board player position]
  (update board :state #(assoc % position player)))

(defn switch-player [current-player]
  (if (= current-player X) O X))

(defmulti win? :dimension)

(defmethod win? :two [board player]
  (let [size (:size board)
        rows (partition size (:state board))
        cols (apply mapv vector rows)
        diag1 (for [i (range size)] (nth (nth rows i) i))
        diag2 (for [i (range size)] (nth (nth rows i) (- size (inc i))))
        all-spaces (concat rows cols [diag1 diag2])
        win-cond (fn [space] (every? #(= player %) space))]
    (boolean (some win-cond all-spaces))))

(defmethod win? :three [board player]
  (let [size (:size board)
        rows (partition size (:state board))
        cols (for [i [[0 3 6] [1 4 7] [2 5 8] [9 12 15] [10 13 16] [11 14 17]
                      [18 21 24] [19 22 25] [20 23 26]]]
               (mapv #(get (:state board) %) i))
        diags (for [i [[0 4 8] [2 4 6] [9 13 17] [11 13 15] [18 22 26] [20 22 24]]]
                (mapv #(get (:state board) %) i))
        depth-cols (apply mapv vector (partition 9 (:state board)))
        depth-rows (for [i [[0 10 20] [3 13 23] [6 16 26]]] (mapv #(get (:state board) %) i))
        depth-diags (for [i [[0 13 26] [2 13 24]]] (mapv #(get (:state board) %) i))
        all-spaces (concat rows cols diags depth-cols depth-rows depth-diags)
        win-cond (fn [space] (every? #(= player %) space))]
    (boolean (some win-cond all-spaces))))

(defn list-empties [board]
  (vec (keep-indexed (fn [index value]
                       (when (= value EMPTY) index)) board)))

(defn terminal-state [board]
  (cond
    (win? board X) 10
    (win? board O) -10
    :else
    0))

(defn terminal? [board]
  (or (win? board X) (win? board O) (not (has-empty-space? (:state board)))))

(defmulti game-over (fn [x & args] (:display x)))

(defmethod game-over :print [board]
  (cond
    (= (terminal-state board) 10) (println "Player X has won the game")
    (= (terminal-state board) -10) (println "Player O has won the game")
    (= (terminal-state board) 0) (println "The game has ended in draw")
    ))

(defmethod game-over :gui [board current-player player game-number difficulty difficulty2]
  {:screen :game-over :board board :current-player current-player :player player :game-number game-number
   :difficulty difficulty :difficulty2 difficulty2})


(defn choose-player []
  (println "Choose your character \n 1) X\n 2) O")
  (let [choice (read)]
    (if (= choice 1) :x :o))
  )

(defn game-player-rel [board]
  (if (= (:game-type board) :ai-vs-human) (choose-player) X))

(defn select-player [board last-state]
  (cond
    (= (:age board) :new) (game-player-rel board)
    (= (:age board) :old) (:player last-state)
    ))

(defn select-current-player [board last-state]
  (cond
    (= (:age board) :new) X
    (= (:age board) :old) (:player last-state)
    ))



(defn human-turn [board player]
  (println (str "Its player " player "'s turn please enter a valid move:"))
  (let [move (read)]
    (if (is-empty? (:state board) move)
      (player-move board player move)
      (do
        (println "Invalid move please try again:")
        (player-move board player (read))))))


(defn get-player [board pos]
  (nth (:state board) pos))

(defn player-to-color [player]
  (cond
    (= player :x) (:black color)
    (= player :o) (:pink color)
    :else
    nil))


(defmulti print-board :dimension)

(defmethod print-board :two [board]
  (doseq [row (partition (:size board) (:state board))]
    (println (str/join " | " row))))

(defmethod print-board :three [board]
  (doseq [layer (partition 9 (:state board))]
    (doseq [row (partition (:size board) layer)]
      (println (str/join " | " row)))
    (println (str/join "---" (repeat (:size board) "+")))
    (println)))


