(ns rasp-monitor-bot.core
  (:require [clojure.core.async :refer [<!!]]
            [morse.polling :as poll]
            [clojure.string :as str]
            [rasp-monitor-bot.configs :as configs]
            [rasp-monitor-bot.handlers :as handlers])
  (:gen-class))

(defn -main
  [& args]
  (when (str/blank? configs/token)
    (println "Please provide configs/token in TELEGRAM_configs/token environment variable!")
    (System/exit 1))
  (when (str/blank? configs/owner)
    (println "Please provide configs/token in OWNER_USERNAME environment variable!")
    (System/exit 1))

  (println "Starting the rasp-monitor-bot")
  (<!! (poll/start configs/token handlers/main)))
