package fr.epita.assistants.myide.domain.entity;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

import fr.epita.assistants.myide.utils.Given;

@Given()
public interface Aspect {

    /**
     * @return The type of the Aspect.
     */
    Aspect.Type getType();

    /**
     * @return The list of features associated with the Aspect.
     */
    default @NotNull List<Feature> getFeatureList() {
        return Collections.emptyList();
    }

    interface Type {
    }
}
