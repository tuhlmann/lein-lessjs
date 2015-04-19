(ns leiningen.integration-spec
  (:use [speclj.core]
        [clojure.java.shell :only [sh]])
  (:require [leiningen.utils :as futils]
            [clojure.java.io :as io]))

(describe "integration tests on tasks"
  ;; This is not ideal but provides some way of testing the tasks (given
  ;; that I have figured out how to include leiningen dependencoes in
  ;; the tests) especially: we are relying on the project.clj file
  ;; (which can't be changed from here)

  (before (with-out-str (futils/delete-directory-recursively! "spec/out")))

  (defn less [profile sub-task] (sh "lein" "with-profile" profile "less" sub-task))


  (context "without source maps"

    (context "once"
      (it "compiles the files in the correct directory"
        (less "spec" "once")

        (let [out-files (file-seq (io/file "spec/out"))]
          (should= 3 (count out-files)))

        (let [file-content (slurp "spec/out/foo.css")
              expected-content ".wide {\n  width: 100%;\n}\n.foo {\n  display: block;\n}\n"]
          (should= expected-content file-content))

        (let [file-content (slurp "spec/out/bar.css")
              expected-content ".bar {\n  display: none;\n}\n"]
          (should= expected-content file-content))))

    (context "clean"
      (it "removes all artifacts that were created by less task"
        (less "spec" "once")
        (should (.exists (io/file "spec/out")))

        (less "spec" "clean")
        (should-not (.exists (io/file "spec/out"))))

      (it "only deletes the artifacts that were created by less task"
        (less "spec" "once")
        (should (.exists (io/file "spec/out")))

        (spit "spec/out/not-generated" "a non generated content")

        (less "spec" "clean")
        (should (.exists (io/file "spec/out/not-generated")))
        (should-not (.exists (io/file "spec/out/bar.css")))
        (should-not (.exists (io/file "spec/out/foo.css"))))))


  (context "with source maps"

    (context "once"
      (it "compiles the files in the correct directory"
        (less "spec-map" "once")

        (let [out-files (file-seq (io/file "spec/out/map"))]
          (should= 5 (count out-files)))

        (let [file-content (slurp "spec/out/map/foo.css")
              expected-content ".wide {\n  width: 100%;\n}\n.foo {\n  display: block;\n}\n/*# sourceMappingURL=foo.css.map */"]
          (should= expected-content file-content))
        (should (.exists (io/file "spec/out/map/foo.css.map")))

        (let [file-content (slurp "spec/out/map/bar.css")
              expected-content ".bar {\n  display: none;\n}\n/*# sourceMappingURL=bar.css.map */"]
          (should= expected-content file-content))
        (should (.exists (io/file "spec/out/map/bar.css.map")))))

    (context "clean"
      (it "removes all artifacts that were created by less task"
        (less "spec-map" "once")
        (should (.exists (io/file "spec/out/map")))

        (less "spec-map" "clean")
        (should-not (.exists (io/file "spec/out/map"))))

      (it "only deletes the artifacts that were created by less task"
        (less "spec-map" "once")
        (should (.exists (io/file "spec/out/map")))

        (spit "spec/out/map/not-generated" "a non generated content")

        (less "spec-map" "clean")
        (should (.exists (io/file "spec/out/map/not-generated")))
        (should-not (.exists (io/file "spec/out/map/bar.css")))
        (should-not (.exists (io/file "spec/out/map/bar.css.map")))
        (should-not (.exists (io/file "spec/out/map/foo.css")))
        (should-not (.exists (io/file "spec/out/map/foo.css.map")))))))
