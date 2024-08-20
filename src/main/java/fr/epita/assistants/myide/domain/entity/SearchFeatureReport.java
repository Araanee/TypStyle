package fr.epita.assistants.myide.domain.entity;

import java.util.List;

import javax.validation.constraints.NotNull;

/**
 * @param searchResult All file node where the query have been found.
 * @param isSuccess  Is the report successful.
 */
public record SearchFeatureReport(@NotNull List<Node> searchResult, boolean isSuccess) implements Feature.ExecutionReport {
    public List<Node> getResults() {
        return searchResult;
    }

    @Override
    public boolean isSuccess() {
        return isSuccess;
    }
}