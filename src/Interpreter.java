import java.util.Random;
import java.util.Scanner;

public class Interpreter {

    public static int interpret(String instruction, Process process, String[] memory) throws Exception {
        if (isArithmeticOperation(instruction)) {
            return arithmeticOperation(instruction, process, memory);
        } else if (isMemoryOperation(instruction)) {
            return memoryOperation(instruction, process, memory);
        } else if (isSystemOperation(instruction)) {
            return systemOperation(instruction, process, memory);
        } else if (isJumpOperation(instruction)) {
            return jumpOperation(instruction, process, memory);
        }

        return 0;
    }

    private static int systemOperation(String instruction, Process process, String[] memory) {

        int blockedTime = new Random().nextInt(30) + 10;
        System.out.println("blocked time: " + blockedTime);

        if (instruction.equals("SYSCALL 1")) {
            System.out.println("Valor do acumulador: " + memory[process.getAccumulatorMemoryPosition()]);
        } else if (instruction.equals("SYSCALL 2")) {
            System.out.println("Digite um valor inteiro: ");
            Scanner scanner = new Scanner(System.in);
            memory[process.getAccumulatorMemoryPosition()] = String.valueOf(scanner.nextInt());
        }

        memory[process.getStateMemoryPosition()] = CPU.STATUS_BLOCKED;
        memory[process.getBlockedTimeMemoryPosition()] = String.valueOf(blockedTime);
        incrementPC(memory, process, 1);

        return 1;
    }

    private static void incrementPC(String[] memory, Process process, int total) {
        memory[process.getPCMemoryPosition()] = String.valueOf(Integer.valueOf(memory[process.getPCMemoryPosition()]) + total);
    }

    private static boolean isArithmeticOperation(String instruction) {
        return instruction.contains("ADD") ||
                instruction.contains("SUB") ||
                instruction.contains("MULT") ||
                instruction.contains("DIV");
    }

    private static boolean isMemoryOperation(String instruction) {
        return instruction.contains("LOAD") ||
                instruction.contains("STORE");
    }

    private static boolean isJumpOperation(String instruction) {
        return instruction.contains("BRANY") ||
                instruction.contains("BRPOS") ||
                instruction.contains("BRZERO") ||
                instruction.contains("BRNEG")
                ;
    }

    private static boolean isSystemOperation(String instruction) {
        return instruction.contains("SYSCALL");
    }

    private static int arithmeticOperation(String instruction, Process process, String[] memory) throws Exception {
        int value = 0;

        if (instruction.contains("#")) {
            value = Integer.valueOf(instruction.split("#")[1]);
        } else {
            value = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);
        }

        int newAcc = Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]);

        if (instruction.contains("ADD")) {
            newAcc += value;
        } else if (instruction.contains("SUB")) {
            newAcc -= value;
        } else if (instruction.contains("MULT")) {
            newAcc *= value;
        } else if (instruction.contains("DIV")) {
            newAcc /= value;
        }

        memory[process.getAccumulatorMemoryPosition()] = String.valueOf(newAcc);
        incrementPC(memory, process, 1);

        return 1;
    }

    private static int jumpOperation(String instruction, Process process, String[] memory) throws Exception {
        int accValue = Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]);
        int labelPosition = searchForLabelValue(process.startMemoryAllocation, process.totalMemory, instruction, memory);

        move(instruction, accValue, process, memory, labelPosition);

        return 1;
    }

    private static void move(String instruction, int accValue, Process process, String[] memory, int newPosition) {
        if (instruction.contains("BRPOS") && accValue > 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (instruction.contains("BRZERO") && accValue == 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (instruction.contains("BRNEG") && accValue < 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (instruction.contains("BRANY")) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else {
            incrementPC(memory, process, 1);
        }
    }

    private static int memoryOperation(String instruction, Process process, String[] memory) throws Exception {

        if (instruction.contains("LOAD")) {
            int valueToAdd = 0;

            valueToAdd = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);

            memory[process.getAccumulatorMemoryPosition()] = String.valueOf(valueToAdd);

        } else if (instruction.contains("STORE")) {
            int positionToStore = searchForVariablePosition(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);

            memory[positionToStore] = memory[positionToStore].split(" ")[0] + " " + memory[process.getAccumulatorMemoryPosition()];
        }

        incrementPC(memory, process, 1);
        return 1;
    }

    private static int searchForVariablePosition(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        int cont = 0;

        while (cont < processMaxMem) {

            if (memory[cont + processMemoryPosition].toLowerCase().startsWith(variable.toLowerCase())) {
                return cont + processMemoryPosition;
            }

            cont++;
        }

        throw new Exception("Variavel '" + variable + "' não encontrada.");
    }

    private static int searchForVariableValue(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        return Integer.valueOf(memory[searchForVariablePosition(processMemoryPosition, processMaxMem, variable, memory)].split(" ")[1]);
    }

    private static int searchForLabelValue(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        int cont = 0;

        while (cont < processMaxMem) {

            if (memory[cont + processMemoryPosition].toLowerCase().contains("label " + variable.split(" ")[1].toLowerCase())) {
                return Integer.valueOf(memory[cont + processMemoryPosition].split(" ")[2]);
            }

            cont++;
        }

        throw new Exception("Valor do label '" + variable + "' não encontrado.");
    }
}
