(ns tic-tac-toe.play-game-web
  (:require [reagent.core :as r]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.algorithm :as alg]))


(defonce game-map (r/atom (conj (utility/->game-state nil nil 0 nil nil) {:playing? false})))

(defn update-map [game-map key value]
  (swap! game-map assoc key value))

(defn add-to-map [game-map key value]
  (swap! game-map conj {key value}))

(defn update-board [game-map key value]
  (swap! game-map update-in [:board] conj {key value}))

(defn play-human-turn [board current-player move]
  (if (utility/is-empty? (:state board) move)
    (do
    (update-map game-map :player (utility/switch-player current-player))
    (utility/player-move board current-player move)
    )
    board))

(defn play-ai-turn [board current-player player diff diff2]
  (update-map game-map :player (utility/switch-player current-player))
  (alg/process-game-board board current-player player diff diff2))

(defn update-game-state [board]
  (swap! game-map assoc-in [:board] board))

(defn update-state-only [state]
  (swap! game-map update-in [:board :state] (constantly state)))

(def key-string {:x "X" :o "O" :e "~"})

(defn key-to-string [marker]
  (get key-string marker))

(def terminal-to-winner {10 "Player X has won!" -10 "Player O has won!" 0 "The game is a draw" })

(defn input-field [name value update]
  [:input {:type :radio :name name :value value
           :on-change update}])

(defn select-board-menu []
  [:p [:strong "Select a Board: "
       [:br]
       [:br]
       [:label "3 by 3"]
       (input-field "board" (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})
                    #(update-map game-map :board (conj (utility/init-board (utility/->Three-by-three)) {:display :gui})))
       [:br]
       [:label "4 by 4"]
       (input-field "board" (conj (utility/init-board (utility/->Four-by-four)) {:display :gui})
                    #(update-map game-map :board (conj (utility/init-board (utility/->Four-by-four)) {:display :gui})))
       ]])

(defn select-game-mode []
  [:p [:strong "Select a Game type: "
       [:br]
       [:br]
       [:label "Player vs Computer"]
       (input-field "mode" :ai-vs-human #(update-board game-map :game-type :ai-vs-human))
       [:br]
       [:label "Player vs Player"]
       (input-field "mode" :human-vs-human #(update-board game-map :game-type :human-vs-human))
       [:br]
       [:label "Computer vs Computer"]
       (input-field "mode" :ai-vs-ai #(update-board game-map :game-type :ai-vs-ai))]])

(defn select-difficulty [ai-number ai-keyword name]
  [:p [:strong (str "Select a difficulty for: " ai-number)
       [:br]
       [:br]
       [:label "easy"]
       (input-field name 1 #(update-map game-map ai-keyword 1))
       [:br]
       [:label "medium"]
       (input-field name 2 #(update-map game-map ai-keyword 2))
       [:br]
       [:label "unbeatable"]
       (input-field name 3 #(update-map game-map ai-keyword 3))
       [:br]
       [:label "NA"]
       (input-field name 0 #(update-map game-map ai-keyword 0))]])


(defn select-player-menu []
  [:p [:strong "Select a player: "
       [:br]
       [:br]
       [:label "X"]
       (input-field "player" :x #(update-board game-map :user-player :x))
       [:br]
       [:label "O"]
       (input-field "player" :o #(update-board game-map :user-player :o))
       [:br]
       [:label "NA"]
       (input-field "player" :x #(update-board game-map :user-player :x))]]
  )

(defmulti create-square :human-turn?)

(defmethod create-square true [current-game-map position]
    (doall
    [:td
     [:button {:id  position :style  {:color "blue"
                                      :font-size "30px"
                                      :display "inline-block"
                                      :background-color "black"
                                      :padding "50px 50px"}
               :on-click #(update-game-state (play-human-turn (:board current-game-map) (:player current-game-map)
                                                              position))}
      (key-to-string (nth (:state (:board @game-map)) position))]]))

(defmethod create-square false [current-game-map position]
      [:td
       [:button {:id  position :style {:color "blue"
                                       :font-size "30px"
                                       :display "inline-block"
                                       :background-color "black"
                                       :padding "50px 50px"}
                 :on-click #(update-game-state (play-ai-turn (:board current-game-map) (:player current-game-map)
                                                                   (:user-player (:board current-game-map)) (:difficulty current-game-map)
                                                                   (:difficulty2 current-game-map)))}
        (key-to-string (nth (:state (:board @game-map)) position))]])

(defn create-row [board row-number]
  (let [size (:size board)]
     [:tr
      (for [pos (range size)]
        (create-square @game-map (+ (* row-number size) pos)))
      ]))

(defn return-beginning-board [board]
  (vec (map (fn [_] :e) board)))

(defn restart-game []
    (update-state-only (return-beginning-board (:state (:board @game-map))))
    (update-map game-map :player (:user-player (:board @game-map))))

(defn restart-button []
  [:button {:style {:color "blue"}
            :on-click #(restart-game)} "restart"])

(defn create-game-board []
  (let [board (:board @game-map)
        size (:size board)]
    (update-map game-map :human-turn? (alg/human-turn? (:board @game-map) (:player @game-map)
                                              (:user-player (:board @game-map))))
    [:div{:style {:display "flex" :justify-content "center"
                  :align-items "center"
                  :height "100vh"
                  }}
     [:table
      (for [row (range  size)]
        (create-row (:board @game-map) row))]
     (restart-button)
     (if (utility/terminal? board)
       [:h2 (get terminal-to-winner (utility/terminal-state board))])]))

(defn start-game []
  (update-map game-map :playing? true)
  (update-map game-map :player :x)
  (add-to-map game-map :human-turn? (alg/human-turn? (:board @game-map) (:player @game-map)
                                            (:user-player (:board @game-map))))
  )

(defn render-menu []
  [:div
   (select-board-menu)
   (select-game-mode)
   (select-difficulty "Ai 1" :difficulty "diff1")
   (select-difficulty "Ai 2" :difficulty2 "diff2")
   (select-player-menu)
   [:button {:on-click #(start-game)} "Start Game"]])

(defn create-game []
    (if (:playing? @game-map)
      (create-game-board)
      (render-menu))
  )

(defn run-ttt []
  (create-game))









