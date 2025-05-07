package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _permutation = perm;
        _name = name;
        _setting = 0;
        _notches = notches;
        _alreadyMoved = false;

    }

    private int _setting = 0;

    void set(int posn) {
        _setting = posn;
    }

    int convertForward(int p) {
        int result = _permutation.permute(p+_setting);
        result = _permutation.wrap(result - _setting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }

        return result;
    }
    int convertBackward(int e) {
        int result = _permutation.invert(e+_setting);
        result = _permutation.wrap(result - _setting);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(result));
        }
        return result;
    }

    @Override
    void advance() {
        set(permutation().wrap(_setting + 1));
    }

    @Override
    String notches() {return _notches;}


    boolean rotates() {
        return true;
    }


    @Override
    boolean atNotch() {
        char[] notchLetters = _notches.toCharArray();
        for (int i = 0; i < notchLetters.length; i += 1){
            if (alphabet().toChar(_setting) == notchLetters[i]){
                return true;
            }
        }
        return false;
    }

    private Permutation _permutation;
    private final String _name;
    private final String _notches;
    public boolean _alreadyMoved;

}
