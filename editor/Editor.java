package editor;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Node;
import java.util.ArrayList;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollBar;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;


public class Editor extends Application {
    int WINDOW_WIDTH = 500;
    int WINDOW_HEIGHT = 500;
    int margin = 5;
    int usableScreenWidth;
    Group root;
    Group textRoot;
    Rectangle cursor;
    ScrollBar scrollBar;
    double xpos = margin;
    double ypos = 0;
    LinkedListDeque <Text> output;
    Copyfile copier;
    String filename;
    int startfontsize;
    String startfontname;

    public Editor() {
        startfontsize = 12;
        startfontname = "Verdana";
        Text dummy = new Text(margin, 0, "a");
        dummy.setFont(Font.font(startfontname, startfontsize));
        cursor = new Rectangle(margin, 0, 1, dummy.getLayoutBounds().getHeight());
    }

    private class MouseEventHandler implements EventHandler<MouseEvent> {

        MouseEventHandler(Group root) {
              }

        @Override
        public void handle(MouseEvent mouseEvent) {

            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            if (output.linetracker.size() > 0) {
                int i = 0;

                if (mousePressedY <= output.linetracker.get(output.linetracker.size() -1).item.getY() + output.linetracker.get(output.linetracker.size() -1).item.getLayoutBounds().getHeight()) {
                    while (i < output.linetracker.size() - 1 && output.linetracker.get(i).item.getY() + output.linetracker.get(i).item.getLayoutBounds().getWidth() < mousePressedY) {
              		      i++;
                    }
                    output.currentNode = output.linetracker.get(i);

                  	while (output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth() < mousePressedX) {
                  		  output.currentNode = output.currentNode.next;
                  	}
                  	if (output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth() - mousePressedX
                    > mousePressedX - output.currentNode.item.getY() && output.currentNode.prev.item.getY() == output.currentNode.item.getY()) {
                  		output.currentNode = output.currentNode.prev;
                  	}
                  	xpos = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
                  	ypos = output.currentNode.item.getY();
                    updateCursor();
                }
            }
      }

        private void updateCursor() {
            if (output.currentNode != output.sentinel) {
                double textHeight = output.currentNode.item.getLayoutBounds().getHeight();
                cursor.setHeight(textHeight);
                if (output.currentNode.item.getText().compareTo("\n") == 0) {
                    cursor.setHeight(textHeight/2);
                }
            }
            cursor.setWidth(1);
            cursor.setX(xpos);
            cursor.setY(ypos);
        }
    }

    private class KeyEventHandler implements EventHandler<KeyEvent> {

        private static final int STARTING_FONT_SIZE = 12;
        private static final int STARTING_TEXT_POSITION_X = 5;
        private static final int STARTING_TEXT_POSITION_Y = 0;

        private int fontSize = STARTING_FONT_SIZE;

        private String fontName = "Verdana";

        public void render() {
            output.linetracker = new ArrayList<>();
            double x = 5;
            double y = 0;
            double wordwidth = 0;
            output.bookmark = output.sentinel.next;
            output.word = new ArrayList<>();
            for (int a = 0; a < output.size(); a++) {
                if (output.bookmark.item == null) {
                    break;
                }
                if (output.bookmark.item.getText().equals(" ")) {
                    output.bookmark.item.setX(x);
                    output.bookmark.item.setY(y);
                    x += output.bookmark.item.getLayoutBounds().getWidth();
                    output.bookmark = output.bookmark.next;
                }
                else if (output.bookmark.item.getText().equals("\n")) {
                  output.bookmark.item.setX(x);
                  output.bookmark.item.setY(y);
                    x = margin;
                    y += gettextheight();
                    output.linetracker.add(output.bookmark);
                    output.bookmark = output.bookmark.next;
                } else {
                  wordwidth = 0;
                  output.word = new ArrayList<>();
                    while (output.bookmark != output.sentinel && !output.bookmark.item.getText().equals("\n") && !output.bookmark.item.getText().equals(" ")) {
                        output.word.add(output.bookmark);
                        wordwidth += output.bookmark.item.getLayoutBounds().getWidth();
                        output.bookmark = output.bookmark.next;
                    }
                    if ( x + wordwidth < usableScreenWidth - 5) {
                        for (int k = 0; k < output.word.size(); k++) {
                            output.word.get(k).item.setX(x);
                            output.word.get(k).item.setY(y);
                            x += output.word.get(k).item.getLayoutBounds().getWidth();
                            if (output.word.get(k).item.getX() == margin) {
                                output.linetracker.add(output.word.get(k));
                            }
                        }
                    } else {
                        if (x != margin) {
                            y += gettextheight();
                            x = margin;
                        }
                        for (int k = 0; k < output.word.size(); k++) {
                            if (x > usableScreenWidth - 10) {
                                x = margin;
                                y += gettextheight();
                            }
                            output.word.get(k).item.setX(x);
                            output.word.get(k).item.setY(y);
                            x += output.word.get(k).item.getLayoutBounds().getWidth();
                            if (output.word.get(k).item.getX() == margin) {
                                output.linetracker.add(output.word.get(k));
                            }
                        }
                    }
                }
            }
            if (output.currentNode == output.sentinel) {
                xpos = margin;
                ypos = 0;
            } else {
                xpos = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
                ypos = output.currentNode.item.getY();
                if (output.currentNode.item.getText().equals("\n")) {
                    xpos = margin;
                    ypos += gettextheight();
                }
            }
            updateCursor();
        }



