import java.util.Random;

public class Interpreter {

    public static int interpret(String instruction, Process process, String[] memory) throws Exception {
        if (instruction.contains("ADD")) {
            return add(instruction, process, memory);
        } else if (instruction.contains("SUB")) {
            return sub(instruction, process, memory);
        } else if (instruction.contains("LOAD")) {
            return load(instruction, process, memory);
        } else if (instruction.equals("SYSCALL 1")) {
            incrementPC(memory, process, 1);
            return new Random().nextInt(30) + 10;
        } else if (instruction.contains("BRPOS")) {
            return brpos(instruction, process, memory);
        }

        return 0;
    }

    private static void incrementPC(String[] memory, Process process, int total) {
        memory[process.getPCMemoryPosition()] = String.valueOf(Integer.valueOf(memory[process.getPCMemoryPosition()]) + total);
    }

    private static int add(String instruction, Process process, String[] memory) throws Exception {
        int valueToAdd = 0;

        if (instruction.contains("#")) {
            valueToAdd = Integer.valueOf(instruction.split("#")[1]);
        } else {
            valueToAdd = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);
        }

        memory[process.getAccumulatorMemoryPosition()] = String.valueOf(Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]) + valueToAdd);
        incrementPC(memory, process, 1);

        return 1;
    }

    private static int sub(String instruction, Process process, String[] memory) throws Exception {
        int valueToSub = 0;

        if (instruction.contains("#")) {
            valueToSub = Integer.valueOf(instruction.split("#")[1]);
        } else {
            valueToSub = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);
        }

        memory[process.getAccumulatorMemoryPosition()] = String.valueOf(Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]) - valueToSub);
        incrementPC(memory, process, 1);

        return 1;
    }

    private static int brpos(String instruction, Process process, String[] memory) throws Exception {
        int accValue = Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]);

        if (accValue > 0) {
            int valueToAdd = searchForLabelValue(process.startMemoryAllocation, process.totalMemory, instruction, memory);
            memory[process.getPCMemoryPosition()] = String.valueOf(valueToAdd);
        } else {
            incrementPC(memory, process, 1);
        }

        return 1;
    }

    private static int load(String instruction, Process process, String[] memory) throws Exception {
        int valueToAdd = 0;

        valueToAdd = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);

        memory[process.getAccumulatorMemoryPosition()] = String.valueOf(Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]) + valueToAdd);
        incrementPC(memory, process, 1);

        return 1;
    }

    private static int searchForVariableValue(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        int cont = 0;

        while (cont < processMaxMem) {

            if (memory[cont + processMemoryPosition].toLowerCase().startsWith(variable.toLowerCase())) {
                return Integer.valueOf(memory[cont + processMemoryPosition].split(" ")[1]);
            }

            cont++;
        }

        throw new Exception("Valor da variavel '" + variable + "' não encontrado.");
    }

    private static int searchForLabelValue(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        int cont = 0;

        while (cont < processMaxMem) {

            if (memory[cont + processMemoryPosition].toLowerCase().contains("label " + variable.split(" ")[1].toLowerCase())) {
                return Integer.valueOf(memory[cont + processMemoryPosition].split(" ")[2]);
            }

            cont++;
        }

        throw new Exception("Valor da variavel '" + variable + "' não encontrado.");
    }
}
