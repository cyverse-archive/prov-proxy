(ns prov-proxy.controllers
  (:use [slingshot.slingshot :only [try+ throw+]]
        [clojure-commons.error-codes])
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(defn has-field
  [in-map field]
  (when-not (contains? in-map field)
    (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
             :field field})))

(defn log-object
  [hmap desc]
  (log/info (str desc ": " (json/json-str hmap)))
  hmap)

(defn get-object-uuid
  [object-id]
  (log-object "get-object" {:object-id object-id})
  object-id)

(defn add-object
  [object-map]
  (has-field object-map :id)
  (has-field object-map :name)
  (has-field object-map :desc)

  (-> (hash-map :service_object_id (:id object-map)
                :object_name       (:name object-map)
                :object_desc       (:desc object-map))
      (log-object "add-object")
      json/json-str))

(defn log-prov
  [object-map caller-ip]
  (has-field object-map :object-id)
  (has-field object-map :user)
  (has-field object-map :service)
  (has-field object-map :event)
  (has-field object-map :category)

  (-> (hash-map
       :uuid              (get-object-uuid (:object-id object-map))
       :username          (:user object-map)
       :service_name      (:service object-map)
       :event_name        (:event object-map)
       :category_name     (:category object-map)
       :request_ipaddress caller-ip
       :proxy_user_id     (:proxy-user object-map)
       :event_data        (:data object-map))
      (log-object "log-provenance")
      json/json-str))