        //             for (int a = 0; a < output.local.size(); a++) {
        //                 if (a == 0) {
        //                     output.local.get(a).item.setX(x);
        //                     y = output.local.get(a).item.getY() + output.local.get(a).item.getLayoutBounds().getHeight();
        //                     output.local.get(a).item.setY(y);
        //                     if (output.local.get(a).item.getText().equals("/r")) {
        //                         y = output.local.get(a).item.getY() + output.local.get(a).item.getLayoutBounds().getHeight()/2;
        //                         output.local.get(a).item.setY(y);
        //                     }
        //                     x += output.local.get(a).item.getLayoutBounds().getWidth();
        //                 }
        //                 if (output.local.get(a).item.getX() + output.local.get(a).item.getLayoutBounds().getWidth() > usableScreenWidth - margin) {
        //                     x = margin;
        //                     y += output.local.get(a).item.getLayoutBounds().getHeight();
        //                     output.local.get(a).item.setX(x);
        //                     output.local.get(a).item.setY(y);
        //                 } else {
        //                     output.local.get(a).item.setX(x);
        //                     output.local.get(a).item.setX(y);
        //                     x += output.local.get(a).item.getLayoutBounds().getWidth();
        //                 }
        //             }
        //         }
        //     }
        //     if (output.currentNode == output.sentinel) {
        //         xpos = margin;
        //         ypos = 0;
        //     } else {
        //         xpos = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
        //         ypos = output.currentNode.item.getY();
        //     }
        //     updateCursor();
        // }
        // public void render() {
        //     output.linetracker = new ArrayList<>();
        //     double x = margin;
        //     double y = 0;
        //     boolean hasspace = false;
        //     for (int i = 0; i < output.size(); i++) { //for every letter to display
        //         if (x + output.get(i).item.getLayoutBounds().getWidth() > usableScreenWidth - margin) { //if its at the end of a line
        //             if (hasspace) {
        //                 while (!output.get(i).item.next.getText().equals(" ")) {
        //                     i--;
        //                 }
        //                 x = margin;
        //                 y += output.get(i).item.getLayoutBounds().getHeight();
        //                 output.linetracker.add(output.get(i-1));
        //                 hasspace = false;
        //             } else {
        //             x = margin;
        //             y += output.get(i).item.getLayoutBounds().getHeight(); //since theyre all the same height anyway just use another one because newline is too tall
        //             output.linetracker.add(output.get(i-1));
        //             hasspace = false;
        //             }
        //         }
        //         if (output.get(i).item.getText().compareTo("\r") == 0) {
        //             x = margin;
        //             y += output.get(i).prev.item.getLayoutBounds().getHeight();
        //             output.linetracker.add(output.get(i));
        //             hasspace = false;
        //         }
        //         output.get(i).item.setX(x); //otherwise just increase x each time
        //         output.get(i).item.setY(y);
        //         if (!output.get(i).item.getText().equals(" ")) {
        //             hasspace = true;
        //         }
        //         x += output.get(i).item.getLayoutBounds().getWidth();
        //         if (output.get(i).next == output.sentinel) {
        //             output.linetracker.add(output.get(i));
        //         }
        //     }
        //
        //     if (output.currentNode == output.sentinel) { //if you're at the start of of the file
        //         xpos = margin;
        //         ypos = 0;
        //     } else { //otherwise set x to the spot after the currentNode
        //         xpos = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
        //         ypos = output.currentNode.item.getY();
        //     }
        //
        //     updateCursor(); //sets the cursor to xpos and ypos
        //
        public double gettextheight() {
            Text tester = new Text("a");
            tester.setFont(Font.font(fontName, fontSize));
            return tester.getLayoutBounds().getHeight();
        }

        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
        }

