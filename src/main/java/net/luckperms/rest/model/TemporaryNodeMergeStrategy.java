package net.luckperms.rest.model;

import com.google.gson.annotations.SerializedName;

public enum TemporaryNodeMergeStrategy {

    @SerializedName("add_new_duration_to_existing")
    ADD_NEW_DURATION_TO_EXISTING,

    @SerializedName("replace_existing_if_duration_longer")
    REPLACE_EXISTING_IF_DURATION_LONGER,

    @SerializedName("none")
    NONE

}
