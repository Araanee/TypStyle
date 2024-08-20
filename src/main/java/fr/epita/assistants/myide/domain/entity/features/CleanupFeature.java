package fr.epita.assistants.myide.domain.entity.features;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;


public class CleanupFeature implements Feature{

    @Override
     /**
     * Remove all nodes of trash files.
     * Trash files are listed, line by line,
     * in a ".myideignore" file at the root of the project.
     */
    public ExecutionReport execute(Project project, Object... params) {

        //Get the root of the project
        Path root = project.getRootNode().getPath();

        //file when trash files are listed
        String fileName = ".myideignore";

        //Get the file (located at the root)
        Path ignorePath = root.resolve(fileName);
        
        if (!Files.exists(ignorePath)) {
            return new ExecutionReportImpl(false);
        }
        
        //Get all the lines of the ignore file
        try {
            var lines = Files.readAllLines(ignorePath);
            for(String line: lines)
            {
                var toDelete = root.resolve(line.trim());
                if (Files.exists(toDelete))
                {
                    if (Files.isDirectory(toDelete)) 
                    {
                        DirectoryStream<Path> directoryStream = Files.newDirectoryStream(toDelete);
                        for (Path path : directoryStream) {
                            Files.delete(path);
                        }     
                        Files.delete(toDelete);
                    }
                    else {
                        // Delete file
                        Files.delete(toDelete);
                    }
                }   
            }
        } 
        catch (IOException e) {
            return new ExecutionReportImpl(false);
        }
        return new ExecutionReportImpl(true);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Any.CLEANUP;
    }
    
}