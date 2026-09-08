package chromaforge.launcher.util.commands;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.InstanceInfo;
import chromaforge.launcher.util.ConsoleUtils;

public class RmiCommand extends Command {

    public RmiCommand() {
        this.keyword = "rmi";
        this.args = "<instance>";
        this.help = "removes the specified instance";
    }

    @Override
    public void execute(String[] args, LauncherPaths paths) {
        String instanceName = nextArg(args);
        System.out.println("Removing '" + instanceName + "'...");

        try {
            InstanceService.remove(new InstanceInfo(instanceName, "0.0.0"), paths);
            ConsoleUtils.success("Instance '" + instanceName + "' removed");
        } catch (Exception e) {
            ConsoleUtils.error("Could not remove '" + instanceName + "' : " + e.getMessage());
        }
    }
}
