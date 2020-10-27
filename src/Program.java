import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    private static final String DEFAULT_FOLDER = "programs/";
    private static final Pattern PATTERN = Pattern.compile("(\\S+\\s\\S+)");

    public String fileName;
    private boolean debugMode;

    public int totalInstructions = 0;
    public int totalVariables = 0;
    public int totalLabels = 0;

    public List<String> memory = new ArrayList<>();
    private List<String> labels = new ArrayList<>();


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

        boolean isVariable = false;

        while (br.ready()) {
            String linha = br.readLine();

            if (!linha.isBlank()) {
                if (linha.equals(".code")) {
                    continue;
                } else if (linha.equals(".data")) {
                    isVariable = true;
                    continue;
                } else if (linha.equals(".endcode")) {
                    totalInstructions = i;
                    i = 0;
                } else if (linha.equals(".enddata")) {
                    totalVariables = i;
                    isVariable = false;
                    i = 0;
                } else {

                    //linha contém label
                    if (linha.contains(":")) {
                        totalLabels++;
                        String[] args = linha.split(":");


                        labels.add("LABEL " + args[0].trim() + " " + i);

                        if (args.length > 1) {
                            Matcher m = PATTERN.matcher(args[1]);
                            m.find();
                            memory.add(m.group());
                        } else {
                            i--;
                        }


                    } else {
                        String variable = isVariable ? "variable " : "";

                        Matcher m = PATTERN.matcher(linha);
                        m.find();
                        memory.add(variable + m.group());
                    }

                    i++;
                }
            }
        }

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

        String format = "%-15s%-30s%n";
        System.out.printf(format, "Posição", "Instrução");

        for (int i = 0; i < memory.size(); i++) {
            System.out.printf(format, i, memory.get(i));
        }

        System.out.println("==========================================================================");
        System.out.println("==========================================================================");
        System.out.println("Contadores");
        System.out.println("==========================================================================");

        System.out.println("Total de instruções: " + totalInstructions);
        System.out.println("Total de variáveis: " + totalVariables);
        System.out.println("Total de marcações: " + totalLabels);

        System.out.println("");
    }
}
