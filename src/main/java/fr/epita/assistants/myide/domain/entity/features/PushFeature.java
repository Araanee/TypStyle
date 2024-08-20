package fr.epita.assistants.myide.domain.entity.features;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class PushFeature implements Feature{

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
                PushCommand push = git.push();
                push.setCredentialsProvider(new UsernamePasswordCredentialsProvider("username", "password"));
                Iterable<PushResult> pushRes = push.call();
                for(var res : pushRes )
                {
                    if(!res.getRemoteUpdates().isEmpty())
                    {
                        return new ExecutionReportImpl(true) ;
                    }
                }
                
                return new ExecutionReportImpl(false) ;
            } 
            catch (IOException e){}  
        } 
        catch (GitAPIException e) {}
        return new ExecutionReportImpl(false);
    }
    

    @Override
    public Type type() {
        return Mandatory.Features.Git.PUSH;
    }
    
}