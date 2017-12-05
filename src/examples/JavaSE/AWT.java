package examples.JavaSE;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import com.typingdna.TypingDNARecorder;

public class AWT {

    private JFrame frame;

    /**
     * Launch a demo application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    AWT window = new AWT();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create a demo application.
     */
    public AWT() {
        new TypingDNARecorder();
        initialize();
    }

    /**
     * At this point you can send the typing patterns to your servers
     * from where you should save and verify users on the TypingDNA Auth. API (servers).
     */
    private void doSomethingWithTP(String typingPattern) {
        StringSelection selection = new StringSelection(typingPattern);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        System.out.println(typingPattern);
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblEnterAnyText = new JLabel("Enter any text (120-160 chars)");
        lblEnterAnyText.setFont(new Font("Lucida Grande", Font.PLAIN, 16));
        lblEnterAnyText.setBounds(52, 54, 280, 16);
        frame.getContentPane().add(lblEnterAnyText);

        JTextArea textMain = new JTextArea();
        textMain.setWrapStyleWord(true);
        textMain.setLineWrap(true);
        Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        textMain.setBorder(border);
        textMain.setBounds(53, 86, 355, 65);
        frame.getContentPane().add(textMain);

        JButton btnGetPattern = new JButton("Copy Pattern");
        btnGetPattern.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int type = 0; // 1 for diagram pattern (email, password, short identical texts), 0 for any-text typing pattern (random text)
                int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
                String text = ""; // (Only for type 1) a typed string that you want the typing pattern for
                int textId = 0; // (Optional, only for type 1) a personalized id for the typed text, 0 = ignore
                boolean extended = false; // (Only for type 1) specifies if full information about what was typed is produced, including the actual key pressed, if false, only the order of pressed keys is kept (no actual content)
                String typingPattern = TypingDNARecorder.getTypingPattern(type, length, text, textId, extended);
                doSomethingWithTP(typingPattern);
                textMain.requestFocus();
            }
        });
        btnGetPattern.setBounds(51, 164, 172, 36);
        frame.getContentPane().add(btnGetPattern);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(381, 243, 63, 29);
        btnReset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TypingDNARecorder.reset();
                textMain.setText("");
                textMain.requestFocus();
            }
        });
        frame.getContentPane().add(btnReset);

        JButton btnGetDiagram = new JButton("Copy Diagram");
        btnGetDiagram.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int type = 1; // 1 for diagram pattern (email, password, short identical texts), 0 for any-text typing pattern (random text)
                int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
                String text = textMain.getText(); // (Only for type 1) a typed string that you want the typing pattern for
                int textId = 0; // (Optional, only for type 1) a personalized id for the typed text, 0 = ignore
                boolean extended = true; // (Only for type 1) specifies if full information about what was typed is produced, including the actual key pressed, if false, only the order of pressed keys is kept (no actual content)
                String typingPattern = TypingDNARecorder.getTypingPattern(type, length, text, textId, extended);
                doSomethingWithTP(typingPattern);
                textMain.requestFocus();
            }
        });
        btnGetDiagram.setBounds(236, 163, 172, 36);
        frame.getContentPane().add(btnGetDiagram);

        /*
         * Please add listeners for keyReleased and keyPressed on all targets that you want to record on.
         */

        textMain.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                TypingDNARecorder.keyPressed(e.getKeyCode(),e.getKeyChar(),e.getModifiers() == 1 ? true : false);
            }
            public void keyTyped(KeyEvent e) {
                TypingDNARecorder.keyTyped(e.getKeyChar());
            }
            public void keyReleased(KeyEvent e) {
                TypingDNARecorder.keyReleased(e.getKeyCode(),e.getModifiers() == 1 ? true : false);
            }
        });
    }
}
