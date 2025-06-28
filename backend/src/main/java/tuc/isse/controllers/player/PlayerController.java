package tuc.isse.controllers.player;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import tuc.isse.repositories.PlayerRepository;

@Controller
@RequestMapping("/api/v1/player")
@MessageMapping("/player")
public class PlayerController {
    @Autowired
    private PlayerRepository playerRepository;
}
