package tuc.isse.schemas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePlayerRequest {
    @NotBlank(message = "Player name is required")
    @Schema(description = "Name of the player", example = "John Doe")
    private String name;
    @Schema(description = "Age of the player", example = "25")
    private int age;
}