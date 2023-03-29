(ns throw.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as str]))

;; -------------------------
;; Views

(def dice (r/atom ""))
(def result (r/atom ""))

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
  (println dice)
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
    (str (conj (:previous final-state) (:current final-state))))) ;; for now put this in str so that it doesnt think it's a html tag
    
(defn parse-and-swap [dice]
  (swap! result #(parse-dice dice)))

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Throw some dice"]
     [:div
      [:input {:type :text
               :value @dice
               :on-change #(reset! dice (.-value (.-target %)))}]]
     [:button {:type "submit"
               :on-click #(parse-and-swap @dice)} "Throw!"]
     [:p "The result: " @result]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
