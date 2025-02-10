package org.redlightwhisperer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.redlightwhisperer.gui.IntersectionController;

import java.io.IOException;

import java.io.File;
import java.util.List;

public class Main extends Application {

    public static void main(String[] args) {
        System.out.println(args.length);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        List<String> args = getParameters().getRaw();

        for (String arg : args) {
            System.out.println(arg);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        CommandList commandList;
        try {
            System.out.println(args.getFirst());
            commandList = objectMapper.readValue(new File("src/main/resources/" + args.getFirst()), CommandList.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Couldn't parse json, error: " + e);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open the file, error: " + e);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main_view.fxml"));
        Pane root = loader.load();
        IntersectionController controller = loader.getController();
        controller.setCommandListAndOutputFile(commandList, args.get(1));

        Scene scene = new Scene(root, 800, 800);
        primaryStage.setTitle("Red Light Whisperer Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}