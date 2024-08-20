package fr.epita.assistants.myide.domain.entity.features;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestFeature implements Feature{
    @Override
    public ExecutionReport execute(Project project, Object... params) {
        Path projectPath = project.getRootNode().getPath();

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "test");
            processBuilder.directory(projectPath.toFile());
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            boolean success = exitCode == 0;
            if (success) {
                System.out.println("Test successful");
            } else {
                System.out.println("Test failed");
                System.out.println(output.toString());
            }

            return new ExecutionReportImpl(success);

        } catch (IOException | InterruptedException e) {
            return new ExecutionReportImpl(false);
        }
    }
    @Override
    public Type type() {
        return Mandatory.Features.Maven.TEST;
    }
}