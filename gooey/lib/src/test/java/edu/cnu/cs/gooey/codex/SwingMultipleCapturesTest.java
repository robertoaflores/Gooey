package edu.cnu.cs.gooey.codex;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.swing.JFrame;

import org.junit.jupiter.api.Test;

import edu.cnu.cs.gooey.Gooey;
import edu.cnu.cs.gooey.GooeyFrame;

class SwingMultipleCapturesTest {
    @Test
    void testTwoSequentialCaptures() {
        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                JFrame frame = new JFrame("first");
                frame.setSize(100, 100);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            @Override
            public void test(JFrame frame) {
                assertEquals("first", frame.getTitle());
            }
        });

        Gooey.capture(new GooeyFrame() {
            @Override
            public void invoke() {
                JFrame frame = new JFrame("second");
                frame.setSize(100, 100);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
            @Override
            public void test(JFrame frame) {
                assertEquals("second", frame.getTitle());
            }
        });
    }
}
