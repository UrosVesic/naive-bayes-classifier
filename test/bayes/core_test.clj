(ns bayes.core-test
  (:import (org.testcontainers.containers MySQLContainer))
  (:require [clojure.test :refer :all]
            [bayes.core :refer :all]
            [bayes.handlers :as handlers]
            [honey.sql.helpers :refer :all]
            [bayes.preparator :as prep]))

(def container (doto (MySQLContainer. "mysql:8.0.22")
                 (.withDatabaseName "clojure")
                 (.withUsername "root")
                 (.withPassword "root")
                 (.withInitScript "db.sql")
                 (.start)))

(use-fixtures :once
  (fn [f]
    ;; Setup
    (prep/prepare-data)
    ;; Run the tests
    (f)))

(deftest test-get-emails-handler
  (testing "Get emails for a receiver"
    (let [response (handlers/get-emails-handler {:path-params {:receiver "2"}})
          body (:body response)
          sender (:sender (first body))
          receiver (:receiver (first body))]
      ;; Assertions
      (is (= 200 (:status response)))
      (is (= 1 (count body)))
      (is (= sender "janedoe@example.com"))
      (is (= receiver "johndoe@example.com")))))

(deftest test-classify-email
  (testing "Classify email"
    (let [response (handlers/classify-email {:body-params {:email "This is a test email content"}})]
      ;; Assertions
      (is (some? (:prediction (:body response)))))))

(deftest test-classify-email-missing
  (testing "Classify email with missing email text"
    (let [response (handlers/classify-email {:body-params {}})]
      ;; Assertions
      (is (= 400 (:status response)))
      (is (= "Email text is missing" (:error (:body response)))))))

(deftest test-save-email-handler
  (testing "Save email with all fields present"
    (let [response (handlers/save-email-handler {:body-params {:sender "sender@example.com" :receiver "receiver@example.com" :subject "Test Subject" :content "Test content"}})]
      ;; Assertions
      (is (= 201 (:status response))))))

(deftest test-save-email-handler-missing-fields
  (testing "Save email with missing fields"
    (let [response (handlers/save-email-handler {:body-params {:sender "sender@example.com"}})]
      ;; Assertions
      (is (= 400 (:status response)))
      (is (= "Email data is missing" (:error (:body response)))))))

(deftest test-save-user
  (testing "Save user with email"
    (let [response (handlers/save-user {:body-params {:email "user@example.com"}})]
      ;; Assertions
      (is (= 201 (:status response))))))

(deftest test-save-user-missing-email
  (testing "Save user without email"
    (let [response (handlers/save-user {:body-params {}})]
      ;; Assertions
      (is (= 400 (:status response)))
      (is (= "User data is missing" (:error (:body response)))))))


