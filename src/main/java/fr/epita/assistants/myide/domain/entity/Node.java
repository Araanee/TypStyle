package fr.epita.assistants.myide.domain.entity;

import java.util.List;
import java.nio.file.Path;
import javax.validation.constraints.NotNull;

import fr.epita.assistants.myide.utils.Given;

@Given()
public interface Node {

    /**
     * @return The Node path.
     */
    @NotNull Path getPath();

    /**
     * @return The Node type.
     */
    @NotNull Type getType();

    /**
     * If the Node is a Folder, returns a list of its children,
     * else returns an empty list.
     *
     * @return List of node
     */
    @NotNull List<@NotNull Node> getChildren();

    default boolean isFile() {
        return getType().equals(Types.FILE);
    }

    default boolean isFolder() {
        return getType().equals(Types.FOLDER);
    }

    enum Types implements Type {
        FILE,
        FOLDER,
        IMAGE,
    }

    interface Type {

    }
}
