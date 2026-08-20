package chromaforge.launcher.interfaces;

@FunctionalInterface
public interface Progress {
    void onProgress(long done, long total);
}
