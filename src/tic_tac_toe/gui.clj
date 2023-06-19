(ns tic-tac-toe.gui
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.algorithm :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            ))

(defn save-current-board [board player number diff1 diff2]
  (insert-board board player number)
  (save-board board player number diff1 diff2 edn-file))


(defn setup [starting-game-state]
  {:frame-rate     (q/frame-rate 60)
   :color-mode     (q/color-mode :rgb)
   :background     (q/background 255)
   :board          (:board starting-game-state)
   :current-player (:current-player starting-game-state)
   :player         (:player starting-game-state)
   :game-number    (:game-number starting-game-state)
   :difficulty     (:difficulty starting-game-state)
   :difficulty2    (:difficulty2 starting-game-state)
   })

(defmulti square-size :dimension)

(defmethod square-size :two [board]
  (/ (q/width) (:size board)))

(defmethod square-size :three [board]
  (/ (q/width) (* (:size board) (:size board))))

(defmulti mouse-to-board (fn [x & args] (:dimension x)))

(defmethod mouse-to-board :two [board x-cord y-cord]
  (+ (int (/ x-cord (square-size board)))
     (* (:size board) (int (/ y-cord (square-size board))))))

(defmethod mouse-to-board :three [board x-cord y-cord]
  (let [square (square-size board)
        shift (* square (:size board))
        layer (quot x-cord shift)
        x (quot (mod x-cord shift) square)
        y (quot (mod y-cord shift) square)]
    (+ x (* (:size board) y) (* (:size board) (:size board) layer))))



(defmulti play-game (fn [x & args] (:display x)))

(defmethod play-game :print [board current-player player game-number difficulty difficulty2]
  (loop [game-board board current-player current-player]
    (save-current-board game-board current-player game-number difficulty difficulty2)
    (print-board game-board)
    (if (terminal? game-board)
      (game-over game-board current-player game-number difficulty difficulty2)
      (let [new-board (process-game-board game-board current-player player difficulty difficulty2)]
        (recur new-board (switch-player current-player)))
      )))

(defmethod play-game :gui [board current-player player game-number difficulty difficulty2]
  (if (human-turn? board current-player player)
    {:board  board :current-player current-player
     :player player :game-number game-number :difficulty difficulty :difficulty2 difficulty2}
    (do
      (if (terminal? board)
        (do
        (save-current-board board current-player game-number difficulty difficulty2)
        (game-over board current-player player game-number difficulty difficulty2))
        (let [new-board (process-game-board board current-player player difficulty difficulty2)]
          (save-current-board new-board (switch-player current-player) game-number difficulty difficulty2)
          {:board  new-board :current-player (switch-player current-player)
           :player player :game-number game-number :difficulty difficulty :difficulty2 difficulty2})))))


(defn update-grid [{:keys [board current-player player game-number difficulty difficulty2] :as state}]
  (play-game board current-player player game-number difficulty difficulty2))

(defmulti draw-grid :dimension)

(defmethod draw-grid :two [board]
  (let [square (square-size board)]
    (doseq [x (range 0 (:size board))
            y (range 0 (:size board))]
      (q/stroke 0)
      (q/fill 255)
      (q/rect (* y square) (* x square) square square))))

(defmethod draw-grid :three [board]
  (q/background 255)
  (let [square (square-size board)
        shift (* square (:size board))]
    (doseq [layer (range  3)]
    (doseq [x (range 0 (:size board))
            y (range 0 (:size board))]
      (let [shift-x (* shift layer)]
      (q/stroke 0)
      (q/fill 255)
      (q/rect (+ (* y square) shift-x) (+ (* x square) shift-x) square square))))))

(defmulti draw-move :dimension)

(defmethod draw-move :two [board]
  (doseq [x (range 0 (:size board))
          y (range 0 (:size board))]
    (let [square (square-size board)
          half-size (/ square 2)
          player (get-player board (+ x (* y (:size board))))
          color (player-to-color player)]
      (q/stroke 0)
      (q/stroke-weight 2)
      (q/fill color)
      (cond
        (= player :x) (q/text "X" (+ (* x square) half-size) (+ (* y square) half-size))
        (= player :o) (q/text "O" (+ (* x square) half-size) (+ (* y square) half-size))
        :else
        nil))))

(defmethod draw-move :three [board]
    (let [square (square-size board)
          shift (* square (:size board))
          half-size (/ square 2)]
      (doseq [layer (range 0 3)
              x (range 0 (:size board))
              y (range 0 (:size board))]
        (let [shift-x (* shift layer)
              player (get-player board (+ x (* y (:size board)) (* layer (* (:size board) (:size board)))))
              color (player-to-color player)]
          (q/stroke 0)
          (q/stroke-weight 2)
          (q/fill color)
          (cond
            (= player :x) (q/text "X" (+ (* x square) half-size shift-x) (+ (* y square) half-size shift-x))
            (= player :o) (q/text "O" (+ (* x square) half-size shift-x) (+ (* y square) half-size shift-x))
            :else
            nil)))))


  (defn draw [{:keys [board]}]
  (q/clear)
  (draw-grid board)
  (draw-move board)
  )

(defn handle-mouse [{:keys [board current-player player game-number difficulty difficulty2] :as state} coord]
  (if (human-turn? board current-player player)
    (let [move (mouse-to-board board (:x coord) (:y coord))]
      (if (is-empty? (:state board) move)
        (let [new-board (player-move board current-player move)]
          (save-current-board new-board (switch-player current-player) game-number difficulty difficulty2)
          {:board  new-board :current-player (switch-player current-player)
           :player player :game-number game-number :difficulty difficulty :difficulty2 difficulty2})
        state))
    state))


(defn main-sketch [starting-game-state]
  (q/sketch
    :title "Tic-Tac-Toe"
    :host :host
    :middleware [m/fun-mode]
    :size [300 300]
    :setup (partial setup starting-game-state)
    :draw draw
    :mouse-clicked handle-mouse
    :update update-grid
    ))



