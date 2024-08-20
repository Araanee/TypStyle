package fr.epita.assistants.myide.domain.entity.features;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class AddFeature implements Feature {

   private boolean founded = false;

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        founded = false;
        Path projectPath = project.getRootNode().getPath();
        try 
        {

            // Clone the current project
            Git git = Git.open(projectPath.toFile());
            AddCommand add = git.add();

            for (var p : params) 
            {

                if (p instanceof String string) 
                {

                    String filepath = projectPath+File.separator+string;
                    File f = new File(filepath);
                    if (!f.exists()) 
                        return new ExecutionReportImpl(false);

                    add.addFilepattern(string);
                    founded = true;
                }

                 else {
                    return new ExecutionReportImpl(false);
                }

            }
            
            // On a trv au moins un fichier qui match
            if(founded)
            {
                add.call();
                return new ExecutionReportImpl(true);
            }
            else
            {
                return new ExecutionReportImpl(false);
            }
            
    } catch (IOException | GitAPIException e) {
            return new ExecutionReportImpl(false);
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.ADD;
    }
}
