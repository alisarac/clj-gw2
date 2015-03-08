(ns clj-gw2.core
  (:require [clj-gw2.api :as api]))

(def recipes (api/recipes))
(def recipe-details (map #(api/recipes %) recipes))


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))
