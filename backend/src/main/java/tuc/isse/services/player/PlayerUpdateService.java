package tuc.isse.services.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import tuc.isse.schemas.ErrorResponse;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.services.game.GameSocketService;


@Service
public class PlayerUpdateService {
    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameSocketService gameSocketService;

    /**
     * Updates the character of a player in a game.
     *
     * @param id        The ID of the player.
     * @param character The new character to set for the player.
     * @return ResponseEntity with the updated player or an error message.
     */
    public ResponseEntity<?> updateCharacter(@PathVariable Long id, @RequestBody PlayerEntity.Character character) {
        // 1. Verify if player exists
        PlayerEntity player = playerRepository.findById(id).orElse(null);
        if (player == null) {
            return ResponseEntity.status(404).body(new ErrorResponse("Player not found"));
        }

        // 2. Check if player is in a WAITING state game
        GameEntity game = gameRepository.findById(player.getGame().getId()).orElse(null);
        if (game == null || game.getState() != GameEntity.GameState.WAITING) {
            return ResponseEntity.status(400).body(new ErrorResponse("Player is not in a waiting game"));
        }

        // 3. Check if character is available
        boolean isTaken = game.getPlayers().stream().anyMatch(p -> character.equals(p.getCharacter()));
        if (isTaken) {
            return ResponseEntity.status(409).body(new ErrorResponse("Character is already taken"));
        }

        // 4. Update character and save
        player.setCharacter(character);
        playerRepository.save(player);

        // 5. Notify game update
        gameSocketService.roomUpdate(game);

        return ResponseEntity.ok(new PlayerDTO(player));
    }
}
