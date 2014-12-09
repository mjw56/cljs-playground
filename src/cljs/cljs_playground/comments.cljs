(ns cljs-playground.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [om.core :as om  :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state
  (atom
    {:data {:comments [{ :author "Commenter 1" :text "comment 1" }
                       { :author "Commenter 2" :text "comment 2" }]}}))

(defn Comment
  [comment]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (dom/h2 nil (str (:author comment)) ": " (str (:text comment)))))))

(defn CommentList
  [comments-data]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
               (apply
                 dom/ul nil
                 (om/build-all Comment comments-data)))))) ;; build-all iterates over a sequence and builds a component for each item

(defn CommentBox
  [comments]
  (reify
    om/IRender
    (render [_]
      (dom/div #js { :id "comment-box" }
               (om/build CommentList (:comments comments))))))

(defn handle-change [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))

(defn handle-submit [e owner {:keys [text]} comments]
  (println "comments: " @comments)
  ;; When you trasact! or update!, you need to pass cursor, key(s) where the cursor will be updated and the data/function
  (om/transact! comments :comments #(conj % {:author "Guest" :text text}))
  (om/set-state! owner :text "")
  (let [comment-box (.getElementById js/document "comment-box")]
     (set! (.-scrollTop comment-box) (.-scrollHeight comment-box))
  ))

(defn Input
  [comments owner]
  (reify
    om/IInitState
    (init-state [_]
      {:text nil})
    om/IRenderState
    (render-state [_ state]
        (dom/div #js {:className "input-group col-sm-12"}
               (dom/input #js
                          {:type "text"
                           :ref "text-field"
                           :value (:text state)
                           :className "form-control"
                           :onChange (fn [event] (handle-change event owner state))})
               (dom/span #js {:className "input-group-btn" }
                 (dom/button
                   #js {
                         :className "btn btn-default"
                         :onClick (fn [event] (handle-submit event owner state comments))}
                   "submit"))))))

(defn my-app [global]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/h2 nil "comments")
               (om/build CommentBox (:data global))
               (om/build Input (:data global))))))

(defn main []
  (om/root my-app app-state
           {:target (.getElementById js/document "app")}))
