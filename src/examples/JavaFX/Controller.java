package examples.JavaFX;

import com.typingdna.TypingDNARecorder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private TextField textField;
    @FXML
    public TextArea patternView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        new TypingDNARecorder();
        textField.setOnKeyPressed(e -> TypingDNARecorder.keyPressed(e.getCode().impl_getCode(), (e.getText().length() > 0) ? e.getText().charAt(0) : '\0' , getModifiers(e)));
        textField.setOnKeyTyped(e -> TypingDNARecorder.keyTyped(e.getCharacter().charAt(0)));
        textField.setOnKeyReleased(e -> TypingDNARecorder.keyReleased(e.getCode().impl_getCode(),getModifiers(e)));
    }

    private boolean getModifiers(KeyEvent e) {
        return e.isAltDown() || e.isControlDown() || e.isMetaDown() || e.isShiftDown();
    }

    public void onClearClick(ActionEvent actionEvent) {
        reset();
    }

    public void onTypingPatternClick(ActionEvent actionEvent) {
        String typingPattern = getPattern();
        showPattern(typingPattern);
    }

    public void onDiagramClick(ActionEvent actionEvent) {
        String typingPattern = getDiagram();
        showPattern(typingPattern);
    }
    private void reset() {
        textField.setText("");
        textField.requestFocus();
        showPattern("");
        TypingDNARecorder.reset();
    }

    private void showPattern(String typingPattern) {
        patternView.setText(typingPattern);
    }

    private String getPattern(){
        int type = 0; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)
        int length = 150; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
        String text = textField.getText(); // (Only for type 1) a typed string that you want the typing pattern for
        int textId = 0; // (Optional, only for type 1) a personalized id for the typed text, 0 = ignore
        boolean caseSensitive = false; // (Optional, only for type 1) Used only if you pass a text for type 1
        return TypingDNARecorder.getTypingPattern(type, length, text, textId, caseSensitive);
    }

    private String getDiagram() {
        int type = 2; // 1,2 for diagram pattern (short identical texts - 2 for extended diagram), 0 for any-text typing pattern (random text)
        int length = 0; // (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "")
        String text = textField.getText(); // (Only for type 1) a typed string that you want the typing pattern for
        int textId = 0; // (Optional, only for type 1) a personalized id for the typed text, 0 = ignore
        boolean caseSensitive = false; // (Optional, only for type 1) Used only if you pass a text for type 1
        return TypingDNARecorder.getTypingPattern(type, length, text, textId, caseSensitive);
    }

}
