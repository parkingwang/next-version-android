package com.parkingwang.version.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Scheduler;
import com.parkingwang.version.Version;
import com.parkingwang.version.check.CheckVersionDownloader;
import com.parkingwang.version.check.CheckVersionHandler;
import com.parkingwang.version.source.MockAssetSource;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final MainActivity context = MainActivity.this;
                NextVersion.with(context)
                        .setDebugEnabled(true)
                        .setLocalVersion(Version.local(1, "1.0.0"))
                        .runOn(Scheduler.NewThread.create())
                        // Fir更新源
//                        .addSource(FirIMUrlSource.fromPackageName("pack.name", "token-token"))
//                        .addVersionParser(FirIMVersionParser.create())
                        // Mock更新源
                        .addSource(new MockAssetSource())
                        // Wave主题
//                        .addVersionFoundHandler(WaveVersionHandler.create(context))
//                        .addApkDownloader(WaveProgressDownloader.create(context))
                        // Rocket主题
//                        .addVersionFoundHandler(RocketVersionHandler.create(context))
                        // Check主题
                        .addVersionFoundHandler(CheckVersionHandler.create(context))
                        .addApkDownloader(CheckVersionDownloader.create(context))
                        .check();

            }
        });
    }

}
