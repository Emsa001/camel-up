package tuc.isse.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.GameDTO;
import tuc.isse.entities.GameEntity;

import java.util.List;

public interface GameListApi {

    @Operation(summary = "List all games")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game List",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
    })
    ResponseEntity<List<GameDTO>> getAllGames();

    @Operation(summary = "List a game by room code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game found",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<?> getGame(@PathVariable String roomCode);

    @Operation(summary = "List players in a game by room code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player list",
                    content = @Content(schema = @Schema(implementation = GameEntity.class))),
            @ApiResponse(responseCode = "404", description = "Game not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    ResponseEntity<?> getPlayers(@PathVariable String roomCode);
}
