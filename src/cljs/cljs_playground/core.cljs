(ns cljs-playground.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(defonce app-state (atom {:text "This is some new text"
                          :items ["bird", "cat", "dog"]}))

(defn alist [items]
  (apply
    dom/ul nil
    (map #(dom/li nil %) items)))

(defn main []
(om/root
  (fn [app owner]
    (reify
      om/IInitState
      (init-state [_]
        {:value "" :count 0})
      om/IRenderState
      (render-state [_ {:keys [value]}]
        (dom/div nil
          (dom/h1 nil (:text app))
          (alist (:items app))
          (dom/label nil "Only numeric : ")
          (dom/input #js
            {:value value
             :onChange
             #(let [new-value (-> % .-target .-value)]
                (if (js/isNaN new-value)
                  (om/set-state! owner :value value)
                  (om/set-state! owner :value new-value)))})))))
    app-state
    {:target (. js/document (getElementById "app"))}))
