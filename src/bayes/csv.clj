(ns bayes.csv
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defrecord CsvRow [id label text labelNum])

(defn read-csv [file-path]
  (with-open [reader (io/reader file-path)]
    (let [rows (csv/read-csv reader :header true)
          processed-rows (map (fn [row] (->CsvRow (row 0) (row 1) (row 2) (row 3))) rows)]
      (doall processed-rows))))
