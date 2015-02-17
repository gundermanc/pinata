package com.pinata.service.api;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.pinata.service.datatier.SQLConnection;

import com.pinata.shared.ApiException;
import com.pinata.shared.InstallApiRequest;

/**
 * Application Database Install Resource.
 * @author Christian Gunderman
 */
@Path("install")
public class InstallResource {

    /**
     * Run POST request against /api/v1/install with root_user and
     * root_pass set to the root login to wipe, reinstall, and configure
     * database. install_db.sh script does this.
     * @param jsonBody The json POST body containing the MySQL login.
     * @return A plain text response telling what the result was, or
     * a JSON error message if shit hits the fan. This is an unofficial
     * API, so no big deal.
     */
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(String jsonBody) throws ApiException {
        InstallApiRequest request = new InstallApiRequest();
        request.deserializeFrom(jsonBody);

        SQLConnection sql = SQLConnection.connect(request.rootUser,
                                                  request.rootPass);

        try {
            SQLConnection.install(sql);
        
            return Response.ok("Installation successful.").build();
        } catch (Exception ex) {
            return Response.ok("Error installing: \n " +
                               ex.getCause().getMessage()).build();
        } finally {
            sql.close();
        }
    }
}
