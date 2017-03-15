(defproject rasp-monitor-bot "1.1.2"
  :description "Small Telegram Bot to monitor my Raspberry Pi"
  :url "https://github.com/joaomarcuslf/rasp-monitor-bot"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ             "1.1.0"]
                 [morse               "0.2.4"]
                 [lein-light-nrepl    "0.0.18"]]

  :plugins [[lein-environ "1.1.0"]]

  :main ^:skip-aot rasp-monitor-bot.core
  :target-path "target/%s"
  :repl-options {:timeout 120000 :nrepl-middleware [lighttable.nrepl.handler/lighttable-ops]}

  :profiles {:uberjar {:aot :all}})
