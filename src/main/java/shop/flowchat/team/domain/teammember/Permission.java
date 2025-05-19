package shop.flowchat.team.domain.teammember;

import lombok.Getter;

@Getter
public enum Permission {
    NONE("NONE"),
    READ("READ_AUTHORITY"),
    CREATE("CREATE_AUTHORITY"),
    UPDATE("UPDATE_AUTHORITY"),
    DELETE("DELETE_AUTHORITY");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }
}
