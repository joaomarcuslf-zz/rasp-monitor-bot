(ns rasp-monitor-bot.handlers
  (:require
		[morse.handlers :as handler]
		[clojure.edn :as edn]
		[clojure.java.io :as io]
		[clojure.string :as str]
		[morse.api :as api]
		[clojure.java.shell :refer [sh]]
		[rasp-monitor-bot.formatters :as formatters]
		[rasp-monitor-bot.helpers :as helpers]
		[rasp-monitor-bot.configs :as configs])
  (:gen-class))

;; Handlers

(handler/defhandler main

  ;; Will start  the chat
  (handler/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "started a new chat in:" chat)
      (api/send-text configs/token id (helpers/hello-user chat))
      (api/send-text configs/token id (str "Welcome to rasp-monitor-bot " configs/robot-version "!"))
      (api/send-text configs/token id "Type /help to see the avaiable commands")))

  ;; Will send the avaiable commands
  (handler/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my help in:" chat)
      (api/send-text configs/token id (str/join "\n" (formatters/format-help configs/avaiable-commands)))))

  ;; Will greet the user
  (handler/command-fn "hello"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "greeted me in:" chat)
      (api/send-text configs/token id (helpers/hello-user chat))))

  ;; Will send the project version
  (handler/command-fn "version"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my version in:" chat)
      (api/send-text configs/token id (str "My version is: " configs/robot-version))))

  ;; Will send the repo url
  (handler/command-fn "repo"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for the project link in:" chat)
      (api/send-text configs/token id (str "This is my project repo: " configs/project-url))
      (api/send-text configs/token id "Please, give me some stars if you liked")
      (api/send-text configs/token id "Fell free to fork this project or send any PR")))

  ;; Will send the repo changelog
  (handler/command-fn "changelog"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for the project changelog in:" chat)
      (api/send-document configs/token id (io/file "CHANGELOG.md"))))

  ;; Will run shell commands if user is valid
  (handler/command-fn "command"
    (fn [msg]
      (prn msg)
      (def chat (get msg :chat))
      (def username (get chat :username))
      (def message (get msg :text))
      (def id (get chat :id))
      (println (get chat :first_name) "gave me a command in:" chat)

      (api/send-text configs/token id
        (if (= username configs/owner)
          (str (formatters/format-output (sh (formatters/format-command message))))
          ;; Logical False
          "You don't own me\nI'm not just one of your many toys"))))

  ;; Will send the raspberry
  (handler/command-fn "temp"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my temp in:" chat)
      (api/send-text configs/token id (helpers/temp-to-celsius (helpers/get-temp)))))

  ;; Not found command
  (handler/message-fn
    (fn [{{id :id} :chat :as message}]
      (println (get message :first_name) "asked me something I can't do in:" message)
      (api/send-text configs/token id "Sorry, I can't do that!")
      (api/send-text configs/token id "Type /help to see the avaiable commands"))))