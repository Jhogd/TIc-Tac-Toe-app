(ns tic-tac-toe.utility-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.utility :refer :all]))


(describe "utility functions for tic-tac-toe"

  (it "initializes a 4x4 board"
    (let [board (init-board (->Four-by-four))]
      (should= (vec (repeat (* 4 4) EMPTY)) (:state board))
      (should= 4 (:size board))
      (should= :two (:dimension board))))

  ;(it "initializes a board"
  ;  (should= {:state (vec (repeat (* 4 4) EMPTY)) :size 4 :dimension :two} (init-board(->Four-by-four)))
  ;  (should= {:state (vec (repeat (* 3 3) EMPTY)) :size 3 :dimension :two} (init-board(->Three-by-three)))
  ;  (should= {:state (vec (repeat (* 3 3 3) EMPTY)) :size 3 :dimension :three} (init-board(->Three-dimension)))
  ;  )

  (it "checks if the board has any empty space"
    (should= true (has-empty-space? [:x :e]))
    (should= false (has-empty-space? [:x :o]))
    (should= true (has-empty-space? [:x :o :e]))
    (should= true (has-empty-space? [:x :o :x :o :x :o :x :e])))

  (it "should take a position and return true or false depending on if
  that spot is empty or not"
    (should= true (is-empty? (:state (init-board (->Three-by-three))) 4))
    (should= false (is-empty? [:e :e :e :e :x :e :e :e :e] 4)))

  (it "takes a player and position and moves the player into position"
    (should= {:state [:e :e :e :e :x :e :e :e :e] :size 3 :dimension :two} (player-move (init-board (->Three-by-three)) X 4))
    (should= {:state [:e :e :e :e :e :e :x :e :e] :size 3 :dimension :two} (player-move (init-board (->Three-by-three)) X 6))
    )

  (it "returns the positions within the board
  where there is an empty space"
    (should= [0 1] (list-empties [:e :e :x :x :x :x :x :x :x])))

  (it "checks win conditions and returns true if
  a win condition is met"
    (should= true (win? {:state [:x :x :x :e :o :e :e :e :e] :size 3 :dimension :two} X))
    (should= true (win? {:state [:x :x :e :o :o :o :e :e :e] :size 3 :dimension :two} O))
    (should= true (win? {:state [:x :e :e :x :o :e :x :e :e] :size 3 :dimension :two} X))
    (should= false (win? {:state [:e :x :x :e :o :e :e :e :e] :size 3 :dimension :two} X))
    (should= true (win? {:state [:o :e :e :e :o :e :e :e :o] :size 3 :dimension :two} O))
    (should= true (win? {:state [:o :e :e :e :e :o :e :e :e :e :o :e :e :e :e :o] :size 4 :dimension :two} O))
    (should= true (win? {:state [:o :o :o :o :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two} O))
    (should= false (win? {:state [:o :e :o :o :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two} O))
    (should= false (win? {:state [:o :e :o :o :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two} O))
    (should= true (win? {:state [:o :e :o :o :e :e :e :e :e :e :e :e :o :o :o :o] :size 4 :dimension :two} O))
    (should= true (win? {:state [:o :e :e :e :e :e :e :e :e :o :e :e :e :e :e :e :e :e :o :e :e :e :e :e :e :e :e] :size 3 :dimension :three} O))
    (should= false (win? {:state [:o :o :x :o :x :o :x :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :e :x :e :x :e] :size 3 :dimension :three} O))
    (should= true (win? {:state [:x :o :x :o :x :e :x :e :o :o :e :e :x :e :x :e :e :e :e :e :e :e :x :e :o :e :e] :size 3 :dimension :three} X))
    (should= false (win? {:state [:x :o :e :e :o :x :e :e :x :e :e :e :e :e :e :e :o :e :e :e :x :e :e :e :e :e :e] :size 3 :dimension :three} O))
    )
  (it "returns terminal states 10 -10 and 0 depending on the board"
    (should= 10 (terminal-state {:state [:x :x :x :e :o :e :e :e :e] :size 3 :dimension :two}))
    (should= -10 (terminal-state {:state [:o :o :o :e :o :e :e :e :e] :size 3 :dimension :two}))
    (should= 0 (terminal-state {:state [:e :e :o :e :o :e :e :e :e] :size 3 :dimension :two}))
    (should= 0 (terminal-state {:state [:o :e :o :o :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two}))
    (should= 10 (terminal-state {:state [:x :x :x :x :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two}))
    )
  (it "returns true or false if any of the terminal states are met or not"
    (should= true (terminal? {:state [:x :x :x :e :o :e :o :x :e] :size 3 :dimension :two}))
    (should= false (terminal? {:state [:x :e :o :x :o :e :e :o :e] :size 3 :dimension :two}))
    (should= true (terminal? {:state [:x :x :x :x :e :e :o :e :o :e :x :e :e :e :x :o] :size 4 :dimension :two}))
    ;(should= false (terminal? [:x :e :e :e :e :e :e :e :e :e :e :e :o :o :o :o] 4))
    )
  (it "prints the board based on dimension"
    (should= ":e | :e | :e\n:e | :e | :e\n:e | :e | :e\n"
             (with-out-str (print-board (init-board (->Three-by-three)))))
    (should= ":e | :e | :e\n:e | :e | :e\n:e | :e | :e\n+---+---+\n\n:e | :e | :e\n:e | :e | :e\n:e | :e | :e\n+---+---+\n\n:e | :e | :e\n:e | :e | :e\n:e | :e | :e\n+---+---+\n\n"
             (with-out-str (print-board (init-board (->Three-dimension))))))

  )
