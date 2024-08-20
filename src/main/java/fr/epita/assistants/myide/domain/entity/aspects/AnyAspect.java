package fr.epita.assistants.myide.domain.entity.aspects;

import java.util.Arrays;
import java.util.List;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.features.CleanupFeature;
import fr.epita.assistants.myide.domain.entity.features.DistFeature;
import fr.epita.assistants.myide.domain.entity.features.SearchFeature;

public class AnyAspect implements Aspect {

    @Override
    public Aspect.Type getType() {
        return Mandatory.Aspects.ANY;
    }

    @Override
    public List<Feature> getFeatureList() {
         return Arrays.asList(
            new CleanupFeature(),
            new DistFeature(),
            new SearchFeature()
        );
    }
}