(ns rasp-monitor-bot.formatters-test
  (:require [clojure.test :refer :all]
    [rasp-monitor-bot.formatters :as formatters]))
(def help
  [{ :name "help", :description "test" }])

(deftest format-help-test
  (testing "format-help, Should format an array of help."
    (is (= ["help - test"] (formatters/format-help help)))))

(deftest format-command-t1
  (testing "format-command, Should format an command, removing sudo reference."
    (is (= "rm" (formatters/format-command "sudo rm")))))

(deftest format-command-t2
  (testing "format-command, Should format an command, removing /command reference."
    (is (= "rm" (formatters/format-command "/command rm")))))

(deftest format-output-t1
  (testing "format-output, Should format an command output, when a err is given."
    (is (= "test" (formatters/format-output { :err "test" })))))

(deftest format-output-t2
  (testing "format-output, Should format an command output, when a out is given."
    (is (= "test" (formatters/format-output { :out "test" })))))

(deftest format-output-t3
  (testing "format-output, Should send a default output, when none is given."
    (is (= "No output given" (formatters/format-output {})))))

(deftest format-command-text-test
  (testing "format-command-text, Should format an command text."
    (is (= "user gave me a command {mocked} in:" (formatters/format-command-text "user" "mocked")))))
