(ns tic-tac-toe.core-spec
  (:require [speclj.core :refer :all]
            [tic-tac-toe.core :refer :all]
            [tic-tac-toe.utility :refer :all]
            [tic-tac-toe.algorithm :refer :all]
            [tic-tac-toe.file-persistence :refer :all]
            [tic-tac-toe.database :refer :all]
            [tic-tac-toe.gui :refer :all]
            [tic-tac-toe.db-and-edn :refer :all]
            ))

(describe "Unbeatable tic-tac-toe using minmax algorithm"

  (with-stubs)

  (it "returns true if enough moves have been made to win the game"
    (should= true (enough-moves-win? [:x :x :x :e :o :e :o :e :e]))
    (should= true (enough-moves-win? [:x :x :x :x :e :e :o :e :o :e :x :e :e :e :x :o])))

  (it "returns the map the player wants to use"
    (with-redefs [read (constantly 3)]
    (should= (init-board (->Three-dimension)) (get-board))))

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
                 (game-mode  {:file-type :db}))
    )
    (delete-row {:table :game}))

  (it "should invoke play-game without caring what
       what the position values are"
    (let [moves (atom (range 10))
          next-move (fn [& _] (let [move (first @moves)]
                                (swap! moves rest)
                                move))]
      (with-redefs [best-move next-move
                    read next-move
                    save-board (constantly nil)
                    println (constantly nil)]
        (should= nil (play-game (conj (conj (init-board (->Three-by-three))
                                            {:game-type :human-vs-human}) {:display :print}) X X
                                1 3 0)))
      ) (doseq [i (range 10)] (delete-row {:table :board})))


  (it "run game helper that fills map with game options"
    (with-redefs [read (constantly 1)
                  println (constantly nil)
                  get-game-number (constantly 1)
                  ask-difficulty (stub :ask-difficulty)
                  insert-game (stub :insert)
                  save-current-board (stub :save-board)]
      (should= (conj (conj (conj (init-board(->Three-by-three)) {:game-type :ai-vs-human :age :new}) {:display :print}) {:file-type :db})
               (run-ai-vs-human {:file-type :db}))))

  (it "run game helper that fills map with game options"
    (with-redefs [read (constantly 1)
                  get-game-number (constantly 1)
                  println (constantly nil)
                  ask-difficulty (stub :ask-difficulty)
                  insert-game (stub :insert)
                  save-current-board (stub :save-board)]
      (should= (conj (conj (conj (init-board(->Three-by-three)) {:game-type :ai-vs-ai :age :new}) {:display :print}) {:file-type :db})
               (run-ai-vs-ai {:file-type :db}))))

  (it "run game helper that fills map with game options"
    (with-redefs [read (constantly 1)
                  get-game-number (constantly 1)
                  println (constantly nil)
                  ask-difficulty (stub :ask-difficulty)
                  insert-game (stub :insert)
                  save-current-board (stub :save-board)]
      (should= (conj (conj (conj (init-board(->Three-by-three)) {:game-type :human-vs-human :age :new}) {:display :print}) {:file-type :db})
               (run-human-vs-human {:file-type :db}))))

  (it "determine if we resume or not"
    (with-redefs [last-game (stub :last)
                  game-mode (stub  :game-mode)
                  println (constantly nil)
                  last-game-not-finished? (stub :last-game)
                  read (constantly 2)]
      (should= nil (game-start-options {:file-type :db}))))

  (it "runs the print game or gui game based on user text after run"
    (with-redefs [run-game (stub :run)
                  main-sketch (stub :gui)
                  println (constantly nil)
                  choose-persistence (stub :choose)
                  game-start-options (stub :options)]
    (should= nil (print-or-gui "print"))))

  )



