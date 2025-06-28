package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.PlayerEntity;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PlayerDTO {
    private Long id;
    private String name;
    private Integer age;
    private Integer money;
    private String character;
    private Long gameId;

    public PlayerDTO(PlayerEntity player) {
        this.id = player.getId();
        this.name = player.getName();
        this.age = player.getAge();
        this.money = player.getMoney();
        this.character = player.getCharacter() != null ? player.getCharacter().name() : null;
        this.gameId = player.getGame() != null ? player.getGame().getId() : null;
    }
}