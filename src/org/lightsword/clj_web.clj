;; hello world
(ns org.lightsword.clj_web
  (:require [ring.adapter.jetty :as jetty]))

(defn handler [request] {
                        :status 200,
                        :headers {"Content-Type" "text/html"}
                        :body (str request "\n" "<h1>Hello, World!</h1>")
                        })

(defn start-server []
  (jetty/run-jetty handler {
                            :host "localhost"
                            :port 3000
                            }))



(defn -main [& args]
  (start-server))
