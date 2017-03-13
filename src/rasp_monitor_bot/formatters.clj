(ns rasp-monitor-bot.formatters
  (:require [clojure.string :as str])
  (:gen-class))

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
  (if (not (str/blank? out))
    out
    (if (not (str/blank? err))
      err
      "No output given")))
