(ns clj-gw2.api
  (:require [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [cheshire.core :as json]
            [clojure.string :as s]
            [clojure.core.memoize :as memo]))

(def api-root (or (env :api-root) "https://api.guildwars2.com/v2/"))
(def endpoints #{:items :recipes [:recipes :search] :skins :continents 
                 :floors :maps [:commerce :listings] [:commerce :exchange]
                 [:commerce :prices] :build :colors :files :quaggans :worlds})

(defn api-request 
  "General api request"
  [params f]
  (http/request params f))

(defn api-get 
  "Get subject"
  [subject f]
  (api-request {:url (str api-root subject) :method :get} f))

(defn api-post 
  "Create subject with given parameters, this function will not be used"
  [subject params f]
  (api-request (conj params {:url (str api-root subject) :method :post}) f))

(defn parse-result 
  "Default result parser"
  [{:keys [status headers body error]}]
  (if error
    error
    (json/parse-string body true)))

(def api-get-cached-1m (memo/ttl api-get {} :ttl/threshold 60000))
(def api-get-cached-full (memo/memo api-get))

;; items
(defn items []
  @(api-get-cached-1m "items"  parse-result))

(defn item [id]
  @(api-get-cached-full (str "items/" id) parse-result))

;; recipes
(defn recipes []
  @(api-get-cached-1m "recipes"  parse-result))

(defn recipe [id]
  @(api-get-cached-full (str "recipes/" id) parse-result))

(defn recipe-search-input [input]
  @(api-get-cached-1m (str "recipes/search?" "input=" input) parse-result))

(defn recipe-search-output [output]
  @(api-get-cached-1m (str "recipes/search?" "output=" output) parse-result))

