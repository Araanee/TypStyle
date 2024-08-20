package fr.epita.assistants.myide.domain.entity;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.nio.file.attribute.BasicFileAttributes;

import java.io.File;

import java.nio.file.*;
import javax.validation.constraints.NotNull;


import fr.epita.assistants.myide.domain.entity.aspects.AnyAspect;
import fr.epita.assistants.myide.domain.entity.aspects.GitAspect;
import fr.epita.assistants.myide.domain.entity.aspects.MavenAspect;
import fr.epita.assistants.myide.utils.Logger;

public class ProjectImpl implements Project 
{
    private Node root_;
    private Set<Aspect> aspects_;

    public ProjectImpl(Node rootNode) {
        this.root_ = rootNode;
        aspects_ = findAspect();
    }

    private Set<Aspect> findAspect() {
        Set<Aspect> aspects = new HashSet<>();

        aspects.add(new AnyAspect());

        Path path = root_.getPath();

        // pom.xml?
        if (Files.exists(path.resolve("pom.xml"))) {
            aspects.add(new MavenAspect());
        }

        // .git?
        if (Files.exists(path.resolve(".git")) && Files.isDirectory(path.resolve(".git"))) {
            aspects.add(new GitAspect());
        }

        return aspects;
    }

    private List<Node> InitArchitecture(Path root) {
        /*List<Node> children = new ArrayList<>();
        
        try {
            Files.list(root).forEach(path -> {
                try {
                    BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
                    Node.Types type = attrs.isDirectory() ? Node.Types.FOLDER : Node.Types.FILE;
                    List<Node> childNodes = attrs.isDirectory() ? InitArchitecture(path) : new ArrayList<>();
                    children.add(new NodeImpl(path, type, childNodes));
                } catch (IOException e) {
                    Logger.log("Failed to read attributes for path: " + path.toString());
                }
            });
        } catch (IOException e) {
            Logger.log("Failed to list children for root path: " + root.toString());
        }
        
        return children;*/
        return new ArrayList<>();
    }

   
    @Override
    public Node getRootNode() {
        return root_;
    }

    @Override
    public Set<Aspect> getAspects() {
        return aspects_;
    }

    @Override
    public Optional<Feature> getFeature(@NotNull final Feature.Type featureType) {
        return getAspects().stream()
            .flatMap(aspect -> aspect.getFeatureList().stream())
            .filter(feature -> feature.type().equals(featureType))
            .findFirst();
    }
}
