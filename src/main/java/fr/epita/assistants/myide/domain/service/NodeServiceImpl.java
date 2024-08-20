package fr.epita.assistants.myide.domain.service;

import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeImpl;
import fr.epita.assistants.myide.utils.Given;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;

import java.nio.file.Paths;
import java.util.List;


public class NodeServiceImpl implements NodeService {
    private final Node node;
    public NodeServiceImpl(Node n)
    {
        this.node = n;
    }

    /**
     * Update the content in the range [from, to[.
     * The content must be inserted in any case.
     * i.e. : "hello world" -> update(0, 0, "inserted ") -> "inserted hello world"
     *      : "hello world" -> update(0, 5, "inserted ") -> "inserted world"
     *
     * @param node            Node to update (must be a file).
     * @param from            Beginning index of the text to update.
     * @param to              Last index of the text to update (Not included).
     * @param insertedContent Content to insert.
     * @return The node that has been updated.
     * @throws Exception upon update failure.
     */
    @Override
    public Node update(final Node node, final int from, final int to, final byte[] insertedContent) {
        if (node.getType() != Node.Types.FILE) {
            throw new IllegalArgumentException("Node must be a file");
        }

        Path file = node.getPath();
        try {
            byte[] fileContent = Files.readAllBytes(file);
            String s = new String(fileContent, StandardCharsets.UTF_8);
            String newstring = new String(insertedContent, StandardCharsets.UTF_8);
            String res = s.substring(0, from) + newstring + s.substring(to);
            Files.writeString(file, res);
            return node;
        } catch (IOException e) {
            throw  new RuntimeException(e);
        }
    }


    /**
     * Delete the node given as parameter.
     *
     * @param node Node to remove.
     * @return True if the node has been deleted, false otherwise.
     */
    @Override
    public boolean delete(Node node) 
    {
        if (node == null){
            return false;
        }
        File file = node.getPath().toFile();
        if (file.exists())
        {
            List<Node> list_children = node.getChildren();
            if (!list_children.isEmpty()){
                for (var i : list_children){
                    delete(i);
                }
            }
            return file.delete();
        }
        return false;
    }

    /*private boolean deleteRecursively(Path path) throws IOException
    {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> entries = Files.newDirectoryStream(path))
             {
                for (Path entry : entries) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
        return true;
    }*/
    

    @Override
    public Node create(Node folder, String name, Node.Type type)
    {
        if (folder.getType() != Node.Types.FOLDER) {
            throw new IllegalArgumentException("Parent node must be a folder");
        }

        Path newPath = folder.getPath();
        Path file = newPath.resolve(name);
        try {
            Node n;
            if (type == Node.Types.FOLDER) {
                if (!Files.exists(file)){
                    Files.createDirectories(file);
                    n = new NodeImpl(file, Node.Types.FOLDER);
                }
                else{
                    if (Files.isDirectory(file)){
                        throw new RuntimeException(" error directory");
                    }
                    else{
                        Files.createDirectories(file);
                        n = new NodeImpl(file, Node.Types.FOLDER);
                    }
                }
            }
            else 
            {
                if (!Files.exists(file)){
                    Files.createFile(file);
                    n = new NodeImpl(file, Node.Types.FILE);
                }
                else
                {
                    throw new RuntimeException(" error file");
                }
            }
            folder.getChildren().add(n);
            return n;
        } catch (IOException e) {
             throw new RuntimeException("Error create");
        }
    }

    private Node removeMoved(Node toSearch, Node start) {
        if (start.getChildren().contains(toSearch))
            return start;

        for (var c : start.getChildren()) {
            if (c.getType() == Node.Types.FOLDER) {
                var p = removeMoved(c, toSearch);
                if (p != null)
                    return p;
            }
        }
        return null;
    }

    @Override
    public Node move(Node nodeToMove, final Node destinationFolder) {

        if (destinationFolder.getType() != Node.Types.FOLDER) {
            throw new IllegalArgumentException("Destination must be a folder");
        }

        Path destinationPath = destinationFolder.getPath().resolve(nodeToMove.getPath().getFileName());
        try {
            Files.move(nodeToMove.getPath(), destinationPath);
            Node n = new NodeImpl(destinationPath, nodeToMove.getType());
            destinationFolder.getChildren().add(n);

            if (this.node != null)
            {
                var p = removeMoved(nodeToMove, this.node);
                if (p != null) {
                    p.getChildren().remove(nodeToMove);
                }
            }
            return n;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /* 

    private byte[] concatenateContent(byte[] fileContent, int from, int to, byte[] insertedContent) {
        byte[] before = Arrays.copyOfRange(fileContent, 0, from);
        byte[] after = Arrays.copyOfRange(fileContent, to, fileContent.length);
        byte[] updatedContent = new byte[before.length + insertedContent.length + after.length];

        System.arraycopy(before, 0, updatedContent, 0, before.length);
        System.arraycopy(insertedContent, 0, updatedContent, before.length, insertedContent.length);
        System.arraycopy(after, 0, updatedContent, before.length + insertedContent.length, after.length);

        return updatedContent;
    }*/

    /*public Node searchNode(NodeImpl parent, Path path) {
        if (parent.path.equals(path)) {
            return parent;
        }

        if (parent.getType() == Node.Types.FOLDER) {
            for (Node child : parent.getChildren()) {
                Node result = searchNode((NodeImpl) child, path);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }*/
}