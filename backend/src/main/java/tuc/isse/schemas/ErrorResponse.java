package tuc.isse.schemas;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Standard error response")
public class ErrorResponse {
    @Schema(description = "Error message", example = "Something went wrong")
    private String error;
}