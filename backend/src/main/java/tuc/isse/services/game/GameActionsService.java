package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.dto.LegBettingCardDTO;
import tuc.isse.dto.DesertTileDTO;
import tuc.isse.dto.PlayerDTO;
import tuc.isse.dto.RaceBettingCardDTO;
import tuc.isse.entities.*;
import tuc.isse.models.ChatModel;
import tuc.isse.repositories.BettingCardRepository;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;
import tuc.isse.services.camel.CamelMovementService;
import tuc.isse.services.player.PlayerBetsService;
import tuc.isse.services.player.PlayerTilesService;
import tuc.isse.utils.Logger;

import java.util.*;


/**
 * Controller for handling game actions such as dice rolls, camel movements, and betting.
 */
@Service
public class GameActionsService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    CamelMovementService camelMovementService;

    @Autowired
    PlayerTilesService playerTilesService;

    @Autowired
    GameRoundService gameRoundService;

    @Autowired
    GameSocketService gameSocketService;

    @Autowired
    PlayerBetsService playerBetsService;

    @Autowired
    BettingCardRepository bettingCardRepository;

    // Public methods for handling game actions

    /**
     * Handles the dice roll action for a player in a specific game room.
     *
     * @param roomCode       The code of the game room.
     * @param requestPlayer  The player making the dice roll request.
     */
    @Transactional
    public void handleDiceRoll(String roomCode, PlayerDTO requestPlayer) {
        try{
            // 1. Verify the action request
            GamePlayerResult result = this.verifyActionRequest(roomCode, requestPlayer.getId());
            GameEntity game = result.game;
            PlayerEntity player = result.player;

            // 2. Get all dices that have not been rolled yet
            List<DiceEntity> dices = game.getDices().stream().filter(d -> d.getValue() == null).toList();

            // 3 Select a random dice from the list of unmoved dieces
            DiceEntity dice = dices.isEmpty() ? null : dices.get((int) (Math.random() * dices.size()));

            // 4. If a die is not found or all dices have been rolled, throw an exception (should not happen)
            if (dice == null)
                throw new RuntimeException("No available dice to roll");

            // 5. Roll the dice
            dice.roll();

            // 5. Move the camel and set its last roll value
            boolean isWinner = camelMovementService.moveCamel(dice.getCamel(), dice.getValue());

            // 6.1. Save the updated game state
            game.setBlockActions(true);
            // 6.2. Add money to the player, TODO: do we need a pyramid tile?
            player.setMoney(player.getMoney() + 1);

            // 7. Send a websocket message with the dice roll result
            Map<String, Object> payload = new HashMap<>();
            payload.put("value", dice.getValue());
            payload.put("color", dice.getCamel().getColor());
            payload.put("camels", game.getCamels());

            gameSocketService.action(game, "dice", payload);
            gameRoundService.endTurn(result.player);

            game.setBlockActions(false);
            if(isWinner){
                gameRoundService.gameEnd(game);
            }else if(dices.size() == 1) {
                gameRoundService.startNewLeg(game);
            }

            gameRepository.save(game);

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                if (dices.size() == 1) {
                    ChatModel chatMessage = new ChatModel();
                    chatMessage.setText("A new leg has started!");
                    chatMessage.setAction(ChatModel.Action.SYSTEM);
                    gameSocketService.chatMessage(game, chatMessage);
                }

                gameSocketService.roomUpdate(game);
                }
            }, 2000);
        }catch(RuntimeException e){
            Logger.error("Error handling dice roll: " + e.getMessage());
        }
    }

    /**
     * Handles the placement of a desert tile by a player in a specific game room.
     *
     * @param roomCode  The code of the game room.
     * @param field     The DesertTileDTO containing the tile information.
     */
    @Transactional
    public void receiveTileUpdate(String roomCode, DesertTileDTO field) {
        // 1. Verify the action request
        GamePlayerResult result = this.verifyActionRequest(roomCode, field.getOwnerId());
        GameEntity game = result.game;
        PlayerEntity player = result.player;

        gameRoundService.endTurn(player);

        Map<String, Object> payload = new HashMap<>();
        payload.put("tiles", playerTilesService.placeDesertTile(field));

        //creating chat message
        ChatModel logMessage = new ChatModel();
        logMessage.setText(field.getType().toString());
        logMessage.setAction(ChatModel.Action.TILE);
        logMessage.setPlayerName(player.getName());

        gameSocketService.chatMessage(game, logMessage);

        gameRepository.save(game);
        gameSocketService.turnUpdate(game);

        gameSocketService.action(game, "tiles", payload);
    }

    /**
     * Handles the betting action for a player in a specific game room.
     *
     * @param roomCode  The code of the game room.
     * @param card      The BettingCardDTO containing the betting card information.
     */
    @Transactional
    public void handlePlayerLegBet(String roomCode, LegBettingCardDTO card) {
        // 1. Verify the action request
        GamePlayerResult result = this.verifyActionRequest(roomCode, card.getPlayerId());
        GameEntity game = result.game;
        PlayerEntity player = result.player;

        LegBettingCard availableCard = playerBetsService.getLegCard(game, card.getCamel());
        if(availableCard == null) {
            throw new RuntimeException("No available betting cards for camel color: " + card.getCamel());
        }

        ChatModel logMessage = new ChatModel();
        logMessage.setText(availableCard.getCamel().getColor().toString());
        logMessage.setAction(ChatModel.Action.BET);
        logMessage.setPlayerName(player.getName());
        gameSocketService.chatMessage(game, logMessage);

        gameRoundService.endTurn(player);

        availableCard.setPlayer(player);

        bettingCardRepository.save(availableCard);
        gameRepository.save(game);

        gameSocketService.roomUpdate(game);
    }

    /**
     * Handles the betting action for a player in a specific game room.
     *
     * @param roomCode  The code of the game room.
     * @param card      The RaceBettingCardDTO containing the betting card information.
     */
    @Transactional
    public void handlePlayerRaceBet(String roomCode, RaceBettingCardDTO card) {
        GamePlayerResult result = this.verifyActionRequest(roomCode, card.getPlayerId());
        GameEntity game = result.game;
        PlayerEntity player = result.player;

        RaceBettingCard availableCard = playerBetsService.getRaceCard(game, card.getCamel());
        if(availableCard == null) {
            throw new RuntimeException("No available betting cards for camel color: " + card.getCamel());
        }

        gameRoundService.endTurn(player);

        availableCard.setPlayer(player);
        availableCard.setBetType(card.getBetType());

        ChatModel logMessage = new ChatModel();
        logMessage.setText(availableCard.getBetType().toString());
        logMessage.setAction(ChatModel.Action.BET);
        logMessage.setPlayerName(player.getName());
        gameSocketService.chatMessage(game, logMessage);

        bettingCardRepository.save(availableCard);
        gameRepository.save(game);
        gameSocketService.roomUpdate(game);
    }


    // helper

    /**
     * Helper class to encapsulate the result of a game and player verification.
     */
    public static class GamePlayerResult {
        public final GameEntity game;
        public final PlayerEntity player;

        public GamePlayerResult(GameEntity game, PlayerEntity player) {
            this.game = game;
            this.player = player;
        }
    }

    /**
     * Verifies the action request for a player in a specific game room.
     *
     * @param roomCode       The code of the game room.
     * @param playerId       The Id of a  player making the request.
     * @return A GamePlayerResult containing the game and player entity.
     * @throws RuntimeException if the game or player is not found, or if the player is not part of the game.
     */
    private GamePlayerResult verifyActionRequest(String roomCode, Long playerId) {
        GameEntity game = gameRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Game not found for room code: " + roomCode));
        PlayerEntity player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found: " + playerId));

        if(game.getState() != GameEntity.GameState.IN_PROGRESS){
            throw new RuntimeException("Game is not in progress");
        }

        if (!game.getPlayers().stream().anyMatch(p -> p.getId().equals(player.getId()))) {
            throw new RuntimeException("Player " + player.getId() + " is not part of the game in room " + roomCode);
        }

        if (!player.getId().equals(game.getCurrentPlayer().getId()) || game.isBlockActions()) {
            throw new RuntimeException("It's not your turn");
        }

        return new GamePlayerResult(game, player);
    }
}
