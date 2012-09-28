(ns prov-proxy.core
  (:gen-class)
  (:use prov-proxy.conf
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
            [clojure.data.json :as json]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure-commons.query-params :as qp]
            [prov-proxy.json-body :as jb]
            [ring.adapter.jetty :as jetty]))

(defroutes prov-proxy-routes
  (GET "/" [] "Welcome to Prov-Proxy!")
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
    (jetty/run-jetty app {:port (listen-port)})))
