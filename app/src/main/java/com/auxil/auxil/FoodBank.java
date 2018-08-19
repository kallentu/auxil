package com.auxil.auxil;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

@AutoValue
abstract class FoodBank {

    abstract String name();
    abstract String address();
    abstract int number();
    abstract String website();

    static AutoValue.Builder builder() {
        return new AutoValue_FoodBank.Builder();
    }

    @AutoValue.Builder
    abstract static class Builder {
        abstract Builder setName(String value);
        abstract Builder setAddress(String value);
        abstract Builder setNumber(int value);
        abstract Builder setWebsite(@Nullable String value);
        abstract FoodBank build();
    }
}
