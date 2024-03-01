(ns bayes.routes
  (:require  [ring.adapter.jetty :as ring-jetty]
             [bayes.mapper :as mapper]
             [bayes.db :as db]
             [bayes.classifier :as nb]
             [bayes.preparator :as preparator]
             [reitit.ring :as ring]
             [muuntaja.core :as m]
             [reitit.ring.middleware.muuntaja :as muuntaja]))

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

(def app
  (ring/ring-handler
   (ring/router
    [["/classify" {:post classify-email}]
     ["/email/{receiver}" {:get get-emails-handler}]
     ["/email" {:post save-email-handler}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty app {:port 3000
                             :join? false}))

