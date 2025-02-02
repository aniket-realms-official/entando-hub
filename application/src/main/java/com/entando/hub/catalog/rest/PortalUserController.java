package com.entando.hub.catalog.rest;

import com.entando.hub.catalog.rest.KeycloakUserController.RestUserRepresentation;
import com.entando.hub.catalog.rest.model.UserOrganisationRequest;
import com.entando.hub.catalog.service.PortalUserService;
import com.entando.hub.catalog.service.model.UserRepresentation;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.entando.hub.catalog.config.AuthoritiesConstants.*;

@RestController
@RequestMapping("/api/users")
public class PortalUserController {

    private final PortalUserService portalUserService;

    public PortalUserController(PortalUserService portalUserService) {
        this.portalUserService = portalUserService;
    }
    private final Logger logger = LoggerFactory.getLogger(PortalUserController.class);


    @Operation(summary = "Get all the portal users", description = "Protected api, only eh-admin, eh-author or eh-manager can access it. You can provide the organisationId to filter the results")
    @RolesAllowed({ADMIN, AUTHOR, MANAGER})
    @CrossOrigin
    @GetMapping("/")
    public ResponseEntity<List<RestUserRepresentation>> getUsers(@RequestParam(required = false) String organisationId) {
        logger.debug("REST request to get users by organisation id: {}", organisationId);
        List<UserRepresentation> users;
        users = portalUserService.getUsersByOrganisation(organisationId);
        if (null == users) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users.stream().map(RestUserRepresentation::new).collect(Collectors.toList()), HttpStatus.OK);
    }

    @Operation(summary = "Add a Keycloak user to an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId")
    @RolesAllowed({ADMIN})
    @CrossOrigin
    @PostMapping("/{organisationId}")
    public ResponseEntity<Map<String, Boolean>> addUserToOrganisation(@PathVariable String organisationId, @RequestBody UserOrganisationRequest request) {
        logger.debug("REST request to add user to organisation id: {}", organisationId);

/*
        if (!organisationId.equals(request.getOrganisationId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OrganisationId is not valid");
        }
*/
        boolean result = this.portalUserService.addUserToOrganization(request.getUsername(), organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Remove a Keycloak user from an organisation", description = "Protected api, only eh-admin can access it. You have to provide the organisationId and the username")
    @RolesAllowed({ADMIN})
    @CrossOrigin
    @DeleteMapping("/{organisationId}/user/{username}")
    public ResponseEntity<Map<String, Boolean>> deleteUserFromOrganisation(@PathVariable String organisationId, @PathVariable String username) {
        logger.debug("REST request to delete user from organisation id: {}", organisationId);
        boolean result = this.portalUserService.removeUserFromOrganization(username, organisationId);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

    @Operation(summary = "Delete a Portal User", description = "Protected api, only eh-admin can access it. You have to provide the username")
    @RolesAllowed({ADMIN})
    @CrossOrigin
    @DeleteMapping("/{username}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(@PathVariable String username) {
        logger.debug("REST request to delete user: {}", username);
        boolean result = this.portalUserService.removeUser(username);
        Map<String, Boolean> mapResult = new HashMap<>();
        mapResult.put("result", result);
        return new ResponseEntity<>(mapResult, HttpStatus.OK);
    }

}
