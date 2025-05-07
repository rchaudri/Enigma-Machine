package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotor that has no ratchet and does not advance.
 *  @author
 */
class FixedRotor extends Rotor {

    /** A non-moving rotor named NAME whose permutation at the 0 setting
     * is given by PERM. */
    FixedRotor(String name, Permutation perm) {
        super(name, perm);
        _setting = 0;
        _name = name;
        _perm = perm;
    }

    // FIXME ?
    private int _setting;
    private Permutation _perm;
    private String _name;
}
