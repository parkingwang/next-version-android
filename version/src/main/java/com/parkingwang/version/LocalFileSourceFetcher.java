/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 从文件中读取文本内容
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
final class LocalFileSourceFetcher implements SourceFetcher {

    @Override
    public Response fetch(Source source) throws IOException {
        if (source instanceof LocalFileSource) {
            final File jsonFile = new File(source.path());
            if (jsonFile.exists() && jsonFile.isFile()){
                final BufferedReader reader = new BufferedReader(new FileReader(new File(source.path())));
                final StringBuilder output = new StringBuilder();
                final char[] buffer = new char[1024 * 4];
                while (reader.read(buffer) != -1) {
                    output.append(buffer);
                }
                try{
                    return Response.found(output.toString());
                }finally {
                    reader.close();
                }
            }else{
                return Response.notFound();
            }
        }else{
            return Response.notFound();
        }
    }
}
