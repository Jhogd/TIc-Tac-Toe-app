(ns tic-tac-toe.core
  (:require [clojure.string :as str]
  [tic-tac-toe.utility :refer :all]
  [tic-tac-toe.algorithm :refer :all]
  [tic-tac-toe.file-persistence :refer :all]))


(defn game-over [board]
  (cond
    (= (terminal-state board) 10) (println "Player X has won the game")
    (= (terminal-state board) -10) (println "Player O has won the game")
    (= (terminal-state board) 0) (println "The game has ended in draw")
    ))

(defn enough-moves-win? [board]
  (<= (count (list-empties board)) (- (count board) (- (* 2) 2))))


(defn difficulty [level]
  (cond
    (= level 1) {:level :easy}
    (= level 2) {:level :medium}
    (= level 3) {:level :unbeatable}
    ))


(defn ask-difficulty []
  (println "Please enter a difficulty for this ai:\n 1) easy\n 2) medium\n 3) unbeatable")
  (read))

(defn choose-player []
  (println "Choose your character \n 1) X\n 2) O")
  (let [choice (read)]
    (if (= choice 1) :x :o))
  )

(defn get-board []
  (println "Select a board\n 1) 3\n 2) 4\n 3) 3D")
  (let [choice (read)]
    (cond
      (= choice 1) (init-board (->Three-by-three))
      (= choice 2) (init-board (->Four-by-four))
      (= choice 3) (init-board (->Three-dimension))
      :else
      nil))
  )


(defn ai-turn [board level player]
  (let [move (ai-standard (difficulty level) board player)]
    (println (str "AI " player " has chose move:" move))
    (player-move board player move)))

(defn human-turn [board player]
  (println (str "Its player " player "'s turn please enter a valid move:"))
  (let [move (read)]
    (if (is-empty? (:state board) move)
      (player-move board player move)
      (do
        (println "Invalid move please try again:")
        (player-move board player (read))))))

(def edn-file "official-game-state.txt")

(defn last-game-not-finished? []
  (not (terminal? (:board (grab-last-game edn-file)))))

(defn grab-last-board []
  (:board (grab-last-game edn-file)))


(defn grab-last-player []
  (:player (grab-last-game edn-file)))

(defn grab-last-level []
  (:difficulty (grab-last-game edn-file)))

(defn grab-last-level2 []
  (:difficulty2 (grab-last-game edn-file)))

(defn grab-game-number []
  (:game-number (grab-last-game edn-file)))


;(defn ai-vs-human [game]
(defn ai-vs-human [board player game-number difficulty]
    (loop [game-board board current-player X]
      (save-board game-board current-player game-number difficulty nil edn-file)
      (print-board game-board)
      (if (terminal? game-board)
        (game-over game-board)
        (if (= current-player player)
          (let [new-board (human-turn game-board current-player)]
            (recur new-board (switch-player current-player)))
          (let [new-board (ai-turn game-board difficulty current-player)]
            (recur new-board (switch-player current-player)))))))

;(defn ai-vs-ai [game]
(defn ai-vs-ai [board player game-number difficulty-1 difficulty-2]
    (loop [game-board board current-player player]
      (save-board game-board current-player game-number difficulty-1 difficulty-2 edn-file)
      (print-board game-board)
      (if (terminal? game-board)
        (game-over game-board)
        (if (= current-player X)
          (let [new-board (ai-turn game-board difficulty-1 current-player)]
            (recur new-board (switch-player current-player)))
          (let [new-board (ai-turn game-board difficulty-2 current-player)]
            (recur new-board (switch-player current-player)))))))


(defn human-vs-human [board player game-number]
    (loop [game-board board current-player player]
      (save-board game-board current-player game-number nil nil edn-file)
      (print-board game-board)
      (if (terminal? game-board)
        (game-over game-board)
        (let [new-board (human-turn game-board current-player)]
          (recur new-board (switch-player current-player)))
        )))



(defn game-mode []
  (println "Please select a game mode:\n 1) ai-vs-human\n 2) human-vs-human\n 3) ai-vs-ai")
  (let [choice (read)]
    (cond
      (= choice 1) (ai-vs-human (get-board) (choose-player) (inc (grab-game-number)) (ask-difficulty))
      (= choice 2) (human-vs-human (get-board) X (inc (grab-game-number)))
      (= choice 3) (ai-vs-ai (get-board)  X  (inc (grab-game-number)) (ask-difficulty) (ask-difficulty))
    :else
      nil)))

(defn resume-game []
  (let [board (grab-last-board)
        ;game (grab-last-game)
        player (grab-last-player)
        difficulty-1 (grab-last-level)
        difficulty-2 (grab-last-level2)
        game-number (grab-game-number)]
    ;(make-move game)
    (cond
      (= difficulty-1 nil) (human-vs-human board player game-number)
      (and (not= difficulty-2 nil) (not= difficulty-1 nil)) (ai-vs-ai board player game-number difficulty-1 difficulty-2)
      :else
      (ai-vs-human board player game-number difficulty-1)))
  )

(defn game-start-options []
  (if (last-game-not-finished?)
    (do
      (println "Would you like to continue the last game or start a new one? \n 1) Resume \n 2) New Game")
      (let [choice (read)]
        (cond
          (= choice 1) (resume-game)
          (= choice 2) (game-mode))
        ))
    (game-mode)))

(defn -main [& args]
  (println (grab-games edn-file))
  (game-start-options))