package fr.epita.assistants.myide.domain.entity.features;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class PullFeature implements Feature{
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        Path projectPath = project.getRootNode().getPath();
        try 
        {
            //on clone le projet actuel
            Git git;
            try 
            {
                git = Git.open(projectPath.toFile());
                PullCommand pull = git.pull();
                pull.setCredentialsProvider(new UsernamePasswordCredentialsProvider("username", "password"));
                pull.call();
                return new ExecutionReportImpl(true) ;
            } 
            catch (IOException e){}  
        } 
        catch (GitAPIException e) {}
        return new ExecutionReportImpl(false);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PULL;
    }
    
}