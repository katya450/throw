(ns core-test
  (:require [cljs.test :refer [deftest is testing]]
            [throw.core :refer [tokenize-input]]))

(deftest input-parsing
  (testing "When given a proper string of dice and numbers, returns the data split in a vector"
    (is (= ["3d6" "7" "3d20" "-1"] (tokenize-input "3d6+7+3d20-1")))
    (is (= ["" "-1d6"] (tokenize-input "-1d6"))))
  (testing "When given an improper string of dice and numbers, returns nil" ;; should maybe throw e.
    (is (= [""] (tokenize-input "acb")))))


