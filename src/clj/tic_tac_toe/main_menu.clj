(ns tic-tac-toe.main-menu
(:require [quil.core :as q :include-macros true]
          [tic-tac-toe.utility :refer :all]
          [tic-tac-toe.db-and-edn :refer :all]
          [tic-tac-toe.database :refer :all]))

(defn rect [menu-x menu-y menu-w menu-h offset]
  (q/rect menu-x (+ menu-y offset) menu-w menu-h)
  )

(defn create-two-text [buttons text1 text2 menu-x half-w menu-y half-h]
  (if (= buttons 0)
    (q/text text1 (+ menu-x half-w) (+ (+ menu-y 0) half-h))
    (q/text text2 (+ menu-x half-w) (+ (+ menu-y 100) half-h))))


(defn create-rect [buttons menu-x menu-y menu-w menu-h]
  (if (= buttons 0)
    (rect menu-x menu-y menu-w menu-h 0)
    (rect menu-x menu-y menu-w menu-h 100)))

(defn create-three-rect [buttons menu-x menu-y menu-w menu-h]
  (cond
    (= buttons 0) (rect menu-x menu-y menu-w menu-h  0)
    (= buttons 1) (rect menu-x menu-y menu-w menu-h  100)
    (= buttons 2) (rect menu-x menu-y menu-w menu-h  200)
    ))

(defn create-three-text [buttons text1 text2 text3 menu-x half-w menu-y half-h]
  (cond
    (= buttons 0) (q/text text1 (+ menu-x half-w) (+ (+ menu-y 0) half-h))
    (= buttons 1) (q/text text2 (+ menu-x half-w) (+ (+ menu-y 100) half-h))
    (= buttons 2) (q/text text3 (+ menu-x half-w) (+ (+ menu-y 200) half-h))
    ))

(defn draw-two-button-menu [text1 text2]
  (q/background 128)
  (doseq [buttons (range 2)]
    (let [menu-w (/ (q/width) 2) menu-x (- (q/width) 220)
          half-w (/ menu-w 4)
          menu-h (/ (q/height) 5) menu-y (- (q/height) 250)
          half-h (/ menu-h 2)]
      (q/stroke 0)
      (q/fill (:cyan color))
      (create-rect buttons menu-x menu-y menu-w menu-h)
      (q/fill  (:pink color))
      (create-two-text buttons text1 text2 menu-x half-w menu-y half-h))))

(defn draw-three-button-menu [text1 text2 text3]
  (q/background 128)
  (doseq [buttons (range 3)]
    (let [menu-w (/ (q/width) 2) menu-x (- (q/width) 220)
          half-w (/ menu-w 5)
          menu-h (/ (q/height) 5) menu-y (- (q/height) 275)
          half-h (/ menu-h 2)]
      (q/stroke 0)
      (q/fill (:cyan color))
      (create-three-rect buttons menu-x menu-y menu-w menu-h)
      (q/fill (:pink color))
      (create-three-text buttons text1 text2 text3 menu-x half-w menu-y half-h))))

(defn select-player-gui [last-state game-map]
  (cond
    (= (:age (:board game-map)) :old) (:player last-state)
    (= (:game-type (:board game-map)) :ai-vs-human) (:player game-map)
    :else
    X))

(defmulti update-gui :screen)

(defmethod update-gui :persistence [game-map]
  game-map)

(defmethod update-gui :new-old [game-map]
  game-map)

(defmethod update-gui :game-mode [game-map]
  (if (contains? (:board game-map) :age)
    game-map
    (update-in game-map [:board] conj {:age :new})))

(defmethod update-gui :ask-difficulty [game-map]
  game-map)

(defmethod update-gui :ask-difficulty2 [game-map]
  game-map)

(defmethod update-gui :ask-board [game-map]
  game-map)

(defmethod update-gui :ask-player [game-map]
  game-map)

(defn old-or-new-map [game-map last-state current-player game-number player]
  (if (= (:age (:board game-map)) :old)
    (update-in (conj (conj (update-in last-state [:board] assoc :age :old) {:current-player current-player}) {:screen :game}) [:board] assoc :display :gui)
    (do
      (insert-game game-number (:difficulty game-map) (:difficulty2 game-map))
      (assoc (assoc (assoc (assoc game-map :player player) :current-player current-player)
               :screen :game) :game-number game-number))))

(defmethod update-gui :game-setup [game-map]
  (let [last-state (last-game (:board game-map))
        player (select-player-gui last-state game-map)
        current-player (select-current-player (:board game-map) last-state)
        game-number (increase-game-number-or-no game-map)]
    (old-or-new-map game-map last-state current-player game-number player)))

(defmulti draw :screen)

(defmethod draw :persistence [game-map]
  (q/clear)
  (let [text1 "Data-base"
        text2 "Edn-file"]
    (draw-two-button-menu text1 text2)))

(defmethod draw :new-old [game-map]
  (q/clear)
  (let [text1 "Resume"
        text2 "New-game"]
    (draw-two-button-menu text1 text2)))


(defmethod draw :game-mode [game-map]
  (q/clear)
  (let [text1 "ai-vs-human"
        text2 "human-vs-human"
        text3 "ai-vs-ai"]
    (draw-three-button-menu text1 text2 text3)))

