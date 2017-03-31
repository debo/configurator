(ns kii.ui.conf.components
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [kii.ui.conf.actions.components]
            [kii.ui.conf.keyboard.components]
            [kii.ui.conf.layer-select.components]
            [kii.ui.conf.key-group.components]
            [kii.ui.conf.subscriptions]))


;;==== Main Configurator Layout ====;;
(defn main-comp []
  [:div
   [kii.ui.conf.components.actions/actions]
   [:div
    [kii.ui.conf.components.layer-select/layer-tabs]
    [kii.ui.conf.components.keyboard/keyboard]
    [kii.ui.conf.components.key-group/key-groups]]])

(defn main
  []
  (let [loaded? (rf/subscribe [:conf/loaded?])]
    (fn []
      (if @loaded?
        (main-comp)
        [:h2 "LOADING... Enhance your calm." ]))))