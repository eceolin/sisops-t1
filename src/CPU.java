import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class CPU {

    public static final int SCALING_PRIORITY = 1;
    public static final int SCALING_ROUND_ROBIN = 2;

    public static final int HIGHEST_PRIOR = 0;
    public static final int MEDIUM_PRIOR = 1;
    public static final int LOWEST_PRIOR = 2;

    protected String[] memory;

    protected static final String STATUS_IDLE = "IDLE";
    protected static final String STATUS_RUNNING = "RUNNING";
    protected static final String STATUS_FINISHED = "FINISHED";
    protected static final String STATUS_BLOCKED = "BLOCKED";


    public CPU() {
    }

    public void start() {

        this.memory = new String[calculateMemorySize()];
        this.memory[0] = "1"; //reserved to OS

        int clock = 0;

        boolean running = true;

        while (running) {

            Process process = this.getNextReadyProcess(clock);

            if (process != null) {
                initializeMemory(process);

                int newClock = run(clock, process);

                clock = newClock;
            } else {
                boolean isAllFinished = isAllFinished();

                if (!isAllFinished) {
                    clock++;
                } else {
                    break;
                }
            }
        }

        System.out.println("FIM");


    }

    private boolean isAllFinished() {
        return getAllProcesses()
                .stream()
                .filter(p -> {
                    String value = memory[p.getStateMemoryPosition()];

                    return value == null || !STATUS_FINISHED.equals(value);

                }).count() == 0;
    }

    private void initializeMemory(Process process) {

        int initialPosition = Integer.valueOf(memory[0]);

        process.initialize(initialPosition);

        memory[process.getAccumulatorMemoryPosition()] = "0";
        memory[process.getPCMemoryPosition()] = String.valueOf(process.getFirstInstructionMemoryPosition());
        memory[process.getStateMemoryPosition()] = STATUS_IDLE;
        memory[process.getArrivalTimeMemoryPosition()] = String.valueOf(process.arrivalTime);
        memory[process.getIdleTimeMemoryPosition()] = "0";
        memory[process.getRunningTimeMemoryPosition()] = "0";
        memory[process.getTurnAroundTimeMemoryPosition()] = "0";
        memory[process.getBlockedTimeMemoryPosition()] = "0";

        int cont = process.getFirstInstructionMemoryPosition();

        for (String instruction : process.program.memory) {
            memory[cont] = instruction;

            //serve para alterar para a nova posicao de memoria
            //label quando inicializa utiliza a posicao de memoria sem estar na memoria principal
            if (instruction.contains("LABEL")) {
                String[] inst = instruction.split(" ");

                int newPosition = Integer.valueOf(inst[2]) + process.getFirstInstructionMemoryPosition();

                memory[cont] = inst[0] + " " + inst[1] + " " + newPosition;
            }

            cont++;
        }

        memory[0] = String.valueOf((Integer.valueOf(memory[0]) + process.totalMemory));
    }

    //add 1 because de first memory address is reserved to the last used memory position
    private int calculateMemorySize() {
        return getAllProcesses().stream()
                .map(p -> p.totalMemory)
                .collect(Collectors.summingInt(Integer::intValue)) + 1;

    }

    protected void finishProcess(Process process) {
        memory[process.getStateMemoryPosition()] = STATUS_FINISHED;
    }

    protected void updateAllTimes() {
        for (Process process : getAllProcesses()) {

            String processStatus = memory[process.getStateMemoryPosition()];

            if (STATUS_IDLE.equals(processStatus)) {
                incrementTime(process.getIdleTimeMemoryPosition(), 1);
            } else if (STATUS_RUNNING.equals(processStatus)) {
                incrementTime(process.getRunningTimeMemoryPosition(), 1);
            } else if (STATUS_BLOCKED.equals(processStatus)) {
                int actual = Integer.valueOf(memory[process.getBlockedTimeMemoryPosition()]);

                if (actual > 0) {
                    decrementTime(process.getBlockedTimeMemoryPosition(), 1);
                } else {
                    memory[process.getStateMemoryPosition()] = STATUS_IDLE;
                    incrementTime(process.getIdleTimeMemoryPosition(), 1);
                }

            }

            if (!STATUS_FINISHED.equals(processStatus)) {
                incrementTime(process.getTurnAroundTimeMemoryPosition(), 1);
            }

        }
    }

    private void incrementTime(int memoryPosition, int totalTimeToIncrement) {
        memory[memoryPosition] = String.valueOf(Integer.parseInt(memory[memoryPosition]) + 1);
    }

    private void decrementTime(int memoryPosition, int totalTimeToDecrement) {
        memory[memoryPosition] = String.valueOf(Integer.parseInt(memory[memoryPosition]) - 1);
    }

    protected abstract Process getNextReadyProcess(int clock);

    protected abstract int run(int clock, Process process);

    protected abstract List<Process> getAllProcesses();
}
