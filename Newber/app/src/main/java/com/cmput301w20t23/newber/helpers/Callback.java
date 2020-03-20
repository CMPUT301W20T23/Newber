package com.cmput301w20t23.newber.helpers;

public interface Callback<T> {
    void myResponseCallback (T result); // whatever your return type is: string, integer, etc.
}