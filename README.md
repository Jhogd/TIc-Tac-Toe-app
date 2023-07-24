# tic-tac-toe


To run all clj and cljc spec run the command clj -M:spec

To run cljs specs and not compile to production, run the command clj -M:cljs and uncomment the second run-cmd section labeled specs in cljs.edn file and comment the top run-command block labeled production

To run production and build the app.js file run clj -M:cljs and make sure to uncomment the run-cmd section labeled production in the cljs.edn file and comment out the spec run-cmd block.

To run terminal game run the command clj -M:start

To run the gui game run the command clj-M:start-gui
