package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author
 */
public final class Main {

    /**
     * Process a sequence of encryptions and decryptions, as
     * specified by ARGS, where 1 <= ARGS.length <= 3.
     * ARGS[0] is the name of a configuration file.
     * ARGS[1] is optional; when present, it names an input file
     * containing messages.  Otherwise, input comes from the standard
     * input.  ARGS[2] is optional; when present, it names an output
     * file for processed messages.  Otherwise, output goes to the
     * standard output. Exits normally if there are no errors in the input;
     * otherwise with code 1.
     */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /**
     * Open the necessary files for non-option arguments ARGS (see comment
     * on main).
     */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
            _input2 = getInput2(args.get(1));
        } else {
            _input = new Scanner(System.in);
            _input2 = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));

        } else _output = System.out;
    }

    /**
     * Return a Scanner reading from the file named NAME.
     */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    private Scanner getInput2(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Return a PrintStream writing to the file named NAME.
     */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(name);
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /**
     * Configure an Enigma machine from the contents of configuration
     * file _config and apply it to the messages in _input, sending the
     * results to _output.
     */

    private void process() {
        Machine machine = readConfig();
        String plugboardConfiguration = "";
        String incomingString = "";
        String outputString = "";
        String rotorSettings = "";
        int _numRotors = machine.numRotors();
        String line;
            if (!(_input.hasNext("\\*"))) {
                throw new EnigmaException("no configuration");
            }

            while (_input.hasNextLine()) {
                line = _input.nextLine();
                incomingString = "";
                outputString = "";
                if (line.isEmpty()) {
                    _output.println();
                } else {
                    if (line.charAt(0) == '*') {
                        plugboardConfiguration = "";
                        incomingString = "";
                        String[] configurationInput = line.split("\\s");
                        String[] rotorSlots = new String[_numRotors];
                        for (int i = 1; i < _numRotors+1; i += 1){
                            rotorSlots[i-1] = configurationInput[i];
                            try {
                                machine.insertRotors(rotorSlots);
                            }
                            catch (EnigmaException e) {
                            }
                        }
                        for(int i = 0; i < _numRotors; i +=1){
                            if(i!=0 && machine.getRotor(i).reflecting()){
                                throw error("reflector wrong");
                            }
                            if(1==2){
                                throw error("reflector wrong");
                            }
                        }
                        if (configurationInput[_numRotors+1].length() != _numRotors-1){
                            throw error("short");
                        }
                        rotorSettings = configurationInput[_numRotors+1];
                        char[] testSet = rotorSettings.toCharArray();
                        for (int i=0;i<testSet.length;i+=1){
                            if (Character.isDigit(testSet[i])){
                                throw error("bad char");
                            }
                        }
                        setUp(machine, rotorSettings);
                        for (int i = _numRotors+2; i < configurationInput.length; i = i + 1 ) {
                            plugboardConfiguration += configurationInput[i];
                        }
                        machine.setPlugboard(new Permutation(plugboardConfiguration, _alphabet));
                    } else {
                        String[] letterInputs;
                        letterInputs = line.split("\\s");
                        int i = 0;
                        while(i< letterInputs.length) {
                            incomingString += letterInputs[i];
                            i += 1;
                        }
                        outputString = machine.convert(incomingString);
                        printMessageLine(outputString);
                    }
                }
            }
}


    //String[] rotorOrder2 = rotorOrder.toArray(new String[enigmaMachine.numRotors()]);
    //enigmaMachine.insertRotors(rotorOrder2);
    //enigmaMachine.setRotors(rotorSettings);
    //Permutation plugboardPerm = new Permutation(plugboardConfiguration, enigmaMachine.alphabet());
    //enigmaMachine.setPlugboard(plugboardPerm);
    //outputString += enigmaMachine.convert(incomingString);



    /**
     * Return an Enigma machine configured from the contents of configuration
     * file _config.
     */
    private Machine readConfig() {
        try {
            String alphabetLine = _config.next();
            _alphabet = new Alphabet(alphabetLine);
            int numRotors = _config.nextInt();
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNextLine() && _config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }

    }

    /**
     * Return a rotor, reading its description from _config.
     */
    private Rotor readRotor() {
        try {
            String rotorName = _config.next();
            String rotorType = _config.next();
            String notches = rotorType.substring(1);
            StringBuilder rotorCycles = new StringBuilder();
            while (_config.hasNext("\\(([^)]+)\\)")) {
                rotorCycles.append(_config.next());
            }
            Permutation rotorPerm = new Permutation(rotorCycles.toString(), _alphabet);
            if (rotorType.charAt(0) == 'M') {
                return new MovingRotor(rotorName, rotorPerm, notches);
            }
            if (rotorType.charAt(0) == 'N') {
                return new FixedRotor(rotorName, rotorPerm);
            }
            if (rotorType.charAt(0) == 'R') {
                return new Reflector(rotorName, rotorPerm);
            }
            return new FixedRotor(rotorName, rotorPerm);
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /**
     * Set M according to the specification given on SETTINGS,
     * which must have the format specified in the assignment.
     */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /**
     * Return true iff verbose option specified.
     */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */

    private void printMessageLine(String msg) {
        for (int index = 0; index < msg.length(); index += 1) {
            _output.print(msg.charAt(index));
            if (((index + 1) % 5) == 0) {
                _output.print(" ");
            }
        }
        _output.println();
        //_output.print(_input.tokens());
    }






       /* int lineLength;
        int startIndex = 0;
        String s;
        String counter = "";
        while(_input2.hasNextLine()){
            if (_input2.hasNext("\\*")) {
                s = _input2.nextLine();
                s = _input2.nextLine();

                String line = s;
                lineLength = line.replaceAll("\\s+", "").length();

                for (int i = 0; i < lineLength; i += 1) {

                    counter += msg.charAt(startIndex + i);
                    if (((i + 1) % 5) == 0) {

                        counter += " ";
                    }
                }
                startIndex = (startIndex + lineLength);
                counter += "\n";
            }
            else{
                s = _input2.nextLine();
                String line = s;
                lineLength = line.replaceAll("\\s+", "").length();

                for (int i = 0; i < lineLength; i += 1) {

                    counter += (msg.charAt(startIndex + i));
                    if (((i + 1) % 5) == 0) {

                        counter += " ";
                    }
                }
                startIndex = (startIndex + lineLength);

                counter += "\n";

            }
        }
        _output.print(counter);*/

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private final Scanner _input;
    private final Scanner _input2;

    /** Source of machine configuration. */
    private final Scanner _config;

    /** File for encoded/decoded messages. */
    private static PrintStream _output;
    /** True if --verbose specified. */
    private static boolean _verbose;
}
