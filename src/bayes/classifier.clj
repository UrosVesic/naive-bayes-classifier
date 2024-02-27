(ns bayes.classifier
  (:require [clojure.string :as str]))

(defn preprocess [document]
  (when document
    (let [processed-document (str/lower-case document)]
      (str/split processed-document #"\s+")))) ;; Split the document into words

(defn build-vocabulary [documents]
  (reduce (fn [vocab doc]
            (into vocab (preprocess doc)))
          #{} documents))

(defn update-word-counts [word-counts document]
  (reduce (fn [counts word]
            (update counts word (fnil inc 0)))
          word-counts
          (preprocess document)))

(defn train [documents labels]
  (let [vocabulary (build-vocabulary documents)
        training-data (map vector documents labels)
        initial-state {:spam-word-counts {}
                       :ham-word-counts {}
                       :spam-emails 0
                       :ham-emails 0
                       :vocabulary vocabulary}]
    (reduce (fn [{:keys [spam-word-counts ham-word-counts spam-emails ham-emails] :as state} [doc label]]
              (if (= label "spam")
                {:spam-word-counts (update-word-counts spam-word-counts doc)
                 :ham-word-counts ham-word-counts
                 :spam-emails (inc spam-emails)
                 :ham-emails ham-emails
                 :vocabulary vocabulary}
                {:spam-word-counts spam-word-counts
                 :ham-word-counts (update-word-counts ham-word-counts doc)
                 :spam-emails spam-emails
                 :ham-emails (inc ham-emails)
                 :vocabulary vocabulary}))
            initial-state
            training-data)))

(defn predict [{:keys [spam-word-counts ham-word-counts spam-emails ham-emails vocabulary]} document]
  (let [words (preprocess document)
        total-emails (+ spam-emails ham-emails)
        spam-prob (Math/log (/ spam-emails total-emails))
        ham-prob (Math/log (/ ham-emails total-emails))
        calc-prob (fn [word-counts total-class-emails]
                    (reduce (fn [acc word]
                              (+ acc (Math/log (/ (inc (get word-counts word 0))
                                                  (+ total-class-emails (count vocabulary))))))
                            0
                            words))]
    (if (> (+ spam-prob (calc-prob spam-word-counts spam-emails))
           (+ ham-prob (calc-prob ham-word-counts ham-emails)))
      "spam"
      "ham")))
