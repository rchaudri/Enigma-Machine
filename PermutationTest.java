package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;

import java.util.HashMap;

import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /**
     * Testing time limit.
     */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(100000000);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private Alphabet alpha = new Alphabet(UPPER_STRING);
    private Alphabet test = new Alphabet("UTRZXY");

    /**
     * Check that perm has an alphabet whose size is that of
     * FROMALPHA and TOALPHA and that maps each character of
     * FROMALPHA to the corresponding character of FROMALPHA, and
     * vice-versa. TESTID is used in error messages.
     */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha, Alphabet alphabet) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                    e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                    c, perm.invert(e));
            int ci = alphabet.toInt(c), ei = alphabet.toInt(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                    ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                    ci, perm.invert(ei));
        }
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        char B = 'B';
        assertEquals('A', p.permute(B));
    }

    @Test
    public void test2PermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        char A = 'A';
        assertEquals('C', p.permute(A));
    }

    @Test
    public void test3PermuteChar() {
        Permutation p = new Permutation("(BAG)(DC)", new Alphabet("ABCDEFG"));
        char G = 'G';
        assertEquals('B', p.permute(G));
        assertEquals('A', p.permute('B'));
        assertEquals('E', p.permute('E'));
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BAG)(DC)(ZYX)(MOP)", new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals('Y', p.invert('X'));
        assertEquals('P', p.invert('M'));
        assertEquals('E', p.invert('E'));
    }

    @Test
    public void test2InvertChar() {
        Permutation code = new Permutation("(BAKUGN)(ZY)(OIK)(L)(PHQ)", new Alphabet("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));
        assertEquals('G', code.invert('N'));
        assertEquals('N', code.invert('B'));
        assertEquals('L', code.invert('L'));
        assertEquals('Y', code.invert('Z'));
        assertEquals('Z', code.invert('Y'));
        assertEquals('K', code.invert('O'));
    }

    @Test
    public void derangementTest() {
        Permutation code = new Permutation("(ABCDEF)(GHIJKLMN)", new Alphabet("ABCDEFGHIJKLMN"));
        assertEquals(true, code.derangement());

    }

    @Test
    public void derangement2Test() {
        Permutation code = new Permutation("(ZFQ)(XYD)", new Alphabet("ZYXQDFAB"));
        assertEquals(false, code.derangement());
        assertNotEquals(true, code._alphabet.contains('I'));
        assertEquals(3, code.invert(0));
        assertEquals('D', code.invert('X'));
        assertEquals(1, code.permute(2));
    }

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING, alpha);
    }

    @Test
    public void checkerPerm() {
        perm = new Permutation("(ZYX)(URT)", new Alphabet("UTRZXY"));
        checkPerm("test", "XTUYXR", "ZURXZT", test);

    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.permute('Z');

    }
}