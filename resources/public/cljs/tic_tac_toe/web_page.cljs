(ns tic-tac-toe.web-page
  (:require [goog.dom :as gdom]
            [reagent.core :as reagent]
            [reagent.dom :as rdom]
            [tic-tac-toe.play-game-web :as play-game]))

(defn ^:export main []
  (rdom/render [play-game/run-ttt] (js/document.getElementById "app")))

(main)