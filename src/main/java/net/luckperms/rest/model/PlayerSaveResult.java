package net.luckperms.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Set;
import java.util.UUID;

public class PlayerSaveResult extends AbstractModel {
    private final Set<Outcome> outcomes;
    private final String previousUsername; // nullable
    private final Set<UUID> otherUniqueIds; // nullable

    public PlayerSaveResult(Set<Outcome> outcomes, String previousUsername, Set<UUID> otherUniqueIds) {
        this.outcomes = outcomes;
        this.previousUsername = previousUsername;
        this.otherUniqueIds = otherUniqueIds;
    }

    public Set<Outcome> outcomes() {
        return this.outcomes;
    }

    public String previousUsername() {
        return this.previousUsername;
    }

    public Set<UUID> otherUniqueIds() {
        return this.otherUniqueIds;
    }

    public enum Outcome {

        @SerializedName("clean_insert")
        CLEAN_INSERT,

        @SerializedName("no_change")
        NO_CHANGE,

        @SerializedName("username_updated")
        USERNAME_UPDATED,

        @SerializedName("other_unique_ids_present_for_username")
        OTHER_UNIQUE_IDS_PRESENT_FOR_USERNAME,
    }

}
