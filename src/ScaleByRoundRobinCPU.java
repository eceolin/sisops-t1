import java.util.LinkedList;
import java.util.List;

public class ScaleByRoundRobinCPU extends CPU {

    List<Process> allProcesses;
    LinkedList<Process> readyProcesses;
    int quantum;

    public ScaleByRoundRobinCPU(List<Process> processes, int quantum) {
        this.allProcesses = processes;
        this.readyProcesses = new LinkedList<>();
        this.quantum = quantum;
    }

    @Override
    protected Process getNextReadyProcess(int clock) {

        check(clock);

        for (Process process : allProcesses) {
            if (memory[process.getStateMemoryPosition()].equals(STATUS_IDLE) && !readyProcesses.contains(process)) {
                readyProcesses.offer(process);
            }
        }

        return readyProcesses.poll();
    }

    private void check(int clock) {
        for (Process process : allProcesses) {
            if (memory[process.getStateMemoryPosition()].equals(STATUS_NOT_READY) && process.arrivalTime <= clock) {
                memory[process.getStateMemoryPosition()] = STATUS_IDLE;
            }
        }
    }

    @Override
    protected int run(int clock, Process process) throws Exception {
        int actualClock = clock;

        int localClock = 0;

        while (localClock < quantum) {
            if (!isBlocked(process)) {
                memory[process.getStateMemoryPosition()] = STATUS_RUNNING;

                int actualPcPosition = Integer.valueOf(memory[process.getPCMemoryPosition()]);

                String instruction = memory[actualPcPosition];

                System.out.println(instruction);

                loadProcesses(clock);

                actualClock += Interpreter.interpret(instruction, process, memory);

                if (!isBlocked(process) && instruction.equals("SYSCALL 0")) {
                    finishProcess(process);
                    break;
                } else if (isLastInstruction(process)) {
                    finishProcess(process);
                    break;
                }
            } else {
                break;
            }

            updateAllTimes();

            System.out.println(memory[process.getAccumulatorMemoryPosition()]);
            System.out.println(memory[process.getTurnAroundTimeMemoryPosition()]);

            localClock++;

            System.out.println("");
        }

        //timeout
        if (!memory[process.getStateMemoryPosition()].equals(STATUS_FINISHED) &&
                !memory[process.getStateMemoryPosition()].equals(STATUS_BLOCKED)) {
            memory[process.getStateMemoryPosition()] = STATUS_IDLE;
        }

        return actualClock;

    }

    @Override
    protected List<Process> getAllProcesses() {
        return allProcesses;
    }
}
