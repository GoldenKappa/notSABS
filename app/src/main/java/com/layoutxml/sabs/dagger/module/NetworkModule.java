package com.layoutxml.sabs.dagger.module;

import android.util.Log;

import com.layoutxml.sabs.dagger.scope.AdhellApplicationScope;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;

@Module
public class NetworkModule {

    private static final String TAG = NetworkModule.class.getCanonicalName();

    @Provides
    @AdhellApplicationScope
    OkHttpClient providesOkHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        Log.i(TAG, "Providing okHttpClient");
        return okHttpClient;
    }

    @Provides
    @AdhellApplicationScope
    Gson providesGson() {
        return new Gson();
    }
}
