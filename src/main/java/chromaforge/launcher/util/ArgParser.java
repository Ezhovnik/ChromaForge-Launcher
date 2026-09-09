package chromaforge.launcher.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArgParser {

    private static final class ArgC {
        final boolean isOption;
        final String longName;
        final String shortName;
        final String placeholder;
        final String help;

        ArgC(boolean isOption, String longName, String shortName, String placeholder, String help) {
            this.isOption = isOption;
            this.longName = longName;
            this.shortName = shortName;
            this.placeholder = placeholder;
            this.help = help;
        }
    }

    private final List<ArgC> specs = new ArrayList<>();
    private final Map<String, ArgC> aliases = new LinkedHashMap<>();
    private final Map<String, String> parsed = new LinkedHashMap<>();
    private final List<String> positionals = new ArrayList<>();

    public ArgParser flag(String longName, String shortName) {
        return flag(longName, shortName, null);
    }

    public ArgParser flag(String longName, String shortName, String help) {
        add(false, longName, shortName, null, help);
        return this;
    }

    public ArgParser option(String longName, String shortName, String placeholder) {
        return option(longName, shortName, placeholder, null);
    }

    public ArgParser option(String longName, String shortName, String placeholder, String help) {
        add(true, longName, shortName, placeholder, help);
        return this;
    }

    private void add(boolean isOption, String longName, String shortName, String placeholder, String help) {
        ArgC spec = new ArgC(isOption, longName, shortName, placeholder, help);
        specs.add(spec);
        aliases.put(longName, spec);
        if (shortName != null) {
            aliases.put(shortName, spec);
        }
    }

    public void parse(String[] args) {
        parse(args, 0);
    }

    public void parse(String[] args, int start) {
        parsed.clear();
        positionals.clear();
        for (int i = start; i < args.length; ++i) {
            String arg = args[i];
            if (arg.equals("--")) {
                while (++i < args.length) {
                    positionals.add(args[i]);
                }
                return;
            }
            if (arg.startsWith("-") && arg.length() > 1) {
                i = parseNamed(args, i);
            } else {
                positionals.add(arg);
            }
        }
    }

    private int parseNamed(String[] args, int index) {
        String arg = args[index];
        int eq = arg.indexOf('=');
        String name = eq >= 0 ? arg.substring(0, eq) : arg;
        String inline = eq >= 0 ? arg.substring(eq + 1) : null;

        ArgC spec = aliases.get(name);
        if (spec == null) {
            throw new RuntimeException("Unknown option: " + name);
        }

        if (!spec.isOption) {
            if (inline != null) {
                throw new RuntimeException("Flag " + name + " does not take a value");
            }
            parsed.put(spec.longName, "true");
            return index;
        }

        String value = inline;
        if (value == null) {
            if (index + 1 >= args.length || args[index + 1].startsWith("-")) {
                throw new RuntimeException("Missing value for " + name);
            }
            value = args[++index];
        }
        parsed.put(spec.longName, value);
        return index;
    }

    public boolean has(String name) {
        ArgC spec = aliases.get(name);
        return parsed.containsKey(spec != null ? spec.longName : name);
    }

    public String value(String name) {
        ArgC spec = aliases.get(name);
        return parsed.get(spec != null ? spec.longName : name);
    }

    public List<String> positionals() {
        return positionals;
    }

    public String flagsHelp() {
        StringBuilder sb = new StringBuilder();
        for (ArgC spec : specs) {
            String left = spec.longName;
            if (spec.isOption && spec.placeholder != null) {
                left += " " + spec.placeholder;
            }
            if (spec.shortName != null) {
                left = spec.shortName + ", " + left;
            }
            sb.append(String.format("      %-30s", left));
            if (spec.help != null) {
                sb.append(spec.help);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
}
