package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.io.LauncherPaths;
import chromaforge.launcher.services.InstanceService;
import chromaforge.launcher.util.ConsoleUtils;
import chromaforge.launcher.util.ExitCode;
import chromaforge.launcher.util.InstanceInfo;

public class InstancesCommand extends Command {

    public InstancesCommand() {
        this.keyword = "instances";
        this.args = "";
        this.help = "list existing instances";
    }

    @Override
    public ExitCode execute(String[] args, LauncherPaths paths) {
        List<InstanceInfo> instances = InstanceService.list(paths);
        if (instances.isEmpty()) {
            ConsoleUtils.warn("No instances found");
            ConsoleUtils.tip("  Use 'launcher new <name> <version>' to create a new instance");
            return ExitCode.SUCCESS;
        }

        System.out.println("Instances " + ConsoleUtils.ConsoleColor.DIM + "(" + instances.size() + ")" + ConsoleUtils.ConsoleColor.RESET);
        for (InstanceInfo inst : instances) {
            System.out.println("  " + ConsoleUtils.ConsoleColor.GREEN + ConsoleUtils.ConsoleSymbols.CHECK + ConsoleUtils.ConsoleColor.RESET
                + " " + ConsoleUtils.ConsoleColor.BOLD + inst.name() + ConsoleUtils.ConsoleColor.RESET
                + " " + ConsoleUtils.ConsoleColor.DIM + "(core v" + inst.coreVersion() + ")" + ConsoleUtils.ConsoleColor.RESET);
        }
        return ExitCode.SUCCESS;
    }
}
