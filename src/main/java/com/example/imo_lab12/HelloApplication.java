package com.example.imo_lab12;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class HelloApplication extends Application {

    private Label weatherLabel;

    private Timer timer;

    private int state = 0;
    private double t = 0;
    private double tau = 0;
    private double totalTime = 0;

    private List<State> states;

    double[][] Q = {
            {-0.4, 0.3, 0.1},
            {0.4, -0.8, 0.4},
            {0.1, 0.4, -0.5}
    };

    boolean isRunning;

    private double[] finalProbs = {
            24.0 / 63.0,
            19.0 / 63.0,
            20.0 / 63.0
    };

    @Override
    public void start(Stage primaryStage) {
        weatherLabel = new Label("Погода");
        weatherLabel.setStyle("-fx-font-size: 14pt;");
        states = new ArrayList<>();
        states.add(new State("Солнечно"));
        states.add(new State("Дождливо"));
        states.add(new State("Пасмурно"));

        Button startButton = new Button("Старт");
        Button stopButton = new Button("ОТЧЕТ");

        startButton.setOnAction(e -> startTimer());
        stopButton.setOnAction(e -> stopTimer());

        VBox root = new VBox(10);
        root.setPadding(new Insets(20)); // Установка отступов от краев
        root.getChildren().addAll(weatherLabel, startButton, stopButton);
        root.setStyle("-fx-alignment: center;"); // Выравнивание по центру

        // Создание сцены и установка корневого элемента
        Scene scene = new Scene(root, 300, 200);

        // Установка заголовка окна и отображение сцены
        primaryStage.setTitle("Погода");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void stopTimer() {
        isRunning = false;

        System.out.println("Всего прошло времени: " + totalTime);

        for (State stateObject: states)
        {
            System.out.println("В состоянии " + stateObject.name + " потрачено: " + stateObject.time + ". " + stateObject.time / totalTime);
        }

        double chiSquare = 0;

        for (int i = 0; i < states.size(); i++)
        {
            chiSquare += (states.get(i).time * states.get(i).time ) / (totalTime * finalProbs[i]);
        }
        chiSquare -= totalTime;

        System.out.println("Хи-квадрат: " + chiSquare);

        if (chiSquare > 6)
        {
            System.out.println("Различия значительны");
        }
        else
        {
            System.out.println("Различия незначительны");
        }

    }

    private void startTimer() {
        timer = new Timer();
        isRunning = true;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    switchState();
                });
            }
        }, 0, 100);

    }


    private void timerTick() {
        t += 0.1;
        if (t > tau) {
            switchState();
        }

        if (!isRunning) {
            timer.cancel();
        }
    }

    private void switchState() {
        tau = Math.log(ThreadLocalRandom.current().nextDouble()) / Q[state][state];
        states.get(state).time += tau;
        totalTime += tau;
        t = 0;

        double A = ThreadLocalRandom.current().nextDouble();
        for (int i = 0; i < states.size(); i++) {
            if (i == state) continue;
            A -= -Q[state][i] / Q[state][state];
            if (A <= 0) {
                state = i;
                break;
            }
        }
        weatherLabel.setText(states.get(state).name);
    }

    public static void main(String[] args) {
        launch(args);
    }

}