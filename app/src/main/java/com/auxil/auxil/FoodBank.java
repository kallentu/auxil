package com.auxil.auxil;

import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;

import java.io.Serializable;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

/** Data object for holding food bank information, */
@AutoValue @FirebaseValue
public abstract class FoodBank implements Serializable{

    public abstract String name();
    public abstract String address();
    public abstract String number();
    public abstract String website();

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
