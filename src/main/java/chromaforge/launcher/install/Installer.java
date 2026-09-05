package chromaforge.launcher.install;

import java.nio.file.Path;

import chromaforge.launcher.github.AssetInfo;
import chromaforge.launcher.interfaces.Progress;

public interface Installer {
    void install(AssetInfo asset, Path installDir);

    default Progress consoleProgress() {
        return (done, total) -> {
            int percent = (int) (done * 100.0 / total);
            int barLength = 50;
            int filled = (int) (done * barLength / total);

            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < barLength; i++) {
                bar.append(i < filled ? "=" : "-");
            }
            bar.append("] ").append(percent).append("%");

            System.out.print("\r" + bar);
            System.out.flush();

            if (done == total) {
                System.out.println();
            }
        };
    }
}
