package tuc.isse.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "desert_tiles")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DesertTileEntity {
    final public static int VALUE = 1;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private TileType type;
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    @JsonIgnore
    private GameEntity game;

    // In DesertTileEntity
    @OneToOne
    @JoinColumn(name = "owner_id")
    @JsonBackReference
    private PlayerEntity owner;

    // enums
    public enum TileType {
        OASIS, MIRAGE
    }
}
