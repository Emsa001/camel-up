package tuc.isse.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatModel {
    private Action action;
    private String playerName;
    private String text;

    public enum Action {
        TILE,
        BET,
        MOVE,
        SYSTEM,
        MESSAGE;

        @JsonCreator
        public static Action fromString(String key) {
            return key == null ? null : Action.valueOf(key.toUpperCase());
        }

        @JsonValue
        public String getValue() {
            return this.name();
        }
    }
}
