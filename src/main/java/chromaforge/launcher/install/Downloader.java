package chromaforge.launcher.install;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import javax.net.ssl.SSLException;

import chromaforge.launcher.coders.sha256.Sha256;
import chromaforge.launcher.interfaces.Progress;

public class Downloader {
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration REQUEST_TIMEOUT = Duration.ofMinutes(5);

    private static final int BUFFER_SIZE = 64 * 1024;

    private static final long RETRY_BACKOFF_MS = 500L;

    private final HttpClient client = HttpClient.newBuilder()
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(CONNECT_TIMEOUT)
        .build();

    public Path download(
        String url,
        Path target,
        InstallListener listener,
        String expectedSha256,
        int maxAttempts
    ) throws IOException, InterruptedException {
        IOException lastFailure = null;
        for (int attempt = 1; attempt <= maxAttempts; ++attempt) {
            try {
                return downloadOnce(url, target, listener.progress(), expectedSha256);
            } catch (IOException e) {
                lastFailure = e;
                if (attempt == maxAttempts || !isRetryable(e)) {
                    throw e;
                }
                listener.onStatus("Retry " + (attempt + 1) + "/" + maxAttempts + " (" + e.getMessage() + ")");
                Thread.sleep(RETRY_BACKOFF_MS * attempt);
            }
        }
        throw lastFailure;
    }

    private Path downloadOnce(String url, Path target, Progress progress, String expectedSha256) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("User-Agent", "ChromaForge-Launcher")
            .timeout(REQUEST_TIMEOUT)
            .GET()
            .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        try (InputStream in = response.body()) {
            int status = response.statusCode();
            if (status != 200) {
                String body = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                throw new HttpStatusException(status, body);
            }

            long total = response.headers().firstValueAsLong("Content-Length").orElse(-1);
            long done = 0;
            byte[] buffer = new byte[BUFFER_SIZE];

            try (OutputStream out = Files.newOutputStream(target)) {
                int n;
                while ((n = in.read(buffer)) != -1) {
                    out.write(buffer, 0, n);
                    done += n;
                    if (progress != null) {
                        progress.onProgress(done, total);
                    }
                }
            }

            if (total >= 0 && done != total) {
                throw new IOException("Incomplete download: expected " + total + ", got " + done);
            }
        }

        if (expectedSha256 != null && !expectedSha256.isBlank()) {
            String actual;
            try (InputStream fileIn = Files.newInputStream(target)) {
                actual = Sha256.hash(fileIn);
            }
            if (!actual.equalsIgnoreCase(expectedSha256)) {
                throw new ChecksumMismatchException(url, expectedSha256, actual);
            }
        }

        return target;
    }

    private static boolean isRetryable(IOException e) {
        if (e instanceof HttpStatusException hse) {
            int s = hse.status;
            return s == 408 || s == 429 || (s >= 500 && s < 600);
        }
        if (e instanceof ChecksumMismatchException) {
            return true; 
        }
        if (e instanceof HttpTimeoutException
                || e instanceof SocketException
                || e instanceof SSLException) {
            return true;
        }
        return false;
    }

    static class HttpStatusException extends IOException {
        final int status;

        HttpStatusException(int status, String body) {
            super("HTTP " + status + (body.isBlank() ? "" : ": " + body));
            this.status = status;
        }
    }

    static class ChecksumMismatchException extends IOException {
        final String expected, actual;

        ChecksumMismatchException(String url, String expected, String actual) {
            super("SHA-256 mismatch for " + url + ": expected " + expected + ", got " + actual);
            this.expected = expected;
            this.actual = actual;
        }
    }
}
