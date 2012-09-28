(ns prov-proxy.conf
  (:require [clojure-commons.props :as props]
            [clojure-commons.clavin-client :as clavin]
            [clojure.tools.logging :as log]))

(def conf (atom nil))

(defn listen-port [] (Integer/parseInt (get @conf "prov-proxy.listen-port")))

(defn init
  []
  (let [zkconf (props/parse-properties "zkhosts.properties")
        zkurl  (get zkconf "zookeeper")]
    (clavin/with-zk zkurl
      (when-not (clavin/can-run?)
        (log/warn "THIS APPLICATION CANNOT RUN ON THIS MACHINE!")
        (log/warn "THIS APPLICATION WILL NOT EXECUTE CORRECTLY!"))

      (reset! conf (clavin/properties "prov-proxy")))))

(defn local-init
  [local-config-path]
  (reset! conf (props/read-properties local-config-path)))
