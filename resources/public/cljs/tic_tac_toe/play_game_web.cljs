(ns tic-tac-toe.play-game-web
  (:require [reagent.core :as r]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.algorithm :as alg]))


(defonce game-map (r/atom (utility/->game-state nil nil 0 nil nil)))

(defn update-map [key value]
  (swap! game-map assoc key value))

(defn update-board [key value]
  (swap! game-map update-in [:board] conj {key value}))

(defn play-game [board current-player player difficulty difficulty2 move]
  (update-map :player (utility/switch-player player))
  (if (alg/human-turn? board current-player player)
    (utility/player-move board current-player move)
    (alg/process-game-board board current-player player difficulty difficulty2)))

(defn update-game-state [board current-player player difficulty difficulty2 move]
  (swap! game-map assoc-in [:board] :state (play-game board current-player player difficulty difficulty2 move)))

(def key-string {:x "X" :o "O" :e ""})

(defn key-to-string [marker]
  (get key-string marker))

(def terminal-to-winner {10 "Player X has won!" -10 "Player O has won!" 0 "The game is a draw" })

(defn input-field [value update]
  [:input {:type :radio :value value
           :on-change update}])

(defn select-board-menu []
  [:p [:strong "Select a Board: "
       [:label "3 by 3"]
       (input-field (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})
                    #(update-map :board (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})))
       [:label "4 by 4"]
       (input-field (conj (utility/init-board (utility/->Four-by-four)) {:display :gui})
                    #(update-map :board (conj (utility/init-board (utility/->Four-by-four)) {:display :gui})))
       ]])

(defn select-game-mode []
  [:p [:strong "Select a Game type: "
       [:label "Player vs Computer"]
       (input-field :ai-vs-human #(update-board :game-type :ai-vs-human))
       [:label "Player vs Player"]
       (input-field :human-vs-human #(update-board :game-type :human-vs-human))
       [:label "Computer vs Computer"]
       (input-field :ai-vs-ai #(update-board :game-type :ai-vs-ai))]])

(defn select-difficulty [ai-number ai-keyword]
  [:p [:strong (str "Select a difficulty for: " ai-number)
  [:label "easy"]
       (input-field 1 #(update-map ai-keyword 1))
  [:label "medium"]
       (input-field 2 #(update-map ai-keyword 2))
  [:label "unbeatable"]
       (input-field 3 #(update-map ai-keyword 3))
  [:label "NA"]
       (input-field 0 #(update-map ai-keyword 0))]])


(defn select-player-menu []
  [:p [:strong "Select a player: "
       [:label "X"]
       (input-field :x #(update-board :user-player :x))
       [:label "O"]
       (input-field :o #(update-board :user-player :o))
       [:label "NA"]
       (input-field :x #(update-board :user-player :x))]]
  )

(defn create-square [position]
  (let [state (:state (:board @game-map))]
    [:td
     [:button {:id       position
               :on-click #(update-game-state (:board @game-map) (:player @game-map)
                                            (:user-player (:board @game-map))
                                            (:difficulty @game-map) (:difficulty2 @game-map) position)}
      (key-to-string (nth state position))]]))

(defn create-row [board row-number]
  (let [size (:size board)]
  [:tr
   (for [pos (range size)]
     (create-square  (+ (* row-number size) pos)))
   ]))

(defn create-game-board []
  (let [board (:board @game-map)
        size (:size board)]
    [:div
     [:table
      (for [row (range  size)]
        (create-row (:board @game-map) row))]
     (if (utility/terminal? board)
       [:h2 (get terminal-to-winner (utility/terminal-state board))])]))

(defn start-game [board]
(swap! game-map assoc :playing? true)
  (swap! game-map assoc :player :x)
)

(defn render-menu []
  [:div
   (select-board-menu)
   (select-game-mode)
   (select-difficulty "Ai 1" :difficulty)
   (select-difficulty "Ai 2" :difficulty2)
   (select-player-menu)
   [:button {:on-click #(start-game (:board @game-map))} "New Game"]])

(defn create-menu []
  (try
    (if (:playing? @game-map)
      (create-game-board)
      (render-menu))
    (catch :default e (prn "e: " e))))

(defn main []
  (println @game-map)
  (create-menu))









