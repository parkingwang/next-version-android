/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version.fetcher;

import android.content.Context;

import com.parkingwang.version.Source;
import com.parkingwang.version.SourceFetcher;
import com.parkingwang.version.source.MockAssetSource;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public final class MockAssetSourceFetcher implements SourceFetcher {

    private final Context mContext;

    public MockAssetSourceFetcher(Context context) {
        mContext = context;
    }

    @Override
    public Response fetch(Source source) throws IOException {
        if (source instanceof MockAssetSource) {
            final String fileName = source.path();
            final BufferedInputStream reader = new BufferedInputStream(mContext.getResources().getAssets().open(fileName));
            final ByteArrayOutputStream writer = new ByteArrayOutputStream();
            try {
                int result = reader.read();
                while(result != -1) {
                    writer.write((byte) result);
                    result = reader.read();
                }
                return Response.found(writer.toString());
            }finally {
                reader.close();
                writer.close();
            }
        }else{
            return Response.notFound();
        }
    }
}
