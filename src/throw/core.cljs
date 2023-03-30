(ns throw.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as str]))

;; -------------------------
;; Views

(def dice (r/atom ""))
(def result (r/atom 0))

(defn throw-one-die [times sides]
  (* times (+ 1 (rand-int sides))))

;; (defn parse-dice [dice]
;;   (println dice)
;;   (let [;dice-thrown (first (str/split throw #"[+-]")) ;; supports only 1 die now, think about this later
;;         times (first (str/split dice #"d"))
;;         sides (last (str/split dice #"d"))]
;;     (swap! result #(throw-one-die times sides))))

(defn digit? [char]
  (boolean (re-matches #"^\d$" char)))

(defn parse-dice [dice]
  (let [dice-split (str/split dice #"")
        final-state (reduce
                     (fn [acc, char]
                       (cond
                         (digit? char) {:current (str (:current acc) char) :previous (:previous acc)}
                         (= "+" char) {:current "" :previous (conj (:previous acc) (:current acc))}
                         (= "-" char) {:current "-" :previous (conj (:previous acc) (:current acc))}
                         :else acc))
                     {:current "" :previous []}
                     dice-split)]
   ;; final-state ;;; {:current "444", :previous ["2" "-11"]}
    (conj (:previous final-state) (:current final-state))))
    
(defn calculate [throw]
  (reduce + (map int throw)))

(defn parse-and-swap [dice]
  (let [dice-parsed (parse-dice dice)
        throw-calculated (calculate dice-parsed)]
    (reset! result throw-calculated)))

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Throw!"]
     [:div
      [:input {:type :text
               :value @dice
               :on-change #(reset! dice (.-value (.-target %)))}]]
     [:button {:type "submit"
               :on-click #(parse-and-swap @dice)} "Go!"]
     [:p "The result: " @result]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
