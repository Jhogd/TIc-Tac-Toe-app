(ns tic-tac-toe.core
  (:require
    [tic-tac-toe.database :refer :all]
    [tic-tac-toe.utility :refer :all]
    [tic-tac-toe.algorithm :refer :all]
    [tic-tac-toe.file-persistence :refer :all]
    [tic-tac-toe.gui :refer :all]
           ))


(defn enough-moves-win? [board]
  (<= (count (list-empties board)) (- (count board) (- (* 2) 2))))

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

(defn choose-persistence []
  (println "Please choose to run off the edn file or database\n 1) edn file\n 2) database")
  (let [choice (read)]
    (cond
      (= choice 1) {:file-type :edn}
      (= choice 2) {:file-type :db})))

(defn ask-display []
  (println "How would you like to display the game?\n 1) Terminal \n 2) Gui")
  (let [choice (read)]
    (cond
      (= choice 1)  {:display :print}
      (= choice 2)  {:display :gui}
      )))

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

(defn run-ai-vs-human [per-choice display]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))
        difficulty (ask-difficulty)]
    (insert-game game-number difficulty 0)
    (save-current-board board X game-number difficulty 0)
    (conj (conj (conj board {:game-type :ai-vs-human :age :new}) display) per-choice)))


(defn run-human-vs-human [per-choice display]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))]
    (insert-game game-number 0 0)
    (save-current-board board X game-number 0 0)
    (conj (conj (conj board {:game-type :human-vs-human :age :new}) display) per-choice)))

(defn run-ai-vs-ai [per-choice display]
  (let [board (get-board)
        game-number (inc (get-game-number per-choice))
        difficulty (ask-difficulty)
        difficulty2 (ask-difficulty)]
    (insert-game game-number difficulty difficulty2)
    (save-current-board board X game-number difficulty difficulty2)
    (conj (conj (conj board {:game-type :ai-vs-ai :age :new}) display) per-choice)))

(defn game-mode [per-choice display]
  (println "Please select a game mode:\n 1) ai-vs-human\n 2) human-vs-human\n 3) ai-vs-ai")
  (let [choice (read)]
    (cond
      (= choice 1) (run-ai-vs-human per-choice display)
      (= choice 2) (run-human-vs-human per-choice display)
      (= choice 3) (run-ai-vs-ai per-choice display)
    :else
      nil)))


(defn game-start-options [display persistence-choice]
  (let [last-state (last-game persistence-choice)]
  (if (nil? (:board last-state))
    (game-mode persistence-choice display)
    (if (last-game-not-finished? last-state)
    (do
      (println "Would you like to continue the last game or start a new one? \n 1) Resume \n 2) New Game")
      (let [choice (read)]
        (cond
          (= choice 1) (conj (assoc (assoc (:board last-state) :age :old) :display (get display :display)) persistence-choice)
          (= choice 2) (game-mode persistence-choice display)
        )))
    (game-mode persistence-choice display)))))

(defmulti start-game :display)

(defmethod start-game :gui [board per-choice]
  (let [last-state (last-game per-choice)
        player (select-player board last-state)
        current-player (select-current-player board last-state)]
    (main-sketch (conj (assoc (assoc last-state :board board)
                         :player player) {:current-player current-player}))))

(defmethod start-game :print [board per-choice]
  (let [last-game-state (last-game per-choice)
        game-number (:game-number last-game-state)
        player (select-player board last-game-state)
        current-player (select-current-player board last-game-state)
        difficulty (:difficulty last-game-state)
        difficulty2 (:difficulty2 last-game-state)]
    (play-game board current-player player game-number difficulty difficulty2)))

(defn run-game [display per-choice]
  (let [board (game-start-options display per-choice)]
    (start-game board per-choice)))


(defn -main [& args]
  (let [display (ask-display)]
    (run-game display (choose-persistence))))