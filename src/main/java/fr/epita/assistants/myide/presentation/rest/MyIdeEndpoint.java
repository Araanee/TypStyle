package fr.epita.assistants.myide.presentation.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import netscape.javascript.JSObject;
import fr.epita.assistants.myide.utils.Logger;

import fr.epita.assistants.myide.presentation.rest.FileRequest.OpenFileRequest;
import fr.epita.assistants.myide.presentation.rest.FileRequest.CreateFileRequest;
import fr.epita.assistants.myide.presentation.rest.FileRequest.DeleteFileRequest;

import fr.epita.assistants.myide.presentation.rest.FolderRequest.OpenProjectRequest;
import fr.epita.assistants.myide.presentation.rest.FolderRequest.CreateFolderRequest;
import fr.epita.assistants.myide.presentation.rest.FolderRequest.DeleteFolderRequest;

import fr.epita.assistants.myide.presentation.rest.Feature.ExecFeatureRequest;
import fr.epita.assistants.myide.presentation.rest.Move.MoveRequest;
import fr.epita.assistants.myide.presentation.rest.Update.UpdateRequest;

import fr.epita.assistants.myide.presentation.rest.Responses;


@Path("/api")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MyIdeEndpoint {

    @GET @Path("/hello")
    public Response helloWorld()
    {
        Logger.log("Saying hello !");
        return Response.ok("Hello World !").build();
    }

    @POST @Path("/open/file")
    public Response openFile(OpenFileRequest req)
    {
        Logger.log("Opening file " + req.getPath());
        return Responses.openFileResponse(req);
    }

    @POST @Path("/open/project")
    public Response openProject(OpenProjectRequest req)
    {
        Logger.log("Opening project " + req.getPath());
        return Responses.OpenProjectResponse(req);
    }

    @POST @Path("/create/file")
    public Response createFile(CreateFileRequest req)
    {
        Logger.log("Creating file " + req.getPath());
        return Responses.CreateFileResponse(req);
    }

    @POST @Path("/create/folder")
    public Response createFolder(CreateFolderRequest req)
    {
        Logger.log("Creating folder " + req.getPath());
        return Responses.CreateFolderResponse(req);
    }

    @POST @Path("/delete/file")
    public Response deleteFile(DeleteFileRequest req)
    {
        Logger.log("Deleting file " + req.getPath());
        return Responses.DeleteFileResponse(req);
    }

    @POST @Path("/delete/folder")
    public Response deleteFolder(DeleteFolderRequest req)
    {
        Logger.log("Deleting folder " + req.getPath());
        return Responses.DeleteFolderResponse(req);
    }

    //--------------------EXECFEATURE--------------------------

    @POST @Path("/execFeature")
    public Response execFeature(ExecFeatureRequest req)
    {
        Logger.log("Executing  feature " + req.getFeature() + " with params " + req.getParams() + " on " + req.getProject());
        return Responses.ExecFeatureResponse(req);
    }

    //----------------------MOVE--------------------------------
    @POST @Path("/move")
    public Response move(MoveRequest req)
    {

        Logger.log("Moving " + req.getSrc() + " to " + req.getDst());
        return Responses.MoveResponse(req);
    }

    //----------------------UPDATE------------------------------
    @POST @Path("/update")
    public Response update(UpdateRequest req)
    {

        Logger.log("Updating " + req.getPath() + " " + req.getFrom() + " " + req.getTo() + " " + req.getContent());
        return Responses.UpdateResponse(req);
    }

}