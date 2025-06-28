package tuc.isse.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.CreatePlayerRequest;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;

public interface GameParticipationApi {
    @Operation(summary = "Joins an existing game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully joined the game",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, e.g. room code not found, game is full, game has already started, or player name already taken",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> joinGame(@PathVariable String roomCode, @RequestBody CreatePlayerRequest player);

    @Operation(summary = "Leaves an existing game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully left the game",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "400", description = "Bad request, e.g. room code not found or player not in game",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> leaveGame(@RequestBody PlayerDTO player);
}
