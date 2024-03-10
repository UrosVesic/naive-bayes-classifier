(ns bayes.handlers
  (:require [bayes.mapper :as mapper]
            [bayes.db :as db]
            [bayes.classifier :as nb]
            [bayes.preparator :as preparator]))

(defn classify-email [{:keys [body-params]}]
  (let [email (:email body-params)
        prediction (when email (nb/predict email @preparator/classifier-state))]
    (if prediction
      {:body {:prediction prediction}}
      {:status 400
       :body {:error "Email text is missing"}})))

(defn save-email-handler [{:keys [body-params]}]
  (let [{:keys [sender receiver subject content]} body-params]
    (if (and sender receiver subject content)
      (do
        (db/insert-email sender receiver subject content)
        {:status 201})
      {:status 400
       :body {:error "Email data is missing"}})))

(defn save-user [{:keys [body-params]}]
  (let [{:keys [email]} body-params]
    (if email
      (do
        (db/insert-user email)
        {:status 201})
      {:status 400
       :body {:error "User data is missing"}})))

(defn get-emails-handler [{:keys [path-params]}]
  (let [receiver (:receiver path-params)
        transformed-emails (mapper/transform-emails (db/get-received-emails receiver))
        classified-emails (map (fn [email]
                                 (let [email-body (str (:subject email) " " (:content email))
                                       prediction (nb/predict email-body @preparator/classifier-state)
                                       spam (= prediction "spam")]
                                   (assoc email :spam spam)))
                               transformed-emails)]
    {:status 200
     :body classified-emails}))