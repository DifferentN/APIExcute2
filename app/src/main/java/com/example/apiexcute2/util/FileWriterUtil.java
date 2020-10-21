package com.example.apiexcute2.util;

import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class FileWriterUtil {
    public static void writeString(String fileName,String content){
        File file = obtainFile(fileName);
        writeString(file,content);
    }
    public static void writeString(File file,String content){
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(content);
            bufferedWriter.flush();
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void writeJson(File file, JSONObject jsonObject){
        String content = jsonObject.toJSONString();
        writeString(file,content);
    }
    public static void writeJson(File file, JSONArray jsonArray){
        String content = jsonArray.toJSONString();
        writeString(file,content);
    }
    public static void writeJson(String fileName, JSONObject jsonObject){
        String content = jsonObject.toJSONString();
        File file = obtainFile(fileName);
        writeString(file,content);
    }
    public static void writeJson(String fileName, JSONArray jsonArray){
        String content = jsonArray.toJSONString();
        File file = obtainFile(fileName);
        writeString(file,content);
    }
    private static File obtainFile(String fileName){
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    public static JSONObject readJSONObject(String path){
        File file = new File(path);
        if(!file.exists()){
            Log.i("LZH","JSONObject file not exist "+path);
            return null;
        }
        JSONObject res = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            StringBuilder strBuilder = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null){
                strBuilder.append(line);
            }
            res = JSONObject.parseObject(strBuilder.toString());
            fileInputStream.close();
            inputStreamReader.close();
            reader.close();
        } catch (FileNotFoundException e) {
            Log.i("LZH",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.i("LZH",e.getMessage());
            e.printStackTrace();
        }
        return res;
    }
    public static JSONArray readJSONArray(String path){
        File file = new File(path);
        if(!file.exists()){
            System.out.println("JSONArray file not exist "+path);
            return null;
        }
        JSONArray res = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"UTF-8");
            BufferedReader  reader = new BufferedReader(inputStreamReader);
            StringBuilder strBuilder = new StringBuilder();
            String line = null;
            while((line=reader.readLine())!=null){
                strBuilder.append(line);
            }
            res = JSONArray.parseArray(strBuilder.toString());

            fileInputStream.close();
            inputStreamReader.close();
            reader.close();
        } catch (FileNotFoundException e) {
            Log.i("LZH",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
