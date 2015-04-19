(ns leiningen.render
  (:use leiningen.utils)
  (:require [clojure-watch.core :refer [start-watch]]
            [clojure.java.shell :as shell]
            [clojure.java.io    :as io])
  (:import java.lang.Thread))

(defn build-command-vec [src-file dest-file {:keys [compress source-maps]}]
  (let [src-path (.getPath src-file)
        dest-path (.getPath dest-file)]

    (concat ["lessc"]
             (if source-maps ["--source-map"] [])
             (if compress ["--compress"] [])
             [src-path dest-path])))

(defn render
  [src-file dest-file options]
  (when (not (is-partial? src-file))
    (io/make-parents dest-file)
    (let [opts-vec (build-command-vec src-file dest-file options)]
      (println (str "  [lessc] - " (.getName src-file)))
      ;;(println opts-vec)
      (apply shell/sh opts-vec))))

(defn render-once!
  [options]
  (let [descriptors (files-from options)]
    (doseq [[src-file dest-file] descriptors]
      (render src-file dest-file options))))

(defn watch-path
  "the path to watch is taken from the first src entry minus the file name"
  [{:keys [src]}]
  (.getParent (io/file (first src)))
  )

(defn render-loop!
  ([options]
    (render-once! options)
    (start-watch [{:path (watch-path options)
                   :event-types [:create :modify :delete]
                   :callback (fn [_ _] (render-once! options))
                   :options {:recursive true}}])
    (let [t (Thread/currentThread)]
      (locking t
        (.wait t)))))
