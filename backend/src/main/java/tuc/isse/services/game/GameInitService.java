package tuc.isse.services.game;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.entities.*;
import tuc.isse.repositories.BettingCardRepository;
import tuc.isse.services.player.PlayerTilesService;
import tuc.isse.utils.Logger;

import java.util.Comparator;
import java.util.stream.Collectors;

@Service
public class GameInitService {

    @Autowired
    PlayerTilesService playerTilesService;

    /**
     * Initializes the game round and sets the first player.
     * This method sets the initial round and turn to 1, and selects the youngest player as the current player.
     *
     * @param game The GameEntity to initialize.
     */
    public void initGameRound(@NotNull GameEntity game) {
        game.setRound(1);

        PlayerEntity youngestPlayer = game.getPlayers().stream()
                .min((p1, p2) -> Integer.compare(p1.getAge(), p2.getAge()))
                .orElse(null);

        game.setTurn(game.getPlayers().indexOf(youngestPlayer) + 1);
        game.setCurrentPlayer(youngestPlayer);
    }

    /**
     * Initializes the money for each player in the game.
     * Each player starts with a default amount of money.
     *
     * @param game The GameEntity to initialize money for.
     */
    public void initMoney(@NotNull GameEntity game) {
        // Initialize players with default money and character selection
        game.getPlayers().forEach(player -> {
            player.setMoney(PlayerEntity.INITIAL_MONEY);
        });
    }

    /**
     * Initializes the camels and dices for the game.
     * Stack position is set based on the initial random positions of the camels.
     *
     * @param game The GameEntity to initialize camels for.
     */
    public void initCamels(@NotNull GameEntity game) {
        game.getCamels().clear();
        game.getDices().clear();

        for (CamelEntity.CAMEL_COLOR color : CamelEntity.CAMEL_COLOR.values()) {
            CamelEntity camel = new CamelEntity(color, game);
            game.getCamels().add(camel);
            game.getDices().add(new DiceEntity(camel, game));
        }

        // Set initial positions for camels after randomly setting their positions
        game.getCamels().stream()
            .collect(Collectors.groupingBy(CamelEntity::getPosition))
            .forEach((position, camelsAtPos) -> {
                for (int i = 0; i < camelsAtPos.size(); i++) {
                    camelsAtPos.get(i).setStackPosition(i);
                }
            });
    }

    /**
     * Initializes leg betting cards for each camel in the game.
     * Each camel has three betting cards with different values.
     *
     * @param game The GameEntity to initialize leg betting cards for.
     */
    public void initLegBettingCards(@NotNull GameEntity game) {
        game.getLegBettingCards().clear();

        for (CamelEntity camel : game.getCamels()) {
            game.getLegBettingCards().add(new LegBettingCard(camel, 2, game));
            game.getLegBettingCards().add(new LegBettingCard(camel, 3, game));
            game.getLegBettingCards().add(new LegBettingCard(camel, 5, game));
        }
    }

    /**
     * Initializes race betting cards for each camel in the game.
     * Each player has a set of betting cards for each camel,
     *
     * @param game The GameEntity to initialize leg betting cards for.
     */
    public void initRaceBettingCards(@NotNull GameEntity game) {
        game.getRaceBettingCards().clear();

        int i = 1;
        for (PlayerEntity player : game.getPlayers()) {
            for (CamelEntity camel : game.getCamels()) {
                game.getRaceBettingCards().add(new RaceBettingCard(camel, i, game));
            }
            i++;
        }
    }

    /**
     * Initializes the game with default settings for starting / restarting.
     * This method sets up the game round, player money, camels, and betting cards.
     *
     * @param game The GameEntity to be initialized.
     */
    public void init(GameEntity game) {
        this.initGameRound(game);
        this.initMoney(game);
        this.initCamels(game);
        this.initLegBettingCards(game);
        this.initRaceBettingCards(game);
        playerTilesService.deleteDesertTiles(game);
    }
}
