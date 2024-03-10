(ns bayes.routes
  (:require  [ring.adapter.jetty :as ring-jetty]
             [bayes.handlers :as handlers]

             [reitit.ring :as ring]
             [muuntaja.core :as m]
             [reitit.ring.middleware.muuntaja :as muuntaja]))

(def app
  (ring/ring-handler
   (ring/router
    [["/classify" {:post handlers/classify-email}]
     ["/email/{receiver}" {:get handlers/get-emails-handler}]
     ["/email" {:post handlers/save-email-handler}]
     ["/user" {:post handlers/save-user}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty app {:port 3000
                             :join? false}))

