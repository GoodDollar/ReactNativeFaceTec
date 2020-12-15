package org.gooddollar.util;

import android.os.Build;
import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class NetworkingHelpers {
    private static OkHttpClient _apiClient = null;
    private static OkHttpClient createApiClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60,TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        // Enabling support for TLSv1.1 and TLSv1.2 on Android 4.4 and below.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(new TLSSocketFactory())
                        .build();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            return client;
        }

        return client;
    }

    public static String OK_HTTP_BUILDER_TAG = "APIRequest";
    public static String OK_HTTP_RESPONSE_CANCELED = "Canceled";

    public static synchronized OkHttpClient getApiClient() {
        if (_apiClient == null) {
            _apiClient = createApiClient();
        }
        return _apiClient;
    }

    /*
     * Cancels all in flight requests.
     */
    static public void cancelPendingRequests() {
        OkHttpClient client = getApiClient();

        // Cancel all queued calls
        for (Call call : client.dispatcher().queuedCalls()) {
            if(Objects.equals(call.request().tag(), OK_HTTP_BUILDER_TAG))
                call.cancel();
        }
        // Cancel all running calls
        for (Call call : client.dispatcher().runningCalls()) {
            if (Objects.equals(call.request().tag(), OK_HTTP_BUILDER_TAG))
                call.cancel();
        }
    }
}

/*
 * Implementation of RequestBody that allows upload progress to be retrieved
 */
class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private Listener listener;

    ProgressRequestBody(RequestBody requestBody, Listener listener) {
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        ProgressStream progressStream = new ProgressStream(sink.outputStream(), contentLength());
        BufferedSink progressSink = Okio.buffer(Okio.sink(progressStream));
        requestBody.writeTo(progressSink);
        progressSink.flush();
    }

    protected final class ProgressStream extends OutputStream {
        private final OutputStream stream;
        private long totalBytes;
        private long bytesSent;

        ProgressStream(OutputStream stream, long totalBytes) {
            this.stream = stream;
            this.totalBytes = totalBytes;
        }

        @Override
        public void write(@NonNull byte[] b, int off, int len) throws IOException {
            this.stream.write(b, off, len);
            if(len < b.length) {
                this.bytesSent += len;
            }
            else {
                this.bytesSent += b.length;
            }
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes);
        }

        @Override
        public void write(int b) throws IOException {
            this.stream.write(b);
            this.bytesSent += 1;
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes);
        }
    }

    interface Listener {
        void onUploadProgressChanged(long bytesWritten, long totalBytes);
    }
}

// A custom networking class is required in order to support 4.4 and below.
class TLSSocketFactory extends SSLSocketFactory {

    private SSLSocketFactory delegate;

    public TLSSocketFactory() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, null);
        delegate = context.getSocketFactory();
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return delegate.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return delegate.getSupportedCipherSuites();
    }

    @Override
    public Socket createSocket() throws IOException {
        return enableTLSOnSocket(delegate.createSocket());
    }

    @Override
    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort));
    }

    private Socket enableTLSOnSocket(Socket socket) {
        if(socket != null && (socket instanceof SSLSocket)) {
            ((SSLSocket)socket).setEnabledProtocols(new String[] {"TLSv1.1", "TLSv1.2"});
        }
        return socket;
    }
}
