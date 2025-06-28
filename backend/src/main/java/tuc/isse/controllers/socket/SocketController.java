package tuc.isse.controllers.socket;

import org.springframework.beans.factory.annotation.Autowired;
import tuc.isse.entities.PlayerEntity;
import tuc.isse.repositories.GameRepository;
import tuc.isse.repositories.PlayerRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketController {

    protected static final Map<String, PlayerEntity> sessionPlayerMap = new ConcurrentHashMap<>();

    @Autowired
    protected PlayerRepository playerRepository;

    @Autowired
    protected GameRepository gameRepository;

}
