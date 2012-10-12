(ns prov-proxy.controllers
  (:use [slingshot.slingshot :only [try+ throw+]]
        [clojure-commons.error-codes])
  (:require [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(defn rand-uuid
  []
  (str (java.util.UUID/randomUUID)))

(def objects (atom (hash-map)))

(defn has-field
  [in-map field]
  (when-not (contains? in-map field)
    (throw+ {:error_code ERR_BAD_OR_MISSING_FIELD
             :field field})))

(defn log-object
  [hmap desc]
  (log/warn (str desc ": " (json/json-str hmap)))
  hmap)

(defn does-not-exist
  [object-id]
  (json/json-str
   {:error_code ERR_DOES_NOT_EXIST
    :object-id object-id}))

(defn get-object-uuid
  [object-id]
  (log-object "get-object" {:object-id object-id})
  (if (contains? @objects object-id)
    {:uuid (get @objects object-id)}
    {:status 404 :body (does-not-exist object-id)}))

(defn add-object
  [object-map]
  (log/warn (str "add-object: " object-map))
  
  (has-field object-map :id)
  (has-field object-map :name)
  (has-field object-map :desc)

  (let [new-uuid (rand-uuid)]
    (-> (hash-map :service_object_id (:id object-map)
                  :object_name       (:name object-map)
                  :object_desc       (:desc object-map)
                  :parent_uuid       (:parent object-map))
        (log-object "add-object"))
    (reset! objects (assoc @objects (:id object-map) new-uuid))
    (assoc object-map :uuid new-uuid)))

(defn log-prov
  [object-map caller-ip]
  (log/warn (str "log-prov: " object-map))
  
  (has-field object-map :id)
  (has-field object-map :user)
  (has-field object-map :service)
  (has-field object-map :event)
  (has-field object-map :category)

  (-> (hash-map
       :uuid              (:uuid (get-object-uuid (:id object-map)))
       :username          (:user object-map)
       :service_name      (:service object-map)
       :event_name        (:event object-map)
       :category_name     (:category object-map)
       :request_ipaddress caller-ip
       :proxy_user_id     (:proxy-user object-map)
       :event_data        (:data object-map))
      (log-object "log-provenance"))
  (dissoc object-map :data))

