package com.cmput301w20t23.newber.helpers;

/**
 * An interface to facilitate getting asynchronous data from Firestore. The function will be called
 * by the Database Adapter when the data is finally received in the OnCompleteListeners
 * @param <T> A generic argument
 *
 * https://stackoverflow.com/questions/57330766/how-to-get-data-from-any-asynchronous-operation-in-android/57330767
 * @author Ibrahim Aly
 */
public interface Callback<T> {
    void myResponseCallback(T result);
}