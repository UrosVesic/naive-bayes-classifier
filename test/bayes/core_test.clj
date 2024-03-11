(ns bayes.core-test
  (:import (org.testcontainers.containers MySQLContainer))
  (:require [clojure.test :refer :all]
            [bayes.core :refer :all]
            [bayes.handlers :as handlers]
            [clojure.java.jdbc :as j]
            [honey.sql.helpers :refer :all]
            [honey.sql :as sql]))

(def container (doto (MySQLContainer. "mysql:8.0.22")
                 (.withDatabaseName "clojure")
                 (.withUsername "root")
                 (.withPassword "root")
                 (.withInitScript "db.sql")
                 (.start)))

(def mysql-db
  (let [host (.getContainerIpAddress container)
        port (.getMappedPort container 3306)]
    {:host host
     :port port
     :dbtype "mysql"
     :dbname "clojure"
     :user "root"
     :password "root"}))

(deftest test-user-count
  (testing "Asserting that the user count is 3"
    (let [result (j/query mysql-db ["select * from user"])
          count (count result)]
      (is (= 2 count)))))