(defmethod draw :ask-difficulty [game-map]
  (q/clear)
  (let [text1 "easy"
        text2 "medium"
        text3 "unbeatable"]
    (draw-three-button-menu text1 text2 text3)))

(defmethod draw :ask-difficulty2 [game-map]
  (q/clear)
  (let [text1 "easy"
        text2 "medium"
        text3 "unbeatable"]
    (draw-three-button-menu text1 text2 text3)))

(defmethod draw :ask-board [game-map]
  (q/clear)
  (let [text1 "classic"
        text2 "4-by-4"
        text3 "3D"]
    (draw-three-button-menu text1 text2 text3)))

(defmethod draw :ask-player [game-map]
  (q/clear)
  (let [text1 "X"
        text2 "O"]
    (draw-two-button-menu text1 text2)))

(defmethod draw :game-setup [game-map]
  (q/clear)
  (let [text1 "X"
        text2 "O"]
    (draw-two-button-menu text1 text2)))

(defmethod draw :game-over [game-map]
  (q/clear)
  (let [text1 "Play again?"
        text2 "Quit"]
    (draw-two-button-menu text1 text2)))


(defn button-selected? [x y y-offset y-offset2]
  (and (and (> x (- (q/width) 220)) (< x (+ (- (q/width) 220) (/ (q/width) 2))))
       (and (> y (+ (- (q/height) y-offset) y-offset2)) (< y (+ (+ (- (q/height) y-offset) y-offset2) (/ (q/height) 5))))))

(defn check-last-game-state [game-map]
  (let [last-game-state (last-game game-map)]
    (if (or (nil? (:board last-game-state)) (not (last-game-not-finished? last-game-state)))
      :game-mode
      :new-old)))

(defn ask-difficulty-or-board [game-board]
  (cond
    (= (:game-type (:board game-board)) :ai-vs-human) :ask-player
    (= (:game-type (:board game-board)) :ai-vs-ai) :ask-difficulty2))

(defn update-menu-board [game-map new-map new-screen]
  (assoc (update-in game-map [:board] conj new-map) :screen new-screen))

(defmulti handle-mouse :screen)

(defmethod handle-mouse :persistence [game-map coord]
    (cond
      (button-selected? (:x coord) (:y coord) 250 0)   (update-menu-board game-map {:file-type :db} (check-last-game-state  {:file-type :db}))
      (button-selected? (:x coord) (:y coord) 250 100) (update-menu-board game-map {:file-type :edn} (check-last-game-state {:file-type :edn}))
      :else
      game-map))

(defmethod handle-mouse :new-old [game-map coord]
    (cond
      (button-selected? (:x coord) (:y coord) 250 0)   (update-menu-board game-map {:age :old} :game-setup)
      (button-selected? (:x coord) (:y coord) 250 100) (update-menu-board game-map {:age :new} :game-mode)
      :else
      game-map))

(defmethod handle-mouse :game-mode [game-map coord]
    (cond
      (button-selected? (:x coord) (:y coord) 275 0)   (update-menu-board game-map {:game-type :ai-vs-human} :ask-difficulty)
      (button-selected? (:x coord) (:y coord) 275 100) (update-menu-board game-map {:game-type :human-vs-human} :ask-board)
      (button-selected? (:x coord) (:y coord) 275 200) (update-menu-board game-map {:game-type :ai-vs-ai} :ask-difficulty)
      :else
      game-map))

(defn update-menu-map [game-map key value new-screen]
  (assoc (assoc game-map key value) :screen new-screen))

(defmethod handle-mouse :ask-difficulty [game-map coord]
    (cond
      (button-selected? (:x coord) (:y coord) 275 0)   (update-menu-map game-map :difficulty 1 (ask-difficulty-or-board game-map))
      (button-selected? (:x coord) (:y coord) 275 100) (update-menu-map game-map :difficulty 2 (ask-difficulty-or-board game-map))
      (button-selected? (:x coord) (:y coord) 275 200) (update-menu-map game-map :difficulty 3 (ask-difficulty-or-board game-map))
      :else
      game-map))

(defmethod handle-mouse :ask-difficulty2 [game-map coord]
    (cond
      (button-selected?(:x coord) (:y coord) 275 0)   (update-menu-map game-map :difficulty2 1 :ask-board)
      (button-selected?(:x coord) (:y coord) 275 100) (update-menu-map game-map :difficulty2 2 :ask-board)
      (button-selected?(:x coord) (:y coord) 275 200) (update-menu-map game-map :difficulty2 3 :ask-board)
      :else
      game-map))

(defmethod handle-mouse :ask-board [game-map coord]
   (cond
      (button-selected? (:x coord) (:y coord) 275 0) (update-menu-board game-map (init-board (->Three-by-three)) :game-setup)
      (button-selected? (:x coord) (:y coord) 275 100) (update-menu-board game-map (init-board (->Four-by-four)) :game-setup)
      (button-selected? (:x coord) (:y coord) 275 200) (update-menu-board game-map (init-board (->Three-dimension)) :game-setup)
      :else
      game-map))

(defmethod handle-mouse :ask-player [game-map coord]
   (cond
      (button-selected? (:x coord) (:y coord) 250 0)   (update-menu-map game-map :player X :ask-board)
      (button-selected? (:x coord) (:y coord) 250 100) (update-menu-map game-map :player O :ask-board)
      :else
      game-map))

