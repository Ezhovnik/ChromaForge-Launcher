package chromaforge.launcher.util.commands;

import java.util.List;

import chromaforge.launcher.util.LauncherVersion;

public class HelpCommand extends Command {
    private List<Command> commands;

    public HelpCommand(List<Command> commands) {
        this.keyword = "help";
        this.args = "";
        this.help = "display this help";

        this.commands = commands;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("ChromaForge-Launcher v" + LauncherVersion.VERSION);

        for (Command cmd : commands) {
            System.out.print(String.format("%-24s", cmd.keyword + " " + cmd.args));
            System.out.println("- " + cmd.help);
        }
    }
}
