package fr.epita.assistants.myide.domain.entity.features;

import fr.epita.assistants.myide.domain.entity.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class SearchFeature implements Feature{

    @Override
       /**
             * Fulltext search over project files.
             */
    public ExecutionReport execute(Project project, Object... params) {

        if (params.length == 0 ) {
            throw new IllegalArgumentException("Search text must be provided");
        }
        return new SearchFeatureReport(new ArrayList<>(), false);

    /*
        // Get the text to search
        String SearchText = params[0].toString();

        // Get the root path of the project
        Path root = project.getRootNode().getPath();
        
        List<Node> searchResults = new ArrayList<>();
        try {
            // Foreach verify if it contains the line
            Files.walk(root)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {

                        // Read all the lines and put them in a list
                        List<String> lines = Files.readAllLines(file);

                        for (int i = 0; i < lines.size(); i++) {
                            if (lines.get(i).contains(SearchText)) {
                                try {
                                    searchResults.add(new SearchNode(file.toString(), i + 1, lines.get(i)));
                                } catch (URISyntaxException e) {
                                    // TODO Auto-generated catch block
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            return new SearchFeatureReport(searchResults, true);
        } catch (IOException e) {
            return new SearchFeatureReport(searchResults, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }*/
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.SEARCH;
    }
    
}