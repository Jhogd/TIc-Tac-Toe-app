(ns tic-tac-toe.algorithm-spec
  (:require [speclj.core :refer :all]
  [tic-tac-toe.algorithm :refer :all]
             [tic-tac-toe.utility :refer :all]))

(describe "unbeatable algo"


  (it "returns a maximum score and is half of the minmax function"
    (should= 0 (max-value (init-board (->Three-by-three)) O 1))
    ;(should= 0 (max-value [:e :e :o :e :x :e :e :e :e] O 1))
    )

  (it "returns a minimum score and is half of the minmax function"
    (should= 0 (min-value (init-board (->Three-by-three)) O 1))
    )

  (it "returns true or false based on random number and standard"
    (with-redefs [rand (constantly 0.5)]
      (should= true (level-decision? 0.8))))

 (it "returns a move for the ai-standard to return"
    (with-redefs [rand (constantly 0.4)
                  rand-nth (constantly 0)]
      (should= 0 (ai-standard {:level :easy} (init-board (->Three-by-three)) O))
      ))


  (it "returns the best move for the ai to make based on the minmax function"
    (with-redefs [println (constantly nil)]
      (should= 0 (best-move (init-board (->Three-by-three)) X))))

  )