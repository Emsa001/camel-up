package tuc.isse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tuc.isse.entities.DesertTileEntity;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DesertTileDTO {
    private Long id;

    private DesertTileEntity.TileType type;
    private int position;
    private Long ownerId;
    private Long gameId;

    public DesertTileDTO(DesertTileEntity desertTileEntity) {
        this.id = desertTileEntity.getId();
        this.type = desertTileEntity.getType();
        this.position = desertTileEntity.getPosition();
        if (desertTileEntity.getOwner() != null) {
            this.ownerId = desertTileEntity.getOwner().getId();
        } else {
            this.ownerId = null;
        }
        if (desertTileEntity.getGame() != null) {
            this.gameId = desertTileEntity.getGame().getId();
        } else {
            this.gameId = null;
        }
    }
}
