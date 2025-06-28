package tuc.isse.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.entities.PlayerEntity;

public interface PlayerUpdateApi {

    @Operation(summary = "Update player character")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character updated successfully",
                    content = @Content(schema = @Schema(implementation = PlayerEntity.class))),
            @ApiResponse(responseCode = "400", description = "Player not in waiting game",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Player not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Character already taken",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<?> updateCharacter(@PathVariable Long id, @RequestBody PlayerEntity.Character character);
}
