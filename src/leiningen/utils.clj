(ns leiningen.utils
  (:require [me.raynes.fs     :as fs]
            [clojure.java.io  :as io]
            [clojure.string   :as string]))

(def ^:private default-options {:src ["resources/style.less"]
                                :compress false
                                :source-maps true
                                :delete-output-dir true
                                :output-directory "resources/public/css"})

(defn normalize-options
  [options]
  (merge default-options options))

(defn- dest-file
  [src-file src-dir dest-dir]
  (let [src-dir  (.getCanonicalPath (io/file src-dir))
        dest-dir (.getCanonicalPath (io/file dest-dir))
        src-path (.getCanonicalPath src-file)
        rel-src-path (string/replace src-path src-dir "")
        rel-dest-path (string/replace rel-src-path (fs/extension src-file) ".css")]
    (io/file (str dest-dir rel-dest-path))))

;(defn files-from
;  [{:keys [src output-directory]}]
;  (let [file-filter (fn [file]
;                      (case (fs/extension file)
;                        ".less" true
;                        false))
;        source-files (fs/find-files* src file-filter)]
;    (reduce #(assoc %1 %2 (io/file (dest-file %2 src output-directory))) {} source-files)))

(comment
  Takes the source files and the output path and creates a list
  of source file and resulting output file)

(defn files-from
  [{:keys [src output-directory]}]
  (reduce #(assoc %1 (io/file %2) (io/file (dest-file (io/file %2) (.getParent (io/file %2)) output-directory))) {} src))

(defn is-partial?
  [file]
  (.startsWith (.getName file) "_"))

(defn exists
  [dir]
  (and dir (.exists (io/file dir))))

(defn dir-empty?
  [dir]
  (not (reduce (fn [memo path] (or memo (.isFile path))) false (file-seq (io/file dir)))))

(defn delete-file!
  [file]
  (when (.exists file)
    (println (str "Deleting: " file))
    (io/delete-file file)))

(defn delete-directory-recursively!
  [base-dir]
  (doseq [file (reverse (file-seq (io/file base-dir)))]
    (delete-file! file)))
