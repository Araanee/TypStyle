package fr.epita.assistants.myide.domain.entity.aspects;

import java.util.Arrays;
import java.util.List;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.features.AddFeature;
import fr.epita.assistants.myide.domain.entity.features.CommitFeature;
import fr.epita.assistants.myide.domain.entity.features.PullFeature;
import fr.epita.assistants.myide.domain.entity.features.PushFeature;

public class GitAspect implements Aspect {

    @Override
    public Aspect.Type getType() {
        return Mandatory.Aspects.GIT;
    }

    @Override
    public List<Feature> getFeatureList() {
         return Arrays.asList(
            new AddFeature(),
            new CommitFeature(),
            new PullFeature(),
            new PushFeature()
        );
    }
}