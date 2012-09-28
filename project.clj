(defproject prov-proxy "0.1.0-SNAPSHOT"
  :description "A proxy for the iPlant provenance API."
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.json "0.1.1"]
                 [org.clojure/tools.logging "0.2.3"]
                 [org.clojure/tools.cli "0.2.1"]
                 [slingshot "0.10.1"]
                 [compojure "1.0.1"]
                 [ring/ring-jetty-adapter "1.0.1"]
                 [ring/ring-devel "1.0.1"]
                 [org.iplantc/clojure-commons "1.2.1-SNAPSHOT"]]
  :aot [prov-proxy.core]
  :main prov-proxy.core
  :min-lein-version "2.0.0"
  :plugins [[lein-ring "0.7.5"]
            [org.iplantc/lein-iplant-rpm "1.3.2-SNAPSHOT"]]
  :ring {:init prov-proxy.controllers/init,
         :handler prov-proxy.core/app}
  :profiles {:dev {:resource-paths ["local-conf"]
                   :dependencies [[midje "1.4.0"]
                                  [lein-midje "2.0.0-SNAPSHOT"]]}}
  :repositories
  {"iplantCollaborative"
   "http://projects.iplantcollaborative.org/archiva/repository/internal/"})
