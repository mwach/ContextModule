package itti.com.pl.arena.cm.server.geoportal.gov.pl.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeoportalResponse {

    private static final int LAST = -1;

    private Map<String, List<Map<String, String>>> layers;

    public GeoportalResponse() {
        layers = new HashMap<>();
    }

    public void addValue(String layerName, String key, String value) {
        getLayerElement(layerName, LAST).put(key, value);
    }

    public String getValue(String layerName, int layerElement, String key) {
        Map<String, String> layer = getLayerElement(layerName, layerElement);
        return layer == null ? null : layer.get(key);
    }

    public Set<String> getLayersIds() {
        return layers.keySet();
    }

    public int getLayerElementsCount(String layerId) {
        if (layers.containsKey(layerId)) {
            return layers.get(layerId).size();
        }
        return 0;
    }

    private Map<String, String> getLayerElement(String layerName, int element) {
        List<Map<String, String>> layerElements = layers.get(layerName);
        return layerElements.get(element == LAST ? layerElements.size() - 1 : element);
    }

    public void startNewLayer(String layerId) {
        if (!layers.containsKey(layerId)) {
            layers.put(layerId, new ArrayList<Map<String, String>>());
        }
        List<Map<String, String>> layerElements = layers.get(layerId);
        layerElements.add(new HashMap<String, String>());
    }
}
