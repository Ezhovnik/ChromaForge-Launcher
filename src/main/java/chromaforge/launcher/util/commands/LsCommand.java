package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.services.CoreService;

public class LsCommand extends Command {
    public LsCommand() {
        this.keyword = "ls";
        this.args = "";
        this.help = "list installed engine versions";
    }

    @Override
    public void execute(String[] args) {
        List<String> installed = CoreService.listInstalled();
        if (installed.isEmpty()) {
            System.out.println("No installed versions found");
        } else {
            System.out.println("Installed versions:");
            for (String v : installed) {
                System.out.println("  " + v);
            }
        }
    }
}
