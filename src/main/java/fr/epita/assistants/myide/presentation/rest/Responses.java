package fr.epita.assistants.myide.presentation.rest;

import java.io.File;
import java.nio.file.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.Response;

import fr.epita.assistants.myide.utils.Logger;
import io.vertx.core.json.JsonObject;
import fr.epita.assistants.myide.presentation.rest.FileRequest.OpenFileRequest;
import fr.epita.assistants.myide.presentation.rest.FileRequest.CreateFileRequest;
import fr.epita.assistants.myide.presentation.rest.FileRequest.DeleteFileRequest;

import fr.epita.assistants.myide.presentation.rest.FolderRequest.OpenProjectRequest;
import fr.epita.assistants.myide.presentation.rest.FolderRequest.CreateFolderRequest;
import fr.epita.assistants.myide.presentation.rest.FolderRequest.DeleteFolderRequest;

import fr.epita.assistants.myide.presentation.rest.Feature.ExecFeatureRequest;
import fr.epita.assistants.myide.presentation.rest.Move.MoveRequest;
import fr.epita.assistants.myide.presentation.rest.Update.UpdateRequest;

import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.myide.domain.service.ProjectServiceImpl;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.NodeServiceImpl;

import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.ProjectImpl;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.NodeImpl;

import java.io.IOException;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.presentation.ExecResponse.ExecResponse;
import fr.epita.assistants.myide.utils.FeatureIdentifier;


public class Responses 
{
    private static Node proj = null;

    public static Response openFileResponse(OpenFileRequest req) {
        if (req.getPath() == null || req.getPath().isEmpty())
        {
            Logger.logError("openfile empty " + req.getPath());
            return Response.status(400).build();
        }

        Path file = Paths.get(req.getPath());
        if (!Files.exists(file) || Files.isDirectory(file)) {
            Logger.logError("cant open " + req.getPath());
            return Response.status(400).build();
        }
        else {
            var content = "";
            try {
                content = new String(Files.readAllBytes(file));
            } catch (IOException e) {
                Logger.logError("cant open " + req.getPath());
                return Response.status(400).build();
            }
            return Response.ok(content).build();
        }
    }

    public static Response OpenProjectResponse(OpenProjectRequest req) {
        if (req == null || req.getPath() == null || req.getPath().isEmpty()) {
            assert req != null;
            Logger.logError("OpenProjectResponse : " + req.getPath());
            return Response.status(400).build();
        }

        Path path = Paths.get(req.getPath());

        if (!Files.exists(path) || !Files.isDirectory(path))
        {
            Logger.logError("OpenProjectResponse failed path is file or already exists : " + req.getPath());
            return Response.status(400).build();
        }

        proj = new NodeImpl(path, Node.Types.FOLDER);

        NodeServiceImpl nodeService = new NodeServiceImpl(proj);
        ProjectServiceImpl projectService = new ProjectServiceImpl(nodeService);

        try {
            projectService.load(path);
            return Response.ok("proj opened").build();
        } catch (RuntimeException e) {
            Logger.logError("OpenProjectResponse failed : " + req.getPath() + " " + e);
            return Response.status(400).build();
        }

    }

    public static Response CreateFileResponse(CreateFileRequest req) {
        // marche sauf pour les absolute path

        if (req == null || req.getPath() == null || req.getPath().isEmpty()) {
            assert req != null;
            Logger.logError("CreateFileResponse : " + req.getPath());
            return Response.status(400).build();
        }

        Path path = Paths.get(req.getPath());
        String name = path.getFileName().toString();
        Path parent = path.getParent();
        Node n = new NodeImpl(path, Node.Types.FILE);
        NodeServiceImpl nodeService = new NodeServiceImpl(n);
        var folder = new NodeImpl(parent, Node.Types.FOLDER);

        try {
            nodeService.create(folder, name, Node.Types.FILE);
            return Response.ok(200).build();

        } catch (RuntimeException e) {
            Logger.logError(" create file failed : " + req.getPath() + " " + e.getMessage());
            return Response.status(400).build();
        }
    }

    public static Response CreateFolderResponse(CreateFolderRequest req) {

        if (req == null || req.getPath() == null || req.getPath().isEmpty()) {
            assert req != null;
            Logger.logError("CreateFileResponse : " + req.getPath());
            return Response.status(400).build();
        }

        Path path = Paths.get(req.getPath());
        String name = path.getFileName().toString();
        Path parent = path.getParent();
        Node n = new NodeImpl(path, Node.Types.FILE);
        NodeServiceImpl nodeService = new NodeServiceImpl(n);
        var folder = new NodeImpl(parent, Node.Types.FOLDER);

        try {
            nodeService.create(folder, name, Node.Types.FOLDER);
            return Response.ok(200).build();

        } catch (RuntimeException e) {
            Logger.logError(" create folder failed : " + req.getPath() + " " + e.getMessage());
            return Response.status(400).build();
        }
    }

    public static Response DeleteFileResponse(DeleteFileRequest req) {
        // check for empty or null path
        if (req.getPath() == null || req.getPath().isEmpty())
        {
            Logger.logError("DeleteFileResp : " + req.getPath());
            return Response.status(400).build();
        }
        Path p = Paths.get(req.getPath());
        NodeServiceImpl nodeserv = new NodeServiceImpl(proj);
        NodeImpl nodeimpl = new NodeImpl(p,Node.Types.FILE);


        try{
            var deleted = nodeserv.delete(nodeimpl);
            if (!deleted){
                Logger.logError("DeleteFileResp : " + req.getPath());
                return Response.status(400).build();
            }
            return Response.ok("Deleted File ! (Success)").status(200).build();

        } catch(RuntimeException e){

            Logger.logError("DeleteFileResp : " + req.getPath() + "e message :" + e);
            return Response.status(400).build();
        }
    }

