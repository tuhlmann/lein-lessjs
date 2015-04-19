(ns leiningen.utils
  (:use [speclj.core]
        [leiningen.utils])
  (:require [clojure.java.io :as io]))

(describe "file-util"

  (describe "fn normalize-options"

    (context "defaults"
      (it "uses the 'resources' folder"
        (should= ["resources/style.less"]
          (:src (normalize-options {}))))

      (it "deletes the output directory"
        (should (:delete-output-dir (normalize-options {}))))

      (it "contains a :compress formatting style for less"
        (should= false (:compress (normalize-options {}))))

      (it "contains a :source-maps formatting style for less"
        (should= true (:source-maps (normalize-options {})))))

    (context "overwriting defaults"
      (it "lets you set sources folder"
        (should= "other/folder"
          (:src (normalize-options {:src "other/folder"}))))

      (it "lets you unset the delete outpout directory flag"
        (should-not (:delete-output-dir (normalize-options {:delete-output-dir false}))))

      (it "lets you set the formatting style for sass"
        (should= :compressed (:style (normalize-options {:style :compressed}))))))


  (describe "fn dir-empty?"
    (it "is true when the directory is empty"
      (should (dir-empty? "spec/files/empty_dir")))

    (it "is false when the directory not is empty"
      (should-not (dir-empty? "spec/files"))))

  (describe "fn delete-directory-recursively!"
    (it "doesn't do anything if the file doesn't exists"
      (should-not (.exists (io/file "spec/file/does_not_exits")))
      (with-out-str
        (should-not-throw (delete-directory-recursively! "spec/file/does_not_exits")))
      (should-not (.exists (io/file "spec/file/does_not_exits"))))

    (it "deletes the directory recursively"
      (io/make-parents "spec/out/sub/blah")
      (spit "spec/out/blah" "blah")
      (spit "spec/out/sub/blah" "blah")
      (with-out-str
        (delete-directory-recursively! "spec/out"))
      (should-not (.exists (io/file "spec/out"))))))
