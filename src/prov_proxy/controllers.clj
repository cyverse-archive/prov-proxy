(ns prov-proxy.controllers
  (:use [slingshot.slingshot :only [try+ throw+]]
        [clojure-commons.error-codes])
  (:require [clojure.data.json :as json]))

(defn get-object-uuid
  [object-id]
  object-id)

(defn has-field
  [in-map field]
  (when-not (contains? in-map field)
    (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
             :field field})))

(defn add-object
  [object-map]
  (has-field object-map :id)
  (has-field object-map :name)
  (has-field object-map :desc)
  
  (json/json-str
   (hash-map :service_object_id (:id object-map)
             :object_name       (:name object-map)
             :object_desc       (:desc object-map))))

(defn log-prov
  [object-map caller-ip]
  (has-field object-map :object-uuid)
  (has-field object-map :user)
  (has-field object-map :service)
  (has-field object-map :event)
  (has-field object-map :category)

  (json/json-str
   (hash-map
    :uuid              (:object-uuid object-map)
    :username          (:user object-map)
    :service_name      (:service object-map)
    :event_name        (:event object-map)
    :category_name     (:category object-map)
    :request_ipaddress caller-ip
    :proxy_user_id     (:proxy-user object-map)
    :event_data        (:data object-map))))

