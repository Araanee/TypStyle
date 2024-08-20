package fr.epita.assistants.myide.domain.entity.features;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CompileFeature implements Feature{

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        Path projectPath = project.getRootNode().getPath();

        try {
            // Create the process builder for the Maven command
            ProcessBuilder processBuilder = new ProcessBuilder("mvn", "compile");
            processBuilder.directory(projectPath.toFile());
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();

            // Capture the output
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            } 

            // Wait for the process to complete
            int exitCode = process.waitFor();

            // Check if the compilation was successful
            boolean success = exitCode == 0;
            if (success) {
                System.out.println("Compilation successful");
            } else {
                System.out.println("Compilation failed");
                System.out.println(output.toString());
            }

            return new ExecutionReportImpl(success);

        } catch (IOException | InterruptedException e) {
            return new ExecutionReportImpl(false);
        }
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.COMPILE;
    }
    
}