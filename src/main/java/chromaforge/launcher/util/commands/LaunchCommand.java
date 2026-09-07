package chromaforge.launcher.util.commands;

import java.nio.file.Path;

import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.InstanceInfo;
import chromaforge.launcher.data.dv.dvObject;
import chromaforge.launcher.data.dv.dvValue;
import chromaforge.launcher.data.dv.dvString;
import chromaforge.launcher.coders.toml.TomlParser;
import chromaforge.launcher.io.FileUtils;
import chromaforge.launcher.io.LauncherPaths;

public class LaunchCommand extends Command {

    public LaunchCommand() {
        this.keyword = "launch";
        this.args = "<instance name>";
        this.help = "runs the specified instance";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String instanceName = nextArg(args);
        Path instanceDir = paths.getInstanceDir(instanceName);
        if (!FileUtils.isDir(instanceDir)) {
            ConsoleUtils.error("Instance with that name does not exist");
            return;
        }

        String coreVersion = "";
        dvValue root = TomlParser.parse(FileUtils.readString(instanceDir.resolve("instance.toml")));
        if (root instanceof dvObject obj) {
            dvValue cv = obj.entries().get("core_version");
            if (cv instanceof dvString s) {
                coreVersion = s.value();
            }
        }

        System.out.println("Launching '" + instanceName + "'...");
        InstanceService.launch(
            new InstanceInfo(instanceName, coreVersion),
            paths
        );
    }
}
