(ns throw.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as str]))

;; -------------------------
;; Views

(def input (r/atom ""))
(def result (r/atom 0))

(defn throw-one-die [die]
  (let [times (first (str/split die #"d"))
        sides (last (str/split die #"d"))]
    (* times (+ 1 (rand-int sides)))))

(defn digit? [char]
  (boolean (re-matches #"^\d$" char)))

(defn die? [maybe-die]
  (boolean (re-matches #"^-?\d{1,3}d\d{1,3}" maybe-die)))

;; in case of a special case, one can (BUT SHOULD HIM?) also give one die with "d6" and NOT "1d6"
(defn parse-input [dice]
  (let [dice-split (str/split dice #"")
        final-state (reduce
                     (fn [acc, char]
                       (cond
                         (or (= "d" char) (digit? char)) {:current (str (:current acc) char) :previous (:previous acc)}
                         (= "+" char) {:current "" :previous (conj (:previous acc) (:current acc))}
                         (= "-" char) {:current "-" :previous (conj (:previous acc) (:current acc))}
                         :else acc)) ;; should maybe throw something with "2e8"? right now it just handles it as num
                     {:current "" :previous []}
                     dice-split)]
    (conj (:previous final-state) (:current final-state)))) ;; ["1d8" "4" "3d20" "-1d4"]

(defn calculate [throw]
  (reduce + (map int throw)))

(defn throw-dice [input-dice]
  (map #(throw-one-die %) input-dice))

;; input-parsed: map over this, with a fn that deducts which fn to use for it AND returns an anonym fn with no params, 
;; then this all return vec of fns 
;; fex [throw fn-anynym-no-params throw ]

(defn parse-and-swap [input]
  (let [input-parsed (parse-input input) ;; this could be a ->>
        dice-thrown (vec (throw-dice (filterv die? input-parsed))) ;; this is stupid.
        just-numbers (map int (filterv #(not= die? %) input-parsed)) ;; this is also stupid.
        throw-parsed (concat dice-thrown just-numbers)
        throw-calculated (calculate throw-parsed)]
    (reset! result throw-calculated)))

(def throwable-dice-sides [4 6 8 10 12 20 100])

(defn die-button [d]
  [:div {:class "die-img"} d])

(defn dice []
  [:div {:class "die-container"} 
   (map #(die-button %) throwable-dice-sides)])

(defn home-page []
  (fn []
    [:span.main
     [:h1 "Throw!"]
     [dice]
     [:div
      [:input {:type :text
               :value @input
               :on-change #(reset! input (.-value (.-target %)))}]]
     [:button {:type "submit"
               :on-click #(parse-and-swap @input)} "Go!"]
     [:p "The result: " @result]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
