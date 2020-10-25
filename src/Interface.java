import java.util.*;

public class Interface {

    public static Comparator<Process> comparatorPriority = Comparator.comparingInt(c -> c.priority);
    public static Comparator<Process> comparatorCode = Comparator.comparingInt(c -> c.code);

    static SortedSet<Process> processes = new TreeSet<>(comparatorPriority.thenComparing(comparatorCode));

    static boolean debugMode = false;
    static Integer scalingType;
    static Integer quantum;

    private static final int SCALING_OPTION = 0;
    private static final int LOAD_PROGRAM_OPTION = 1;
    private static final int RUN_SYSTEM_OPTION = 2;
    private static final int EXIT_OPTION = 3;

    private static final int YES_OPTION = 1;
    private static final int NO_OPTION = 0;

    public static void main(String[] args) throws Exception {
        debugMode = args.length > 0 && args[0].equals("debug");

        carregar();
    }


    private static void carregar() throws Exception {
        int opcao = showInitialMenu();

        while (opcao == SCALING_OPTION) {
            if (scalingType != null) {
                System.out.println("Você não pode alterar a política de escalonamento após a seleção.");
            } else {
                scalingType = showScalingType();

                if (scalingType == CPU.SCALING_ROUND_ROBIN) {
                    showQuantumSelector();
                }
            }

            carregar();
        }

        while (opcao == LOAD_PROGRAM_OPTION) {
            if (scalingType == null) {
                System.out.println("Você precisa selecionar primeiro uma política de escalonamento.");
            } else {
                showLoadMenu();
            }

            carregar();
        }

        if (opcao == RUN_SYSTEM_OPTION) {
            if (processes.size() > 0) {
                startSystem();
            } else {
                System.out.println("Não existem programas. Sistema finalizado!");
                System.exit(0);
            }
        }

        if (opcao == EXIT_OPTION) {
            System.exit(0);
        }
    }

    private static void showQuantumSelector() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o valor da varíavel quantum:");

        quantum = scanner.nextInt();
    }

    private static int showScalingType() {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Escolha a política desejada:");
        System.out.println(CPU.SCALING_PRIORITY + " - Prioridade sem preempção");
        System.out.println(CPU.SCALING_ROUND_ROBIN + " - Round robin com quantum definível");

        System.out.println("Digite a opção desejada:");

        int opcao = scanner.nextInt();

        while (opcao != CPU.SCALING_PRIORITY && opcao != CPU.SCALING_ROUND_ROBIN) {
            System.out.println("Opção inválida.");
            System.out.println("Digite a opção desejada:");
            opcao = scanner.nextInt();
        }

        return opcao;
    }

    private static void startSystem() throws Exception {
        if (scalingType == CPU.SCALING_PRIORITY) {
            new ScaleByPriorityCPU(processes).start();
        }

    }

    private static int showInitialMenu() {

        System.out.println("");
        System.out.println("Política de escalonamento: " + (scalingType != null ? scalingType : "Não selecionada."));
        if (scalingType != null && CPU.SCALING_PRIORITY == scalingType) {
            System.out.println("Programas/Prioridade: ");
            processes.forEach(c -> System.out.println("[Programa = " + c.program.fileName + " | Prioridade = " + c.priority + "]"));
        } else if (scalingType != null && CPU.SCALING_ROUND_ROBIN == scalingType) {
            processes.forEach(c -> System.out.println("[Programa = " + c.program.fileName));
            System.out.println("Quantum = " + quantum);
        }

        System.out.println("");
        System.out.println("Escolha a operação desejada:");
        System.out.println("" + SCALING_OPTION + " - Selecionar política de escalonamento");
        System.out.println("" + LOAD_PROGRAM_OPTION + " - Carregar programas para execução");
        System.out.println("" + RUN_SYSTEM_OPTION + " - Iniciar sistema");
        System.out.println("" + EXIT_OPTION + " - Sair");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite a opção desejada:");

        int opcao = scanner.nextInt();

        while (opcao != SCALING_OPTION && opcao != LOAD_PROGRAM_OPTION && opcao != RUN_SYSTEM_OPTION && opcao != EXIT_OPTION) {
            System.out.println("Opção inválida.");
            System.out.println("Digite a opção desejada:");
            opcao = scanner.nextInt();
        }

        return opcao;
    }

    private static void showLoadMenu() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do programa a ser carregado");
        String nomePrograma = scanner.nextLine();

        System.out.println("A prioridade do programa por padrão é baixa (" + CPU.LOWEST_PRIOR + ")");
        System.out.println("Você deseja alterar? (" + YES_OPTION + " - sim), (" + NO_OPTION + " - não)");
        int shouldChangePrior = scanner.nextInt();

        while (shouldChangePrior != NO_OPTION && shouldChangePrior != YES_OPTION) {
            System.out.println("Opção inválida.");
            System.out.println("Você deseja alterar? (" + YES_OPTION + " - sim), (" + NO_OPTION + " - não)");
            shouldChangePrior = scanner.nextInt();
        }

        int priority = CPU.LOWEST_PRIOR;

        if (shouldChangePrior == YES_OPTION) {
            System.out.println("Tipos de prioridade:");
            System.out.println(CPU.HIGHEST_PRIOR + " - alta prioridade");
            System.out.println(CPU.MEDIUM_PRIOR + " - prioridade média");
            System.out.println(CPU.LOWEST_PRIOR + " - baixa prioridade");
            System.out.println("Digite a prioridade desejada para o programa '" + nomePrograma + "' ("
                    + CPU.HIGHEST_PRIOR + ", " + CPU.MEDIUM_PRIOR + " ou " + CPU.LOWEST_PRIOR + "):");

            priority = scanner.nextInt();

            while ((priority != CPU.HIGHEST_PRIOR) && (priority != CPU.MEDIUM_PRIOR) && (priority != CPU.LOWEST_PRIOR)) {
                System.out.println("Opção inválida.");
                System.out.println("Digite a prioridade desejada para o programa '" + nomePrograma + "' ("
                        + CPU.HIGHEST_PRIOR + ", " + CPU.MEDIUM_PRIOR + " ou " + CPU.LOWEST_PRIOR + "):");
                priority = scanner.nextInt();
            }
        }


        System.out.println("Digite o instante de carga desejado para o programa '" + nomePrograma + "':");
        int arrivalTime = scanner.nextInt();

        Program program = new Program(nomePrograma, debugMode);

        if (program != null) {
            Process process = new Process(processes.size(), program, priority, arrivalTime, program.memory.size());

            processes.add(process);
        }

    }
}
