
package com.bot.wattanachaitodolist.infra.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.function.Function;

/**
 * <p>HTTP request execution<p>
 */
public final class Client {

    /**
     * <p>execute HTTP requests</p>
     */
    public static <T, R> R getClient(
            final String url,
            final Class<T> service,
            final Function<T, Call<R>> function) {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        T t = retrofit.create(service);
        Call<R> call = function.apply(t);
        try {
            return call.execute().body();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
