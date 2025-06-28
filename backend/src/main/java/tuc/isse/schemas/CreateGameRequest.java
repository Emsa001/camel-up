package tuc.isse.schemas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request body for creating a game")
public class CreateGameRequest {
    @NotBlank(message = "Game name is required")
    @Schema(description = "Name of the game", example = "Trivia Night")
    private String name;

    @Schema(description = "Whether the game is private", example = "false")
    private Boolean isPrivate;

    @Schema(description = "Host player info")
    private CreatePlayerRequest host;
}