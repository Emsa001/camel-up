package tuc.isse.services.game;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.entities.*;
import tuc.isse.repositories.GameRepository;
import tuc.isse.services.camel.CamelMovementService;
import tuc.isse.services.player.PlayerBetsService;
import tuc.isse.services.player.PlayerTilesService;
import tuc.isse.utils.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing game rounds.
 * This service handles the logic for starting, ending, and managing events during a game round.
 * It will automatically set turn to the next player, and recognize the end of the leg.
 */
@Service
public class GameRoundService {

    @Autowired
    GameRepository gameRepository;

    @Autowired
    GameSocketService gameSocketService;

    @Autowired
    GameInitService gameInitService;

    @Autowired
    PlayerTilesService playerTilesService;

    @Autowired
    CamelMovementService camelMovementService;

    @Autowired
    PlayerBetsService playerBetsService;

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Transactional
    public void gameEnd(GameEntity game) {
        playerBetsService.raceBettingEval(game);

        game.setState(GameEntity.GameState.FINISHED);
        gameRepository.save(game);
       gameSocketService.lobbyUpdate(game);

        scheduler.schedule(() -> {
            if(game.getState() != GameEntity.GameState.FINISHED) return;
            Logger.log("Game " + game.getRoomCode() + " has ended and will be deleted in 60 seconds.");
            game.setPlayers(List.of());
            gameSocketService.lobbyUpdate(game);
            gameRepository.delete(game);
        }, 60, TimeUnit.SECONDS);
    }

    /**
     * Starts a new leg in the game.
     * This method resets the camels' moved status, increments the round number,
     * and initializes the leg betting cards for the new leg.
     *
     * @param game The game entity for which to start a new leg.
     */
    @Transactional
    public void startNewLeg(GameEntity game) {
        Logger.log("Starting a new leg for game: " + game.getRoomCode());

        for(DiceEntity dice : game.getDices()) {
            dice.setValue(null);
            dice.getCamel().setMoved(false);
        }

        game.setRound(game.getRound() + 1);

        // Give money for leg bets
        playerBetsService.legBettingEval(game);

        // TODO?: Exchange all pyramid tiles for money

        // Reset the leg betting cards for the new leg
        gameInitService.initLegBettingCards(game);

        // Reset all desert tiles
        playerTilesService.deleteDesertTiles(game);

        gameRepository.save(game);
    }

    private void setNextTurn(GameEntity game) {
        List<PlayerEntity> sortedByAge = game.getPlayers().stream()
                .sorted(Comparator.comparingInt(PlayerEntity::getAge))
                .toList();

        PlayerEntity current = game.getCurrentPlayer();

        // find index of current player in the age-sorted list
        int currentIndex = sortedByAge.indexOf(current);

        // calculate next index (wrap around)
        int nextIndex = (currentIndex + 1) % sortedByAge.size();

        PlayerEntity nextPlayer = sortedByAge.get(nextIndex);

        game.setTurn(game.getTurn() + 1);
        game.setCurrentPlayer(nextPlayer);
    }

    @Transactional
    public void endTurn(PlayerEntity player) {
        GameEntity game = player.getGame();
        if (game == null) {
            throw new IllegalStateException("Player is not part of any game.");
        }

        this.setNextTurn(game);
        gameRepository.save(game);
    }
}
