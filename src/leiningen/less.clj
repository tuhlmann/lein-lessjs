(ns leiningen.less
  (:require [robert.hooke         :as hooke]
            [leiningen.help       :as lhelp]
            [leiningen.clean      :as lclean]
            [leiningen.compile    :as lcompile]
            [leiningen.core.main  :as lmain]
            [leiningen.utils      :refer :all]
            [leiningen.render     :refer :all]
            [leiningen.cleaner    :refer :all]
            [clojure.string       :as string]))

(defn- once
  [options]
  (println "Compiling files " (string/join ", " (:src options)) "to" (:output-directory options))
  (render-once! options))

(defn- compile-hook [task & args]
  (apply task args)
  (once (normalize-options (:less (first args)))))

(defn- auto
  [options]
  (println "Ready to compile files " (string/join ", " (:src options)) "to" (:output-directory options))
  (render-loop! options))

(defn- clean
  [options]
  (println "Deleting files generated by lein-lessjs in" (:output-directory options))
  (clean-all! options))

(defn- clean-hook [task & args]
  (apply task args)
  (clean (normalize-options (:less (first args)))))

(defn- abort [s]
  (println s)
  (lmain/abort))

(defn less
  {:help-arglists '([once auto clean]) :subtasks [#'once #'auto #'clean]}
  ([_]
    (abort (lhelp/help-for "less")))

  ([project subtask & args]
    (let [options (normalize-options (:less project))]
      (case (keyword subtask)
        :once (once options)
        :auto (auto options)
        :clean (clean options)
        (abort (str "Subtask" \" subtask \" "not found. " (lhelp/subtask-help-for *ns* #'less)))))))

(defn activate []
  (hooke/add-hook #'lclean/clean #'clean-hook)
  (hooke/add-hook #'lcompile/compile #'compile-hook))