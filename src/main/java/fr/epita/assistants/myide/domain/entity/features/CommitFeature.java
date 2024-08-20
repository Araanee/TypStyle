package fr.epita.assistants.myide.domain.entity.features;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class CommitFeature implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        if (params.length == 0 ) {
            throw new IllegalArgumentException("Commit message must be provided");
        }
        Path projectPath = project.getRootNode().getPath();
        try {
            String commitMessage = (String) params[0];

            Git git = Git.open(projectPath.toFile());

            CommitCommand commit = git.commit();
            commit.setMessage(commitMessage);
            
            commit.call();
           
            return new ExecutionReportImpl(true);
        } catch (IOException | GitAPIException e) {
            return new ExecutionReportImpl(false);
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.COMMIT;
    }
}