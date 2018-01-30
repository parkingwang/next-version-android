/*
 * Copyright (c) 2017. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.parkingwang.version;

import java.io.IOException;

/**
 * @author 陈永佳 (chenyongjia@parkingwang.com, yoojiachen@gmail.com)
 */
public interface SourceFetcher {

    Response fetch(Source source) throws IOException;

    class Response implements Checkable {

        private final boolean mFound;

        public final String text;

        private Response(boolean found, String text) {
            this.mFound = found;
            this.text = text;
        }

        public static Response found(String text){
            return new Response(true, text);
        }

        public static Response notFound(){
            return new Response(false, "{}");
        }

        @Override
        public boolean isValid() {
            return mFound;
        }
    }
}
