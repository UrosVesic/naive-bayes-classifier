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

(defn insert-email [sender receiver subject content]
  (insert (-> {:insert-into [:email]
               :columns [:subject :sender :receiver :content]
               :values [[subject sender receiver content]]}
              (sql/format {:pretty true}))))

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

(comment
  (query (-> (select :*)
             (from :email)
             (join [:user :u1] [:= :email.receiver :u1.id])
             (join [:user :u2] [:= :email.sender :u2.id])
             (where [:= :receiver 2])
             sql/format))
  (insert (-> {:insert-into [:email]
               :columns [:subject :sender :receiver :content]
               :values [["Congrats" "Smith" "John" "Congrats! You have won a lottery!"]]}
              (sql/format {:pretty true}))))