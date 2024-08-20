package fr.epita.assistants.myide.domain.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.utils.Given;

@Given
public class ProjectServiceImpl implements ProjectService
{
    private final NodeServiceImpl nodeService;

    public ProjectServiceImpl(NodeServiceImpl nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public Project load(final Path root) {
        if (Files.isDirectory(root)) {
            Node node = new NodeImpl(root, Node.Types.FOLDER);
            ProjectImpl project = new ProjectImpl(node);
            return project;
        }
        throw new RuntimeException("load failed");
    }

    @Override
    public @NotNull Feature.ExecutionReport execute(@NotNull final Project project,
                                                     @NotNull final Feature.Type featureType,
                                                     final Object... params) {
        var featureOptional = project.getFeature(featureType);
        if (!featureOptional.isPresent()) {
            throw new IllegalArgumentException("no feature matched " + featureType);
        }
        else
            return featureOptional.get().execute(project, params);
    }

    @Override
    public NodeService getNodeService() {
        return nodeService;
    }
}
