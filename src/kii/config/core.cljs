(ns kii.config.core
  (:require [kii.keys.firmware.map :as fw]
            [kii.keys.core :as keys]))

(defn de-key [m f]
  (into
    {}
    (map (fn [[k v]] [(name k) (if f (f k v) v)]) m)))

(defn mangle-layer
  [n layer]
  (do
    (let [k (get fw/keys (:key layer))]
      {"key" (-> k :aliases first)
       "label" (:label k)})))

(defn mangle
  [config]
  {"header"     (de-key (:header config) nil)
   "defines"    (mapv #(de-key (:data %) nil) (:defines config))
   "matrix"     (map (fn [key]
                       (de-key
                         key
                         (fn [k v]
                           (if (= k :layers)
                             (let [layers (into {} (filter #(-> % second :key some?) v))]
                               (de-key layers mangle-layer))
                             v))))
                     (:matrix config))
   "leds"       (mapv #(de-key % nil) (:leds config))
   "custom"     (de-key (:custom config) nil)
   "animations" (de-key (:animations config) nil)})

(defn normalize-layers
  [layers]
  (into
    {}
    (map (fn [[layer data]]
           (let [okey (:key data)
                 mapped (fw/alias->key okey)
                 iec (get (keys/key->iec) (:name mapped))]
             [layer
              (keys/merge mapped iec)
              ]))
         layers)))

(defn normalize
  [config]
  (let [matrix (:matrix config)
        min-left (apply min (map :x matrix))
        min-top (apply min (map :y matrix))
        defines (or (:defines config) [])
        leds (or (:leds config) [])
        custom (or (:custom config) {})
        animations (or (:animations config) {})]
    (assoc config
      :matrix (vec (map #(merge % {:x      (- (:x %) min-left)
                                   :y      (- (:y %) min-top)
                                   :layers (normalize-layers (:layers %))})
                        matrix))
      :defines (mapv (fn [d] {:id (random-uuid) :data d})
                     defines)
      :leds leds
      :custom custom
      :animations animations)
    ))