(ns bayes.preparator
  (:require [bayes.csv :as csv]
            [bayes.classifier :as nb]))

(def classifier-state (atom nil))

(defn prepare-data []
  (let [csv-rows (csv/read-csv "C:\\Users\\uros.vesic\\IdeaProjects\\clojure\\bayes-clojure\\dataset.csv")
        shuffled-rows (shuffle csv-rows)
        emails (map :text shuffled-rows)
        labels (map :label shuffled-rows)
        training-emails (take 4000 emails)
        training-labels (take 4000 labels)
        test-emails (drop 4000 emails)
        test-labels (drop 4000 labels)]

    ;; Train the classifier and update the global classifier state atom
    (reset! classifier-state (nb/train training-emails training-labels))

    (let [{:keys [spam-emails ham-emails vocabulary]} @classifier-state]
      (println "Number of spam emails:" spam-emails)
      (println "Number of ham emails:" ham-emails)
      (println "Vocabulary size:" (count vocabulary)))

    (when (seq test-emails)
      (let [results (map (fn [email label]
                           (let [prediction (nb/predict email @classifier-state)]
                             (= prediction label)))
                         test-emails test-labels)
            correct-predictions (count (filter true? results))
            total-predictions (count test-emails)]
        (println (str "Correctly predicted: " correct-predictions "/" total-predictions))))))
