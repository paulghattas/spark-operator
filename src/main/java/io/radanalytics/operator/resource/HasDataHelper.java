/*
 * Copyright 2018, OSHINKO authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.radanalytics.operator.resource;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.radanalytics.operator.ClusterInfo;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A helper for parsing the data section inside the K8s resource
 */
public class HasDataHelper {

    private static Yaml snake = new Yaml(new Constructor(ClusterInfo.class));

    /**
     * Returns the value of the {@code data.workerNodes} of the given {@code resource}.
     */
    public static Optional<String> workers(ConfigMap cm) {
        return getValue(cm, "workerNodes");
    }

    /**
     * Returns the value of the {@code data.masterNodes} of the given {@code resource}.
     */
    public static Optional<String> masters(ConfigMap cm) {
        return getValue(cm, "masterNodes");
    }

    /**
     * Returns the value of the {@code data.customImage} of the given {@code resource}.
     */
    public static Optional<String> image(ConfigMap cm) {
        return getValue(cm, "customImage");
    }

    public static ClusterInfo parseCM(ConfigMap cm) {
        String yaml = cm.getData().get("config");
        Representer representer = new Representer();
        representer.getPropertyUtils().setSkipMissingProperties(true);
        snake = new Yaml(new Constructor(ClusterInfo.class), representer);
        try {
            ClusterInfo cluster = snake.load(yaml);
            cluster.setName(cm.getMetadata().getName());
            return cluster;
        } catch (NullPointerException npe) {
            throw new IllegalArgumentException("Unable to parse yaml definition of configmap, check if you don't have typo: \n" +
            cm.getData().get("config"));
        }
    }

    private static Optional<String> getValue(ConfigMap cm, String key) {
        return Optional.ofNullable(cm.getData().get(key));
    }
}