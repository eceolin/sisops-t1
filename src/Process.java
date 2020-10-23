public class Process {

    public int code;
    public Program program;
    public int priority;

    public int startMemoryAllocation;
    public int arrivalTime;

    public int totalMemory;

    public static final int TOTAL_RESERVED_POSITIONS = 7;

    public Process(int code, Program program, int priority, int arrivalTime, int totalMemory) {
        this.code = code;
        this.startMemoryAllocation = -1;
        this.program = program;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.totalMemory = totalMemory + TOTAL_RESERVED_POSITIONS;
    }

    public void initialize(int initialPosition) {
        this.startMemoryAllocation = initialPosition;
    }

    public int getAccumulatorMemoryPosition() {
        return startMemoryAllocation;
    }

    public int getPCMemoryPosition() {
        return startMemoryAllocation + 1;
    }

    public int getStateMemoryPosition() {
        return startMemoryAllocation + 2;
    }

    public int getFirstInstructionMemoryPosition() {
        return startMemoryAllocation + 7;
    }

    public int getArrivalTimeMemoryPosition() {
        return startMemoryAllocation + 3;
    }

    public int getIdleTimeMemoryPosition() {
        return startMemoryAllocation + 4;
    }

    public int getRunningTimeMemoryPosition() {
        return startMemoryAllocation + 5;
    }

    public int getTurnAroundTimeMemoryPosition() {
        return startMemoryAllocation + 6;
    }
}
