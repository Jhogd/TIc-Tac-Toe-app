(ns tic-tac-toe.file-persistence
  (:require [clojure.string :as str]
            [tic-tac-toe.utility :as utility :refer :all]
            [clojure.java.io :as io]))


(defn grab-last-game [file-name]
  (with-open [reader (io/reader file-name)]
    (->> (line-seq reader)
         (remove str/blank?)
         last
         read-string)))

(defn save-board [board current-player game-number level level-two file-name]
  (spit file-name (utility/->game-state board current-player game-number level level-two)
        :append true)
  (spit file-name "\n" :append true))

(defn grab-games [file-name]
  (slurp file-name))
