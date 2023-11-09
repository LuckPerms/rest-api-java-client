package net.luckperms.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Locale;

public enum NodeType {

    @SerializedName("regex_permission")
    REGEX_PERMISSION,

    @SerializedName("inheritance")
    INHERITANCE,

    @SerializedName("prefix")
    PREFIX,

    @SerializedName("suffix")
    SUFFIX,

    @SerializedName("meta")
    META,

    @SerializedName("weight")
    WEIGHT,

    @SerializedName("display_name")
    DISPLAY_NAME;

    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
