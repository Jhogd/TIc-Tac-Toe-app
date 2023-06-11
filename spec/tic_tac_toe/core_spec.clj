(ns tic-tac-toe.core-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.core :refer :all]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.algorithm :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            ))

(describe "Unbeatable tic-tac-toe using minmax algorithm"

  (with-stubs)


  (it "returns true if enough moves have been made to win the game"
    (should= true (enough-moves-win? [:x :x :x :e :o :e :o :e :e]))
    (should= true (enough-moves-win? [:x :x :x :x :e :e :o :e :o :e :x :e :e :e :x :o])))

  (it "brings back a random position from a col or row"
    (with-redefs [rand-nth (stub :mock-rand-nth {:return 1})]
      (should= 1 (first-moves [:o :e :o :o :e :e :e :e :e :e :e :e :e :e :e :o] X))))



  (it "should give a printed statement depending on who won the game"
    (should= "Player X has won the game\n" (with-out-str (game-over {:state [:x :x :x :e :o :e :e :e :e] :size 3 :dimension :two})))
    (should= "Player O has won the game\n" (with-out-str (game-over {:state [:x :e :x :o :o :o :e :e :e] :size 3 :dimension :two})))
    (should= "Player O has won the game\n" (with-out-str (game-over {:state [:o :o :o :o :e :e :e :e :e :e :e :e :e :e :e :o] :size 4 :dimension :two})))
    )

  (it "returns a deviation depending on what difficulty it is"
    (should= {:level :easy} (difficulty 1))
    (should= {:level :medium} (difficulty 2))
    (should= {:level :unbeatable} (difficulty 3))
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

  (it "makes a move based on the best-move function"
    (with-redefs [println (constantly nil)]
      (should= {:state [:x :e :e :e :e :e :e :e :e] :size 3 :dimension :two} (ai-turn (init-board (->Three-by-three)) 3 X))))

  (it "gives me an error that the spot is filled then make me re choose"
    (with-redefs [println (constantly nil)]
      (with-in-str "0\n3\n"
                   (should= {:state [:x :e :e :o :e :e :e :e :e] :size 3}
                            (human-turn {:state [:x :e :e :e :e :e :e :e :e] :size 3} O)))))


  (it "should switch from player X to player O and vice versa"
    (should= O (switch-player X)))

  (it "asks the user a difficulty and returns the answer"
    (with-redefs [read (stub :mock-read {:return 3})
                  println (constantly nil)]
      (should= 3 (ask-difficulty))))

  (it "asks the user who they want to play as"
    (with-redefs [read (stub :mock-read {:return :o})
                  println (constantly nil)]
      (should= :o (choose-player))))

  (it "asks the user what game-mode they want to play as"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    read (constantly 3)
                    save-board (constantly nil)
                    ask-difficulty (constantly 1)
                    println (constantly nil)]
        (should= nil (game-mode)))))

  (it "should invoke ai move and human move without caring what
       what the position values are"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    read next-move
                    save-board (constantly nil)
                    ask-difficulty (constantly 1)
                    get-board (constantly (init-board (->Three-by-three)))
                    println (constantly nil)]
        (should= nil (ai-vs-human (init-board (->Three-by-three)) X 3 1)))))

  (it "should invoke ai vs ai moves without caring what
       what the position values are"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    ask-difficulty (constantly 1)
                    save-board (constantly nil)
                    get-board (constantly (init-board (->Three-by-three)))
                    println (constantly nil)]
        (should= nil (ai-vs-ai (init-board (->Three-by-three)) X 3 1 1)))))

  (it "should invoke human vs human moves without caring what
       what the position values are"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    read next-move
                    save-board (constantly nil)
                    ask-difficulty (constantly 1)
                    get-board (constantly (init-board (->Three-by-three)))
                    println (constantly nil)]
        (should= nil (human-vs-human (init-board (->Three-by-three)) X 2)))))

  (it "checks if the last game read in is finished or not if it is false else true"
    (spit "test-game-state.txt" (utility/->game-state (init-board (->Three-by-three)) "X" 1 1 1))
    (with-redefs [edn-file "test-game-state.txt"]
      (should= true (last-game-not-finished?))))

  (it " if there is a chance for a resume then it resumes or starts new"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    read (constantly 3)
                    save-board (constantly nil)
                    ask-difficulty (constantly 1)
                    println (constantly nil)]
        (should= nil (game-mode)))))
  )

