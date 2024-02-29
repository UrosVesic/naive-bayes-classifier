(ns bayes.classifier
  (:require [clojure.string :as str]
            [bayes.processor :as processor]))

(defn preprocess [document]
  (when document
    (let [processed-document (str/lower-case document)]
      (str/split processed-document #"\s+")))) ;; Split the document into words

(defn build-vocabulary [documents]
  ;; Reduce the documents into a set of unique preprocess words
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
        ;; map to [doc, label] pairs
        training-data (map vector documents labels)
        ;; initial state object
        initial-state {:spam-word-counts {}
                       :ham-word-counts {}
                       :spam-emails 0
                       :ham-emails 0
                       :vocabulary vocabulary}]
    ;; accumulator is a map containing counts and the current item is a vector [doc label] where doc is a document and label is its classification ("spam" or "ham") .
    (reduce (fn [{:keys [spam-word-counts ham-word-counts spam-emails ham-emails]} [doc label]]
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

(defn predict [document {:keys [spam-word-counts ham-word-counts spam-emails ham-emails vocabulary]}]
  (let [vector (processor/text-to-vector vocabulary (preprocess document))
        vocab-list (vec vocabulary)
        total-emails (+ spam-emails ham-emails)
        log-spam-emails-ratio (Math/log (/ spam-emails (double total-emails)))
        log-ham-emails-ratio (Math/log (/ ham-emails (double total-emails)))
        probabilities (reduce
                       (fn [{:keys [total-spam-probability total-ham-probability]} index]
                         (let [word (nth vocab-list index)
                               count-in-spam (+ 1 (get spam-word-counts word 0))
                               count-in-ham (+ 1 (get ham-word-counts word 0))
                               single-spam-probability (* (nth vector index 0) (Math/log (/ count-in-spam (+ spam-emails (count vocabulary)))))
                               single-ham-probability (* (nth vector index 0) (Math/log (/ count-in-ham (+ ham-emails (count vocabulary)))))]
                           {:total-spam-probability (+ total-spam-probability single-spam-probability)
                            :total-ham-probability (+ total-ham-probability single-ham-probability)}))
                       {:total-spam-probability log-spam-emails-ratio
                        :total-ham-probability log-ham-emails-ratio}
                       (range (count vocabulary)))]
    (if (> (:total-spam-probability probabilities) (:total-ham-probability probabilities)) "spam" "ham")))

