(ns tic-tac-toe.core
  (:require
    [tic-tac-toe.database :refer :all]
    [tic-tac-toe.utility :refer :all]
    [tic-tac-toe.algorithm :refer :all]
    [tic-tac-toe.file-persistence :refer :all]
    [tic-tac-toe.db-and-edn :refer :all]
    [tic-tac-toe.gui :refer :all]))


(defn enough-moves-win? [board]
  (<= (count (list-empties board)) (- (count board) (- (* 2) 2))))

(defn ask-difficulty []
  (println "Please enter a difficulty for this ai:\n 1) easy\n 2) medium\n 3) unbeatable")
  (read))


(def board-choice {1 (init-board (->Three-by-three))
                   2 (init-board (->Four-by-four))
                   3 (init-board (->Three-dimension))})

(defn get-board []
  (println "Select a board\n 1) 3\n 2) 4\n 3) 3D")
  (let [choice (read)]
    (get board-choice choice)
  ))


(defn choose-persistence []
  (println "Please choose to run off the edn file or database\n 1) edn file\n 2) database")
  (let [choice (read)]
    (cond
      (= choice 1) {:file-type :edn}
      (= choice 2) {:file-type :db})))


(defn play-game  [board current-player player game-number difficulty difficulty2]
  (loop [game-board board current-player current-player]
    (save-current-board game-board current-player game-number difficulty difficulty2)
    (print-board game-board)
    (if (terminal? game-board)
      (game-over game-board)
      (let [new-board (process-game-board game-board current-player player difficulty difficulty2)]
        (recur new-board (switch-player current-player)))
      )))



(defn run-ai-vs-human [per-choice]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))
        difficulty (ask-difficulty)]
    (insert-game game-number difficulty 0)
    (save-current-board board X game-number difficulty 0)
    (conj (conj (conj board {:game-type :ai-vs-human :age :new}) {:display :print}) per-choice)))


(defn run-human-vs-human [per-choice]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))]
    (insert-game game-number 0 0)
    (save-current-board board X game-number 0 0)
    (conj (conj (conj board {:game-type :human-vs-human :age :new}) {:display :print}) per-choice)))

(defn run-ai-vs-ai [per-choice]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))
        difficulty (ask-difficulty)
        difficulty2 (ask-difficulty)]
    (insert-game game-number difficulty difficulty2)
    (save-current-board board X game-number difficulty difficulty2)
    (conj (conj (conj board {:game-type :ai-vs-ai :age :new}) {:display :print}) per-choice)))

(defn game-mode [per-choice]
  (println "Please select a game mode:\n 1) ai-vs-human\n 2) human-vs-human\n 3) ai-vs-ai")
  (let [choice (read)]
    (cond
      (= choice 1) (run-ai-vs-human per-choice )
      (= choice 2) (run-human-vs-human per-choice )
      (= choice 3) (run-ai-vs-ai per-choice )
    :else
      nil)))


(defn game-start-options [persistence-choice]
  (let [last-state (last-game persistence-choice)]
  (if (nil? (:board last-state))
    (game-mode persistence-choice)
    (if (last-game-not-finished? last-state)
    (do
      (println "Would you like to continue the last game or start a new one? \n 1) Resume \n 2) New Game")
      (let [choice (read)]
        (cond
          (= choice 1) (assoc (conj (assoc (:board last-state) :age :old) persistence-choice) :display :print)
          (= choice 2) (game-mode persistence-choice)
        )))
    (game-mode persistence-choice)))))

(defn start-game  [board per-choice]
  (let [last-game-state (last-game per-choice)
        game-number (:game-number last-game-state)
        player (select-player board last-game-state)
        current-player (select-current-player board last-game-state)
        difficulty (:difficulty last-game-state)
        difficulty2 (:difficulty2 last-game-state)]
    (play-game board current-player player game-number difficulty difficulty2)))

(defn run-game []
  (let [per-choice (choose-persistence)
        board (game-start-options per-choice)]
    (start-game board per-choice)))

(defn print-or-gui [user-text]
  (cond
    (= user-text "print") (run-game)
    (= user-text "gui") (main-sketch)
    ))

(defn -main [& args]
  (print-or-gui (first args)))