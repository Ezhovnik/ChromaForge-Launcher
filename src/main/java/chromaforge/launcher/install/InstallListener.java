package chromaforge.launcher.install;

import chromaforge.launcher.interfaces.Progress;

public interface InstallListener {
    default void onStatus(String status) {
    }

    default Progress progress() {
        return null;
    }
}