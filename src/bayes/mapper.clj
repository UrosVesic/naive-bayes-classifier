(ns bayes.mapper)

(defn transform-email-record [record]
  {:receiver (:email record)
   :sender (:email_2 record)
   :subject (:subject record)
   :content (:content record)})

(defn transform-emails [emails]
  (map transform-email-record emails))
