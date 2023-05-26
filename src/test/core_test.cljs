(ns core-test
  (:require [cljs.test :refer [deftest is testing]]
            [throw.core :refer [parse-input]]))

(deftest input-parsing
  (testing "When given a proper string of dice and numbers, returns the data split in a vector"
    (is (= ["3d6" "7" "3d20" "-1"] (parse-input "3d6+7+3d20-1")))
    (is (= ["" "-1d6"] (parse-input "-1d6"))))
  (testing "When given an improper string of dice and numbers, returns nil" ;; should maybe throw e.
    (is (= [""] (parse-input "acb")))))


