package com.example.zjf.retrofitrxjavarequestdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
*@description `Retrofit + RxJava` 实现 网络请求 的功能
 *              实现功能：将中文翻译成英文 - > 显示到界面
                实现方案：采用Get方法对 金山词霸API 发送网络请求
                    先切换到工作线程 发送网络请求
                    再切换到主线程进行 UI更新

                步骤说明:
                    添加依赖
                    创建 接收服务器返回数据 的类 --- Translation.java
                    创建 用于描述网络请求 的接口（区别于传统形式） --- GetRequest_Interface.java
                    创建 Retrofit 实例
                    创建 网络请求接口实例 并 配置网络请求参数（区别于传统形式）
                    发送网络请求（区别于传统形式）
                    发送网络请求
                    对返回的数据进行处理
*
*@author zjf
*@date 2018/10/23 19:46
*/
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //步骤4：创建Retrofit对象
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/")// 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create())//设置使用Gson解析(记得加入依赖)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())// 支持RxJava
                .build();

        // 步骤5：创建 网络请求接口 的实例
        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

        // 步骤6：采用Observable<...>形式 对 网络请求 进行封装
        Observable<Translation> observable = request.getCall();
        // 步骤7：发送网络请求
        observable.subscribeOn(Schedulers.io())    // 在IO线程进行网络请求
                .observeOn(AndroidSchedulers.mainThread())    // 回到主线程 处理请求结果
                .subscribe(new Observer<Translation>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }

                    @Override
                    public void onNext(Translation value) {
                        value.show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "请求失败");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "请求成功");
                    }
                });
    }
}
