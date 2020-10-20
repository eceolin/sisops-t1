import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program extends BaseMemoryControl {

    private static final String DEFAULT_FOLDER = "programs/";
    private static final Pattern PATTERN = Pattern.compile("(\\S+\\s\\S+)");

    public String fileName;
    private boolean debugMode;

    public int totalInstructions = 0;
    public int totalVariables = 0;
    public int totalLabels = 0;

    public List<InstructionArgument> memory = new ArrayList<>();
    private List<InstructionArgument> labels = new ArrayList<>();


    public Program(String filename, boolean debugMode) {
        this.fileName = filename;
        this.debugMode = debugMode;
        try {
            loadFile();
        } catch (Exception e) {
            System.out.println("Não foi possível ler o programa '" + filename + "'. " + e.getMessage());
        }
    }

    private void loadFile() throws Exception {
        InputStream in = Program.class
                .getClassLoader()
                .getResourceAsStream(DEFAULT_FOLDER.concat(fileName));

        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        int i = 0;
        int cont = 0;

        while (br.ready()) {
            String linha = br.readLine();

            if (!linha.isBlank()) {
                if (linha.equals(".code")) {
                    firstInstructionAddress = cont;
                    continue;
                } else if (linha.equals(".data")) {
                    firstVariableAddress = cont;
                    continue;
                } else if (linha.equals(".endcode")) {
                    totalInstructions = i;
                    lastInstructionAddress = cont;
                    i = 0;
                } else if (linha.equals(".enddata")) {
                    totalVariables = i;
                    lastVariableAddress = cont;
                    i = 0;
                } else {

                    //linha contém label
                    if (linha.contains(":")) {
                        totalLabels++;
                        String[] args = linha.split(":");

                        Matcher m = PATTERN.matcher(args[1]);
                        m.find();
                        String[] group = m.group().split(" ");

                        labels.add(new InstructionArgument(args[0].trim(), String.valueOf(i).trim()));
                        memory.add(new InstructionArgument(group[0].trim(), group[1].trim()));
                    } else {
                        Matcher m = PATTERN.matcher(linha);
                        m.find();
                        String[] group = m.group().split(" ");
                        memory.add(new InstructionArgument(group[0].trim(), group[1].trim()));
                    }

                    cont++;
                    i++;
                }
            }
        }

        firstLabelAddress = memory.size();
        lastLabelAddress = memory.size() + labels.size();

        memory.addAll(labels);

        if (debugMode) {
            printDebugValues();
        }

        br.close();
    }

    private void printDebugValues() {
        System.out.println("==========================================================================");
        System.out.println("Memória");
        System.out.println("==========================================================================");

        String format = "%-15s%-30s%s%n";
        System.out.printf(format, "Posição", "Instrução", "Argumento");

        for (int i = 0; i < memory.size(); i++) {
            System.out.printf(format, i, memory.get(i).instruction, memory.get(i).argument);
        }

        System.out.println("==========================================================================");
        System.out.println("==========================================================================");
        System.out.println("Contadores");
        System.out.println("==========================================================================");

        System.out.println("Total de instruções: " + totalInstructions);
        System.out.println("Total de variáveis: " + totalVariables);
        System.out.println("Total de marcações: " + totalLabels);

        System.out.println("==========================================================================");
        System.out.println("==========================================================================");
        System.out.println("Posições");
        System.out.println("==========================================================================");

        System.out.println("Posição de memória da primeira variável: " + firstVariableAddress);
        System.out.println("Posição de memória da última variável: " + lastVariableAddress);
        System.out.println("Posição de memória da primeira instrução: " + firstInstructionAddress);
        System.out.println("Posição de memória da última instrução: " + lastInstructionAddress);
        System.out.println("Posição de memória da primeira marcação: " + firstLabelAddress);
        System.out.println("Posição de memória da última marcação: " + lastLabelAddress);
        System.out.println("");
    }
}

class InstructionArgument {
    public String instruction;
    public String argument;

    public InstructionArgument(String instruction, String argument) {
        this.instruction = instruction;
        this.argument = argument;
    }
}
