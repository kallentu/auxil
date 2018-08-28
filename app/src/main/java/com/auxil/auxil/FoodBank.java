package com.auxil.auxil;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

/** Data object for holding food bank information, */
@AutoValue @FirebaseValue
abstract class FoodBank {

    abstract String name();
    abstract String address();
    abstract String number();
    abstract String website();

    static Builder builder() {
        return new AutoValue_FoodBank.Builder();
    }

    // Create AutoValue_Taco.FirebaseValue instance from AutoValue_Taco instance
    public Object toFirebaseValue() {
        return new AutoValue_FoodBank.FirebaseValue(this);
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
