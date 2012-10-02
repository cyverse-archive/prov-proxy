(ns prov-proxy.core
  (:gen-class)
  (:use prov-proxy.conf
        prov-proxy.controllers
        compojure.core
        clojure-commons.error-codes
        [ring.middleware
         params
         keyword-params
         nested-params
         multipart-params
         cookies
         session
         stacktrace])
  (:require [clojure.tools.cli :as cli]
            [clojure.tools.nrepl.server :as nrepls]
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure-commons.query-params :as qp]
            [prov-proxy.json-body :as jb]
            [ring.adapter.jetty :as jetty]))

(def repl-server (atom nil))

(defroutes prov-proxy-routes
  (GET "/" [] "Welcome to Prov-Proxy!")
  
  (GET "/0.1/object/:object-id" [object-id]
       (trap "get-object-uuid" get-object-uuid object-id))

  (PUT "/0.1/object" {body :body}
       (trap "add-object" add-object body))

  (PUT "/0.1/log" {body      :body
                   caller-ip :remote-addr}
       (trap "log" log-prov body caller-ip))
  
  (route/not-found "Not found."))

(defn parse-args
  [args]
  (cli/cli
   args
   ["-c" "--config"
    "Set the local config file to read from. Bypasses Zookeeper."
    :default nil]
   ["-h" "--help"
    "Show help."
    :default false
    :flag true]
   ["-p" "--port"
    "Set the port to listen on."
    :default 31372
    :parse-fn #(Integer. %)]))

(defn site-handler
  [routes]
  (-> routes
      jb/parse-json-body
      wrap-multipart-params
      wrap-keyword-params
      wrap-nested-params
      qp/wrap-query-params
      wrap-stacktrace))

(def app (site-handler prov-proxy-routes))

(defn print-help
  [help-str]
  (println help-str)
  (System/exit 0))

(defn -main
  [& args]
  (let [[opts args help-str] (parse-args args)]
    (cond
     (:help opts)
     (print-help help-str)

     (:config opts)
     (local-init (:config opts))

     (nil? (:config opts))
     (init))

    (reset! repl-server (nrepls/start-server :port 7888))
    
    (jetty/run-jetty app {:port (listen-port)})))
