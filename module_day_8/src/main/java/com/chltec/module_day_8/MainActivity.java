package com.chltec.module_day_8;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.rairmmd.andsqlite.AndSQLiteInstance;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String imageUrl = "http://b-ssl.duitang.com/uploads/blog/201409/06/20140906090448_tfauZ.jpeg";
    private ImageView imageView;
    private String imageUrl1 = "http://b-ssl.duitang.com/uploads/item/201507/04/20150704145038_zMX5a.jpeg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_show).setOnClickListener(this);

//        MySqliteHelper sqliteHelper = new MySqliteHelper(this, "my.db", null, 1);
//        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
//        database.execSQL("insert into my_table values (0,'张三', 'ewfafef', 'feawfawef');");
        AndSQLiteInstance.init(this, "my.db", false);

        Student student = new Student();
        for (int i = 0; i < 1000; i++) {
            student.setName("李四").setAge(i);
            AndSQLiteInstance.getInstance().save(student);
        }


//        Student student = new Student().setName("李四").setAge(10);
//        AndSQLiteInstance.getInstance().save(student);

//        ArrayList<Student> students = AndSQLiteInstance.getInstance().query(Student.class);
//        ArrayList<Student> students1 = AndSQLiteInstance.getInstance().query(new QueryBuilder<>(Student.class)
//                .whereGreaterThan("age", 10).appendOrderDescBy("age"));
//        AndSQLiteInstance.getInstance().update();
//        AndSQLiteInstance.getInstance().deleteDatabase();
    }


    private void doDownload() {
        try {
            URL url = new URL(imageUrl);
//            data/data/包名/cache/
//            File cacheDir = getCacheDir();
            //data/data/包名/files/

            File sdcard = Environment.getExternalStorageDirectory();
            File yoju = new File(sdcard, "yoju1");
            yoju.mkdirs();

//            File filesDir = getFilesDir();
            final File imageFile = new File(yoju, "image.jpg");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            if (httpURLConnection.getResponseCode() == 200) {
                InputStream is = httpURLConnection.getInputStream();
                FileOutputStream fos = new FileOutputStream(imageFile);
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) != -1) {
                    fos.write(bytes, 0, length);
                }
                fos.flush();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                        imageView.setImageBitmap(bitmap);
                    }
                });
            } else {
                Toast.makeText(this, "连接图片资源失败", Toast.LENGTH_SHORT).show();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
        }
    }


    private void downloadImage() {
        imageUrl1 = "http://b-ssl.duitang.com/uploads/item/201507/04/20150704145038_zMX5a.jpeg";

        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        Request request = new Request.Builder().url(imageUrl1)
                .addHeader("token", "efewfwefweafawef").build();
        Call call = okHttpClient.newCall(request);
//        Response response = call.execute();
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("请求成功");
                if (response.isSuccessful()) {
                    long current = 0;
                    File sdcard = Environment.getExternalStorageDirectory();
                    File yoju = new File(sdcard, "yoju1");
                    yoju.mkdirs();
                    final File imageFile = new File(yoju, "image.jpg");

                    BufferedSink sink = Okio.buffer(Okio.sink(imageFile));
                    Buffer buffer = sink.buffer();
                    long total = response.body().contentLength();
                    long len;
                    int bufferSize = 200 * 1024; //200kb
                    BufferedSource source = response.body().source();
                    while ((len = source.read(buffer, bufferSize)) != -1) {
                        current += len;
                        int progress = ((int) ((current * 100 / total)));
                        System.out.println("进度：" + progress);
                    }
                    source.close();
                    sink.close();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("请求失败：" + e.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        doDownload();
//                    }
//                });
//                thread.start();
                downloadImage();
                break;
            case R.id.btn_show:
//                File sdcard = Environment.getExternalStorageDirectory();
//                File yoju = new File(sdcard, "yoju1");
//                final File imageFile = new File(yoju, "image.jpg");
//                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getPath());
//                imageView.setImageBitmap(bitmap);

                Picasso.get().load(imageUrl1).placeholder(R.mipmap.ic_launcher).into(imageView);

                break;
            default:
                break;
        }
    }
}
