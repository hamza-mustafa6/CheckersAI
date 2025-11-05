package com.Hamza.RLCheckers.CheckersLogic;

import javax.swing.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class RL {

    public double[] weights; 
    public double discountFactor;
    public double learningRate; 
    public double epsilon; 
    public Random random = new Random();
    public Board finalBoardAfterEpisode;
    public Board currentBoard;
    public int numWhiteWin = 0;
    public int numBlackWin = 0;
    // public static void main(String[] args) {
    //     RL agent = new RL(0.99, 0.05, 0.01);
    //     agent.train(100);
    // }
    public RL(double discountFactor, double learningRate, double epsilon){
        this.discountFactor = discountFactor;
        this.learningRate = learningRate; 
        this.epsilon = epsilon;
        loadWeights("weights.txt");

    }


    public void loadWeights(String filename) {
        File weightsFile = new File(filename);
        if (!weightsFile.exists() || weightsFile.length() == 0) {
            System.out.println("Initializing weights to 0.");
            weights = new double[10];
            Arrays.fill(weights, 0.0);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String[] listOfWeights = reader.readLine().trim().split(",");
            weights = new double[listOfWeights.length];
            for (int i = 0; i < listOfWeights.length; i++) {
                weights[i] = Double.parseDouble(listOfWeights[i]);
            }
            System.out.println("Weights loaded from file." + Arrays.toString(weights));

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading weights. Initializing to 0.");
            weights = new double[10];
            Arrays.fill(weights, 0.0);
        }
    }

    public void saveWeights(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (double weight : weights) {
                writer.print(weight+ ",");
            }
        } catch (IOException e) {
            System.err.println("Failed to save weights: " + e.getMessage());
        }
    }


    public double[] getFeatures(Board board, Move move){
        /*Features at index
            * 0: numOfMyPieces
            * 1: numOfEnemyPieces
            * 2: numOftMyKings
            * 3: numOfEnemyKings
            * 4: numOfMyPiecesUnderThreat
            * 5: numOfEnemyPiecesUnderThreat
            * 6: numOfMoves
            * 7: numOfEnemyMoves
            * 8: averageNumOfPiecesForward
            * 9: bias
            */

        double[] features = new double[10];
        int numOfMyPieces = 0;
        int numOfEnemyPieces = 0;
        int numOfMyKings = 0;
        int numOfEnemyKings = 0;
        int numOfMyPiecesUnderThreat = 0;
        int numOfEnemyPiecesUnderThreat = 0;
        int numOfMoves = 0;
        int numOfEnemyMoves = 0;
        double averageNumOfMyPiecesForward = 0.0;
        //feature 0 and 1
        for(stone[] row: board.getBoard()){
            for(stone s: row){
                if (s == null) continue;
                if(s.getTeam() == move.stone.getTeam()){
                    numOfMyPieces++; 
                    if(underThreat(board, s)) numOfMyPiecesUnderThreat++;
                    if(s.kingStatus()){
                        numOfMyKings++;
                    }
                    numOfMoves += s.getValidMoves(board.getBoard()).size();
                } else{
                    numOfEnemyPieces++; 
                    if(underThreat(board, s)) numOfEnemyPiecesUnderThreat++;
                    if(s.kingStatus()){
                        numOfEnemyKings++;
                    }
                    numOfEnemyMoves += s.getValidMoves(board.getBoard()).size();
                }
            }
        }
         averageNumOfMyPiecesForward = forwardPieces(board, move.getStone().getTeam());




        features[0] = numOfMyPieces/12.0;
        features[1] = numOfEnemyPieces/12.0;
        features[2] = numOfMyKings/12.0;
        features[3] = numOfEnemyKings/12.0;
        features[4] = numOfMyPiecesUnderThreat/12.0;
        features[5] = numOfEnemyPiecesUnderThreat/12.0;
        features[6] = numOfMoves/28.0;
        features[7] = numOfEnemyMoves/28.0;
        features[8] = averageNumOfMyPiecesForward/84.0;
        features[9] = 1.0; 
        return features; 
    }


    public boolean underThreat(Board board, stone piece) {
        int row = piece.getPosition()[0];
        int col = piece.getPosition()[1];
        char enemyTeam;
        if(piece.getTeam() == 'w'){
            enemyTeam = 'b';
        } else{
            enemyTeam = 'w'; 
        }

        int[][] directions = {{-1, -1}, {-1, 1},{1, -1},  {1, 1}};
        for (int[] dir : directions) {
            int enemyRow = row + dir[0];
            int enemyCol = col + dir[1];
            int landingRow = row - dir[0];
            int landingCol = col - dir[1];
    
            if (inBounds(enemyRow, enemyCol) && inBounds(landingRow, landingCol)) {
                stone potentialEnemy = board.getBoard()[enemyRow][enemyCol];
                stone landingSpot = board.getBoard()[landingRow][landingCol];
    
                if (potentialEnemy != null &&
                    potentialEnemy.getTeam() == enemyTeam &&
                    landingSpot == null) {
                    return true;
                }
            }
        }
    
        return false;
    }
    //helper method to determine if a position is out of bounds or not. 
    public boolean inBounds(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }


    public double qValue(double[] currFeatures){
        //dot product of features and weights
        double q = 0.0;

        for(int i = 0; i < weights.length; i++){
            q += weights[i] * currFeatures[i];
        }

        return q; 
    }

    public Move bestMove(Board currBoard, boolean whitesTurn){
        //chooses the bestMove by looping through every move option and comparing the q values of all of them and choosing the best one. remember to keep it epsilon greedy 
        Move bestMove = null; 
        double bestQValue = Double.NEGATIVE_INFINITY; 
        List<Move> validMoves = currBoard.getAllValidMoves(whitesTurn); 
        if(validMoves.isEmpty()){
            return bestMove; 
        }
        for(Move move: validMoves){
            double tempQ = qValue(getFeatures(currBoard, move)); 
            if(tempQ > bestQValue || (tempQ == bestQValue && Math.random() < 0.5)){
                bestQValue = tempQ; 
                bestMove = move; 
            }
        }
        return bestMove; 
    }
    

    public double getReward(Board oldBoard, Board newBoard, Move move){
        double reward = 0;


        char myTeam = move.getStone().getTeam();
        char enemyTeam; 
        if(myTeam == 'w'){
            enemyTeam = 'b'; 
        } else{
            enemyTeam = 'w'; 
        }

        int enemyPiecesBefore = countPieces(oldBoard, enemyTeam, false); 
        int enemyPiecesAfter = countPieces(newBoard, enemyTeam, false);
        int myKingsBefore = countPieces(oldBoard, myTeam, true);
        int myKingsAfter = countPieces(newBoard, myTeam, true);
        double forwardPiecesBefore = forwardPieces(oldBoard, move.getStone().getTeam());
        //System.out.println("Old Board");
        //oldBoard.printBoard();
        double forwardPiecesAfter = forwardPieces(newBoard, move.getStone().getTeam());
        //System.out.println("new Board");
        //newBoard.printBoard();
        if(enemyPiecesAfter < enemyPiecesBefore){
            reward += 5;
        }

        if(myKingsAfter>myKingsBefore){
            reward += 3;
        }

        if(newBoard.winner == myTeam){
            reward += 10;
        }
        if(newBoard.winner == enemyTeam){
            reward -= 10;
        }

        reward += (forwardPiecesAfter - forwardPiecesBefore);

//        boolean isWhite = false;
//        if(myTeam =='w'){
//            isWhite = true;
//        }
//
//        double newAvailableMoves = newBoard.getAllValidMoves(isWhite).size();
//
//        reward += newAvailableMoves;

        //System.out.println(reward);
        return reward;
    }


    public double forwardPieces(Board board, char team){
        double forward;
        double totalRow = 0;
        double numOfPieces = 0;



        for(stone[] row: board.getBoard()){
            for(stone s: row){
                if(s == null) continue;
                if(s.getTeam() == team){
                    totalRow += s.getPosition()[0];
                    numOfPieces ++;
                }
            }
        }
        if(numOfPieces ==0){
            return 0;
        }
        forward = totalRow/numOfPieces;
        if(team == 'w'){
            forward = 7.0-forward;
        }

        return forward;
    }
    //counts the pieces of a given time in order to compute a part of the reward. 
    public int countPieces(Board board, char team, boolean countKing){
        int numPieces = 0;
        for(stone[] row: board.getBoard()){
            for(stone s: row){
                if(s == null) continue; 

                if(s.getTeam()==team){
                    if(countKing){
                        if(s.kingStatus()){
                            numPieces++; 
                        }
                    } else{
                    numPieces++;
                    }
                }
            }
        }
        return numPieces; 
    }

    public Move randomMove(Board board, boolean whitesTurn){
        List<Move> validMoves = board.getAllValidMoves(whitesTurn); 
        int numMoves = validMoves.size(); 
        return validMoves.get(random.nextInt(numMoves));
    }

    public void episode(PlayerType white, PlayerType black,  Board board){

        int counter = 0;
        boolean whitesTurn = true;
        Move bestCurrentMove = null;
        while (!board.getAllValidMoves(whitesTurn).isEmpty() && counter < 1000){
            bestCurrentMove = null;
            PlayerType currentPlayer;
            if(whitesTurn){
                currentPlayer = white;
            } else{
                currentPlayer = black;
            }

            switch (currentPlayer) {

                case SARSA:

                    if(bestCurrentMove == null){
                        if(bestMove(board,whitesTurn) == null){
                            System.out.println("no more moves");
                            board.printBoard();
                            continue;
                        }

                        double randomMoveProb = random.nextDouble();
                        if(randomMoveProb<epsilon){
                            bestCurrentMove = randomMove(board, whitesTurn);
                        } else{
                            bestCurrentMove = bestMove(board,whitesTurn);
                        }
                    }


                    Board newBoard = board.copy();

                    int[] pos = bestCurrentMove.getStone().getPosition();
                    stone correctStone = newBoard.getStone(pos[0], pos[1]);

                    Move updatedMove = new Move(correctStone, bestCurrentMove.getMove());
                    newBoard.movePiece(updatedMove);


                    //newBoard.movePiece(bestCurrentMove);

                    //reward
                    double r = getReward(board, newBoard, bestCurrentMove);
                    //System.out.println("Reward: " + r);

                    //next move for weight adjust
                    Move nextMove = bestMove(newBoard, !whitesTurn);

                    //our features for qvalues for target and current
                    double[] currentFeatures = getFeatures(board,bestCurrentMove);
                    double currentQ = qValue(currentFeatures);
                    double targetQ;
                    if(nextMove == null){
                        targetQ = 0;
                    } else{
                        double[] newFeatures = getFeatures(newBoard, nextMove);
                        targetQ = qValue(newFeatures);
                    }

                    double difference = learningRate*(r + discountFactor * targetQ - currentQ);

                    for(int i = 0; i<weights.length; i++){
                        weights[i] += difference*currentFeatures[i];
                    }
                    //System.out.println("Updated weights: " + Arrays.toString(weights));


                    board = newBoard;
                    bestCurrentMove = nextMove;

                    //board.printBoard();
                    counter++;
                    break;

                case ALPHABETA:
                    AlphaBeta abplayer = new AlphaBeta();

                    Move bestAlphaBetaMove = abplayer.bestmove(3, board, false);
//                    Board alphaBetaBoard = board.copy();
//                    alphaBetaBoard.movePiece(bestAlphaBetaMove);
//                    board = alphaBetaBoard;
                    board.movePiece(bestAlphaBetaMove);
                    //board.printBoard();
                    counter++;
                    break;
                case HUMAN:

                    break;
            }


            whitesTurn = !whitesTurn;
        }

        board.printBoard();
        finalBoardAfterEpisode = board.copy();

        if(board.winner == 'w'){
            numWhiteWin ++;
        }
        if(board.winner == 'b'){
            numBlackWin ++;
        }



    }

    public void train(int epochs){
        for(int i = 0; i< epochs; i++){
            System.out.println("Episode " + i);
            currentBoard = new Board(); 
            episode(PlayerType.SARSA, PlayerType.ALPHABETA, currentBoard); 
        }
        System.out.println("finished " + epochs + " epochs");
        System.out.println("Finished with weights: " + Arrays.toString(weights));
        saveWeights("weights.txt");

        System.out.println("White won: " + numWhiteWin);
        System.out.println("Black won: " + numBlackWin);

        //You can add this after every single game, and it can show you everything moving which is probably nicer to look at than just the last episode.
        // However for training's sake, it would be a hastle.

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Checkers Viewer");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new CheckersViewer(finalBoardAfterEpisode));
            frame.pack();
            frame.setResizable(false);
            frame.setVisible(true);
        });

    }
    
}
