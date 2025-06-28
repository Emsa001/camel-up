package tuc.isse.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;

public interface GameLifecycleApi {
    @Operation(summary = "Start a new game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game started successfully",
                     content = @Content(schema = @Schema(implementation = GameEntity.class))),
        @ApiResponse(responseCode = "400", description = "Bad request, e.g. room code not found or player already in game",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Game not found",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> startGame(@RequestBody PlayerDTO player);

    @Operation(summary = "Restart an existing game")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Game restarted successfully",
                     content = @Content(schema = @Schema(implementation = GameEntity.class))),
        @ApiResponse(responseCode = "400", description = "Bad request, e.g. room code not found or player not in game",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Game not found",
                     content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> restartGame(@RequestBody PlayerDTO player);
}
