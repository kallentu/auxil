package com.auxil.auxil;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

/** Data object for holding food bank information, */
@AutoValue
abstract class FoodBank {

    abstract String name();
    abstract String address();
    abstract String number();
    abstract String website();

    static Builder builder() {
        return new AutoValue_FoodBank.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setName(String value);
        abstract Builder setAddress(String value);
        abstract Builder setNumber(String value);
        abstract Builder setWebsite(@Nullable String value);
        abstract FoodBank build();
    }
}
