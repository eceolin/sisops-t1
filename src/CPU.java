import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public abstract class CPU {

    public static final int SCALING_PRIORITY = 1;
    public static final int SCALING_ROUND_ROBIN = 2;

    public static final int HIGHEST_PRIOR = 0;
    public static final int MEDIUM_PRIOR = 1;
    public static final int LOWEST_PRIOR = 2;

    protected List<InstructionArgument> memory;

    public CPU() {
        this.memory = new ArrayList<>();
    }

    public void start() {

        int clock = 0;

        boolean running = true;

        while(running) {

            Process process = this.getNextReadyProcess();

            if (process == null) {
                running = false;
                continue;
            }

            if (!process.isInitialized()) {
                initializeMemory(process);
            }

            int newClock = run(clock, process);

            process.processingTime += (newClock-clock);


            clock = newClock;
        }


    }

    private void initializeMemory(Process process) {

        int initialPosition = memory.size();

        process.initialize(initialPosition);

        memory.add(new InstructionArgument("acc", "0"));
        memory.add(new InstructionArgument("pc", String.valueOf(process.firstInstructionAddress)));
        memory.addAll(process.program.memory);
    }

    protected abstract Process getNextReadyProcess();

    protected abstract int run(int clock, Process process);
}
