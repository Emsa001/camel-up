package tuc.isse.services.camel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.entities.CamelEntity;
import tuc.isse.entities.GameEntity;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.models.ChatModel;
import tuc.isse.entities.DesertTileEntity;
import tuc.isse.repositories.DesertTileRepository;
import tuc.isse.services.game.GameSocketService;
import tuc.isse.utils.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service
public class CamelMovementService {

    @Autowired
    GameSocketService gameSocketService;

    @Autowired
    DesertTileRepository fieldRepository;

    /**
     * Moves a camel based on the room code, camel color, and dice value.
     *
     * @param camel The camel entity to be moved.
     * @param value The dice value determining the movement.
     * @return A string representing the chat message to be sent, indicating the camel color and the value moved.
     */
    public boolean moveCamel(CamelEntity camel, int value) {
        int rawPosition = camel.getPosition() + value;
        GameEntity game = camel.getGame();
        String chatText = camel.getColor().toString().toUpperCase() + " " + value;

        // 1. Check if the position is desert tile
        List<DesertTileEntity> tiles = fieldRepository.findByGame(game);

        DesertTileEntity.TileType type = null;
        for (DesertTileEntity tile : tiles) {
            if (tile != null && tile.getPosition() == rawPosition) {
                type = tile.getType();
                PlayerEntity owner = tile.getOwner();

                String article = ("aeiou".indexOf(Character.toLowerCase(type.toString().charAt(0))) >= 0) ? "an" : "a";
                if (type == DesertTileEntity.TileType.OASIS) {
                    chatText += " and " + article + " Oasis (+1)";
                    rawPosition++;
                } else if (type == DesertTileEntity.TileType.MIRAGE) {
                    chatText += " and " + article + " Mirage (-1)";
                    rawPosition--;
                }

                // Give money to tile owner
                owner.setMoney(owner.getMoney() + DesertTileEntity.VALUE);
                Logger.info("Camel " + camel.getColor() + " stepped on a " + type + " tile at position " + rawPosition + ". Owner " + owner.getName() + " received " + DesertTileEntity.VALUE + " money.");
                break;
            }
        }

        // Normalize position
        int newPosition = (rawPosition - 1) % 16 + 1;

        // 2. Select the stack to move (camel and anything above it)
        List<CamelEntity> camelsToMove = game.getCamels().stream()
                .filter(c -> c.getPosition() == camel.getPosition() && c.getStackPosition() >= camel.getStackPosition())
                .sorted(Comparator.comparingInt(CamelEntity::getStackPosition))
                .toList();

        // 3. Determine new stack position at destination
        int baseStack;
        if (type == DesertTileEntity.TileType.MIRAGE) {
            // Place camels BELOW any existing ones at newPosition
            int minStack = game.getCamels().stream()
                    .filter(c -> c.getPosition() == newPosition)
                    .mapToInt(CamelEntity::getStackPosition)
                    .min()
                    .orElse(1); // default to 1, meaning bottom of empty

            baseStack = minStack - camelsToMove.size();

            // Ensure no negative stack (shift if needed)
            if (baseStack < 1) {
                int shift = 1 - baseStack;
                baseStack = 1;

                // Shift existing camels up
                game.getCamels().stream()
                        .filter(c -> c.getPosition() == newPosition)
                        .forEach(c -> c.setStackPosition(c.getStackPosition() + shift));
            }

            // Assign camels to new position from bottom up
            for (int i = 0; i < camelsToMove.size(); i++) {
                CamelEntity c = camelsToMove.get(i);
                c.setPosition(newPosition);
                c.setRawPosition(rawPosition);
                c.setStackPosition(baseStack + i);
            }
        } else {
            // Default (normal or Oasis): place on top
            int maxStack = game.getCamels().stream()
                    .filter(c -> c.getPosition() == newPosition)
                    .mapToInt(CamelEntity::getStackPosition)
                    .max()
                    .orElse(0);

            for (int i = 0; i < camelsToMove.size(); i++) {
                CamelEntity c = camelsToMove.get(i);
                c.setPosition(newPosition);
                c.setRawPosition(rawPosition);
                c.setStackPosition(maxStack + 1 + i);
            }
        }

        // 4. Mark the original camel as moved
        camel.setMoved(true);
        camel.setLastRoll(value);
        camel.setRawPosition(rawPosition);

        // 5. Chat log
        ChatModel logMessage = new ChatModel();
        logMessage.setText(chatText);
        logMessage.setAction(ChatModel.Action.MOVE);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                gameSocketService.chatMessage(game, logMessage);
            }
        }, 2000);

        // 6. Return if camel passed the finish line
        return rawPosition > 16;
    }

    public void sortByWinners(List<CamelEntity> camels) {
        camels.sort(Comparator
                .comparingInt(CamelEntity::getRawPosition).reversed()
                .thenComparing(Comparator.comparingInt(CamelEntity::getStackPosition).reversed())
        );
    }
}
