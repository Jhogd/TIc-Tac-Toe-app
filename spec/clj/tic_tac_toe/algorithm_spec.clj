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

  (it "returns the move that results in a terminal state for either player"
    (should= 5 (best-move-win {:state [:e :e :e :x :x :e :e :e :e] :size 3 :dimension :two}
                              X {0 1 1 3 2 3 7 1 8 1 5 3}))
    (should= 3 (best-move-win {:state [:o :e :e :e :e :e :o :e :e] :size 3 :dimension :two}
                              O {3 -2  7 -2 8 1 5 -2}))

    (should= 1 (best-move-win {:state [:x :e :x :e :o :x :o :e :e] :size 3 :dimension :two}
                              O {1 -2  7 -2 8 1 5 -2}))
    (should= 1 (best-move-win {:state [:x :e :x :e :o :x :o :e :e] :size 3 :dimension :two}
                              O {1 0N, 3 5/2, 7 -10/3, 8 0N}))
    )

  (it "true or false if map contains negative value"
    (should= true (contains-neg? {1 -1 2 2 3 7}))
    (should= false (contains-neg? {1 1 2 2 3 7})))

  (it "returns a map with only the key and values of the highest number"
    (should= {1 3 2 3} (filter-greatest-vals {1 3 2 3 4 1 5 1})))

  (it "returns smallest key associated with value"
    (should= {1 1 2 1} (filter-smallest-vals {1 1 2 1 3 4 5 7})))

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
      (should= 0 (best-move (init-board (->Three-by-three)) X))))

  (it "returns true if it is human-turn"
    (should= true (human-turn? (conj (conj (init-board (->Three-by-three))
                                           {:game-type :ai-vs-human})
                                     {:display :print})  X
                               X )))
  )