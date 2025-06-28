package tuc.isse.controllers.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tuc.isse.repositories.GameRepository;

/**
 * Base controller for game-related operations.
 * This controller is used to define common properties and methods for game controllers.
 */
@Controller
@RequestMapping("/api/v1/game")
@MessageMapping("/game")
public class GameController {
    @Autowired
    protected GameRepository gameRepository;
}