    public static Response DeleteFolderResponse(DeleteFolderRequest req) {
        // check for empty or null path
        if (req.getPath() == null || req.getPath().isEmpty())
        {
            Logger.logError("DeleteFolderResp : " + req.getPath());
            return Response.status(400).build();
        }
        Path p = Paths.get(req.getPath());
        NodeServiceImpl nodeserv = new NodeServiceImpl(proj);
        NodeImpl nodeimpl = new NodeImpl(p,Node.Types.FOLDER);


        try{
        var deleted = nodeserv.delete(nodeimpl);
        if (!deleted){
            Logger.logError("DeleteFolderResp : " + req.getPath());
            return Response.status(400).build();
        }
        return Response.ok("Deleted folder ! (Success)").status(200).build();

        } catch(RuntimeException e){

            Logger.logError("DeleteFolderResp : " + req.getPath() + "e message :" + e);
            return Response.status(400).build();
        }
    }

    public static Response ExecFeatureResponse(ExecFeatureRequest req) {

        if (proj == null) {
            Logger.logError("ExecFeatureResponse : no project opened + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(400).build();
        }

        if (req.getFeature() == null || req.getFeature().isEmpty() || req.getProject() == null || req.getProject().isEmpty())
        {
            Logger.logError("ExecFeatureResponse : check strings + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(400).build();
        }

        Path path = Paths.get(req.getProject());
        if (!Files.exists(path) || !Files.isDirectory(path))
        {
            Logger.logError("ExecFeatureResponse : check project + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(400).build();
        }

        String featureToExec = req.getFeature();
        Feature.Type feature = FeatureIdentifier.identifyFeature(featureToExec);
        if (feature == null) {
            Logger.logError("ExecFeatureResponse : feature invalid + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(400).build();
        }

        NodeServiceImpl nodeService = new NodeServiceImpl(proj);
        ProjectServiceImpl projectService = new ProjectServiceImpl(nodeService);

        try {
            Project project = projectService.load(path);
            var res = projectService.execute(project, feature, req.getParams());

            if (res.isSuccess())
                return Response.ok().build();

            Logger.logError("ExecFeatureResponse : feature invalid + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(500).entity("Failed to execute feature : " + feature + " !").build();
        } catch (RuntimeException e) {
            Logger.logError("ExecFeatureResponse : feature invalid + feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
            return Response.status(500).entity("Failed to execute feature : " + feature + " !").build();
        }
    }

    public static Response MoveResponse(MoveRequest req) 
    {
        if (req.src == null || req.src.isEmpty() || req.dst == null || req.dst.isEmpty())
        {
            Logger.logError("MoveResponse : " + req.src + " / " + req.dst);
            return Response.status(400).build();
        }

        Path src = Paths.get(req.getSrc());
        Path dst = Paths.get(req.getDst());

        Node srcn = new NodeImpl(src, Node.Types.FILE);
        Node dstn = new NodeImpl(dst, Node.Types.FOLDER);

        NodeServiceImpl nodeService = new NodeServiceImpl(proj);

        try {
            nodeService.move(srcn, dstn);
            return Response.ok("moved").build();
        } catch (RuntimeException e) {
            Logger.logError("MoveResponse : " + req.src + " / " + req.dst);
            return Response.status(400).build();
        }
    }
/*
    public static void moveDirectory(Path src, Path dst) throws IOException 
    {
        if (Files.notExists(dst)) 
        
            Files.createDirectories(dst);   //createFolder si destination n'exitse pas 


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(src)) {
            for (Path entry : stream) {
                Path destPath = dst.resolve(src.relativize(entry)); //pour avoir absolute path"
                
                if (Files.isDirectory(entry)) 
                {
                    moveDirectory(entry, destPath);
                } else {
                    Files.move(entry, destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        // Supprimez le dossier source après avoir déplacé son contenu
        File todelete = new File(src.toString());
        //deleteDirectory(todelete);
    }
*/
    public static Response UpdateResponse(UpdateRequest req)
    {

         if (req.getPath() == null || req.getPath().isEmpty()) {
             Logger.logError("update failed" + req.path + " " + req.from + " " + req.to + " " + req.content);
             return Response.ok("Empty or null path").status(400).build();
         }

        if (req.content == null || req.content.isEmpty()) {
            Logger.logError("update failed" + req.path + " " + req.from + " " + req.to + " " + req.content);
            return Response.status(400).build();
        }

        if (req.from < 0 || req.from > req.to) {
            Logger.logError("update failed" + req.path + " " + req.from + " " + req.to + " " + req.content);
            return Response.status(400).build();
        }

        Path path = Paths.get(req.getPath());

        if (!Files.exists(path)) {
            Logger.logError("update failed" + req.path + " " + req.from + " " + req.to + " " + req.content);
            return Response.status(400).build();
        }

        try {
            Node n = new NodeImpl(path, Node.Types.FILE);
            NodeServiceImpl nodeService = new NodeServiceImpl(n);
            nodeService.update(n, req.from, req.to, req.content.getBytes());
            return Response.ok("File updated successfully!").build();
        } catch (RuntimeException e) {
            Logger.logError("update failed" + req.path + " " + req.from + " " + req.to + " " + req.content);
            return Response.status(400).build();
        }
    }
}