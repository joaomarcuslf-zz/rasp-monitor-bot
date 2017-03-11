(ns rasp-monitor-bot.helpers-test
  (:require [clojure.test :refer :all]
    [rasp-monitor-bot.helpers :as helpers]))

(def chat {
  :first_name "Test"
})

(deftest hello-user-test
  (testing "hello-user, Should greet user."
    (is (= "Hello Test" (helpers/hello-user chat)))))

(def int-mock "1000\n")

(deftest parse-int-test
  (testing "parse-int, Should parse string to integer."
    (is (= 1000 (helpers/parse-int int-mock)))))

(deftest get-temp-test
  (testing "get-temp, Should get a float number."
    (is (= java.lang.Float (type (helpers/get-temp))))))

(deftest temp-to-celsius-test
  (testing "temp-to-celsius, Should transform temp on celcius string."
    (is (= "50 ºC" (helpers/temp-to-celsius 50)))))
