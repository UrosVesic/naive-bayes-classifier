(ns bayes.db
  (:require [clojure.java.jdbc :as j]
            [honey.sql.helpers :refer :all]
            [honey.sql :as sql]))

(def mysql-db {:host "localhost"
               :dbtype "mysql"
               :dbname "clojure"
               :user "root"
               :password "root"})

(j/query mysql-db
         ["select * from email"])

(defn query [q]
  (j/query mysql-db q))

(defn insert [q]
  (j/db-do-prepared mysql-db q))

(defn get-user-by-email [email]
  (query (-> (select :*)
             (from :user)
             (where [:= :email email])
             sql/format)))

(defn insert-user [email]
  (insert (-> {:insert-into [:user]
               :columns [:email]
               :values [[email]]}
              (sql/format {:pretty true}))))

(defn insert-email [sender-email receiver-email subject content]
  (let [sender-result (get-user-by-email sender-email)
        receiver-result (get-user-by-email receiver-email)
        sender-id (:id (first sender-result))
        receiver-id (:id (first receiver-result))]
    (insert (-> {:insert-into [:email]
                 :columns [:subject :sender :receiver :content]
                 :values [[subject sender-id receiver-id content]]}
                (sql/format {:pretty true})))))

(defn get-received-emails [receiver]
  (query (-> (select :*)
             (from :email)
             (join [:user :u1] [:= :email.receiver :u1.id])
             (join [:user :u2] [:= :email.sender :u2.id])
             (where [:= :receiver receiver])
             sql/format)))

(-> (select *)
    (from :email)
    sql/format)