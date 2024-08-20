package fr.epita.assistants.myide.domain.entity.features;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Feature.ExecutionReport;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;

public class DistFeature implements Feature{
    @Override
    /**
     * Remove all trash files and create a zip archive.
     * Archive name must be the same as the project name (root node name).
     */
    public ExecutionReport execute(Project project, Object... params) {
        try {

            // Remove all trash files using the cleanup feature
            project.getFeature(Mandatory.Features.Any.CLEANUP).get().execute(project);

            // Get the root of the project
            Path projectPath = project.getRootNode().getPath();

            // We get the file name of the project
            String projectName = projectPath.getFileName().toString();

            // We create a new zip filename with the same name as the project name 
            Path zipPath = projectPath.resolveSibling(projectName + ".zip");
            try (
                //Create ZipOutputStream zos by passing FileOutputStream fos which contains a path to output file.
                FileOutputStream fos = new FileOutputStream(zipPath.toFile()); 
                ZipOutputStream zos = new ZipOutputStream(fos)) 
            {
                // Putting all the files into a string list
                List<Path> filesToBeZipped = Files.walk(projectPath)
                        .filter(Files::isRegularFile)
                        .toList();

                for (Path file : filesToBeZipped) {

                    File fileToBeZipped = file.toFile();
                    try (FileInputStream fis = new FileInputStream(fileToBeZipped)) {
                        ZipEntry zipEntry = new ZipEntry(projectPath.relativize(file).toString());
                        zos.putNextEntry(zipEntry);

                        byte[] bytes = new byte[1024];
                       
                        while ((fis.read(bytes)) >= 0) {
                            zos.write(bytes, 0, bytes.length);
                        }

                        zos.closeEntry();
                        fis.close();
                    }
                }
                return new ExecutionReportImpl(true);
            }

        } catch (IOException e) {
            return new ExecutionReportImpl(false);
        }
    }
    

    @Override
    public Type type() {
        return Mandatory.Features.Any.DIST;
    }
    
}

