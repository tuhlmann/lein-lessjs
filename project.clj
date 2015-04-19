(defproject lein-lessjs "0.1.0"
  :description "LESS autobuilder plugin"
  :url "https://github.com/tuhlmann/lein-lessjs"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.cemerick/pomegranate "0.2.0"]
                 [clojure-watch "LATEST"]
                 [me.raynes/fs "1.4.6"]]

  :profiles {:dev {:dependencies [[speclj "2.5.0"]]
                   :plugins [[speclj "2.5.0"]]
                   :test-paths ["spec/"]}

             :spec {:less {:src ["spec/files/foo.less" "spec/files/bar.less"]
                           :source-maps false
                           :output-directory "spec/out"}}

             :spec-map {:less {:src ["spec/files/foo.less" "spec/files/bar.less"]
                               :source-maps true
                               :output-directory "spec/out/map"}}}

  :hooks [leiningen.less]

  :eval-in-leiningen true
  :min-lein-version "2.5.0"
  )
