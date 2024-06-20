package com.example.demo;

import javafx.animation.*;
import javafx.beans.binding.StringExpression;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Game {
    Button restartbutton=new Button("Restart");
    Button startbutton=new Button("Start");
    Button revivebutton=new Button("Revive: 5 cherries");
    private double minheight=300;
    ArrayList<displayable> gameobjects=new ArrayList<>();
    Random random=new Random();
    AnchorPane pane;
    Scene scene;
    double dist=0;

    ArrayList<Platform> platforms=new ArrayList<>(3);
    private AnimationTimer ant;
    Platform p1,p2,p3;
    Stick s,s1,s2;
    hero h;
    private boolean transitioning;
    int score=0;
    int cherry_count=0;
    private Rectangle[] trashstick={null};
    private ImageView background;
    private BGHandler imhandle;
    private TextHandler tt;
    private Cherry cherry;
    private int highScore;


    public void init(AnchorPane p, Scene st,ImageView bg){
        score=0;
        cherry_count=0;
        this.background=bg;

        this.scene=st;
        this.pane=p;

        imhandle=new BGHandler(background);
        tt=new TextHandler(pane);
        p1=new Platform(50,minheight);
        transitioning=false;
        highScore=tt.getHighScore();
        p2=new Platform(random.nextDouble(200)+p1.getRect().getWidth() +200 ,minheight);

        //System.out.println(pane.getWidth());
        this.s=Stick.getInstance(0,minheight-10);
        this.h=new hero(p1.getWidth()-50+p1.getRect().getX(),minheight-50);
        this.cherry=new Cherry();

        gameobjects.add(h);gameobjects.add(p1);gameobjects.add(p2);gameobjects.add(s);gameobjects.add(cherry);
        platforms.add(p1);platforms.add(p2);

        cherry.set(platforms,minheight);


        restartbutton.setScaleX(2);
        restartbutton.setScaleY(2);
        restartbutton.setLayoutX(350);
        restartbutton.setLayoutY(400);
        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                pane.getChildren().remove(restartbutton);
                reset();
                display();
            }
        };
        restartbutton.setOnAction(event);


        startbutton.setScaleX(2);
        startbutton.setScaleY(2);
        startbutton.setLayoutX(350);
        startbutton.setLayoutY(400);

        EventHandler<ActionEvent> start = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                tt.Clear();
                pane.getChildren().remove(startbutton);
                display();
            }
        };

        startbutton.setOnAction(start);


        revivebutton.setScaleX(2);
        revivebutton.setScaleY(2);
        revivebutton.setLayoutX(300);
        revivebutton.setLayoutY(200);
        EventHandler<ActionEvent> revival= new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                saveGameData();

                reset();

                getSavedData();
                cherry_count=cherry_count-5;
                display();

            }
        };
        revivebutton.setOnAction(revival);

    }
    public void startScreen(){
        ScaleTransition startbuttontransition=new ScaleTransition(Duration.millis(1000),startbutton);
        startbuttontransition.setByX(1.5);
        startbuttontransition.setByY(1.5);
        startbuttontransition.setAutoReverse(true);
        startbuttontransition.setCycleCount(Animation.INDEFINITE);
        startbuttontransition.play();
        tt.Gamestart();
        pane.getChildren().add(startbutton);
    }
    private void saveGameData() {
        try {
            File file = new File("game_data.txt");
            FileWriter writer = new FileWriter(file);
            // Write score and cherry count to the file
            writer.write("Score: " + score + "\n");
            writer.write("Cherry count: " + cherry_count + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSavedData() {
        try {
            File file = new File("game_data.txt");
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            int savedScore = -1; // Initialize to an invalid value
            int savedCherryCount = -1; // Initialize to an invalid value

            // Reading the data from the file
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Score:")) {
                    savedScore = Integer.parseInt(line.substring(7).trim());
                    score=savedScore;
                } else if (line.startsWith("Cherry count:")) {
                    savedCherryCount = Integer.parseInt(line.substring(13).trim());
                    cherry_count=savedCherryCount;
                }
            }

            reader.close();

            // Use the retrieved score and cherry count data
            if (savedScore != -1 && savedCherryCount != -1) {
                System.out.println("Retrieved Score: " + savedScore);
                System.out.println("Retrieved Cherry count: " + savedCherryCount);
                // Assign to current score and cherry count variables if needed
                // this.score = savedScore;
                // this.cherry_count = savedCherryCount;
            } else {
                System.out.println("No saved data found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void display(){

        for(displayable i:gameobjects){
            i.display(pane);
        }
        tt.Score(score);
        tt.Cherry(cherry_count);
        tt.Highscore();

    }
    public void hideObjects(){
        for(displayable i: gameobjects){
            i.remove(pane);
        }

    }

    public void reset(){
        s.getRect().setHeight(0);
        pane.getChildren().clear();
        pane.getChildren().add(background);
        tt.Clear();
        gameobjects.clear();
        platforms.clear();
        init(pane,scene,background);


    }

    private void gameover(){
        tt.GameOver();
        if(!pane.getChildren().contains(restartbutton)){
            pane.getChildren().add(restartbutton);
        }
        if(!pane.getChildren().contains(revivebutton)&&cherry_count>=5){
            pane.getChildren().add(revivebutton);
        }

    }

    public void loop(){
        final boolean[] once = {true};

        ant=new AnimationTimer() {
            @Override
            public void handle(long l) {
                boolean crossed=(h.getRect().getWidth()+h.getRect().getX())>=platforms.get(0).getRect().getX()+10;
                if(transitioning&&h.isFlipped()){
                    //System.out.println("flipped still");
                    if(crossed&& once[0]){
                        once[0] =false;

                        h.stopWalking();
                        h.FallAnimate(pane.getHeight(),()->{gameover();});


                    }
                    if(h.getRect().getX()+h.getRect().getWidth()>=cherry.getRect().getX()&&h.getRect().getX()<=(cherry.getRect().getX()+cherry.getRect().getWidth())&&!cherry.isHidden()){
                        cherry.hide(pane);
                        cherry_count++;
                        tt.updatecherry(cherry_count);
                        //System.out.println("hwy");
                    }

                }

                s.activity(()->{transition();});

                    scene.setOnKeyReleased(e->{
                        once[0]=true;
                        if(e.getCode()==KeyCode.ENTER&&s.getConditions()[0]&&!transitioning){
                            s.getConditions()[0]= !s.getConditions()[0];

                        }
                    });

                    scene.setOnKeyPressed(e->{
                        //System.out.println("helo");
                        if(e.getCode()==KeyCode.ENTER&&(!s.getConditions()[1]&&!transitioning)){
                            s.reset(platforms.get(0).getRect().getX()+platforms.get(0).getRect().getWidth()-s.getWidth(),h.getRect().getY()+h.getRect().getHeight()-s.getHeight(),pane,trashstick);
                            s.getConditions()[1]=true;

                        }
                        else if(e.getCode()==KeyCode.ENTER&&transitioning&&!crossed){
                            h.flip();
                        }
                    });



            }
        };
        ant.start();

    }
    public void transition(){
        transitioning=true;

        double d1=platforms.get(1).getRect().getX()-platforms.get(0).getRect().getX();
        this.dist=d1;
        double d2=platforms.get(1).getRect().getWidth()+platforms.get(1).getRect().getX()-platforms.get(0).getRect().getX()-platforms.get(0).getWidth();
        double d3=platforms.get(1).getRect().getX()-s.getRect().getX();
        double d4=d3+platforms.get(1).getWidth();
        boolean b1=platforms.get(1).inZone(s.getRect().getX()+s.getRect().getHeight());

        Platform trash=platforms.get(0);
        //double tempdist=this.dist-pane.getWidth();
        Platform temp=new Platform( platforms.get(1).getRect().getX()+platforms.get(1).getWidth()+ random.nextDouble(300)+30,pane.getHeight());

        platforms.add(temp);
        gameobjects.add(temp);
        platforms.remove(trash);
        gameobjects.remove(trash);

        if(cherry.isHidden()){

            if(d1>3*h.getRect().getWidth()){
                cherry.display(pane);
                cherry.set(platforms,minheight);
            }
        }


        if(s.getRect().getHeight()>=d3-5 && s.getRect().getHeight()<d4+5){
            if(b1){
                setscore();
                tt.OneUp();
            }
            temp.display(pane);
            pane.getChildren().remove(h.getRect());
            h.display(pane);
            temp.apperAnimation(pane.getHeight()-minheight);
            h.WalkAnimate(0,d2,0,()->{transitionall(d1,
                    ()->{
                trash.remove(pane);
                if(!cherry.isHidden() && cherry.missed(h)){
                    cherry.remove(pane);
                }
            });
            });
        }
        else{
            h.WalkAnimate(0,s.getRect().getHeight()+h.getRect().getWidth()/2,0,()->{h.FallAnimate(pane.getHeight(), this::gameover);});
        }


        //transitionall(dist);

    }
    private void setscore(){
        score++;
        tt.UpdateScore(score);
    }

    public void transitionall(double dist,Runnable aftertransition){
        setscore();
        if (score > highScore) {
            highScore = score;
            tt.UpdateScore(score);
        }
        imhandle.update();
        for(Node i:pane.getChildren()){
            if(i.getClass().equals(Rectangle.class)){
                TranslateTransition t=new TranslateTransition(Duration.millis(1000), i);
                t.setByX(-dist);
                t.play();
                t.setOnFinished(e->{
                    if(transitioning){
                        transitioning=false;
                        aftertransition.run();
                    }


                });
                if(i!=s.getRect()){ //check if object is of platform class
                    Rectangle rect=(Rectangle)i;
                   // System.out.println("platform moved\n\n");
                    t.setOnFinished(event -> {
                        rect.setX(rect.getX() + rect.getTranslateX());
                        rect.setY(rect.getY() + rect.getTranslateY());
                        rect.setTranslateX(0);
                        rect.setTranslateY(0);
                    });
                }
            }



        }


    }

}

class BGHandler{

    Image night;
    Image morning;
    Image evening;
    ImageView background;
    FadeTransition fade;
    FadeTransition fade2;
    int time;

    private int current_image_index;
    Image[] images;

    Thread t1;

    BGHandler(ImageView background){
        Image night=new Image("file:gamebg5.png");
        Image morning=new Image("file:gamebg6.png");
        Image evening=new Image("file:gamebg3.png");
        this.background=background;
        fade=new FadeTransition(Duration.millis(1000),this.background);
        fade.setFromValue(100);
        fade.setToValue(0);
        fade2=new FadeTransition(Duration.millis(1000),this.background);
        fade2.setFromValue(0);
        fade2.setToValue(100);


        images= new Image[]{morning, evening, night};
        current_image_index=0;
        time=1;
    }

    private int getNextImageIndex(){
        if(current_image_index>=2){
            current_image_index=0;
        }
        else{
            current_image_index++;
        }
        return current_image_index;
    }

    public Image getNextImage(){
        return images[getNextImageIndex()];
    }
    public void update(){
        if(time>=5){
            fade.play();
            background.setImage(getNextImage());
            fade2.play();

            time=0;
        }
        time++;

    }

}

class FadingText extends Text{
    int durationms;
    FadeTransition fade;

    FadingText(String content,int duration_milllisecond){
        super(content);
        this.durationms=duration_milllisecond;
        this.fade=new FadeTransition(Duration.millis(durationms),this);
        fade.setFromValue(100);
        fade.setToValue(0);
    }

    public void display(AnchorPane pane,double x,double y){
        this.setX(x);
        this.setY(y);
        pane.getChildren().add(this);
        fade.play();
        fade.setOnFinished(e->{
            pane.getChildren().remove(this);
        });
    }

}

class TextHandler{
    private Text gameovertext;
    private FadingText oneuptext;
    private Text scoretext;
    private Text cherrytext;
    private Text stickherotext;
    private Text highScoreText;
    private AnchorPane pane;
    private int highScore = 0;

    TextHandler(AnchorPane p){
        this.pane=p;
        stickherotext=new Text(50,120,"Stick Hero");
        cherrytext=new Text(20,70,"cherries :"+0);
        gameovertext=new Text("Game Over");
        scoretext=new Text(20,40,"Score :"+0);
        cherrytext.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
        cherrytext.setFill(Color.WHITE);
        oneuptext=new FadingText("Perfect!!\n  +1",1000);
        scoretext.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
        scoretext.setFill(Color.WHITE);
        oneuptext.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 40));
        oneuptext.setFill(Color.WHITE);
        gameovertext.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 100));
        gameovertext.setFill(Color.WHITE);
        stickherotext.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 100));
        stickherotext.setFill(Color.BLACK);
        highScoreText = new Text("High Score: " + getHighScore());
        highScoreText.setFont(Font.font("verdana", FontWeight.BOLD, FontPosture.REGULAR, 30));
        highScoreText.setFill(Color.WHITE);
        highScoreText.setY(100);
        highScoreText.setX(20);

    }
    public int getHighScore() {

        try (BufferedReader br = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = br.readLine();
            if (line != null && !line.isEmpty()) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        return highScore;
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void Gamestart(){
        pane.getChildren().add(stickherotext);
    }
    void OneUp(){
        oneuptext.display(pane,pane.getWidth()/2-100,pane.getHeight()/4);
    }
    void Cherry(int count){

        cherrytext.setText("cherries :"+count);
        pane.getChildren().add(cherrytext);
    }
    void UpdateScore(int score){
        scoretext.setText("SCORE: " + score);
        if (score > highScore) {
            highScore = score;
            highScoreText.setText("High Score: " + highScore);
            saveHighScore();
        }
    }
    void updatecherry(int cherries){
        cherrytext.setText("cherries :"+cherries);
    }
    void Score(int score){
        scoretext.setText("Score: "+score);
        pane.getChildren().add(scoretext);
    }
    void GameOver(){
        gameovertext.setY(pane.getHeight()/2-100);
        gameovertext.setX(50);
        //System.out.println(gameovertext.getWrappingWidth());
        if(!pane.getChildren().contains(gameovertext)){
            pane.getChildren().add(gameovertext);
        }

    }
    void Highscore(){
        pane.getChildren().add(highScoreText);
    }
    void Clear(){
        pane.getChildren().remove(cherrytext);
        pane.getChildren().remove(scoretext);
        pane.getChildren().remove(gameovertext);
        pane.getChildren().remove(stickherotext);
    }
}
