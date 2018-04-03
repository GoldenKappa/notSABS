package com.layoutxml.sabs;


import android.app.Application;

import com.layoutxml.sabs.dagger.component.AppComponent;
import com.layoutxml.sabs.dagger.component.DaggerAppComponent;
import com.layoutxml.sabs.dagger.module.AppModule;

public class App extends Application {
    private static App instance;
    private AppComponent appComponent;

    public static App get() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        appComponent = initDagger(instance);
    }

    protected AppComponent initDagger(App application) {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
    }


    public AppComponent getAppComponent() {
        return appComponent;
    }
}
