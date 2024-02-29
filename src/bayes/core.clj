(ns bayes.core
  (:gen-class)
  (:require [clojure.data.csv :as csv]
            [bayes.classifier :as nb]
            [clojure.java.io :as io]))

(defrecord CsvRow [id label text labelNum])

(defn read-csv [file-path]
  (with-open [reader (io/reader file-path)]
    (let [rows (csv/read-csv reader :header true)
          processed-rows (map (fn [row] (->CsvRow (row 0) (row 1) (row 2) (row 3))) rows)]
      (doall processed-rows))))

(defn -main [& args]
  (let [csv-rows (read-csv "C:\\Users\\uros.vesic\\IdeaProjects\\clojure\\bayes-clojure\\dataset.csv")
        shuffled-rows (shuffle csv-rows)
        emails (map :text shuffled-rows)
        labels (map :label shuffled-rows)
        training-emails (take 4000 emails)
        training-labels (take 4000 labels)
        test-emails (drop 4000 emails)
        test-labels (drop 4000 labels)]

    ;; Train the classifier
    (let [classifier-state (nb/train training-emails training-labels)]
      (let [{:keys [spam-emails ham-emails  vocabulary]} classifier-state]
        (println "Number of spam emails:" spam-emails)
        (println "Number of ham emails:" ham-emails)
        (println "Vocabulary size:" (count vocabulary)))

      (when (seq test-emails)
        (let [results (map (fn [email label]
                             (let [prediction (nb/predict email classifier-state)]
                               (= prediction label)))
                           test-emails test-labels)
              correct-predictions (count (filter true? results))
              total-predictions (count test-emails)]
          (println (str "Correctly predicted: " correct-predictions "/" total-predictions)))))))












