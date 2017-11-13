package com.stone.pile.activity;

import java.io.Serializable;

/**
 * Created by admin on 2017/10/28.
 */

public class jsonbean extends base implements Serializable {

    //    emoname = ['angry','disgust','fear','happy','sad','surprise','neutral']
    public String url;
    jsonbean(base res){
        super();
        url = "";
        this.angry = res.angry;
        this.disgust = res.disgust;
        this.happy=res.happy;
        this.emotion = res.emotion;
        this.fear = res.fear;
        this.sad = res.sad;
        this.neutral = res.neutral;
        this.num = res.num;
        this.surprise=res.surprise;
    }

    @Override
    public String toString() {
        return "jsonbean{" +
                "num=" + num +
                ", emotion=" + emotion +
                ", angry=" + angry +
                ", disgust=" + disgust +
                ", fear=" + fear +
                ", happy=" + happy +
                ", sad=" + sad +
                ", surprise=" + surprise +
                ", neutral=" + neutral +
                ", url=" + url +
                '}';
    }
}
class base implements Serializable{
    public int angry;
    public int disgust;
    public int fear;
    public int happy;
    public int sad;
    public int surprise;
    public int neutral;
    public int num;
    public int emotion;
    @Override
    public String toString() {
        return "base{" +
                "angry=" + angry +
                ", disgust=" + disgust +
                ", fear=" + fear +
                ", happy=" + happy +
                ", sad=" + sad +
                ", surprise=" + surprise +
                ", neutral=" + neutral +
                ", num=" + num +
                ", emotion=" + emotion +
                '}';
    }

}