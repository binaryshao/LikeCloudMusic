package sbingo.likecloudmusic.network;

import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import sbingo.likecloudmusic.utils.NetUtils;

/**
 * Author: Sbingo
 * Date:   2016/12/12
 */

public enum HttpManager {
    Instance;
    private final String TAG = "OkHttp";

    private static final String baseUrl = "";

    private static volatile OkHttpClient mOkHttpClient;


    HttpManager() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            synchronized (HttpManager.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10, TimeUnit.SECONDS)
                            .writeTimeout(10, TimeUnit.SECONDS)
                            .addInterceptor(mNetInterceptor)
                            .addInterceptor(mLoggingInterceptor)
                            .build();
                }
            }
        }
        return mOkHttpClient;
    }

    private final Interceptor mLoggingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = null;
            String bodyString = null;
            try {
                Request request = chain.request();
                long t1 = System.nanoTime();
                Logger.t(TAG).d(String.format("request -->:%s", request.url().toString()));
                response = chain.proceed(request);
                bodyString = response.body().string();
                if (response.code() == 200) {
                    long t2 = System.nanoTime();
                    Logger.t(TAG).d(String.format(Locale.getDefault(), "response <-- %s in %.1fms",
                            response.request().url(), (t2 - t1) / 1e6d));
                    Logger.t(TAG).json(bodyString);
                } else {
                    Logger.t(TAG).d(String.format("http响应不成功，响应码为：%s", response.code()));
                    throw new RuntimeException("服务器响应不成功(′⌒`)");
                }
            } catch (IOException e) {
                throw new RuntimeException("服务器好像挂了(′⌒`)");
            }
            return response.newBuilder()
                    .body(ResponseBody.create(response.body().contentType(), bodyString))
                    .build();
        }
    };

    private final Interceptor mNetInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            NetUtils.isNetworkConnected();
            return chain.proceed(chain.request());
        }
    };

}
