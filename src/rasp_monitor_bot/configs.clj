(ns rasp-monitor-bot.configs
  (:require [environ.core :refer [env]])
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
