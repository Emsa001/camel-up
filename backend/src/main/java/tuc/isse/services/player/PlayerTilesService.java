package tuc.isse.services.player;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tuc.isse.dto.DesertTileDTO;
import tuc.isse.entities.DesertTileEntity;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.DesertTileRepository;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;

import java.util.*;

@Service
public class PlayerTilesService {

    @Autowired
    DesertTileRepository desertTileRepository;

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    /**
        * Places a desert tile at specified position.
        * If the player already has a desert tile, it will be replaced.
        *
        * @param tile The DesertTileDTO containing the details of the tile to be placed.
        * @return A list of all desert tiles in the game after placing the new tile.
     */
    @Transactional
    public List<DesertTileEntity> placeDesertTile(DesertTileDTO tile) {
        GameEntity game = gameRepository.findById(tile.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Game not found for id: " + tile.getGameId()));

        PlayerEntity owner = playerRepository.findById(tile.getOwnerId())
                .orElseThrow(() -> new IllegalArgumentException("Player not found for id: " + tile.getOwnerId()));

        if (game.getPlayers().stream().noneMatch(player -> player.getId().equals(owner.getId()))) {
            throw new IllegalArgumentException("Player with id " + owner.getId() + " is not part of the game.");
        }

        // Remove any existing desert tiles for this player
        this.deletePlayerTiles(owner);

        // Create and save the new desert tile
        DesertTileEntity newTile = new DesertTileEntity();
        newTile.setOwner(owner);
        newTile.setPosition(tile.getPosition());
        newTile.setGame(game);
        newTile.setType(tile.getType());

        // ⬅️ Add this line to sync both sides of the relationship
        game.getDesertTiles().add(newTile);

        desertTileRepository.save(newTile);

        return desertTileRepository.findByGame(game);
    }

    /**
     * Deletes all desert tiles associated with the given game.
     * This method breaks the bidirectional relationships between the tiles and their owners,
     * removes the tiles from the game entity, and deletes them from the repository.
     *
     * @param game The game entity whose desert tiles are to be deleted.
     */
    public void deleteDesertTiles(@NotNull GameEntity game) {
        Set<DesertTileEntity> tiles = new HashSet<>(game.getDesertTiles()); // avoid ConcurrentModificationException

        for (DesertTileEntity tile : tiles) {
            // Break bidirectional links
            if (tile.getOwner() != null) {
                tile.getOwner().setDesertTile(null);
                tile.setOwner(null);
            }

            tile.setGame(null);
            game.getDesertTiles().remove(tile);

            desertTileRepository.delete(tile);
        }

        // Flush to sync changes and clear deleted state
        desertTileRepository.flush();
    }

    /**
     * Deletes the desert tile associated with the given player.
     * This method breaks the bidirectional relationship between the player and the desert tile,
     * removes the tile from the game entity, and deletes it from the repository.
     *
     * @param player The player whose desert tile is to be deleted.
     */
    public void deletePlayerTiles(@NotNull PlayerEntity player) {
        DesertTileEntity existing = player.getDesertTile();
        if (existing != null) {
            // Break the bidirectional relationship
            player.setDesertTile(null);
            existing.setOwner(null);

            // Remove from the game entity
            player.getGame().getDesertTiles().remove(existing);
            existing.setGame(null);

            // Delete tile via repository
            desertTileRepository.delete(existing);
            desertTileRepository.flush();
        }
    }

}
