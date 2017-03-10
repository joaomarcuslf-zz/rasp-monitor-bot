(defproject rasp-monitor-bot "0.1.0-SNAPSHOT"
  :description "Small Telegram Bot to monitor my Raspberry Pi"
  :url "https://github.com/joaomarcuslf/rasp-monitor-bot"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot rasp-monitor-bot.core
  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
