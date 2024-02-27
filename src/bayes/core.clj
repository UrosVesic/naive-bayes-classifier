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
  (let [csv-rows (read-csv "")
        emails (map :text csv-rows)
        labels (map :label csv-rows)
        training-emails (take 4000 emails)
        training-labels (take 4000 labels)
        test-emails (drop 4000 emails)
        test-labels (drop 4000 labels)]

    ;; Train the classifier
    (let [classifier-state (nb/train training-emails training-labels)]

      ;; Predict and evaluate the classifier on the test dataset
      (let [predictions (map (fn [email] (nb/predict classifier-state email)) test-emails)
            results (map vector predictions test-labels)
            correct (count (filter (fn [[predicted actual]] (= predicted actual)) results))]

        (doseq [[predicted actual] results]
          (println "Predicted:" predicted ", Actual:" actual))

        (println "Correct:" correct)
        (println "Sum:" (count test-emails))))))











