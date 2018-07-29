package com.qicode.kakaxicm.rxjavademo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Rxjava";


    Integer i = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1.创建被观察者，生产事件
        final Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            //2.订阅的时候发送事件
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);

//                emitter.onError(new Throwable("haha"));
                emitter.onComplete();//onComplete事件发送后，后面的所有事件无效,且后面不能发送错误事件
                emitter.onNext(3);

            }
        });
        //3.定义观察者
        Observer<Integer> observer = new Observer<Integer>() {
            // 1. 定义Disposable类变量
            private Disposable mDisposable;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "开始采用subscribe连接");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "对Next事件" + value + "作出响应");
                //Disposable 可以断开观察关系
//                if(value == 2){
//                    mDisposable.dispose();
//                    Log.e(TAG, "已经切断了连接：" + mDisposable.isDisposed());
//                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "对Complete事件作出响应");
            }
        };
        //4建立联系
        observable.subscribe(observer);

        /**
         * 1.创建操作符
         */
        //1.被观察者创建操作符，上面是create
        //2.just:快速创建1个被观察者对象（Observable),直接发送 传入的事件
        Log.e(TAG, "just操作符");
        Observable.just(1, 2, 3, 4).subscribe(observer);
        //3.fromArray从数组直接创建 直接发送 传入的数组数据
        Log.e(TAG, "fromArray操作符");
        Integer[] items = {0, 1, 2, 3, 4};
        Observable.fromArray(items).subscribe(observer);
        //4.fromIterable 从集合直接创建 直接发送集合数据
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Observable.fromIterable(list).subscribe(observer);
        /**
         * 5下列方法一般用于测试使用

         <-- empty()  -->
         // 该方法创建的被观察者对象发送事件的特点：仅发送Complete事件，直接通知完成
         Observable observable1=Observable.empty();
         // 即观察者接收后会直接调用onCompleted（）

         <-- error()  -->
         // 该方法创建的被观察者对象发送事件的特点：仅发送Error事件，直接通知异常
         // 可自定义异常
         Observable observable2=Observable.error(new RuntimeException())
         // 即观察者接收后会直接调用onError（）

         <-- never()  -->
         //该方法创建的被观察者对象发送事件的特点：不发送任何事件
         Observable observable3=Observable.never();
         //即观察者接收后什么都不调用
         */
        Observable.empty().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.e(TAG, "empty操作符 accept");
            }
        });
        //6.延迟创建

        /**
         * defer(): 直到有观察者订阅时，才动态创建被观察者对象 & 发送事件
         *  // 2. 通过defer 定义被观察者对象,此时被观察者对象还没创建
         */
        Observable<Integer> deferObservable = Observable.defer(new Callable<ObservableSource<? extends Integer>>() {
            @Override
            public ObservableSource<? extends Integer> call() throws Exception {
                return Observable.just(i);
            }
        });
        //第二次对i赋值
        i = 15;
        //此时，有订阅者订阅时，才会调用defer（）创建被观察者对象（Observable）
        deferObservable.subscribe(new Observer<Integer>() {

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Integer value) {
                //接收的数据是第二次的数据15
                Log.e(TAG, "接收到的整数是" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "对Complete事件作出响应");
            }
        });

        /**
         * timer(),延迟时间发送事件
         */
        //延迟2s后，发送一个long类型数值
        Observable.timer(2, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "开始采用subscribe连接");
            }

            @Override
            public void onNext(Long value) {
                Log.e(TAG, "接收到了事件" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "对Error事件作出响应");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "对Complete事件作出响应");
            }

        });
        /**
         * interval是每隔一定时间发送事件
         */
        // 参数说明：
        // 参数1 = 第1次延迟时间；
        // 参数2 = 间隔时间数字；
        // 参数3 = 时间单位；
//        Observable.interval(3, 1, TimeUnit.SECONDS)
//                // 该例子发送的事件序列特点：延迟3s后发送事件，每隔1秒产生1个数字（从0开始递增1，无限个）
//                .subscribe(new Observer<Long>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//                        Log.d(TAG, "开始采用subscribe连接");
//                    }
//                    // 默认最先调用复写的 onSubscribe（）
//
//                    @Override
//                    public void onNext(Long value) {
//                        Log.d(TAG, "接收到了事件" + value);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d(TAG, "对Error事件作出响应");
//                    }
//
//                    @Override
//                    public void onComplete() {
//                        Log.d(TAG, "对Complete事件作出响应");
//                    }
//
//                });

        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 参数3 = 第1次事件延迟发送时间；
        // 参数4 = 间隔时间数字；
        // 参数5 = 时间单位
        Observable.intervalRange(3, 10, 2, 1, TimeUnit.SECONDS)
                // 该例子发送的事件序列特点：
                // 1. 从3开始，一共发送10个事件；
                // 2. 第1次延迟2s发送，之后每隔2秒产生1个数字（从0开始递增1，无限个）
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Long value) {
                        Log.d(TAG, "接收到了事件" + value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });
        /**
         * range（）发送事件的特点：连续发送 1个事件序列，可指定范围
         */
        // 参数说明：
        // 参数1 = 事件序列起始点；
        // 参数2 = 事件数量；
        // 注：若设置为负数，则会抛出异常
        Observable.range(3,10)
                // 该例子发送的事件序列特点：从3开始发送，每次发送事件递增1，一共发送10个事件
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "开始采用subscribe连接");
                    }
                    // 默认最先调用复写的 onSubscribe（）

                    @Override
                    public void onNext(Integer value) {
                        Log.d(TAG, "range接收到了事件"+ value  );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "对Error事件作出响应");
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "对Complete事件作出响应");
                    }

                });
    }


}
