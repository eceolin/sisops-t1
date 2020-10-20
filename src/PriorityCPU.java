import java.util.*;
import java.util.stream.Collectors;

public class PriorityCPU extends CPU {

    SortedSet<Process> allProcesses;
    PriorityQueue<Process> ready;

    public PriorityCPU(SortedSet<Process> processes) {
        allProcesses = processes;
        updateReadyProcesses(0);
    }

    @Override
    protected Process getNextReadyProcess() {
        return ready.poll();
    }

    @Override
    protected int run(int clock, Process process) {

        int actualClock = clock;

        for (int i = process.firstInstructionAddress; i < process.lastInstructionAddress; i++ ) {

            String instruction = this.memory.get(i).instruction;
            String arg = this.memory.get(i).argument;

            System.out.println(instruction);

            process.pcPosition = i;

            //TODO: +1 substituir por tempo de processamento do argumento (interpretador)
            actualClock = actualClock + 1;
        }

        updateReadyProcesses(actualClock);

        return actualClock;
    }

    private void updateReadyProcesses(int clock) {

        SortedSet<Process> readyProcesses = allProcesses;
        readyProcesses.removeIf(p -> !p.isReady(clock));
        ready = new PriorityQueue<>(readyProcesses);
    }
}
