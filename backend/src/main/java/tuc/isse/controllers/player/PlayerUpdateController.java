package tuc.isse.controllers.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tuc.isse.api.PlayerUpdateApi;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.services.player.PlayerUpdateService;

/**
 * This controller handles updates to player information, such as character details.
 * It uses the PlayerUpdateService to process the update logic.
 */
@RestController
public class PlayerUpdateController extends PlayerController implements PlayerUpdateApi {

    @Autowired
    PlayerUpdateService playerUpdateService;

    /**
     * Updates the character information for a player identified by the given ID.
     *
     * @param id The ID of the player whose character is to be updated.
     * @param character The new character information to be set for the player.
     * @return A ResponseEntity indicating the result of the update operation.
     */
    @PostMapping("/{id}/character")
    public ResponseEntity<?> updateCharacter(@PathVariable Long id, @RequestBody PlayerEntity.Character character) {
        return playerUpdateService.updateCharacter(id, character);
    }
}
