(ns cljs-playground.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(def app-state
  (atom
    {:comments-data [{ :author "Mike Wilcox" :text "ClojureScript is sweet" }
                                            { :author "Rich Hickey" :text "Simplicity is hard work" }]}))

(defn Comment
  [props owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (println "Comment mounting"))
    om/IWillUnmount
    (will-unmount [_]
      (println "Comment unmounting"))
    om/IRender
    (render [_]
      (println "Comment rendered" props)
      (dom/div nil
        (dom/h2 nil (str (:author props)) ": " (str (:text props)))))))

(defn CommentList
  [props owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (println "Comment List mounting"))
    om/IWillUnmount
    (will-unmount [_]
      (println "Comment List unmounting"))
    om/IRender
    (render [_]
      (println "Comment List rendered")
      (dom/div nil
        (apply
          dom/ul nil
          (map #(om/build Comment (:comments-data props %)) props))))))

(defn CommentBox
  [props owner]
  (reify
    om/IWillMount
    (will-mount [_]
      (println "Comment Box mounting"))
    om/IWillUnmount
    (will-unmount [_]
      (println "Comment Box unmounting"))
    om/IRender
    (render [_]
      (println "Comment Box rendered")
      (dom/div nil
        (om/build CommentList props)))))

(defn handle-change [e owner {:keys [text]}]
  (om/set-state! owner :text (.. e -target -value)))

(defn handle-submit [e owner {:keys [text]} data]
  (om/set-state! owner :text "")
  (om/transact! data push))

(defn Input
  [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:text nil})
    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (dom/input #js
          { :type "text"
            :ref "text-field"
            :value (:text state)
            :onChange (fn [event] (handle-change event owner state))})
        (dom/button
          #js { :onClick (fn [event] (handle-submit event owner state data))}
              "submit")))))

(defn my-app [global owner]
  (reify
    om/IRender
    (render [_]
      (println "Root rendered")
      (dom/div nil
        (dom/h2 nil "A mini comments app using clojure and react")
        (om/build CommentBox (:comments-data global))
        (om/build Input ((get global :comments-data) this))))))

(om/root my-app app-state
  {:target (.getElementById js/document "app")})
