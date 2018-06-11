package com.smart.common.http;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.smart.common.util.DebugUtil;
import com.smart.common.util.Utils;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpManger {

    private static OkHttpClient mOkHttpClient;
    private Handler mMainHandler;

    private HttpManger() {
        initOkHttpClient();
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    private static class Holder {
        static final HttpManger INSTANCE = new HttpManger();
    }

    public static HttpManger getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 初始化okttpClient
     */
    private void initOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        String netCachePath = Utils.getAppNetCache();
        File cacheFile = new File(netCachePath);
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 50);
        Interceptor cacheInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                // todo remove log
                DebugUtil.d("initOkHttpClient", request.url().toString());
                if (!Utils.isNetConnected(Utils.getApplication())) {
                    request = request.newBuilder()
                            .cacheControl(CacheControl.FORCE_CACHE)
                            .build();
                }
                Response response = chain.proceed(request);
                Response.Builder newBuilder = response.newBuilder();
                if (Utils.isNetConnected(Utils.getApplication())) {
                    int maxAge = 0;
                    // 有网络时 设置缓存超时时间0个小时
                    newBuilder.header("Cache-Control", "public, max-age=" + maxAge);
                } else {
                    // 无网络时，设置超时为4周
                    int maxStale = 60 * 60 * 24 * 28;
                    newBuilder.header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale);
                }
                return newBuilder.build();
            }
        };
        builder.cache(cache)
                .addInterceptor(cacheInterceptor);
        //设置超时
        builder.connectTimeout(15, TimeUnit.SECONDS);
        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        //错误重连
        builder.retryOnConnectionFailure(true);
        mOkHttpClient = builder.build();
    }

    /**
     * 获取okttpClient
     */
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * 同步的Get请求
     *
     * @param url url
     * @return Response
     */
    public Response getSync(String url) throws IOException {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = mOkHttpClient.newCall(request);
        return call.execute();
    }

    /**
     * 同步的Get请求
     *
     * @param url url
     * @return 字符串
     */
    public String getSyncString(String url) throws IOException {
        Response execute = getSync(url);
        if (execute.isSuccessful()) {
            ResponseBody body = execute.body();
            if (body != null) {
                return body.string();
            }
        }
        return null;
    }


    /**
     * 异步的get请求
     *
     * @param url      url
     * @param callback callback
     */
    public void getAsync(String url, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        deliveryResult(callback, request);
    }


    /**
     * 同步的Post请求
     *
     * @param url    url
     * @param params post的参数
     * @return response
     */
    public Response postSync(String url, Param... params) throws IOException {
        Request request = buildPostRequest(url, params);
        return mOkHttpClient.newCall(request).execute();
    }


    /**
     * 同步的Post请求
     *
     * @param url    url
     * @param params post的参数
     * @return 字符串
     */
    public String postSyncString(String url, Param... params) throws IOException {
        Response response = postSync(url, params);
        ResponseBody body = response.body();
        if (body != null) {
            return body.string();
        }
        return null;
    }

    /**
     * 异步的post请求
     *
     * @param url      url
     * @param callback callback
     * @param params   请求参数
     */
    public void postAsync(String url, final ResultCallback callback, Param... params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步的post请求
     *
     * @param url      url
     * @param callback callback
     * @param params   请求参数
     */
    public void postAsync(String url, final ResultCallback callback, Map<String, String> params) {
        Param[] paramsArr = map2Params(params);
        Request request = buildPostRequest(url, paramsArr);
        deliveryResult(callback, request);
    }

    /**
     * 同步 基于post的文件上传
     *
     * @param url      url
     * @param files    files
     * @param fileKeys fileKeys
     * @param params   请求参数
     * @return Response
     * @throws IOException IOException
     */
    public Response postFileSync(String url, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步 基于post的文件上传
     *
     * @param url     url
     * @param file    file
     * @param fileKey fileKey
     * @return Response
     * @throws IOException IOException
     */
    public Response postFileSync(String url, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 同步 基于post的文件上传
     *
     * @param url     url
     * @param file    file
     * @param fileKey fileKey
     * @param params  请求参数
     * @return Response
     * @throws IOException IOException
     */
    public Response postFileSync(String url, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步基于post的文件上传
     *
     * @param url      url
     * @param callback callback
     * @param params   请求参数
     * @param files    files
     * @param fileKeys fileKeys
     * @throws IOException IOException
     */
    public void postFileAsync(String url, ResultCallback callback, File[] files, String[] fileKeys, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, files, fileKeys, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件不带参数上传
     *
     * @param url      url
     * @param callback callback
     * @param file     file
     * @param fileKey  fileKey
     * @throws IOException IOException
     */
    public void postFileAsync(String url, ResultCallback callback, File file, String fileKey) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, null);
        deliveryResult(callback, request);
    }

    /**
     * 异步基于post的文件上传，单文件且携带其他form参数上传
     *
     * @param url      url
     * @param callback callback
     * @param file     file
     * @param fileKey  fileKey
     * @param params   请求参数
     * @throws IOException IOException
     */
    public void postFileAsync(String url, ResultCallback callback, File file, String fileKey, Param... params) throws IOException {
        Request request = buildMultipartFormRequest(url, new File[]{file}, new String[]{fileKey}, params);
        deliveryResult(callback, request);
    }

    /**
     * 异步下载文件
     *
     * @param url         url
     * @param destFileDir 本地文件存储的文件夹
     * @param callback    callback
     */
    public void downloadAsync(final String url, final String destFileDir, final ResultCallback callback) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        final Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                sendFailedStringCallback(e, callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len;
                FileOutputStream fos = null;
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        is = body.byteStream();
                        File file = new File(destFileDir, getFileName(url));
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        //如果下载文件成功，第一个参数为文件的绝对路径
                        sendSuccessResultCallback(file.getAbsolutePath(), callback);
                    }
                } catch (IOException e) {
                    sendFailedStringCallback(e, callback);
                } finally {
                    close(is);
                    close(fos);
                }
            }
        });
    }

    /**
     * 获取文件名
     *
     * @param path 路径
     * @return 文件名
     */
    private String getFileName(String path) {
        int separatorIndex = path.lastIndexOf("/");
        return (separatorIndex < 0) ? path : path.substring(separatorIndex + 1, path.length());
    }

    /**
     * 创建多文件表单请求
     *
     * @param url      url
     * @param files    files
     * @param fileKeys fileKeys
     * @param params   params
     * @return Request
     */
    private Request buildMultipartFormRequest(String url, File[] files,
                                              String[] fileKeys, Param[] params) {
        params = validateParam(params);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        for (Param param : params) {
            builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + param.key + "\""),
                    RequestBody.create(null, param.value));
        }
        if (files != null) {
            RequestBody fileBody = null;
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                builder.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + fileKeys[i] + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 猜测文件Mime类型
     *
     * @param path path
     * @return Mime类型
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }

    /**
     * 为防止参数为空 验证参数是否为空为空则创建一个参数数组
     *
     * @param params 需要验证的参数
     * @return 验证后的参数
     */
    private Param[] validateParam(Param[] params) {
        if (params == null) {
            return new Param[0];
        } else {
            return params;
        }
    }

    /**
     * 把map参数转换成param数组
     *
     * @param params map参数
     * @return param数组
     */
    private Param[] map2Params(Map<String, String> params) {
        if (params == null) {
            return new Param[0];
        }
        int size = params.size();
        Param[] res = new Param[size];
        Set<Map.Entry<String, String>> entries = params.entrySet();
        int i = 0;
        for (Map.Entry<String, String> entry : entries) {
            res[i++] = new Param(entry.getKey(), entry.getValue());
        }
        return res;
    }

    /**
     * 把请求到的数据使用handler回调到主线程运行
     *
     * @param callback callback
     * @param request  request
     */
    private void deliveryResult(final ResultCallback callback, final Request request) {
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                sendFailedStringCallback(e, callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    ResponseBody body = response.body();
                    if (body != null) {
                        final String string = body.string();
                        sendSuccessResultCallback(string, callback);
                    } else {
                        sendFailedStringCallback(new IllegalArgumentException("ResponseBody is Empty !"), callback);
                    }
                } catch (IOException e) {
                    sendFailedStringCallback(e, callback);
                }

            }
        });
    }

    /**
     * 在主线程中处理onError回调
     *
     * @param e        Exception
     * @param callback callback
     */
    private void sendFailedStringCallback(final Exception e, final ResultCallback callback) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * 在主线程中处理onSuccess回调
     *
     * @param response response
     * @param callback callback
     */
    private void sendSuccessResultCallback(final String response, final ResultCallback callback) {
        mMainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onResponse(response);
                }
            }
        });
    }

    /**
     * 创建post请求
     *
     * @param url    url
     * @param params 参数
     * @return 创建号好的post请求
     */
    private Request buildPostRequest(String url, Param... params) {
        if (params == null) {
            params = new Param[0];
        }
        FormBody.Builder builder = new FormBody.Builder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
    }

    /**
     * 请求参数holder
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public interface ResultCallback {

        /**
         * 请求失败
         *
         * @param errorMsg 错误信息
         */
        void onError(String errorMsg);

        /**
         * 请求成功
         *
         * @param response 返回的数据
         */
        void onResponse(String response);

    }

    /**
     * 关闭流
     *
     * @param closeable 需要关闭的流
     */
    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
