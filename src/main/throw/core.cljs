(ns throw.core
  (:require
   [reagent.core :as r]
   [reagent.dom :as d]
   [clojure.string :as string]))

;; -------------------------
;; Views

(def input (r/atom ""))
(def result (r/atom 0))

(defn throw-one-die [die]
  (let [count (first (clojure.string/split die #"d"))
        times (case count
                "+" 1 ;; this should be done way before throwing, in the parser.
                "-" -1
                count)
        sides (last (clojure.string/split die #"d"))]
    (* times (+ 1 (rand-int sides)))))

(defn digit? [char]
  (boolean (re-matches #"^\d$" char)))

(defn die? [maybe-die]
  (boolean (re-matches #"^[-+]?(\d{1,3})?d\d{1,3}" maybe-die)))

(defn die-sides [d]
  (last (clojure.string/split d #"d")))

(defn add-times-to-zero-die [die]
  (let [first-item (first (clojure.string/split die #"d"))]
    (case first-item
      "" (str "+1d" (die-sides die))
      "-" (str "-1d" (die-sides die))
      die)))

(defn tokenize-input [input]
  (let [dice-split (clojure.string/split (clojure.string/lower-case input) #"")
        final-state (reduce
                     (fn [acc, char]
                       (cond
                         (or (= "d" char) (digit? char)) {:current (str (:current acc) char) :previous (:previous acc)}
                         (= "+" char) {:current "+" :previous (conj (:previous acc) (:current acc))}
                         (= "-" char) {:current "-" :previous (conj (:previous acc) (:current acc))}
                         :else acc)) ;; should maybe throw an error or something with "2e8"? right now it just handles it as num
                     {:current "" :previous []}
                     dice-split)]
    (mapv #(add-times-to-zero-die %) (remove empty? (conj (:previous final-state) (:current final-state)))))) ;; ["-2d6" "+4" "-33" "+3d8" "+1d10"]

(defn calculate [throw]
  (reduce + (map int throw)))

(defn throw-dice [input-dice]
  (map #(throw-one-die %) input-dice))

;; Some fn should check there's no +- -+ -- ++ stuff! 

(defn make-a-throw [dice numbers]
  (let [dice-thrown (vec (throw-dice dice))
        throw-parsed (concat dice-thrown numbers)
        throw-calculated (calculate throw-parsed)]
    (reset! result throw-calculated)))

(defn parse-and-throw [input]
  (let [input-parsed (tokenize-input input)
        dice (filterv die? input-parsed)
        numbers (map int (filterv #(not= die? %) input-parsed))]
    (make-a-throw dice numbers)))

(defn side-matches? [input-die d]
  (= (die-sides input-die) (die-sides d)))

(defn get-matching-die [d tokenized-input]
   (filter #(side-matches? % d) tokenized-input)) ;; () if no match

(defn existing-die? [d tokenized-input]
  (and (die? d) (get-matching-die d tokenized-input)))

(defn die-times-int [d]
  (let [existing-times (->> (clojure.string/split d #"d")
                         first
                         last)]
   (js/parseInt existing-times)))

(defn die-increased [d tokenized-input]
  (let [die-to-increase (get-matching-die d tokenized-input)
        times-inc (inc (die-times-int die-to-increase)) ;; NOTE! At this point only + dice can be increased because the buttons only adds + dice
        sides (die-sides d)]
    (println die-to-increase)
    (println (die-times-int die-to-increase))    
    (println times-inc)    
    (clojure.string/replace-first (apply str tokenized-input) (re-pattern (str "\\" d)) (str times-inc "d" sides))))

(defn add-button-die [d tokenized-input]
  (if (existing-die? d tokenized-input)
    (let [index-of-d (.indexOf tokenized-input d)]
      (->> (assoc tokenized-input index-of-d (str (die-increased d tokenized-input)))
           (str)
           (reset! input)))
    (swap! input str d))
  ;; then reset the input without losing anything there was before
  )

(defn die-button [d]
  ^{:key d} [:button
             {:class "die-img"
              :on-click #(add-button-die (str "+1d" d) (tokenize-input @input))}
             [:img {:src (str "images/d" d ".jpeg") :width "60px"}] d])

(defn dice []
  [:div {:class "die-container"}
   (map #(die-button %) [4 6 8 10 12 20 100])])

(defn home-page []
  (fn []
    [:span.main
     [dice]
     [:div
      [:input {:type :text
               :value @input
               :on-change #(reset! input (.-value (.-target %)))}]]
     [:button {:type "submit"
               :on-click #(parse-and-throw @input)} "Throw!"]
     [:button {:type "submit"
               :on-click #(reset! input "")} "Reset"]
     [:p "The result is " @result]]))

;; -------------------------
;; Initialize app

(defn mount-root []
  (d/render [home-page] (.getElementById js/document "app")))

(defn ^:export init! []
  (mount-root))
