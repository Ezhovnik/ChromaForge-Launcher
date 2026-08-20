package chromaforge.launcher.github;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;
import java.net.URI;

public class GitHubClient {
    private static final String RELEASES_URL = "https://api.github.com/repos/Ezhovnik/ChromaForge-v2/releases";
    private final HttpClient client = HttpClient.newHttpClient();

    public class GitHubClientException extends RuntimeException {
        public GitHubClientException(String message) {
            super(message);
        }
        public GitHubClientException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public String fetchReleases() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RELEASES_URL))
                .header("User-Agent", "ChromaForge-Launcher")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new GitHubClientException("HTTP " + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new GitHubClientException("Network failure", e);
        } catch (InterruptedException e) {
            throw new GitHubClientException("Interrupted", e);
        }
    }
}
