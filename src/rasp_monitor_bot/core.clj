(ns rasp-monitor-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [environ.core :refer [env]]
            [morse.handlers :as handler]
            [morse.polling :as p]
            [morse.api :as api]
            [clojure.java.shell :refer [sh]])
  (:gen-class))


(def token (env :telegram-token))

(def robot-version (str (env :rasp-monitor-bot-version)))

(def owner (str (env :owner-username)))

(def project-url "https://github.com/joaomarcuslf/rasp-monitor-bot")

(def avaiable-commands
  [
    {:name "/help",      :description "Will show the bot commands"}
    {:name "/hello",     :description "Will make the bot talk with you"}
    {:name "/version",   :description "Will the bot version"}
    {:name "/repo",      :description "Will show the project link"}
    {:name "/changelog", :description "Will send a changelog file"}
    {:name "/temp",      :description "Will show the actual raspberry temperature"}
  ])

;; Will greet the user
(defn hello-user
  [chat]
  (str "Hello " (get chat :first_name)))

;; Will format the help command
(defn format-help
  [help]
  (map
    (fn
      [command]
      (str (get command :name) " - " (get command :description)))
    help))


;; Will remove from the message-command
;; sudo reference
;; /command
(defn format-command
  [raw-command]
  (str/replace (str/replace raw-command #"/command " "") "sudo " ""))

;; Will format command outpu
;; giving precedence to :out ove :err
(defn format-output
  [command-output]
  (def out (get command-output :out))
  (def err (get command-output :err))
  (or out err))

;; Format a string to number
(defn parse-int
  [s]
  (Integer. (re-find  #"\d+" s )))

;; Will give the raspberry temp as integer
(defn get-temp
  []
  (float (/ (parse-int (slurp (io/file "/sys/class/thermal/thermal_zone0/temp"))) 1000)))

;; Will format temp in celsius
(defn temp-to-celsius
  [temp]
  (str temp "ºC"))


;; Handlers

(handler/defhandler handler

  ;; Will start  the chat
  (handler/command-fn "start"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "started a new chat in:" chat)
      (api/send-text token id (hello-user chat))
      (api/send-text token id (str "Welcome to rasp-monitor-bot " robot-version "!"))
      (api/send-text token id "Type /help to see the avaiable commands")))

  ;; Will send the avaiable commands
  (handler/command-fn "help"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my help in:" chat)
      (api/send-text token id (str/join "\n" (format-help avaiable-commands)))))

  ;; Will greet the user
  (handler/command-fn "hello"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "greeted me in:" chat)
      (api/send-text token id (hello-user chat))))

  ;; Will send the project version
  (handler/command-fn "version"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my version in:" chat)
      (api/send-text token id (str "My version is: " robot-version))))

  ;; Will send the repo url
  (handler/command-fn "repo"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for the project link in:" chat)
      (api/send-text token id (str "This is my project repo: " project-url))
      (api/send-text token id "Please, give me some stars if you liked")
      (api/send-text token id "Fell free to fork this project or send any PR")))

  ;; Will send the repo changelog
  (handler/command-fn "changelog"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for the project changelog in:" chat)
      (api/send-document token id (io/file "CHANGELOG.md"))))

  ;; Will run shell commands if user is valid
  (handler/command-fn "command"
    (fn [msg]
      (prn msg)
      (def chat (get msg :chat))
      (def username (get chat :username))
      (def message (get msg :text))
      (def id (get chat :id))
      (println (get chat :first_name) "gave me a command in:" chat)

      (api/send-text token id
        (if (= username owner)
          (str (format-output (sh (format-command message))))
          ;; Logical False
          "You don't own me\nI'm not just one of your many toys"))))

  ;; Will send the raspberry
  (handler/command-fn "temp"
    (fn [{{id :id :as chat} :chat}]
      (println (get chat :first_name) "asked for my temp in:" chat)
      (api/send-text token id (temp-to-celsius (get-temp)))))

  ;; Not found command
  (handler/message-fn
    (fn [{{id :id} :chat :as message}]
      (println (get message :first_name) "asked me something I can't do in:" message)
      (api/send-text token id "Sorry, I can't do that!")
      (api/send-text token id "Type /help to see the avaiable commands"))))


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
