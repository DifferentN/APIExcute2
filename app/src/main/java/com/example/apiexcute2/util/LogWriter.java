package com.example.apiexcute2.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * 单实例模式，确保所有方法调用Log顺序的写入文件中
 */
public class LogWriter {
    private static File file;
    private static BufferedWriter writer;
    private static volatile LogWriter logWriter;
    private static String fName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/methodLog.txt";
    private static boolean token = false;//是否可以写入日志的标志
    private static long preTime;
    private static List<String> list ;
    public LogWriter(){
        file = new File(fName);
        list = new LinkedList<>();
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter fileWriter = new FileWriter(file);
            writer = new BufferedWriter(fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static LogWriter getInstance(){
        if(logWriter==null){
            synchronized (LogWriter.class){
                if(logWriter==null){
                    logWriter = new LogWriter();
                }
            }
        }
        return logWriter;
    }

    /**
     * 向文件中写入方法调用
     * @param log
     */
    public synchronized void writeLog(String log){
        if(!token){
            return;
        }
        list.add(log);
    }

    public static void turnWriteAble(){
        long curTime = System.currentTimeMillis();
        if(curTime-preTime<=300){
            preTime = curTime;
            return;
        }
        preTime = curTime;

        token = !token;
        Log.i("LZH",token+"");
        if(!token){
            if (writer==null){
                Log.i("LZH","writer is null");
                return;
            }
            writeLogList();
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void writeLogList(){
        try {
            for(String log:list){
                writer.write(log+"\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
