package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class homework {

    public static class Coordinate{
        int x, y;
        public Coordinate(){
            x = -1;
            y = -1;
        }
        public Coordinate(int a, int b){
            x = a;
            y = b;
        }

        public boolean equals(Object obj) {
            Coordinate s = (Coordinate) obj;
            return this.x == s.x && this.y == s.y;
        }

        @Override
        public int hashCode() {
            final int prime = 31;//
            //int result = prime * num + area;

            return prime * x + y;
        }
    }

    private static int[] deltaX = {1, 0, 0, -1};
    private static int[] deltaY = {0, 1, -1, 0};
    private static double bestAlpha = Integer.MIN_VALUE;
    private static Coordinate nextMove = new Coordinate(-1, -1);

    public static void main(String[] args) {


        //Date timeone = new Date();
        long start = System.currentTimeMillis();
        double startt = (double) start;

        File file = new File("input");

        double timeLeft = 0;
        int n = 0;
        int numFruit = 0;
        int[][] grid = new int[n][n];

        try {

            Scanner sc = new Scanner(file);

            while (sc.hasNextLine()) {
                //method = sc.nextLine();
                if(sc.hasNext()){
                    n = sc.nextInt();
                }else{
                    break;
                }

                if(sc.hasNext()){
                    numFruit = sc.nextInt();
                }else{
                    break;
                }

                if(sc.hasNext()){
                    timeLeft = sc.nextDouble();
                }else{
                    break;
                }

                sc.nextLine();
                grid = new int[n][n];
                //System.out.println("" + timeLeft + n + numFruit);
                for(int i = 0; i < n; i++){
                    String str = sc.nextLine();
                    //System.out.println(str);
                    for(int j = 0; j < n; j++){
                        switch (str.charAt(j)){
                            case '0':
                                grid[i][j] = 0;
                                break;
                            case '1':
                                grid[i][j] = 1;
                                break;
                            case '2':
                                grid[i][j] = 2;
                                break;
                            case '3':
                                grid[i][j] = 3;
                                break;
                            case '4':
                                grid[i][j] = 4;
                                break;
                            case '5':
                                grid[i][j] = 5;
                                break;
                            case '6':
                                grid[i][j] = 6;
                                break;
                            case '7':
                                grid[i][j] = 7;
                                break;
                            case '8':
                                grid[i][j] = 8;
                                break;
                            case '9':
                                grid[i][j] = 9;
                                break;
                            case '*':
                                grid[i][j] = -1;
                                break;
                            default:break;
                        }
                        //System.out.println(grid[i][j]);
                    }
                }
            }
            sc.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        int depth;
        if(timeLeft > 50) {
            depth = 5;
        }else if(timeLeft > 20){
            depth = 4;
        }else{
            depth = 3;
        }

        //if(n > 22 && numFruit >=8){depth = 3;}//avoid too complex

        double alpha = Integer.MIN_VALUE;
        double beta = Integer.MAX_VALUE;
        boolean myTurn = true;
        double myScore = 0;
        double oppoScore = 0;

        if(timeLeft > 10) {
            double ans = minimax(depth, grid, depth, alpha, beta, myTurn, myScore, oppoScore, timeLeft * 1000 + startt);
        }else{
            greedy(grid);
        }
        if(nextMove.x == -1) {
            //System.out.println(nextMove.x + " " + nextMove.y);
            greedy(grid);
        }
        writeFile(grid, nextMove);
        //System.out.println(ans);
        //Date timetwo = new Date();
        long end = System.currentTimeMillis();
        double endd = (double) end;
        double t = endd - startt;
        System.out.println(t);
    }

    private static double minimax(int depth, int[][] grid, int currDepth, double alpha, double beta, boolean myTurn, double myScore, double oppoScore, double time){
        if(currDepth == 0){
            return myScore - oppoScore;
        }
        int n = grid.length;
        HashSet<Coordinate> visited = new HashSet<>();
        if(myTurn){
            ArrayList<ArrayList<Coordinate>> deadNumList = new ArrayList<>();
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    //Date timetwo = new Date();                //check time
                    long end = System.currentTimeMillis();
                    if(end > time - 10000){
                        //greedy(grid);
                        break;
                    }
                    int count = 0;                            //if it is empty
                    if(grid[i][j] == -1){
                        if(i == n - 1) count++;
                        if(count == n){return alpha;}
                        continue;
                    }

                    if (visited.contains(new Coordinate(i, j))) {
                        continue;
                    }

                    Coordinate curr = new Coordinate(i, j);
                    HashSet<Coordinate> bfsVisited = new HashSet<>();
                    ArrayList<Coordinate> deadNums = new ArrayList<>();
                    Queue<Coordinate> queue = new LinkedList<>();
                    bfsVisited.add(curr);
                    deadNums.add(curr);
                    queue.offer(curr);
                    int cancelNum = grid[i][j];

                    deadNums = bfs(grid, bfsVisited, deadNums, queue, cancelNum);
                    deadNumList.add(deadNums);
                    visited.addAll(deadNums);
                }
            }

            deadNumList.sort(new Comparator<ArrayList<Coordinate>>() {
                @Override
                public int compare(ArrayList<Coordinate> o1, ArrayList<Coordinate> o2) {
                    return o2.size() - o1.size();
                }
            });
            int size = 40;
            if(deadNumList.size() < size) {size = deadNumList.size();}
            for(int i = 0; i < size; i++){
                ArrayList<Coordinate> deadNums = deadNumList.get(i);
                myScore += deadNums.size() * deadNums.size();

                int[][] prevGrid = new int[n][n];
                for(int k = 0; k < n; k++){
                    for(int l = 0; l < n; l++){
                        prevGrid[k][l] = grid[k][l];
                    }
                }

                int[][] newGrid = deleteNums(deadNums, grid);
                //Date timetwo = new Date();                //check time
                long end = System.currentTimeMillis();
                if(end > time - 10000){
                    //greedy(grid);
                    break;
                }
                alpha = Math.max(alpha, minimax(depth, newGrid, currDepth - 1, alpha, beta, false, myScore, oppoScore, time));

                if(currDepth == depth && alpha > bestAlpha){
                    bestAlpha = alpha;
                    nextMove = deadNums.get(0);
                }

                if(beta <= alpha) return beta;
                myScore -= deadNums.size() * deadNums.size();

                for(int k = 0; k < n; k++){
                    for(int l = 0; l < n; l++){
                        grid[k][l] = prevGrid[k][l];
                    }
                }
            }
            greedy(grid);
            return alpha;
        }else{
            ArrayList<ArrayList<Coordinate>> deadNumList = new ArrayList<>();
            for(int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    //Date timetwo = new Date();                //check time
                    long end = System.currentTimeMillis();
                    if(end > time - 50000){
                        //greedy(grid);
                        break;
                    }
                    int count = 0;                            //if it is empty
                    if(grid[i][j] == -1){
                        if(i == n - 1) count++;
                        if(count == n){return alpha;}
                        continue;
                    }

                    if (visited.contains(new Coordinate(i, j))) {
                        continue;
                    }

                    Coordinate curr = new Coordinate(i, j);
                    HashSet<Coordinate> bfsVisited = new HashSet<>();
                    ArrayList<Coordinate> deadNums = new ArrayList<>();
                    Queue<Coordinate> queue = new LinkedList<>();
                    bfsVisited.add(curr);
                    deadNums.add(curr);
                    queue.offer(curr);
                    int cancelNum = grid[i][j];

                    deadNums = bfs(grid, bfsVisited, deadNums, queue, cancelNum);
                    deadNumList.add(deadNums);
                    visited.addAll(deadNums);
                }
            }

            deadNumList.sort(new Comparator<ArrayList<Coordinate>>() {
                @Override
                public int compare(ArrayList<Coordinate> o1, ArrayList<Coordinate> o2) {
                    return o2.size() - o1.size();
                }
            });
            int size = 40;
            if(deadNumList.size() < size) {size = deadNumList.size();}
            for(int i = 0; i < size; i++){
                ArrayList<Coordinate> deadNums = deadNumList.get(i);
                oppoScore += deadNums.size() * deadNums.size();

                int[][] prevGrid = new int[n][n];
                for(int k = 0; k < n; k++){
                    for(int l = 0; l < n; l++){
                        prevGrid[k][l] = grid[k][l];
                    }
                }

                int[][] newGrid = deleteNums(deadNums, grid);
                //Date timetwo = new Date();                //check time
                long end = System.currentTimeMillis();
                if(end > time - 50000){
                    //greedy(grid);
                    break;
                }
                beta = Math.min(beta, minimax(depth, newGrid, currDepth - 1, alpha, beta, true, myScore, oppoScore, time));

//                if(currDepth == depth && alpha > bestAlpha){
//                    bestAlpha = alpha;
//                    nextMove = deadNums.get(0);
//                }

                if(beta <= alpha) return alpha;
                oppoScore -= deadNums.size() * deadNums.size();

                for(int k = 0; k < n; k++){
                    for(int l = 0; l < n; l++){
                        grid[k][l] = prevGrid[k][l];
                    }
                }
            }
            greedy(grid);
            return beta;
        }
    }

    private static void greedy(int[][] grid){
        int n = grid.length;
        int best = 0;
        //Coordinate bestPos = new Coordinate();
        ArrayList<Coordinate> bestdeadNums = new ArrayList<>();
        HashSet<Coordinate> visited = new HashSet<>();
        for(int i = 0; i < n; i++){
            for(int j = 0; j < n; j++){
                if(grid[i][j] == -1){
                    continue;
                }
                if(visited.contains(new Coordinate(i,j))){continue;}    //if already searched, skip

                int myScore = 0;
                Coordinate curr = new Coordinate(i, j);
                HashSet<Coordinate> bfsVisited = new HashSet<>();
                ArrayList<Coordinate> deadNums = new ArrayList<>();
                Queue<Coordinate> queue = new LinkedList<>();
                bfsVisited.add(curr);
                deadNums.add(curr);
                queue.offer(curr);
                int cancelNum = grid[i][j];
                deadNums = bfs(grid, bfsVisited, deadNums, queue, cancelNum);
                visited.addAll(deadNums);
                myScore += deadNums.size() * deadNums.size();

                if(myScore > best){
                    best = myScore;
                    nextMove = curr;
                    bestdeadNums = deadNums;
                }
            }
        }
    }

    private static ArrayList<Coordinate> bfs(int[][] grid, HashSet<Coordinate> visited, ArrayList<Coordinate> deadNums,     //find all connected nums
                                             Queue<Coordinate> queue, int cancelNum){
        int n = grid.length;
        while (!queue.isEmpty()){
            Coordinate curr = queue.poll();

            for(int i = 0; i < 4; i++){
                if(curr.x + deltaX[i] < 0 || curr.x + deltaX[i] > n - 1 || curr.y + deltaY[i] < 0 || curr.y + deltaY[i] > n - 1){
                    continue;
                }
                if(grid[curr.x + deltaX[i]][curr.y + deltaY[i]] == -1){
                    continue;
                }
                Coordinate newGuy = new Coordinate(curr.x + deltaX[i], curr.y + deltaY[i]);
                if(!visited.contains(newGuy) && grid[curr.x + deltaX[i]][curr.y + deltaY[i]] == cancelNum){
                    visited.add(newGuy);
                    deadNums.add(newGuy);
                    queue.offer(newGuy);
                }
            }
        }
        return deadNums;
    }

    private static int[][] deleteNums(ArrayList<Coordinate> deadNums, int[][] grid){
        int n = grid.length;
        Collections.sort(deadNums, new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate o1, Coordinate o2) {
                if(o1.y != o2.y){
                    return o1.y - o2.y;
                }else{
                    return o1.x - o2.x;
                }
            }
        });

        int countContinue = 1;
        for (int i = 0; i < deadNums.size(); i++) {
            int currCol = deadNums.get(i).y;
            int currRow = deadNums.get(i).x;
            if(i < deadNums.size() - 1 && currCol == deadNums.get(i + 1).y && currRow + 1 == deadNums.get(i + 1).x){
                countContinue++;
                continue;
            }

            for(int row = n - 1; row >= 0; row--){            //move the elememts after cancellation
                if(row < currRow - countContinue + 1){
                    grid[row + countContinue][currCol] = grid[row][currCol];
                }
            }

            for(int j = 0; j < countContinue; j++){
                grid[j][currCol] = -1;       //assign the blank as -1
            }

            countContinue = 1;                  //reset the countContinue
        }
        return grid;
    }

    private static void writeFile(int[][] grid, Coordinate nextMove){
        String[] s = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
        int len = grid.length;
        HashSet<Coordinate> bfsVisited = new HashSet<>();
        ArrayList<Coordinate> deadNums = new ArrayList<>();
        Queue<Coordinate> queue = new LinkedList<>();
        bfsVisited.add(nextMove);
        deadNums.add(nextMove);
        queue.offer(nextMove);
        int cancelNum = grid[nextMove.x][nextMove.y];
        deadNums = bfs(grid, bfsVisited, deadNums, queue, cancelNum);
        grid = deleteNums(deadNums, grid);
        try{
            File file = new File("output.txt");
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            writer.println(s[nextMove.y] + (nextMove.x + 1) + "");

            for(int i = 0; i < len; i++){
                for(int j = 0; j < len; j++){
                    switch (grid[i][j]) {
                        case 0:
                            writer.print("0");
                            break;
                        case 1:
                            writer.print("1");
                            break;
                        case 2:
                            writer.print("2");
                            break;
                        case 3:
                            writer.print("3");
                            break;
                        case 4:
                            writer.print("4");
                            break;
                        case 5:
                            writer.print("5");
                            break;
                        case 6:
                            writer.print("6");
                            break;
                        case 7:
                            writer.print("7");
                            break;
                        case 8:
                            writer.print("8");
                            break;
                        case 9:
                            writer.print("9");
                            break;
                        case -1:
                            writer.print("*");
                            break;
                        default:
                            break;
                    }
                }
                writer.println();
            }
            writer.close();
        } catch (IOException e) {
            // do something
        }
    }
}

