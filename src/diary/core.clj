(ns diary.core
    (:require [clojure.string :as str])
    (:gen-class))

(defn -main
  [& args]
  (def records [])

  (defn format-record
    [record]
    (format "%d. %s" (:position record) (:text record)))

  (defn print-records!
    [records]
    (doseq [record records] (println (format-record record))))

  (defn print-choices!
    [records]
    (def choices
      (let [choices {:a "[a]dd" :r "[r]emove (position)"}]
        (if
          (empty? records) (dissoc choices :r)
          choices)))
    (println (format "Your choices: %s" (str/join ", " (vals choices)))))

  (defn start!
    [records]
    (print-records! records)
    (print-choices! records))

  (defn next-position
    [records]
    (let [positions (map #(:position %1) records)]
      (inc (apply max (conj positions 0)))))

  (defn add-record
    [text records]
    (conj records {:position (next-position records) :text text}))

  (defn remove-record
    [position records]
    (let [[item-to-remove] (filter #(= (Integer. position) (Integer. (:position %1))) records)]
      (filter #(not= %1 item-to-remove) records)
    ))

  (println "Hello! There are your records..")
  (start! records)

  (loop [input (str/trim (read-line))]
    (cond
     (= input "a") (do
                     (println "Write your text below")
                     (def records (add-record (read-line) records)))
     (and (some? (not-empty records)) (some? (re-matches #"r[1-9][0-9]*" input))) (do
                                                  (def position (last (re-matches #"r([1-9][0-9]*)" input)))
                                                  (def records
                                                    (let [new-records (remove-record position records)]
                                                      (if
                                                        (= new-records records) (println "Record was NOT removed, probably bad position?")
                                                        (println "Record was removed."))
                                                      new-records)))
     :else (println  "Wrong choice!"))
    (start! records)
    (recur (read-line)))

  (println "Bye!")
  )
