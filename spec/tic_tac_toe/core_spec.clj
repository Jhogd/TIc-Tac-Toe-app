(ns tic-tac-toe.core-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.core :refer :all]
            [tic-tac-toe.utility :as utility]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.algorithm :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            [tic-tac-toe.database :refer :all]
            ))

(describe "Unbeatable tic-tac-toe using minmax algorithm"

  (with-stubs)

  (it "returns true if enough moves have been made to win the game"
    (should= true (enough-moves-win? [:x :x :x :e :o :e :o :e :e]))
    (should= true (enough-moves-win? [:x :x :x :x :e :e :o :e :o :e :x :e :e :e :x :o])))


  (it "should switch from player X to player O and vice versa"
    (should= O (switch-player X)))

  (it "asks the user a difficulty and returns the answer"
    (with-redefs [read (stub :mock-read {:return 3})
                  println (constantly nil)]
      (should= 3 (ask-difficulty))))

  (it "asks the user if they want to run off the database or edn file"
    (with-redefs [read (constantly 2)
                  println (constantly nil)]
      (should= {:file-type :db} (choose-persistence))))

  (it "asks the user who they want to play as"
    (with-redefs [read (stub :mock-read {:return :o})
                  println (constantly nil)]
      (should= :o (choose-player))))

  (it "asks the user what game-mode they want to play as"
      (with-redefs [read (constantly 1)
                    choose-persistence (constantly {:file-type :db})
                    save-board (constantly nil)
                    println (constantly nil)]
        (should= {:state [:e :e :e :e :e :e :e :e :e], :size 3, :dimension :two, :game-type :ai-vs-human, :age :new, :display :print, :file-type :db}
                 (game-mode  {:file-type :db} {:display :print}))
    )
    (delete-row {:table :game}))


  (it "checks if the last game read in is finished or not if it is false else true"
      (should= true (last-game-not-finished? (utility/->game-state (init-board (->Three-by-three)) "X" 1 1 1))))
  )



