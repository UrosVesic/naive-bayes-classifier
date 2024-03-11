(defproject bayes "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.csv "1.0.0"]
                 [ring "1.9.0"]
                 [javax.servlet/servlet-api "2.5"]
                 [metosin/reitit "0.7.0-alpha7"]
                 [metosin/muuntaja "0.6.8"]
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "8.0.33"]
                 [org.testcontainers/mysql "1.19.0"]
                 [org.testcontainers/testcontainers "1.19.0"]
                 [com.github.seancorfield/honeysql "2.5.1103"]]
  :main ^:skip-aot bayes.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
