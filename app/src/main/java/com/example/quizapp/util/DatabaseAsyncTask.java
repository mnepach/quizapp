package com.example.quizapp.util;

import android.os.AsyncTask;
import android.util.Log;

import java.util.function.Consumer;

public class DatabaseAsyncTask<T> extends AsyncTask<Void, Void, T> {
    private static final String TAG = "DatabaseAsyncTask";

    private final TaskSupplier<T> taskSupplier;
    private final Consumer<T> onSuccess;
    private final Consumer<Exception> onError;
    private Exception exception;

    public interface TaskSupplier<T> {
        T get() throws Exception;
    }

    public DatabaseAsyncTask(TaskSupplier<T> taskSupplier, Consumer<T> onSuccess, Consumer<Exception> onError) {
        this.taskSupplier = taskSupplier;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    protected T doInBackground(Void... voids) {
        try {
            return taskSupplier.get();
        } catch (Exception e) {
            exception = e;
            Log.e(TAG, "Database operation failed", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(T result) {
        if (exception != null && onError != null) {
            onError.accept(exception);
        } else if (onSuccess != null) {
            onSuccess.accept(result);
        }
    }
}