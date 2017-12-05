# TypingDNARecorder JAVA
##### A simple way to capture user’s typing patterns in JAVA
Compatible with **JavaSE (awt)** and **JavaFX**

### Usage and description
First you need to add com.typingdna to your Built Path and import **com.typingdna.TypingDNARecorder** class in the app that wants to record a typing pattern. You will need to record typing patterns when a user first creates his account and again whenever you want to authenticate that user on your platform.


### TypingDNARecorder class

Once you create an instance of the TypingDNARecorder class, you need to overwrite the functions for handling key events in specific text fields. All functions you need to call are static. 

Note that implementation of these events is a bit different in JavaSE and JavaFX due to the way these deal with KeyEvent objects. Please check the code examples for each.

**Example**  
```java
new TypingDNARecorder();

textMain.addKeyListener(new KeyAdapter() {
    public void keyPressed(KeyEvent e) {
    	int keyCode = e.getKeyCode();
        int keyChar = e.getKeyChar();
      	boolean modifiers = (e.getModifiers() == 1);
    	TypingDNARecorder.keyPressed(keyCode, keyChar, modifiers);
    }
    public void keyTyped(KeyEvent e) {
    	int keyChar = e.getKeyChar();
    	TypingDNARecorder.keyTyped(keyChar);
    }
    public void keyReleased(KeyEvent e) {
      	int keyCode = e.getKeyCode();
      	boolean modifiers = (e.getModifiers() == 1);
      	TypingDNARecorder.keyReleased(keyCode, modifiers);
    }
});
```

Whenever you want to get the user's typing pattern you have to invoke **TypingDNARecorder.getTypingPattern()** method described in detail below.


### TypingDNA.getTypingPattern(optionsObject)
This is the main function that outputs the user's typing pattern as a `String`

**Returns**: A typing pattern in `String` form  

**Input params**: the following params are required: type, length, text, textId, extended (in this particular order). Detail table below.

| Param | Type | Description |
| --- | --- | --- |
| **type** | `int` | `0 for anytext pattern` (when you compare random typed texts of usually 120-180 chars long) <br> `1 for diagram pattern` (recommended in most cases, for emails, passwords, phone numbers, credit cards, short texts) |
| **length** | `int` | (Optional) the length of the text in the history for which you want the typing pattern, 0 = ignore, (works only if text = "") |
| **text** | `String` | (Only for type 1) a typed string that you want the typing pattern for |
| **textId** | `int` | (Optional, only for type 1) a personalized id for the typed text, 0 = ignore |
| **extended** | `boolean` | (Only for type 1) specifies if full information about what was typed is produced, including the actual key pressed, if false, only the order of pressed keys is kept (no actual content) |


**Example**  
```java
int type = 1;
int length = 0;
String text = textMain.getText();
int textId = 0;
boolean extended = true;

String typingPattern = TypingDNARecorder.getTypingPattern(type, length, text, textId, extended);
```

### TypingDNARecorder.reset()
Resets the history stack of recorded typing events.

### TypingDNARecorder.start()
Automatically called at initilization. It starts the recording of typing events. You only have to call .start() to resume recording after a .stop()

### TypingDNARecorder.stop()
Ends the recording of further typing events.

### License
Apache License, [Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
