package tuc.isse.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.media.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tuc.isse.schemas.CreateGameRequest;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.entities.GameEntity;

public interface GameCreateApi {

    @Operation(summary = "Create a new game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "409", description = "Public game limit reached",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Failed to generate a unique room code",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<Object> createGame(@RequestBody CreateGameRequest gameRequest);
}