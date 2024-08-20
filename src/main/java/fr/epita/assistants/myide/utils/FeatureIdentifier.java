package fr.epita.assistants.myide.utils;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;

public class FeatureIdentifier {

    public static Feature.Type identifyFeature(String featureName) {
        // Check in Any features
        for (Mandatory.Features.Any feature : Mandatory.Features.Any.values()) {
            if (feature.name().equalsIgnoreCase(featureName)) {
                return feature;
            }
        }

        // Check in Git features
        for (Mandatory.Features.Git feature : Mandatory.Features.Git.values()) {
            if (feature.name().equalsIgnoreCase(featureName)) {
                return feature;
            }
        }

        // Check in Maven features
        for (Mandatory.Features.Maven feature : Mandatory.Features.Maven.values()) {
            if (feature.name().equalsIgnoreCase(featureName)) {
                return feature;
            }
        }

        // If no feature is found, return null
        return null;
    }
}
