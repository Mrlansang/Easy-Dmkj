package com.jiyehoo.easydmkj;

import com.alibaba.fastjson.JSON;
import com.squareup.okhttp.Headers;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName CommonHeader
 * @Decription
 * @Author
 * @Date 20.9.9 10:32
 **/
public class CommonHeader {
  /* public String a = {"channelName": "dmkj_Android", "countryCode": "CN", "createTime": int(100 * time.time()),
            "device": "Xiaomi Mi MIX 2S", "hardware": "qcom", "modifyTime": int(100 * time.time()),
            "operator": "%E6%9C%AA%E7%9F%A5", "screenResolution": "1080-2116",
            "startTime": System.c,
            "sysVersion": "Android 29 10", "system": "android", "uuid": "A4:60:46:1F:74:BF", "version": "4.2.6"}

    headers = {
        'standardUA': str(a),
                'Content-Type': 'application/x-www-form-urlencoded',
                'Host': 'appdmkj.5idream.net',
                'Connection': 'Keep-Alive',
                'Accept-Encoding': 'gzip',
                'User-Agent': 'okhttp/3.11.0'
    }*/

    public static Headers getHeader(){
        Headers.Builder builder =new Headers.Builder();
        Map<String,String> map = new HashMap<>();
        map.put("channelName","dmkj_Android");
         map.put("countryCode","CN");
         map.put("createTime",String.valueOf(System.currentTimeMillis()));
         map.put("device","Xiaomi Mi MIX 2S");
         map.put("hardware","qcom");
         map.put("modifyTime",String.valueOf(System.currentTimeMillis()));
         map.put("operator","%E6%9C%AA%E7%9F%A5");
         map.put("screenResolution","1080-2116");
         map.put("startTime",String.valueOf(System.currentTimeMillis()));
         map.put("sysVersion","Android 29 10");
         map.put("system","android");
         map.put("uuid","A4:60:46:1F:74:BF");
         map.put("version","4.2.6");
        builder.set("standardUA", JSON.toJSONString(map))
                .set("Content-Type","application/x-www-form-urlencoded")
                .set("Host","appdmkj.5idream.net")
                .set("Connection","Keep-Alive")
                .set("Accept-Encoding","gzip")
                .set("User-Agent","okhttp/3.11.0");
        return builder.build();
    }
}
