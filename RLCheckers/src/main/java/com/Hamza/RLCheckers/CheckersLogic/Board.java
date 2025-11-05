package com.Hamza.RLCheckers.CheckersLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//A King is going to change the letter to uppercase
public class Board{
    
    public char winner; 
    public stone boardList[][];

    public Board(){
        this.boardList = new stone[8][8]; 
        //Makes the Board, assigning white and black pieces accordingly
        for(int i = 0; i < 8; i++){
            for(int j = 0; j<8;j++){
                if((i+j)%2==1){
                    if(i<=2){
                        boardList[i][j] = new stone(new int[]{i, j}, 'b');
                    }
                    if(i>=5){
                        boardList[i][j] = new stone(new int[]{i, j}, 'w'); 
                    }
                } 
            }
        }
    }

    public Board(Board copy){
        this.boardList = new stone[8][8];
        this.winner = copy.winner;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (copy.boardList[i][j] != null) {
                    this.boardList[i][j] = new stone(copy.boardList[i][j]); 
                }
            }
        }
    }
    public Board copy(){
        return new Board(this); 
    }
    
    public stone[][] getBoard(){
        return boardList; 
    }

    public stone getStone(int row, int col){
        return boardList[row][col]; 
    }
    
    public void movePiece(Move move){
        if(move == null){
            System.out.println("Attempting to a null move");
        } else{

        stone piece = move.getStone();
        boolean isWhite; 
        if(move.getStone().getTeam() == 'w'){
            isWhite = true;
        } else{
            isWhite = false; 
        }

        int[] moveToPosition = move.getMove(); 



        int row = moveToPosition[0];
        int col = moveToPosition[1]; 
        if(checkIfValid(piece, moveToPosition)){
            //plays if there's no enemy
            if(getStone(row, col) == null){
                removePiece(piece);
                setPiece(piece, row, col);
                piece.changePosition(moveToPosition);
            }else{
                //if the enemy is up-right
                if(row < piece.getPosition()[0] && col > piece.getPosition()[1]){
                    enemyUpRight(row, col, piece);

                }//if the enemy is down-right 
                else if(row > piece.getPosition()[0] && col > piece.getPosition()[1]){
                    enemyDownRight(row, col, piece);

                }//if the enemy is up-left 
                else if(row < piece.getPosition()[0] && col < piece.getPosition()[1]){
                    enemyUpLeft(row, col, piece);

                }//if the enemy is down-left 
                else if(row > piece.getPosition()[0] && col < piece.getPosition()[1]){
                    enemyDownLeft(row, col, piece);

                }

            }

            //makes the piece a king if it moves to the end
            if(piece.getTeam() == 'w' && piece.getPosition()[0] == 0){
                if(!piece.kingStatus()){
                    piece.makeKing();
                }
            }
            if(piece.getTeam() == 'b' && piece.getPosition()[0] == 7){
                if(!piece.kingStatus()){
                    piece.makeKing();
                }
            }

            //System.out.println(piece.getTeam() + " has moved to " + Arrays.toString(piece.getPosition()));
        } else{
            //System.out.println("Moving from " + Arrays.toString(piece.getPosition()) + "to " + Arrays.toString(moveToPosition) + " is invalid"); 
        }

        if(gameEnd(!isWhite)){
            winner = move.getStone().getTeam(); 
        }
    }

    }



    public void enemyUpRight(int row, int col, stone piece){
        if (piece == null) {
            System.out.println("Error: Trying to move a null piece!");
            return;
        }
        removePiece(piece);
        piece.changePosition(new int[]{row - 1, col + 1});
        setPiece(piece, row-1, col+1);
        removePiece(getStone(row, col));

        // System.out.println("moved to " + row + ", " + col);
        //handles jump
        // for(int[] e: piece.getValidMoves(boardList)){
        //     if(e[0] == piece.getPosition()[0]-1 && e[1] == piece.getPosition()[1]-1){
        //         removePiece(piece);
        //         setPiece(piece, e[0]-1, e[1]-1);
        //         piece.changePosition(new int[]{e[0]- 1, e[1] - 1});
        //         removePiece(getStone(e[0], e[1]));
        //     }
        // }
    }
    
    public void enemyUpLeft(int row, int col, stone piece){
        if (piece == null) {
            System.out.println("Error: Trying to move a null piece!");
            return;
        }
        removePiece(piece);
        piece.changePosition(new int[]{row - 1, col - 1});
        setPiece(piece, row-1, col-1);
        removePiece(getStone(row, col));
        // System.out.println("moved to " + row + ", " + col);

        //handles jump
        // for(int[] e: piece.getValidMoves(boardList)){
        //     if(e[0] == piece.getPosition()[0]-1 && e[1] == piece.getPosition()[1]+1){
        //         removePiece(piece);
        //         setPiece(piece, e[0]-1, e[1]+1);
        //         piece.changePosition(new int[]{e[0]- 1, e[1] +1});
        //         removePiece(getStone(e[0], e[1]));
        //     }
        // }

    }
    public void enemyDownRight(int row, int col, stone piece){
        if (piece == null) {
            System.out.println("Error: Trying to move a null piece!");
            return;
        }

        removePiece(piece);
        piece.changePosition(new int[]{row + 1, col + 1});
        setPiece(piece, row+1, col+1);
        removePiece(getStone(row, col));
        // System.out.println("moved to " + row + ", " + col);

        //handles jump
        // for(int[] e: piece.getValidMoves(boardList)){
        //     if(e[0] == piece.getPosition()[0]+1 && e[1] == piece.getPosition()[1]-1){
        //         removePiece(piece);
        //         setPiece(piece, e[0]+1, e[1]-1);
        //         piece.changePosition(new int[]{e[0]+ 1, e[1] - 1});
        //         removePiece(getStone(e[0], e[1]));
        //     }
        // }

    }
    public void enemyDownLeft(int row, int col, stone piece){

        if (piece == null) {
            System.out.println("Error: Trying to move a null piece!");
            return;
        }


        removePiece(piece);
        piece.changePosition(new int[]{row + 1, col - 1});
        setPiece(piece, row+1, col-1);
        removePiece(getStone(row, col));
        // System.out.println("moved to " + row + ", " + col);

        //handles jump
        // for(int[] e: piece.getValidMoves(boardList)){
        //     if(e[0] == piece.getPosition()[0]+1 && e[1] == piece.getPosition()[1]+1){
        //         removePiece(piece);
        //         setPiece(piece, e[0]+1, e[1]+1);
        //         piece.changePosition(new int[]{e[0]+ 1, e[1] + 1});
        //         removePiece(getStone(e[0], e[1]));
        //     }
        // }
    }

    public void removePiece(stone piece){

        if (piece == null) {
            System.out.println("Error: Attempted to remove a null piece!");
            return;
        }
        int row = piece.getPosition()[0]; 
        int col = piece.getPosition()[1]; 
        boardList[row][col] = null;
        //System.out.println("removed piece: " + piece.getTeam() + "at " + Arrays.toString(piece.getPosition()));
    }

    public void setPiece(stone piece, int row, int col){
        boardList[row][col] = piece; 
    }

    //checks if a move is valid
    public boolean checkIfValid(stone piece, int[] moveToPosition){
        for(int[] e: piece.getValidMoves(boardList)){
            if(Arrays.equals(e, moveToPosition))
                return true; 
        }
        return false;
    }

    public List<Move> getAllValidMoves(boolean isWhite){
        List<Move> allValidMoves = new ArrayList<>();
        for(stone[] sl: boardList){
            for(stone s: sl){
                if(s == null){
                    continue; 
                }
                if(isWhite){
                    if(s.getTeam() == 'w'){
                        for(int[] m:s.getValidMoves(boardList)){
                            allValidMoves.add(new Move(s, m));
                        }
                    }
                } else{
                    if(s.getTeam() == 'b'){
                        for(int[] m:s.getValidMoves(boardList)){
                            allValidMoves.add(new Move(s, m));
                        }
                    }
                }
            }

        }


        return allValidMoves;
    }

    public void undoMove(){

        
    }

    public boolean gameEnd(boolean isWhite){
        char team;
        if(isWhite){
            team = 'w';
        } else team = 'b';
        
        for(stone[] row: boardList){
            for(stone element: row){
                if(element != null){
                if(element.getTeam() == team){
                    if(!element.getValidMoves(boardList).isEmpty()){
                        return false; 
                        }
                    }
                }
            }
        }
        return true;
    }


    //prints the board
    public void printBoard(){
        System.out.println("  0 1 2 3 4 5 6 7");
        int i = 0;
        for(stone[] arr: boardList){
            System.out.print(i + " ");
            for(stone obj: arr){
                if(obj != null) 
                    if(obj.kingStatus()){
                        System.out.print(Character.toUpperCase(obj.getTeam()) + " ");
                    } else{
                        System.out.print(obj.getTeam() + " ");
                    }
                else 
                    System.out.print("- ");  // Represent empty squares
            }
            i++;
            System.out.println();
        }
        System.out.println();

    }

}