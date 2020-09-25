package com.jiyehoo.easydmkj;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.Objects;

/**
 * @ClassName HttpClient
 * @Decription
 * @Author
 * @Date 20.9.14 22.09
 **/
public class HttpClient {
    private static final OkHttpClient client = new OkHttpClient();
    private static final  String aesKey="4T1JbdlgSM6h1urT";
    private static final  String aesIv="9618913120112010";
    private HttpClient(){
        throw new UnsupportedOperationException("不支持实例化");
    }
    public static String sendOkHttp(String param){
        String result=null;

       /* try {
            Request post = new Request.Builder().url(frontHost()).header("Content-Type", "application/x-fox")
                    .post(RequestBody.create(MediaType.parse("application/x-fox; charset=GBK"), param.getBytes("GBK"))).build();
            long start = System.currentTimeMillis();
            byte[] bytes = client.newCall(post).execute().body().bytes();
            result = new String(bytes,"GBK");
            log.info("请求返回报文sendOkHttp:{},\n消耗时间{}",result,System.currentTimeMillis()-start);
        } catch (IOException e) {
            log.error("请求异常,{}",e);
        }
        if(result == null){
            result = "";
        }*/

        return result;
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject get_token_pho(String acc, String pwd){
        String url = "https://appdmkj.5idream.net/v2/login/phone";
        String desPwd = DesUtil.DES_Encrypt(pwd,"51434574","ECB");
        String sss =String.format("{\"account\":\"%s\"," +
                "\"pwd\":\"%s\"," +
                "\"version\":\"4.2.4\"" +
                "}",acc,desPwd);

        String param =  String.format("{\"account\":\"%s\"," +
                "\"pwd\":\"%s\"," +
                "\"signToken\":\"%s\","+
                "\"version\":\"4.2.4\"" +
                "}",acc,desPwd,DesUtil.signtoken(sss));
      /* String sss = '{"account":"' + acc + '","pwd":"' + pwd_encrypt(pwd) + '","version":"4.2.4"}'
        sss = '{"account":"' + acc + '","pwd":"' + pwd_encrypt(pwd) + '","signToken":"' + get_signtoken(
                str(sss)) + '","version":"4.2.4"}'*/
       // url = "https://appdmkj.5idream.net/v2/login/phone"
        String jiami = new AES().urlBase64encrypt(param,aesKey,aesIv);

        String data ="dataKey=t%2BZ88oeo2xscPIEBzd1JWLr%2Faae06xI9WOwwXOVRupB%2BsAsl1nj2HDpZPc3ygHRlgm0glZajSvF7FsxbGiBe%2FcykCvyhloLZfYPGGLrCZV6ZBVDBHgwg6%2Fkq87A6A%2Bp%2BmTeUyp3eZz4voIGytVkwmlofr0Jn5bgBOitzBJtnq0I%3D&data="
              +jiami;

        Request post = new Request.Builder().url(url).headers(CommonHeader.getHeader())
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), data.getBytes())).build();
        String responseBody="";
        try {
             responseBody = client.newCall(post).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if(Objects.nonNull(responseBody)&& jsonObject.getInteger("code")==100){
            return jsonObject.getJSONObject("data");
        }
    /*    res = requests.post(url, headers=self.headers, data=data).json()
        if res['code'] == '100':
        self.name = res['data']['name']
        self.uid = str(res['data']['uid'])
        self.token = res['data']['token']
        return True
        else:
        return False*/
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject get_ids(String token, int uid){
        String from ="{" +
                "\"catalogId\":\"\"," +
                "\"catalogId2\":\"\"," +
                "\"endTime\":\"\"," +
                "\"joinEndTime\":\"\"," +
                "\"joinFlag\":\"\"," +
                "\"joinStartTime\":\"\"," +
                "\"keyword\":\"\"," +
                "\"level\":\"\"," +
                "\"page\":\"1\"," +
                "\"sort\":\"\"," +
                "\"specialFlag\":\"\"," +
                "\"startTime\":\"\"," +
                "\"status\":\"\"," +
                "\"token\":\"%s\"," +
                "\"uid\":%d," +
                "\"version\":\"4.2.6\"" +
                "}";
        String signtoken = DesUtil.signtoken(String.format(from,token,uid));
        String signformat="{" +
                "\"catalogId\":\"\"," +
                "\"catalogId2\":\"\"," +
                "\"endTime\":\"\"," +
                "\"joinEndTime\":\"\"," +
                "\"joinFlag\":\"\"," +
                "\"joinStartTime\":\"\"," +
                "\"keyword\":\"\"," +
                "\"level\":\"\"," +
                "\"page\":\"1\"," +
                "\"signToken\":\"%s\"," +
                "\"sort\":\"\"," +
                "\"specialFlag\":\"\"," +
                "\"startTime\":\"\"," +
                "\"status\":\"\"," +
                "\"token\":\"%s\"," +
                "\"uid\":%d," +
                "\"version\":\"4.2.6\"" +
                "}";
       String str1 = String.format(signformat,signtoken,token,uid);
        String params = new AES().urlBase64encrypt(str1,aesKey,aesIv);
        String data = "dataKey=t%2BZ88oeo2xscPIEBzd1JWLr%2Faae06xI9WOwwXOVRupB%2BsAsl1nj2HDpZPc3ygHRlgm0glZajSvF7FsxbGiBe%2FcykCvyhloLZfYPGGLrCZV6ZBVDBHgwg6%2Fkq87A6A%2Bp%2BmTeUyp3eZz4voIGytVkwmlofr0Jn5bgBOitzBJtnq0I%3D&data="
                    + params;

        String url = "https://appdmkj.5idream.net/v2/activity/activities";
        Request post = new Request.Builder().url(url).headers(CommonHeader.getHeader())
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), data.getBytes())).build();
        String responseBody="";
        try {
            responseBody = client.newCall(post).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if(Objects.nonNull(responseBody)&& jsonObject.getInteger("code")==100){
            return jsonObject.getJSONObject("data");
        }
     /*   res = requests.post(url, headers=self.headers, data=data).json()
        System.out.println(res);
        names = jsonpath.jsonpath(res, '$..name')
        ids = jsonpath.jsonpath(res, '$..aid')
        status = jsonpath.jsonpath(res, '$..status')
        self.names = names
        self.ids = ids
        self.status = status*/
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject get_info(String id, String token, Integer uid){
        String old = "{\"activityId\":\"%s\",\"token\":\"%s\",\"uid\":%d,\"version\":\"4.2.6\"}";
        String signtoken = DesUtil.signtoken(String.format(old,id,token,uid));

        String str1 = String.format("{\"activityId\":\"%s\",\"signToken\":\"%s\",\"token\":\"%s\",\"uid\":%d,\"version\":\"4.2.6\"}",
                    id,signtoken,token,uid);
       String data = "dataKey=t%2BZ88oeo2xscPIEBzd1JWLr%2Faae06xI9WOwwXOVRupB%2BsAsl1nj2HDpZPc3ygHRlgm0glZajSvF7FsxbGiBe%2FcykCvyhloLZfYPGGLrCZV6ZBVDBHgwg6%2Fkq87A6A%2Bp%2BmTeUyp3eZz4voIGytVkwmlofr0Jn5bgBOitzBJtnq0I%3D&data="
               +  new AES().urlBase64encrypt(str1,aesKey,aesIv);
       String url = "https://appdmkj.5idream.net/v2/activity/detail";
        Request post = new Request.Builder().url(url).headers(CommonHeader.getHeader())
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), data.getBytes())).build();
        String responseBody="";
        try {
            responseBody = client.newCall(post).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSON.parseObject(responseBody);
        if(Objects.nonNull(responseBody)&& jsonObject.getInteger("code")==100){
            return jsonObject.getJSONObject("data");
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject join(String id, String token, Integer uid){
        String old="{\"activityId\":\"%s\",\"data\":\"[]\",\"remark\":\"\",\"token\":\"%s\",\"uid\":\"%s\",\"version\":\"4.2.6\"}";
        String signtoken =  DesUtil.signtoken(String.format(old,id,token,String.valueOf(uid)));

        String str1 =String.format("{\"activityId\":\"%s\",\"data\":\"[]\",\"remark\":\"\",\"signToken\":\"%s\",\"token\":\"%s\",\"uid\":\"%s\",\"version\":\"4.2.6\"}",
                 id, signtoken,token,String.valueOf(uid));
        String data = "dataKey=t%2BZ88oeo2xscPIEBzd1JWLr%2Faae06xI9WOwwXOVRupB%2BsAsl1nj2HDpZPc3ygHRlgm0glZajSvF7FsxbGiBe%2FcykCvyhloLZfYPGGLrCZV6ZBVDBHgwg6%2Fkq87A6A%2Bp%2BmTeUyp3eZz4voIGytVkwmlofr0Jn5bgBOitzBJtnq0I%3D&data="
                + new AES().urlBase64encrypt(str1,aesKey,aesIv);
        String url = "https://appdmkj.5idream.net/v2/signup/submit";
        Request post = new Request.Builder().url(url).headers(CommonHeader.getHeader())
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), data.getBytes())).build();
        String responseBody="";
        try {
            responseBody = client.newCall(post).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = JSON.parseObject(responseBody);
        if(Objects.nonNull(responseBody)&& jsonObject.getInteger("code")==100){
            return jsonObject.getJSONObject("data");
        }
        return null;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void main(String[] args) {
     //  System.out.println(get_token_pho("15892807714", "yy2020510"));
        System.out.println(get_ids("7129F15F304C25967CAF5A894F4444AE",35069158));
    }


}
