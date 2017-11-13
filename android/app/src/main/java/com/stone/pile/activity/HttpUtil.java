package com.stone.pile.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.StateSet;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.reflect.TypeToken;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.jar.Attributes;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.http.HTTP;
import rx.Observable;
import rx.Subscriber;

class HttpUtil {
    public int number=9,emonum = 9;
    public HashMap<String,String> oklist = new HashMap<>();
    public HashMap<String,jsonbean> emolist = new HashMap<>();
    public long time;
    public Handler mhandler;
    public HttpUtil(Handler m){
        mhandler = m;
        time = System.currentTimeMillis();
    }
    public String ip ="http://10.129.218.8";
    public void getemotion(final String imgpath,final String url){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS)
                .build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        System.out.println("emointo_upload"+imgpath);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File f = new File(imgpath);
        if (f != null) {
            System.out.println("into emo"+f.getAbsolutePath());
            builder.addFormDataPart("image","emo"+f.getName(),RequestBody.create(MEDIA_TYPE_PNG, f));
        }
        MultipartBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("error in get emotion");
                emonum--;

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                java.lang.reflect.Type type = new TypeToken<base>() {}.getType();
                String res =response.body().string();
                System.out.println(res);
                base jsonBean = gson.fromJson(res, type);
                jsonbean ans = new jsonbean(jsonBean);
                System.out.println("aaaaaaa"+ans);
                emolist.put(imgpath,ans);
                emonum --;
                System.out.println("emo success"+number+" "+emonum);
                if(number==0 && emonum==0){
                    Message msg =new Message();
                    msg.what = 0;
                    for (Map.Entry<String, String> entry : oklist.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        emolist.get(key).url = value;
                    }
                    msg.obj = emolist;
                    mhandler.sendMessage(msg);
                }
            }
        });
    }
    public void post_UpLoadIMG(final String imgpath, String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(500, TimeUnit.SECONDS)
                .build();
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        System.out.println("into_upload"+imgpath);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        File f = new File(imgpath);
        final String outpath = f.getPath();
        if (f != null) {
            builder.addFormDataPart("image","img"+f.getName(),RequestBody.create(MEDIA_TYPE_PNG, f));
        }
        System.out.print("upload:f==null"+(f==null));
        MultipartBody requestBody = builder.build();
        //构建请求
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                number--;
                System.out.println("detail error"+number+" " +emonum);
                if(number==0 && emonum==0){
                    Message msg =new Message();
                    msg.what = 0;
                    for (Map.Entry<String, String> entry : oklist.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        emolist.get(key).url = value;
                    }
                    msg.obj = emolist;
                    mhandler.sendMessage(msg);
                }
                System.out.println("error"+number+" "+emonum);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File appDir = new File(Environment.getExternalStorageDirectory(), "RESULT");
                    if (!appDir.exists()) {
                        appDir.mkdir();
                    }
                    String fileName = imgpath.substring(imgpath.lastIndexOf('/'));
                    System.out.println("Util"+fileName);
                    File file = new File(appDir, fileName);
                    if(file.exists()){
                        number--;
                        oklist.put(imgpath,file.getAbsolutePath());
                        System.out.println("upload:success and exist"+number+" "+emonum);
                        if(number==0 && emonum==0){
                            Message msg =new Message();
                            msg.what = 0;
                            for (Map.Entry<String, String> entry : oklist.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                emolist.get(key).url = value;
                            }
                            msg.obj = emolist;
                            mhandler.sendMessage(msg);
                        }
                        return;
                    }
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                    oklist.put(imgpath,file.getAbsolutePath());
                    number--;
                    if(number==0 && emonum==0){
                        Message msg =new Message();
                        msg.what = 0;
                        for (Map.Entry<String, String> entry : oklist.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            emolist.get(key).url = value;
                        }
                        msg.obj = emolist;
                        mhandler.sendMessage(msg);
                    }
                    System.out.println("upload:success"+number+" "+emonum);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void processdata(ArrayList<String> Namelist){
        emonum = number = Namelist.size();
        for(int i=0;i<Namelist.size();i++){
            String name = Namelist.get(i);
            String fileName = Namelist.get(i).substring(Namelist.get(i).lastIndexOf('/')+1);
            File appDir = new File(Environment.getExternalStorageDirectory(), "ComPress");
            if(!appDir.exists()){
                appDir.mkdir();
            }
            String fname = Environment.getExternalStorageDirectory()+"/ComPress/"+fileName;
            Compress(name,fname);
        }

    }
    public void Compress(String inputname,final String outname) {
        try {
            final InputStream is = new FileInputStream(new File(inputname));
            File outfile = new File(outname);
            System.out.println("compress:"+outfile.getAbsolutePath());
            if(outfile.exists()){
                System.out.println("compress:exist"+outfile.getAbsolutePath());
                post_UpLoadIMG(outfile.getAbsolutePath(),ip+":8888/update/");
                getemotion(outfile.getAbsolutePath(),ip+":8888/update_res/");
                return ;
            }
            FileOutputStream fos = new FileOutputStream(outfile);
            byte[] buffer = new byte[4096];
            int len = -1;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            is.close();

            final BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap originBitmap = BitmapFactory.decodeFile(outfile.getAbsolutePath(), options);
            Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
            compressOptions.outfile=outname;
            Tiny.getInstance().source(outfile).asFile().withOptions(compressOptions).compress(new FileCallback() {
                @Override
                public void callback(boolean isSuccess, String outfile, Throwable t) {
                    if (!isSuccess) {
                        System.out.print("Compress:error");
                        return;
                    }
                    System.out.print("Compress success:"+outfile);
                    File file = new File(outfile);
                    System.out.println("Compress:success"+file.getAbsolutePath());
                    post_UpLoadIMG(outname,ip+":8888/update/");
                    getemotion(outname,ip+":8888/update_res/");

                }
            });
            System.out.println("Compress may finish:"+outfile);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
//    public static void main(String[] args){
//        HttpUtil.post_UpLoadIMG("C:\\Users\\admin\\Desktop\\face_classification-master\\images\\7.jpg","http://127.0.0.1:8888/update/");
//    }
}