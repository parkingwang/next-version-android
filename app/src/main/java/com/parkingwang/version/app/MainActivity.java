package com.parkingwang.version.app;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.parkingwang.version.NextVersion;
import com.parkingwang.version.Scheduler;
import com.parkingwang.version.Version;
import com.parkingwang.version.fir.FirIMUrlSource;
import com.parkingwang.version.fir.FirIMVersionParser;
import com.parkingwang.version.wave.WaveProgressDownloader;
import com.parkingwang.version.wave.WaveVersionHandler;

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
                        .addSource(FirIMUrlSource.fromPackageName("com.parkingwang.pad3",
                                "token-token"))
                        .addVersionParser(FirIMVersionParser.create())
                        .addVersionFoundHandler(WaveVersionHandler.create(context))
                        .addApkDownloader(WaveProgressDownloader.create(context))
                        .check();

            }
        });
    }

}
