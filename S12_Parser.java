import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * translates s12 assembly instructions into a memFile. output is
 * written to a file and printed to the console for debugging
 * @author chatgpt
 * @author mattbudig
 */
public class S12_Parser {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java Simple12Assembler <inputfile> <outputfile>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try (
            Scanner scanner = new Scanner(new File(inputFile));
            PrintWriter writer = new PrintWriter(outputFile)
        ) {
            int lineNumber = 0;

            // write binary value for PC and ALU
            writer.println("00000000 000000000000");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (!line.isEmpty() && !line.startsWith("//")) { // skip empty & comments
                    String[] parts = line.split("\\s+");
                    String instruction = parts[0].toUpperCase();
                    int operand = (parts.length > 1) ? Integer.parseInt(parts[1], 16) : 0;

                    String binary = assemble(instruction, operand);

                    // Format line number as 2-digit hex (uppercase)
                    String hexLine = String.format("%02X", lineNumber);

                    // Write output with hex line number
                    writer.println(hexLine + binary);

                    // Debug/console feedback
                    System.out.println(hexLine + ": " + line + " -> " + binary);
                }

                lineNumber++;
            }

            System.out.println("Assembly complete. Output written to " + outputFile);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + inputFile);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Translate instruction + operand into opcode + operand
    private static String assemble(String instruction, int operand) {
        String opcode;

        switch (instruction) {
            case "JMP":    opcode = "0000"; break;
            case "JN":     opcode = "0001"; break;
            case "JZ":     opcode = "0010"; break;
            case "LOAD":   opcode = "0100"; break;
            case "STORE":  opcode = "0101"; break;
            case "LOADI":  opcode = "0110"; break;
            case "STOREI": opcode = "0111"; break;
            case "AND":    opcode = "1000"; break;
            case "OR":     opcode = "1001"; break;
            case "ADD":    opcode = "1010"; break;
            case "SUB":    opcode = "1011"; break;
            case "HALT":   opcode = "1111"; 
                           return opcode + " 00000000"; // no operand, pad with zeros
            default:
                throw new IllegalArgumentException("Unknown instruction: " + instruction);
        }

        // Convert operand to 8-bit binary
        String operandBinary = String.format("%8s", Integer.toBinaryString(operand & 0xFF)).replace(' ', '0');

        return opcode + " " + operandBinary;
    }
}