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

  (it "returns a deviation depending on what difficulty it is"
     (should= {:level :easy} (difficulty 1))
     (should= {:level :medium} (difficulty 2))
     (should= {:level :unbeatable} (difficulty 3))
     )

  (it "true or false if map contains negative value"
    (should= true (contains-neg? {1 -1 2 2 3 7}))
    (should= false (contains-neg? {1 1 2 2 3 7}))
    )


  (it "returns key with negative value associated to it"
    (should= 1 (get-neg-key {1 -1 2 2 3 7})))

 (it "returns a move for the ai-standard to return"
    (with-redefs [rand (constantly 0.4)
                  rand-nth (constantly 0)]
      (should= 0 (ai-standard {:level :easy} (init-board (->Three-by-three)) O))
      ))



  (it "returns the best move for the ai to make based on the minmax function"
    (with-redefs [println (constantly nil)
                  rand-nth (constantly 0)]
      (should= 4 (best-move (init-board (->Three-by-three)) X))))

  (it "returns true if it is human-turn"
    (should= true (human-turn? (conj (conj (init-board (->Three-by-three))
                                           {:game-type :ai-vs-human})
                                     {:display :print})  X
                               X )))
  )