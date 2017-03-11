(ns rasp-monitor-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t]
            [clojure.java.shell :refer [sh]])
  (:gen-class))


(def token (env :telegram-token))

(def robot-version (str (env :rasp-monitor-bot-version)))

(def owner (str (env :owner-username)))

(def project-url "https://github.com/joaomarcuslf/rasp-monitor-bot")

(defn hello-user
  [chat]
  (str "Hello " (get chat :first_name)))

(defn format-command
  [raw-command]
  (str/replace (str/replace raw-command #"/command " "") "sudo " ""))

(defn format-output
  [command-output]
  (def out (get command-output :out))
  (def err (get command-output :err))

  (or out err))


(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "started a new chat in:" chat)
      (t/send-text token id (hello-user chat))
      (t/send-text token id (str "Welcome to rasp-monitor-bot " robot-version "!"))
      (t/send-text token id "Type /help to see the avaiable commands")))

  (h/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my help in:" chat)
      (t/send-text token id "/help - Will show the bot commands")
      (t/send-text token id "/hello - Will make the bot talk with you")
      (t/send-text token id "/version - Will the bot version")
      (t/send-text token id "/repo - Will show the project link")))

  (h/command-fn "hello"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "greeted me in:" chat)
      (t/send-text token id (hello-user chat))))

  (h/command-fn "version"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my version in:" chat)
      (t/send-text token id (str "My version is: " robot-version))))

  (h/command-fn "repo"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for the project link in:" chat)
      (t/send-text token id (str "This is my project repo: " project-url))
      (t/send-text token id "Please, give me some stars if you liked")
      (t/send-text token id "Fell free to fork this project or send any PR")))


  (h/command-fn "command"
    (fn [msg]
      (prn msg)
      (def chat (get msg :chat))
      (def username (get chat :username))
      (def message (get msg :text))
      (def id (get chat :id))
      (println (get chat :first_name) "gave me a command in:" chat)

      (t/send-text token id
        (if (= username owner)
          (str (format-output (sh (format-command message))))
          ;; Logical False
          "You don't own me\nI'm not just one of your many toys"))))

  (h/message-fn
    (fn [{{id :id} :chat :as message}]
      (println (get message :first_name) "asked me something I can't do in:" message)
      (t/send-text token id "Sorry, I can't do that!")
      (t/send-text token id "Type /help to see the avaiable commands"))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))
  (when (str/blank? owner)
    (println "Please provde token in OWNER_USERNAME environment variable!")
    (System/exit 1))

  (println "Starting the rasp-monitor-bot")
  (<!! (p/start token handler)))
