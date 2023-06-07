/*
 * Snake game developed by Théo-Selim CHOUABBIA (2022 - 2023)
 * Computer sciences UIT of Arles in Aix-Marseille University
 * ------------------------
 * This is my first project in Java
 * ------------------------
 * I choosed to code and comment in english for the sake of the exercice
 * I hope you'll enjoy it
 */

package com.snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Snake
 * @author Théo-Selim
 * @since 1.0
 * @version 1.0
 * @see JFrame
 */
public class App extends JPanel
{
    private final static int WIDTH = 50;
    private Deque<SnakePart> snake = new ArrayDeque<>();
    private Point apple = new Point(0, 0);
    private Random random = new Random();

    private boolean isGrowing = false;
    private boolean isGameOver = false;

    private int offset = 0;
    private int lastDir = 39; // Essentially to block the snake to go back on itself
    private int newDir = 39;

    /*
     * Main method
     * @description Initialize the key listener and show the JFrame (window)
     * @param args
     * @since 1.0
     * @version 1.0
     * @see JFrame
     */
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Snake");
        final App panel = new App();

        frame.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                // Obligation to implement this virtual method
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // Obligation to implement this virtual method
            }

            @Override
            public void keyPressed(KeyEvent e) {
                panel.onKeyPressed(e.getKeyCode());
            }

        });
        
        frame.setContentPane(panel);
        frame.pack();
        frame.setSize(12 * WIDTH + frame.getInsets().left + frame.getInsets().right, 12 * WIDTH + frame.getInsets().top + frame.getInsets().bottom);        
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    /*
     * Constructor
     * @description Initialize the game
     * @since 1.0
     * @version 1.0
     * @see JFrame
     * @see Thread
     */
    public App(){
        createApple();
        snake.add(new SnakePart(0, 0, 39));
        setBackground(Color.WHITE);
        new Thread(new Runnable(){
            @Override
            public void run(){
                while(true){
                    repaint();
                    try{
                        Thread.sleep(1000/60l);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /*
     * createApple method
     * @description Create an apple on the map (random position) where the snake is not
     * @since 1.0
     * @version 1.0
     * @see Point
     * @see Random
     */
    public void createApple() {
        boolean isOnSnake = false;
        int maxAttempts = 100; // Limite de tentatives
        int attempts = 0; // Compteur de tentatives
    
        do {
            apple.x = random.nextInt(12);
            apple.y = random.nextInt(12);
            isOnSnake = false;
    
            for (SnakePart p : snake) {
                if (p.x == apple.x && p.y == apple.y) {
                    isOnSnake = true;
                    break;
                }
            }
    
            attempts++;
        } while (isOnSnake && attempts < maxAttempts);
    }
    
    /*
     * Paint method
     * @description Paint the snake and the apple
     * @param g
     * @since 1.0
     * @version 1.0
     * @see Graphics
     * @see Point
     * @see Color
     * @see Font
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if(isGameOver){
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", 90, 90));
            g.drawString("Game over", WIDTH*12/2 - g.getFontMetrics().stringWidth("Game over")/2, WIDTH*12/2);
            return;
        }

        offset+=5;
        SnakePart head = null;

        if(offset == WIDTH){
            offset = 0;
            try{
                head = (SnakePart) snake.getFirst().clone();
                head.move();
                head.dir = newDir;
                snake.addFirst(head);
                if(head.x == apple.x && head.y == apple.y){
                    createApple();
                    isGrowing = true;
                }
                if(!isGrowing)
                    snake.pollLast();
                else
                    isGrowing = false;

                if(newDir != lastDir) // If the snake is not going back on itself
                    lastDir = newDir;
            }catch(CloneNotSupportedException e){
                e.printStackTrace();
            }
        }

        g.setColor(Color.RED);
        g.fillOval(apple.x * WIDTH + WIDTH/4, apple.y * WIDTH + WIDTH/4, WIDTH/2, WIDTH/2);

        g.setColor(Color.DARK_GRAY);
        for(SnakePart p : snake){
            if(offset == 0){
                if(p != head){
                    if(p.x == head.x && p.y == head.y){
                        isGameOver = true;
                    }
                }
            }
            if(p.dir == 37 || p.dir == 39)
                g.fillRect(p.x * WIDTH + ((p.dir == 37) ? -offset : offset), p.y * WIDTH, WIDTH, WIDTH);
            else
                g.fillRect(p.x * WIDTH, p.y * WIDTH + ((p.dir == 38) ? -offset : offset), WIDTH, WIDTH);
        }
        g.setColor(Color.BLACK);
        g.drawString("Score : " + (snake.size()-1), WIDTH*12/2 - g.getFontMetrics().stringWidth("Score : " + (snake.size()-1))/2, 20);
        
    }

    /*
     * onKeyPressed method
     * @description Change the direction of the snake when a key is pressed
     * @param keyCode
     * @since 1.0
     * @version 1.0
     * @see KeyEvent
     */
    public void onKeyPressed(int keyCode){
        if(keyCode >=37 && keyCode <= 40){
            if((snake.size() == 1 || Math.abs(keyCode - newDir) != 2) && Math.abs(keyCode - lastDir) != 2)
                newDir = keyCode;
        }
    }    

    /*
     * SnakePart class
     * @description Represent a part of the snake (one square)
     * @since 1.0
     * @version 1.0
     * @see Object
     */
    class SnakePart{
        public int x, y, dir;
    
        public SnakePart(int _x, int _y, int _dir){
            x = _x;
            y = _y;
            dir = _dir;
        }

        public void move(){
            if(dir == 37 || dir == 39){
                x += (dir == 37) ? -1 : 1;
                if(x > 12)
                    x = -1;
                else if(x < -1)
                    x = 12;            
            }
            else{
                y += (dir == 38) ? -1 : 1;
                if(y > 12)
                    y = -1;
                else if(y < -1)
                    y = 12;
            }
        }
    
        /*
         * clone method
         * @description Clone a SnakePart
         * @since 1.0
         * @version 1.0
         * @see Object
         * @see CloneNotSupportedException
         */
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return new SnakePart(x, y, dir);
        }
    }
}
