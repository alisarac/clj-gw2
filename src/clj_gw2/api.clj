(ns clj-gw2.api
  (:require [org.httpkit.client :as http]
            [environ.core :refer [env]]
            [cheshire.core :as json]
            [clojure.string :as s]
            [clojure.core.memoize :as memo]))

(def api-root-v1 (or (env :api-root-v1) "https://api.guildwars2.com/v1/"))
(def api-root-v2 (or (env :api-root-v2) "https://api.guildwars2.com/v2/"))

(defn api-get
  "Get subject"
  [api-root subject]
  (http/get (str api-root subject)))

(def api-get-cached-1m (memo/ttl api-get {} :ttl/threshold 60000))
(def api-get-cached-full (memo/memo api-get))

(defn api-get-one [api-root subject]
  (let [{:keys [body error] :as r} @(api-get-cached-full api-root subject)]
    (when-not error (json/parse-string body true))))

(defn api-get-all [api-root subject ids]
  (let [futures (doall (map #(api-get-cached-full api-root (str subject "/" %)) ids))]
    (for [resp futures
          :let [{:keys [body error] :as r} @resp]
          :when (not error)]
      (json/parse-string body true))))

(defn api-fn-gen 
  "General api function"
  [api-root subject]
  (fn
    ([] (api-get-one api-root subject))
    ([& ids] (api-get-all api-root subject ids))))

(defn api-fn
  "General api function for v2 API"
  [subject] 
  (api-fn-gen api-root-v2 subject))

(defn api-v1-fn
  "General api function for v2 API"
  [subject] 
  (api-fn-gen api-root-v1 subject))


;; v2 api endpoints

(def items (api-fn "items"))


(def recipes (api-fn "recipes"))

(defn recipes-search-input [input]
  (api-get-one api-root-v2 (str "recipes/search?" "input=" input)))

(defn recipes-search-output [output]
  (api-get-one api-root-v2 (str "recipes/search?" "output=" output)))


(def continents (api-fn "continents"))

;; floors are disabled in v2
;; Edit:2015-03-08 it looks it is enabled now
(def floors (api-fn "floors"))

(def maps (api-fn "maps"))

(def skins (api-fn "skins"))

(def commerce-listings (api-fn "commerce/listings"))

;; need to be more modular, quantity=<quantity> is required on sub calls
(def commerce-exchange (api-fn "commerce/exchange"))

(def commerce-prices (api-fn "commerce/prices"))

(def build (api-fn "build"))

(def colors (api-fn "colors"))

(def files (api-fn "files"))

(def quaggans (api-fn "quaggans"))

(def worlds (api-fn "worlds"))


;; v1 endpoints

(def event-names (api-v1-fn "event_names"))

(def map-names (api-v1-fn "map_names"))

(def world-names (api-v1-fn "world_names"))

(def event-details (api-v1-fn "event_details"))

(defn event-details-event-id [id]
  (api-get-one api-root-v1 (str "event_details?event_id=" id)))

(defn guild-details-guild-id [id]
  (api-get-one api-root-v1 (str "guild_details?guild_id=" id)))

(defn guild-details-guild-name [id]
  (api-get-one api-root-v1 (str "guild_details?guild_name=" id)))

(defn map-floor-continent-id-floor [cid floor]
  (api-get-one api-root-v1 (str "map_floor?continent_id=" cid "&floor=" floor)))

(def wvw-matches (api-v1-fn "wvw/matches"))

(defn wvw-match-details-match-id [id]
  (api-get-one api-root-v1 (str "wvw/match_details?match_id=" id)))

(def wvw-objective-names (api-v1-fn "wvw/objective_names"))

(defn render [signature file-id format]
  (str "https://render.guildwars2.com/file/" signature "/" file-id "." format))
