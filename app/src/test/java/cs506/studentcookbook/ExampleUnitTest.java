package cs506.studentcookbook;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() throws Exception {
        char s = '™';
        String j = "®i ™hope the™ thing was ®removed®";

        char[] EXCLUDED_CHARS_DATA = {'™', '®'};
        List<Character> EXCLUDED_CHARS;

        EXCLUDED_CHARS = new ArrayList<Character>(EXCLUDED_CHARS_DATA.length);
        for(int i = 0; i < EXCLUDED_CHARS_DATA.length; i++) {
            EXCLUDED_CHARS.add(i, EXCLUDED_CHARS_DATA[i]);
        }

        String name = j;

        for(Character c : EXCLUDED_CHARS) {
            int index = name.indexOf(c);
            while(index != -1) {
                StringBuilder sb = new StringBuilder(name);
                sb.deleteCharAt(index);
                name = sb.toString();
                index = name.indexOf(c);
            }
        }

        assertEquals(4, 2 + 2);
    }
}