        @Override
        public void handle(KeyEvent keyEvent) {

            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !(keyEvent.isShortcutDown())) {
                String characterTyped = keyEvent.getCharacter();
                if (characterTyped.compareTo("\r") == 0) {
                    Text newline = new Text(xpos, ypos, "\n");
                    output.add(newline);
                    render();
                    updateCursor();
                    keyEvent.consume();
                }
                else if (characterTyped.length() > 0 && characterTyped.charAt(0) != 8) {
                    Text added = new Text(xpos, ypos, characterTyped);
                    added.setTextOrigin(VPos.TOP);
                    added.setFont(Font.font(fontName, fontSize));
                    output.add(added);
                    textRoot.getChildren().add(added);
                    render();
                    updateCursor();
                    keyEvent.consume();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.BACK_SPACE) {
                    if (output.currentNode != output.sentinel) {
                        textRoot.getChildren().remove(output.currentNode().item);
                        double subtract = output.delete().getLayoutBounds().getWidth();
                        xpos -= subtract;
                        render();
                    }
                    keyEvent.consume();
                }
                if (code == KeyCode.LEFT) {
                    if (output.size() > 0) {
                        if (output.currentNode != output.sentinel) {
                            if (output.currentNode.item.getText().compareTo("\n") == 0) {
                                xpos = output.currentNode.prev.item.getX() + output.currentNode.prev.item.getLayoutBounds().getWidth();
                                ypos = output.currentNode.prev.item.getY();
                                output.currentNode = output.currentNode.prev;
                            }
                            else if (xpos > margin) {
                                xpos -= output.currentNode.item.getLayoutBounds().getWidth();
                                output.currentNode = output.currentNode.prev;
                            } else {
                                xpos = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
                                ypos -= output.currentNode.item.getLayoutBounds().getHeight();
                            }
                            updateCursor();
                        }
                    }
                    keyEvent.consume();
                }
                if (code == KeyCode.RIGHT) { //still have to deal with lines where enter is pressed before
                    if (output.currentNode.next != output.sentinel) {
                        if (output.currentNode.next.item.getText().compareTo("\n") == 0) {
                            output.currentNode = output.currentNode.next;
                            xpos = margin;
                            ypos += output.currentNode.item.getLayoutBounds().getHeight()/2;
                        }
                        else if (xpos + output.currentNode.next.item.getLayoutBounds().getWidth() < usableScreenWidth - margin) {
                            xpos = xpos + output.currentNode.next.item.getLayoutBounds().getWidth();
                            updateCursor();
                            output.currentNode = output.currentNode.next;
                        } else {
                            xpos = margin;
                            ypos +=output.currentNode.next.item.getLayoutBounds().getHeight();
                        }
                        updateCursor();
                    }
                    keyEvent.consume();
                }
                if (code == KeyCode.DOWN) { //the thing can't tell if its on the last line...
                    if (output.currentNode != output.sentinel && output.currentNode.next != output.sentinel &&
                      output.currentNode.item.getY() != output.linetracker.get(output.linetracker.size() - 1).item.getY()) {
                        double tempx = xpos;
                        while (output.currentNode.next.item.getText().compareTo("\n") != 0 && tempx + output.currentNode.next.item.getLayoutBounds().getWidth() <= usableScreenWidth - margin) {
                            output.currentNode = output.currentNode.next;
                            tempx += output.currentNode.item.getLayoutBounds().getWidth();
                        }
                        if (output.currentNode.next.item.getText().compareTo("\n") == 0) {
                            output.currentNode = output.currentNode.next;
                            tempx = margin;
                            ypos +=output.currentNode.item.getLayoutBounds().getHeight()/2;
                        } else {
                            ypos = output.currentNode.next.item.getY();
                            tempx = output.currentNode.next.item.getX();
                        }
                        while (tempx < xpos && output.currentNode.next != output.sentinel && output.currentNode.next.item.getText().compareTo("\r") != 0) {
                            output.currentNode = output.currentNode.next;
                            tempx += output.currentNode.item.getLayoutBounds().getWidth();
                        }
                        if (xpos - output.currentNode.item.getX() < tempx - xpos) {
                            tempx -= output.currentNode.item.getLayoutBounds().getWidth();
                            output.currentNode = output.currentNode.prev;
                        }
                        xpos = tempx;
                        updateCursor();
                        keyEvent.consume();
                    }
                }
                if (code == KeyCode.UP) {
                    if (output.currentNode != output.sentinel && output.currentNode.item.getY() > 0) {
                        double tempx = xpos;
                        while (tempx >= margin) {
                            tempx -= output.currentNode.item.getLayoutBounds().getWidth();
                            output.currentNode = output.currentNode.prev;
                        }
                        ypos = output.currentNode.item.getY();
                        tempx = output.currentNode.item.getX() + output.currentNode.item.getLayoutBounds().getWidth();
                        while (tempx - output.currentNode.item.getLayoutBounds().getWidth() > xpos) {
                            tempx -= output.currentNode.item.getLayoutBounds().getWidth();
                            output.currentNode = output.currentNode.prev;
                        }
                        if (xpos - output.currentNode.item.getX() < Math.abs(tempx - xpos)) {
                            tempx -= output.currentNode.item.getLayoutBounds().getWidth();
                            output.currentNode = output.currentNode.prev;
                        }
                        xpos = tempx;
                        updateCursor();
                        keyEvent.consume();
                    }
                }
                if (keyEvent.isShortcutDown()) {
                    if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                        fontSize += 4;
                        int index = 0;
                        for (int i = 0; i < output.size(); i++) {
                            output.get(i).item.setFont(Font.font(fontName, fontSize));
                        }
                        render();
                        updateCursor();
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.MINUS) {
                        fontSize = Math.max(0, fontSize - 4);
                        int index = 0;
                        for (int i = 0; i < output.size(); i++) {
                            output.get(i).item.setFont(Font.font(fontName, fontSize));
                        }
                        render();
                        updateCursor();
                        keyEvent.consume();
                    } else if (code == KeyCode.P) {
                        cursorprint(cursor);
                        keyEvent.consume();
                    } else if (code == KeyCode.S) {
                        copier.save(output, filename);
                        keyEvent.consume();
                    }
                }
            }
        }

        private void updateCursor() {
            if (output.currentNode != output.sentinel) {
                cursor.setHeight(gettextheight());
            }
            cursor.setWidth(1);
            cursor.setX(xpos);
            cursor.setY(ypos);
        }

        public void cursorprint(Rectangle x) {
            System.out.println( (int) Math.round(x.getX()) + ", " + (int) Math.round(x.getY()));
        }
    }

    private class BlinkEventHandler implements EventHandler<ActionEvent> {
        private int currentColor = 0;
        private Color[] boxColors =
                {Color.WHITE, Color.BLACK};

        BlinkEventHandler() {
            changeColor();
        }

        private void changeColor() {
            cursor.setFill(boxColors[currentColor]);
            currentColor = 1 - currentColor;
        }

        @Override
        public void handle(ActionEvent event) {
            changeColor();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeCursorBlink() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        BlinkEventHandler cursorChange = new BlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(0.5), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    @Override
    public void start(Stage primaryStage) {
        // Create a Node that will be the parent of all things displayed on the screen.
        root = new Group();
        textRoot = new Group();
        root.getChildren().add(textRoot);

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);


        KeyEventHandler keyEventHandler =
                new KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);

        textRoot.getChildren().add(cursor);
        makeCursorBlink();

        primaryStage.setTitle("Editor");

        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);

        scrollBar.setMin(0);


        root.getChildren().add(scrollBar);

        usableScreenWidth = WINDOW_WIDTH - (int) Math.round(scrollBar.getLayoutBounds().getWidth());

        scrollBar.setLayoutX(usableScreenWidth);

        scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                    int y = 0;
                    if (output.size() > 0) {
                        y = (int) output.get(output.size()-1).item.getY() + (int) output.get(output.size()-1).item.getLayoutBounds().getWidth();
                    }
                    int difference = y - WINDOW_HEIGHT;
                    scrollBar.setMax(difference + 5); //add a five pixel margin
                    textRoot.setLayoutY(-1*newValue.intValue());
            }
        });

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldScreenWidth,
                    Number newScreenWidth) {
                int difference = newScreenWidth.intValue() - oldScreenWidth.intValue();
                WINDOW_WIDTH += difference;
                usableScreenWidth = WINDOW_WIDTH - (int) Math.round(scrollBar.getLayoutBounds().getWidth());
                scrollBar.setLayoutX(usableScreenWidth);
                keyEventHandler.render();
            }
        });

        filename = getParameters().getUnnamed().get(0);
        copier = new Copyfile();
        copier.open(filename, margin, 0, startfontsize, startfontname);
        output = copier.copied;
        for (int x = 0; x < output.size(); x++) {
            textRoot.getChildren().add(output.get(x).item);
        }
        keyEventHandler.render();

        scene.setOnMouseClicked(new MouseEventHandler(root));
        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {

        launch(args);
    }
}
