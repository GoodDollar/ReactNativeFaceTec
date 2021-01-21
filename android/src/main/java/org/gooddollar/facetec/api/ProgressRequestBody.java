package org.gooddollar.facetec.api;

import androidx.annotation.NonNull;
import java.io.IOException;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;

/*
 * Implementation of RequestBody that allows upload progress to be retrieved
 */
public class ProgressRequestBody extends RequestBody {
    private final RequestBody requestBody;
    private Listener listener;

    public ProgressRequestBody(RequestBody requestBody, Listener listener) {
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

    public interface Listener {
        void onUploadProgressChanged(long bytesWritten, long totalBytes);
    }
}
