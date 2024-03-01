(ns bayes.core
  (:gen-class)
  (:require
   [bayes.routes :as routes]
   [bayes.preparator :as prep]))

(defn -main [& args]

  (prep/prepare-data)
;; Start the web server
  (routes/start))