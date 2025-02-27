import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import pt.ua.deti.tqs.TqsStack;

import java.util.NoSuchElementException;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;
import static org.slf4j.LoggerFactory.getLogger;

public class TqsStackAITest {
    private static final Logger logger = getLogger(lookup().lookupClass());

    private TqsStack<Integer> stack;

    @BeforeEach
    public void init() {
        stack = new TqsStack<>(5);
    }

    @Test
    public void testPushAndPop() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.pop());
        assertEquals(2, stack.pop());
        assertEquals(1, stack.pop());
    }

    @Test
    public void testPopEmptyThrowsException() {
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    public void testPeek() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertEquals(3, stack.peek());
        assertEquals(3, stack.peek());
    }

    @Test
    public void testPeekEmptyThrowsException() {
        assertThrows(NoSuchElementException.class, stack::peek);
    }

    @Test
    public void testEmptyAfterPushingAndPopping() {
        assertTrue(stack.isEmpty());
        stack.push(1);
        assertFalse(stack.isEmpty());
        stack.pop();
        assertTrue(stack.isEmpty());
    }
}

