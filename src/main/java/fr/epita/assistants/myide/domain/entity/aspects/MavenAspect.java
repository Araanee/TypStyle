package fr.epita.assistants.myide.domain.entity.aspects;


import java.util.Arrays;
import java.util.List;

import fr.epita.assistants.myide.domain.entity.Aspect;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.features.CleanFeature;
import fr.epita.assistants.myide.domain.entity.features.CompileFeature;
import fr.epita.assistants.myide.domain.entity.features.ExecFeature;
import fr.epita.assistants.myide.domain.entity.features.InstallFeature;
import fr.epita.assistants.myide.domain.entity.features.PackageFeature;
import fr.epita.assistants.myide.domain.entity.features.TestFeature;
import fr.epita.assistants.myide.domain.entity.features.TreeFeature;

public class MavenAspect implements Aspect {

    @Override
    public Aspect.Type getType() {
        return Mandatory.Aspects.MAVEN;
    }

    @Override
    public List<Feature> getFeatureList() {
        return Arrays.asList(
            new CompileFeature(),
            new CleanFeature(),
            new TestFeature(),
            new PackageFeature(),
            new InstallFeature(),
            new ExecFeature(),
            new TreeFeature()
        );
    }
}
