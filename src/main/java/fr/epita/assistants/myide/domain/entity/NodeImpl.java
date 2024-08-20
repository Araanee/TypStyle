package fr.epita.assistants.myide.domain.entity;

import fr.epita.assistants.myide.utils.Logger;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.List;

public class NodeImpl implements Node {

    private Path path;
    //private final URI uri;
    private final Type type;
    private List<Node> children;

    public NodeImpl(@NotNull Path path, Type type) {
        this.path = path;
        this.type = type;
        try{
            this.children = (type == Node.Types.FOLDER) ? populateChild() : new ArrayList<>();
        } catch(RuntimeException e){
            this.children = new ArrayList<>();
        }
    }

    @Override
    public Path getPath() {
        return path;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public @NotNull List<@NotNull Node> getChildren() {
        return children;
    }

    public void addChildren(Node child)
    {
        children.add(child);
    }

    public List<Node> populateChild() {
        List<Node> l = new ArrayList<>();
        try{
            Files.list(path).forEach(c ->{
                Node.Types t;
                if (!Files.isDirectory(c)){
                    t = Node.Types.FILE;
                }
                else{
                    t = Node.Types.FOLDER;
                }
                Node add_n = new NodeImpl(c,t);
                l.add(add_n);
            });
        }
        catch(RuntimeException | IOException e){
            throw new RuntimeException(e);
        }
        return l;
    }
}
