package com.jiyehoo.easydmkj;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @ClassName Opreation
 * @Decription
 * @Author
 * @Date 20.9.9 10:05
 **/
public class Opreation {
    private  String token ;
    private  String  name;
    private  Integer  uid ;
    private Map<String,String> aids = new HashMap<>();
    private Map<String,String> time_aid = new HashMap<>();


    // 登录
    public boolean login(String user,String passwd){
        String acc = user;
        String  pwd = passwd;
       // 修复bug防止因为token过期
        if(test_token()){
            System.out.println("token登录成功");
            return true;
        }else if(get_token(acc, pwd)){
            System.out.println("密码登录成功");
            return true;
        }else {
            System.out.println("token过期请检查账号密码");
            return false;
        }
    }


    public boolean test_token(){
        if(Objects.isNull(this.token) || Objects.isNull(uid) ){
            return false;
        }
        try{
            JSONObject jsonObject = HttpClient.get_ids(token, Integer.valueOf(uid));
            if(Objects.nonNull(jsonObject)){
                return true;
            }
            return false;
        }catch (Exception e){
            System.out.println("异常");
            return false;
        }
    }
    public boolean get_token(String acc,String pwd){
        JSONObject token_pho = HttpClient.get_token_pho(acc, pwd);
        if(Objects.nonNull(token_pho)){
            this.token = token_pho.getString("token");
            this.name = token_pho.getString("name");
            this.uid = token_pho.getInteger("uid");
                return true;
        }
        return false;
    }


    public void get_aid(){
        if(this.token == null || this.uid == null){
            return;
        }
        JSONObject object = HttpClient.get_ids(this.token, this.uid);
       /* this.names = (List<String>) JSONPath.eval(object.get("list"), "$.name");
        this.ids =  (List<String>) JSONPath.eval(object.get("list"), "$.aid");*/
     /*   List<String> statuses =  (List<String>) JSONPath.eval(object.get("list"), "$.status");*/
        JSONArray list = (JSONArray)object.get("list");
        list.forEach(t->{
            JSONObject t1 = (JSONObject) t;
            if(t1.getInteger("status")== 2 && !aids.containsKey(t1.getString("aid"))){
                aids.put(t1.getString("aid"),t1.getString("name"));
            }
        });
    }

    public void chiken(){
        aids.forEach((aid,name)->{
            JSONObject info = HttpClient.get_info(aid, this.token, this.uid);
            //System.out.println(info);
            if(Objects.isNull(info)){
                System.out.println("查询失败");
            }else {
                time_aid.put(info.getString("joindate"),aid);
            }
        });
    }

    public boolean enter(String aid) {
        JSONObject join = HttpClient.join(aid, this.token, this.uid);
        if(Objects.nonNull(join)){
            System.out.println(aids.get(aid)+"报名成功");
            return true;
        }else{
            System.out.println("报名失败");
            return false;
        }

    }



    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Map<String, String> getAids() {
        return aids;
    }

    public void setAids(Map<String, String> aids) {
        this.aids = aids;
    }

    public Map<String, String> getTime_aid() {
        return time_aid;
    }

    public void setTime_aid(Map<String, String> time_aid) {
        this.time_aid = time_aid;
    }


}
