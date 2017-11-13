package com.stone.pile.entity;

import android.os.Environment;
import android.util.Log;

import com.stone.pile.R;
import com.stone.pile.activity.jsonbean;

/**
 * Created by xmuSistone on 2017/5/12.
 */

public class ItemEntity {
    private int emotion;
    public String getPicPath() {
        return picPath;
    }
    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }
    private String picPath;
    private String type;
    public int getEmotion() {
        return emotion;
    }
    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getOrignImagePath() {
        return OrignImagePath;
    }
    public void setOrignImagePath(String orignImagePath) {
        OrignImagePath = orignImagePath;
    }
    private String description;
    private String OrignImagePath;
    public ItemEntity(jsonbean val,String type){
        System.out.println("val"+val);
        this.type = type;
        String des;
        des = " There is "+ val.num +" people(s).\n";
        if(val.angry!=0){
            des +=val.angry+ " people(s) is angry.";
        }
        if(val.surprise!=0){
            des +=val.surprise+ " people(s) is surprise.";
        }
        if(val.neutral!=0){
            des +=val.neutral+ " people(s) is neutral.";
        }
        if(val.sad!=0){
            des +=val.sad+ " people(s) is sad.";
        }
        if(val.disgust!=0){
            des +=val.disgust+ " people(s) is disgust.";
        }
        if(val.fear!=0){
            des +=val.fear+ " people(s) is fear.";
        }
        if(val.happy!=0){
            des +=val.happy+ " people(s) is happy.";
        }
        Log.d("emotion score is ",""+val.emotion);
        this.emotion = (val.emotion> 0? R.drawable.happy:R.drawable.unhappy);
        this.OrignImagePath= Environment.getExternalStorageDirectory()+"/ComPress/"+val.url.substring(val.url.lastIndexOf('/')+1);
        this.picPath = val.url;
        this.description = des;
    }

}
