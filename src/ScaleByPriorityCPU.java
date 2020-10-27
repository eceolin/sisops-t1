import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScaleByPriorityCPU extends CPU {

    SortedSet<Process> allProcesses;
    PriorityQueue<Process> ready;

    public ScaleByPriorityCPU(SortedSet<Process> processes) {
        allProcesses = processes;
    }

    @Override
    protected Process getNextReadyProcess(int clock) {

        List<Process> readyProcesses = allProcesses
                .stream()
                .filter(isNotOnReadyQueue())
                .filter(isNotFinished())
                .filter(isAvailableToProcess(clock))
                .collect(Collectors.toList());

        SortedSet<Process> readySorted = new TreeSet(Interface.comparatorPriority.thenComparing(Interface.comparatorCode));
        readySorted.addAll(readyProcesses);

        if (ready != null) {
            readySorted.addAll(ready);
        }

        ready = new PriorityQueue<>(readySorted);

        return ready.poll();
    }

    private Predicate<Process> isNotOnReadyQueue() {
        return p -> ready == null || !ready.contains(p);
    }

    private Predicate<Process> isAvailableToProcess(int clock) {
        return p -> {
            return p.arrivalTime <= clock;
        };
    }

    private Predicate<Process> isNotFinished() {
        return p -> {
            String value = memory[p.getStateMemoryPosition()];

            return value == null || value == "-1" || !value.equals(STATUS_FINISHED);
        };
    }

    @Override
    protected int run(int clock, Process process) throws Exception {

        int actualClock = clock;


        while (true) {

            memory[process.getStateMemoryPosition()] = STATUS_RUNNING;

            int actualPcPosition = Integer.valueOf(memory[process.getPCMemoryPosition()]);

            String instruction = memory[actualPcPosition];

            updateAllTimes();
            loadProcesses(actualClock);

            if (!isBlocked(process)) {
                actualClock += Interpreter.interpret(instruction, process, memory);
            } else {
                actualClock += 1;
            }

            //Nesta política não tem timeout (quantum)
            while (isBlocked(process)) {
                updateAllTimes();
            }

            if (instruction.equals("SYSCALL 0") || isLastInstruction(process)) {
                finishProcess(process);
                break;
            }
        }

        return actualClock;
    }

    @Override
    protected List<Process> getAllProcesses() {
        return allProcesses.stream().collect(Collectors.toList());
    }

}
