(ns leiningen.cleaner
  (:use leiningen.utils)
  (:require [clojure.java.io :as io]))

(defn clean-all!
  [{:keys [output-directory delete-output-dir] :as options}]
  (doseq [[_ dest-file] (files-from options)]
    (delete-file! (io/file dest-file))
    (delete-file! (io/file (str (.getPath dest-file) ".map"))))

  (when (and delete-output-dir (exists output-directory) (dir-empty? output-directory))
    (println (str "Destination folder " output-directory " is empty - Deleting it"))
    (delete-directory-recursively! output-directory)))