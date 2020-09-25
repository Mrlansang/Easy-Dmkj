package com.jiyehoo.easydmkj;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Dmk
 * @Decription
 * @Author
 * @Date 20.9.10 11:52
 **/
public class Dmk {
    private static final String dateFormat = "yyyy.MM.dd HH:mm";//"yyyy/MM/dd HH:mm"
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入账号:");
        String user = sc.nextLine();  //读取字符串型输入
        if(Objects.isNull(user) || user.length()<1){
            System.out.println("请输入账号:");
            user = sc.nextLine();
        }
        System.out.println("请输入密码:");
        String passwd = sc.nextLine();
        if(Objects.isNull(passwd) || passwd.length()<1){
            System.out.println("请输入账号:");
            passwd = sc.nextLine();
        }

        Opreation opreation=new Opreation();
        if (opreation.login(user,passwd)) {
            System.out.println("正在初始化");
            System.out.println("欢迎您"+opreation.getName());
            opreation.get_aid();
            opreation.chiken();
            schedule(opreation,user,passwd);
        }
    }

    public static void schedule(Opreation opreation,String user,String passwd){
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(()->{
           new Dmk().update(opreation,user,passwd);
        },0,3600, TimeUnit.SECONDS);
        if(opreation.login(user,passwd)){
            opreation.get_aid();
            opreation.chiken();
            System.out.println("更新成功");
        }
    }
    public void update(Opreation opreation,String user,String passwd){
        if (opreation.login(user,passwd)) {
            opreation.get_aid();
            opreation.chiken();
            System.out.println("更新成功");
            join(opreation,user,passwd);
        }
    }

    public void join(Opreation opreation,String user,String passwd){


            Map<String, String> remove = new HashMap<>();
        opreation.getTime_aid().forEach((t,id)->{
            System.out.println("--------------------");
            System.out.println(opreation.getAids().get(id));
            System.out.println("报名时间:"+t);
        });
            opreation.getTime_aid().forEach((time, aid) -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
                System.out.println(time);

                LocalDateTime dateTime = LocalDateTime.parse(time.split("-")[0], formatter);
                System.out.println("正在等待报名时间.....");
                System.out.println("即将报名的活动:" + time+","+opreation.getAids().get(aid));
                while (true) {
                    boolean isTime = dateTime.isBefore(LocalDateTime.now());
                    if (isTime) {
                        for (int i = 0; i < 4; i++) {
                            opreation.login(user, passwd);
                            if (opreation.enter(aid)) {
                                break;
                            }
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        remove.put(time, aid);
                        break;
                    }
                }
            });
           /* remove.forEach((time1, id) -> {
                opreation.getTime_aid().remove(time1);
                opreation.getAids().remove(id);
            });
*/


        //}
    }
}


