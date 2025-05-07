package enigma;

import java.util.HashMap;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _rotors = _allRotors.toArray(new Rotor[_allRotors.size()]);
        _rotorSlots = new Rotor[_numRotors];
    }

    void setAllZero() {
        for (int i = 0; i < _rotors.length; i += 1) {
            _rotors[i].set(0);
        }
    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {
        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     * #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     * undefined results.
     */
    Rotor getRotor(int k) {
        return _rotorSlots[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < _rotorSlots.length; i += 1) {
            for (int j = 0; j < _rotors.length; j += 1) {
                if (_rotors[j].name().equals(rotors[i])) {
                    _rotorSlots[i] = _rotors[j];
                    break;
                }
            }

        }
    }

    /**
     * Set my rotors according to SETTING, which must be a string of
     * numRotors()-1 characters in my alphabet. The first letter refers
     * to the leftmost rotor setting (not counting the reflector).
     */
    void setRotors(String setting) {
        _letterSetting = setting.toCharArray();
        int _num;
        for (int slot = 1; slot < numRotors(); slot += 1) {
            _num = _alphabet.toInt(_letterSetting[slot - 1]);
            _rotorSlots[slot].set(_num);
        }
    }

    /**
     * Return the current plugboard's permutation.
     */
    Permutation plugboard() {
        return _plugboard;
    }

    /**
     * Set the plugboard to PLUGBOARD.
     */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /**
     * Returns the result of converting the input character C (as an
     * index in the range 0..alphabet size - 1), after first advancing
     * the machine.
     */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /**
     * Advance all rotors to their next position.
     */
    private void advanceRotors() {
        Rotor _fastRotor = _rotorSlots[numRotors()-1];
        boolean[] trackNotch = new boolean[numRotors()];
        for (int i = numRotors() - 1; i > -1; i -= 1) {
                if (_rotorSlots[i].atNotch()) {
                    trackNotch[i] = true;}}
        _fastRotor.advance();
        _fastRotor._alreadyMoved = true;

        for (int i = numRotors() - 1; i > -1; i -= 1) {
            if (trackNotch[i] && !(_rotorSlots[i]._alreadyMoved)) {
                _rotorSlots[i].advance();
                _rotorSlots[i-1].advance();
                _rotorSlots[i]._alreadyMoved = true;
                _rotorSlots[i-1]._alreadyMoved = true;
            }
            else {
                if (trackNotch[i] && (_rotorSlots[i]._alreadyMoved)){
                    _rotorSlots[i-1].advance();
                    _rotorSlots[i-1]._alreadyMoved = true;
                }
            }
        }
        for (int i = numRotors() - 1; i > -1; i -= 1) {
            _rotorSlots[i]._alreadyMoved = false;
    }

        /* for (int i = numRotors() - 1; i > -1; i -= 1) {
            _rotorSlots[i].advance();
            boolean[] trackNotch = new boolean[numRotors()];
            for (int i = numRotors(); i > 0; i -= 1) {
                if (_rotorSlots[i - 1].atNotch()) {
                    trackNotch[i - 1] = true;
                }
            }
            for (int i = numRotors() - 1; i > -1; i -= 1) {
                if (_rotorSlots[i].rotates() && !(_rotorSlots[i].is_alreadyMoved())) {
                    if (trackNotch[i]) {
                        _rotorSlots[i].advance();
                        _rotorSlots[i - 1].advance();
                        if (_rotorSlots[i - 1].rotates()) {
                            _rotorSlots[i - 1]._alreadyMoved = true;
                        }
                    } else {
                        _rotorSlots[i].advance();
                    }
                } else {
                    if (!_rotorSlots[i].rotates()) {
                        _rotorSlots[i].advance();
                    } else {
                        if (trackNotch[i] && _rotorSlots[i]._alreadyMoved) {
                            _rotorSlots[i - 1].advance();
                            if (_rotorSlots[i - 1].rotates()) {
                                _rotorSlots[i - 1]._alreadyMoved = true;
                            }
                        }
                    }
                }
            }
            for (int i = numRotors() - 1; i > -1; i -= 1) {
                if (_rotorSlots[i].rotates()) {
                    _rotorSlots[i]._alreadyMoved = false;
                }
            }
        }*/

}


    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int d = c;
        for (int i = numRotors()-1; i > -1; i -= 1) {
            d = _rotorSlots[i].convertForward(d);
        }
        for (int i = 1; i < _rotorSlots.length; i += 1) {
            d = _rotorSlots[i].convertBackward(d);
        }
        return d;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        char[] chars = msg.toCharArray();
        String output = "";
        char a;
        int b;
        int translate;

        for (int i = 0; i < chars.length; i += 1) {
            a = chars[i];
            b = _alphabet.toInt(a);
            translate = convert(b);
            output += _alphabet.toChar(translate);
        }

        return output;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
    private final int _numRotors;
    private final int _pawls;
    private final Collection<Rotor> _allRotors;
    public final Rotor[] _rotors;
    private Rotor[] _rotorSlots;
    private char[] _letterSetting;
    private Permutation _plugboard;

    // FIXME: ADDITIONAL FIELDS HERE, IF NEEDED.
}
