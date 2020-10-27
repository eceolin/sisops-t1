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

        if (instruction.equalsIgnoreCase("SYSCALL 1")) {
            System.out.println("Valor do acumulador: " + memory[process.getAccumulatorMemoryPosition()]);
        } else if (instruction.equalsIgnoreCase("SYSCALL 2")) {
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

    private static boolean containsIgnorecase(String str, String str2) {
        return str.toLowerCase().contains(str2.toLowerCase()) || str2.toLowerCase().contains(str.toLowerCase());
    }

    private static boolean isArithmeticOperation(String instruction) {
        return containsIgnorecase("ADD", instruction) ||
                containsIgnorecase("SUB", instruction) ||
                containsIgnorecase("MULT", instruction) ||
                containsIgnorecase("DIV", instruction);
    }

    private static boolean isMemoryOperation(String instruction) {
        return containsIgnorecase("LOAD", instruction) ||
                containsIgnorecase("STORE", instruction);
    }

    private static boolean isJumpOperation(String instruction) {
        return containsIgnorecase("BRANY", instruction) ||
                containsIgnorecase("BRPOS", instruction) ||
                containsIgnorecase("BRZERO", instruction) ||
                containsIgnorecase("BRNEG", instruction)
                ;
    }

    private static boolean isSystemOperation(String instruction) {
        return containsIgnorecase("SYSCALL", instruction);
    }

    private static int arithmeticOperation(String instruction, Process process, String[] memory) throws Exception {
        int value = 0;

        if (instruction.contains("#")) {
            value = Integer.valueOf(instruction.split("#")[1]);
        } else {
            value = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);
        }

        int newAcc = Integer.valueOf(memory[process.getAccumulatorMemoryPosition()]);

        if (containsIgnorecase("ADD", instruction)) {
            newAcc += value;
        } else if (containsIgnorecase("SUB", instruction)) {
            newAcc -= value;
        } else if (containsIgnorecase("MULT", instruction)) {
            newAcc *= value;
        } else if (containsIgnorecase("DIV", instruction)) {
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
        if (containsIgnorecase("BRPOS", instruction) && accValue > 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (containsIgnorecase("BRZERO", instruction) && accValue == 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (containsIgnorecase("BRNEG", instruction) && accValue < 0) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else if (containsIgnorecase("BRANY", instruction)) {
            memory[process.getPCMemoryPosition()] = String.valueOf(newPosition);
        } else {
            incrementPC(memory, process, 1);
        }
    }

    private static int memoryOperation(String instruction, Process process, String[] memory) throws Exception {

        if (containsIgnorecase("LOAD", instruction)) {
            int valueToAdd = 0;

            valueToAdd = searchForVariableValue(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);

            memory[process.getAccumulatorMemoryPosition()] = String.valueOf(valueToAdd);

        } else if (containsIgnorecase("STORE", instruction)) {
            int positionToStore = searchForVariablePosition(process.startMemoryAllocation, process.totalMemory, instruction.split(" ")[1], memory);

            memory[positionToStore] = "variable " + memory[positionToStore].split(" ")[1] + " " + memory[process.getAccumulatorMemoryPosition()];
        }

        incrementPC(memory, process, 1);
        return 1;
    }

    private static int searchForVariablePosition(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        int cont = 0;

        while (cont < processMaxMem) {

            if (memory[cont + processMemoryPosition].toLowerCase().startsWith("variable "+ variable.toLowerCase())) {
                return cont + processMemoryPosition;
            }

            cont++;
        }

        throw new Exception("Variavel '" + variable + "' não encontrada.");
    }

    private static int searchForVariableValue(int processMemoryPosition, int processMaxMem, String variable, String[] memory) throws Exception {
        return Integer.valueOf(memory[searchForVariablePosition(processMemoryPosition, processMaxMem, variable, memory)].split(" ")[2]);
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
