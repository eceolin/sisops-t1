public class Process extends BaseMemoryControl {

    public int code;
    public Program program;
    public int priority;
    public int arrivalTime;

    public int accPosition;
    public int pcPosition;

    public int waitingTime;
    public int processingTime;
    public int turnAroundTime;

    public Process(int code, Program program, int priority, int arrivalTime) {
        this.code = code;
        this.program = program;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.accPosition = -1;
        this.pcPosition = -1;
        this.waitingTime = 0;
        this.processingTime = 0;
        this.turnAroundTime = 0;
    }

    public boolean isInitialized() {
        return accPosition != -1;
    }

    public void initialize(int initialPosition) {
        this.accPosition = initialPosition;
        this.pcPosition = initialPosition + 1;
        this.firstLabelAddress = program.firstLabelAddress + initialPosition + 2;
        this.firstInstructionAddress = program.firstInstructionAddress + initialPosition + 2;
        this.firstVariableAddress = program.firstVariableAddress + initialPosition + 2;
        this.lastInstructionAddress = program.lastInstructionAddress + initialPosition + 2;
        this.lastLabelAddress = program.lastLabelAddress + initialPosition + 2;
        this.lastVariableAddress = program.lastVariableAddress + initialPosition + 2;
    }

    //pc position == -1 quando inicia
    public boolean isReady(int clock) {
        return clock >= arrivalTime && (pcPosition == -1 || pcPosition != lastInstructionAddress - 1);
    }
}
