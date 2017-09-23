package editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

/**
 * This program copies all of the text in one file to a new file. The first command line argument
 * should be the name of the file to copy from, and the second command line argument should be the
 * name of the file to copy to.
 */
public class Copyfile {
    LinkedListDeque <Text> copied;

    public void open(String openfile, int x, int y, int startfontsize, String startfontname) {
        copied = new LinkedListDeque<>();
        String inputFilename = openfile;

        try {
            File inputFile = new File(inputFilename);

            if (!inputFile.exists()) {
                return;
            }
            FileReader reader = new FileReader(inputFile);

            BufferedReader bufferedReader = new BufferedReader(reader);

            int intRead = -1;

            while ((intRead = bufferedReader.read()) != -1) {


                char charRead = (char) intRead;
                String letter = Character.toString(charRead);
                Text read = new Text(x, y, letter);
                read.setFont(Font.font(startfontname, startfontsize));
                copied.add(read);
            }

            bufferedReader.close();
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found! Exception was: " + fileNotFoundException);
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
    }

      public void save(LinkedListDeque <Text> readfrom, String savefile) {

        String inputFilename = savefile;

        try {
            File saved = new File(savefile);

            FileWriter writer = new FileWriter(inputFilename);

            for(int i = 0; i < readfrom.size(); i++) {
                String stringread = readfrom.get(i).item.getText();
                if (stringread.equals("\r")) {
                    stringread = "\n";
                }
                writer.write(stringread);
            }

            writer.close();
        } catch (IOException ioException) {
            System.out.println("Error when copying; exception was: " + ioException);
        }
      }
}
