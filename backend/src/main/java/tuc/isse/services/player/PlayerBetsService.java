package tuc.isse.services.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tuc.isse.entities.*;
import tuc.isse.services.camel.CamelMovementService;
import tuc.isse.utils.Logger;

import java.util.List;

@Service
public class PlayerBetsService {

    @Autowired
    CamelMovementService camelMovementService;

    public LegBettingCard getLegCard(GameEntity game, CamelEntity.CAMEL_COLOR camelColor) {
        if (game == null || camelColor == null || game.getLegBettingCards() == null) return null;

        return game.getLegBettingCards().stream()
                .filter(card -> card.getCamel().getColor() == camelColor && card.getPlayer() == null)
                .max((a, b) -> Integer.compare(a.getLabel(), b.getLabel()))
                .orElse(null);
    }

    public RaceBettingCard getRaceCard(GameEntity game, CamelEntity.CAMEL_COLOR camelColor) {
        if (game == null || camelColor == null || game.getRaceBettingCards() == null) return null;

        return game.getRaceBettingCards().stream()
                .filter(card -> card.getCamel().getColor() == camelColor && card.getPlayer() == null)
                .max((a, b) -> Integer.compare(b.getOrder(), a.getOrder()))
                .orElse(null);
    }

    public void legBettingEval(GameEntity game) {

        camelMovementService.sortByWinners(game.getCamels());

        for (LegBettingCard card : game.getLegBettingCards()) {
            PlayerEntity player = card.getPlayer();
            if (player == null) continue;
            int camelsPlace = game.getCamels().indexOf(card.getCamel()) + 1;

            if (camelsPlace == 1) card.setWorth(card.getLabel());
            else if (camelsPlace == 2) card.setWorth(1);
            else if (player.getMoney() > 0) card.setWorth(-1);

            Logger.info("Player: " + player.getName() + " camel: " + card.getCamel().getColor() + " place: " + camelsPlace + " worth: " + card.getWorth());

            player.setMoney(player.getMoney() + card.getWorth());
        }
    }

    public void raceBettingEval(GameEntity game){
        camelMovementService.sortByWinners(game.getCamels());
        game.getCamels().getFirst().setRaceWinner(true);

        List<RaceBettingCard> bettingCards = game.getRaceBettingCards();

        List<RaceBettingCard> WinnerList= bettingCards.stream().filter(raceBettingCard -> raceBettingCard.getBetType()== RaceBettingCard.BetType.WINNER).toList();
        List<RaceBettingCard> LoserList= bettingCards.stream().filter(raceBettingCard -> raceBettingCard.getBetType()== RaceBettingCard.BetType.LOSER).toList();

        ListEval(WinnerList, game);
        ListEval(LoserList, game);
    }

    public void ListEval(List<RaceBettingCard> List, GameEntity game){

        CamelEntity winner = game.getCamels().getFirst();
        CamelEntity loser = game.getCamels().getLast();

        int order = 0;
        int[] EP_REWARDS = {8, 5, 3, 2};

        for (RaceBettingCard card : List) {

            PlayerEntity player = card.getPlayer();
            if(player == null) continue ;
            CamelEntity camel = card.getCamel();
            // Starts from 1 (first bet)

            boolean isCorrect = false;

            if (card.getBetType() == RaceBettingCard.BetType.WINNER && camel.equals(winner)) {
                isCorrect = true;
            } else if (card.getBetType() == RaceBettingCard.BetType.LOSER && camel.equals(loser)) {
                isCorrect = true;
            }

            int currentMoney = player.getMoney();

            if (isCorrect) {
                card.setWorth((order <= 4) ? EP_REWARDS[order] : 1);
                order+= 1;
                Logger.info("player: " + player.getName() + " camel: " + card.getCamel().getColor() + " prize: " + card.getWorth() );
            } else if (currentMoney > 0) {
                card.setWorth(-1);
                Logger.warn("player: " + player.getName() + " camel: " + card.getCamel().getColor() + " penalty -1");
            }

            player.setMoney(currentMoney + card.getWorth());
        }
    }

}
