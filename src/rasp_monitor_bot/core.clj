(ns rasp-monitor-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.api :as t])
  (:gen-class))

; TODO: fill correct token
(def token (env :telegram-token))

(def robot-version "V1")

(defn hello-user
  [chat]
  (str "Hello " (get chat :first_name)))


(h/defhandler handler

  (h/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println "Bot joined new chat: " chat)
      (t/send-text token id (hello-user chat))
      (t/send-text token id (str "Welcome to rasp-monitor-bot " robot-version "!"))
      (t/send-text token id "Type /help to see the avaiable commands")))

  (h/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (println "Help was requested in " chat)
      (t/send-text token id "/help - Will show the bot commands")
      (t/send-text token id "/hello - Will make the bot talk with you")))

  (h/command-fn "hello"
    (fn [{{id :id :as chat} :chat}]
      (println "Bot joined new chat: " chat)
      (t/send-text token id (hello-user chat))))

  (h/message-fn
    (fn [{{id :id} :chat :as message}]
      (println "Intercepted message: " message)
      (t/send-text token id "I don't do a whole lot ... yet."))))


(defn -main
  [& args]
  (when (str/blank? token)
    (println "Please provde token in TELEGRAM_TOKEN environment variable!")
    (System/exit 1))

  (println "Starting the rasp-monitor-bot")
  (<!! (p/start token handler)))
