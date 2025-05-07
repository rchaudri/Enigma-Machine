package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author
 */
class Permutation {

    /**
     * Set this Permutation to that specified by CYCLES, a string in the
     * form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     * is interpreted as a permutation in cycle notation.  Characters in the
     * alphabet that are not included in any cycle map to themselves.
     * Whitespace is ignored.
     */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        _cycles = _cycles.replaceAll("\\s","");
        _sepcycles = _cycles.split("\\)");
    }

    /**
     * Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     * c0c1...cm.
     */
    private void addCycle(String cycle) {
        _cycles = _cycles + "(" + cycle + ")";
    }

    /**
     * Return the value of P modulo the size of this permutation.
     */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /**
     * Returns the size of the alphabet I permute.
     */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        int index = wrap(p);
        char q = _alphabet.toChar(index);
        char ans = permute(q);
        return _alphabet.toInt(ans);
    }

    /**
     * Return the result of applying the inverse of this permutation
     * to  C modulo the alphabet size.
     */
    int invert(int c) {
        int index = wrap(c);
        char q = _alphabet.toChar(index);
        char ans = invert(q);
        return _alphabet.toInt(ans);
    }

    /**
     * Return the result of applying this permutation to the index of P
     * in ALPHABET, and converting the result to a character of ALPHABET.
     */
    char permute(char p) {
        if (!(_alphabet.contains(p))){
            throw new EnigmaException("not in alphabet");
        }
        else{
        char return_char = p;
        int appears = _cycles.indexOf(p);
        if (appears == -1) {
            return p;
        } else {
        for (int cycle_index = 0; cycle_index < _sepcycles.length; cycle_index += 1) {
            for (int index = 0; index < _sepcycles[cycle_index].length(); index += 1) {
                if ((_sepcycles[cycle_index].charAt(index) == p) && ((index + 1) == _sepcycles[cycle_index].length())) {
                    return_char = _sepcycles[cycle_index].charAt(1);
                    return return_char;
                } else {
                    if (_sepcycles[cycle_index].charAt(index) == p) {
                        return_char = _sepcycles[cycle_index].charAt(index+1);
                        return return_char;
                }
            }
        }
    }
        return return_char;
        }
    }
    }
    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!(_alphabet.contains(c))) {
            throw new EnigmaException("not in alphabet");
        } else {
            char return_char = c;
            int appears = _cycles.indexOf(c);
            if (appears == -1) {
                return c;
            } else {
                for (int cycle_index = (_sepcycles.length - 1); cycle_index > -1; cycle_index -= 1) {
                    for (int index = _sepcycles[cycle_index].length() - 1; index > -1; index -= 1) {
                        if ((_sepcycles[cycle_index].charAt(index) == c) && ((index - 1) == 0)) {
                            return_char = _sepcycles[cycle_index].charAt(_sepcycles[cycle_index].length() - 1);
                            return return_char;
                        } else {
                            if (_sepcycles[cycle_index].charAt(index) == c) {
                                return_char = _sepcycles[cycle_index].charAt(index - 1);
                                return return_char;
                            }
                        }
                    }
                }
            }
            return return_char;
        }
    }

                        /** Return the alphabet used to initialize this Permutation. */
                        Alphabet alphabet () {
                            return _alphabet;
                        }

                        /** Return true iff this permutation is a derangement (i.e., a
                         *  permutation for which no value maps to itself). */
                        boolean derangement () {
                            for (int i = 0; i < size(); i += 1){
                                if (permute(i) == i) {
                                    return false;
                                }
                            }
                            return true;
                        }

                        /** Alphabet of this permutation. */
                        public Alphabet _alphabet;

                        public String _cycles;

                        public String[] _sepcycles;

                        // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
                    }