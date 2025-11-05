package com.Hamza.RLCheckers.CheckersLogic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class stone {
    private int[] position; 
    final private char team;
    private boolean isKing = false;


    public stone(int[] position, char team){
        this.position = position; 
        this.team = team; 

    }

    public stone(stone copy){
        this.position = Arrays.copyOf(copy.getPosition(), 2);
        this.team = copy.getTeam(); 
        this.isKing = copy.kingStatus(); 
    }

    public stone copy(){
        return new stone(this); 
    }
    public void changePosition(int[] newPos){
        this.position = newPos; 
    }
    public int[] getPosition(){
        return this.position;
    }
    public boolean kingStatus(){
        return this.isKing; 
    }
    public char getTeam(){
        return this.team;
    }
    public void makeKing(){
        if (!this.isKing) {
            this.isKing = true;
        }
    }
    public List<int[]> getValidMoves(stone[][] boardlist){
        //stores the valid pieces
        List<int[]> valid = new ArrayList<>();
    
        //checks if we're validating a king or a normal piece's moves
        if(this.isKing){
            //checks that up left won't hit a wall
            valid.add(upLeft(boardlist));
            //checks that down left won't hit a wall
            valid.add(downLeft(boardlist)); 
            //checks that going up-right won't hit a wall
            valid.add(upRight(boardlist)); 
            //checks that going down-right won't hit a wall
            valid.add(downRight(boardlist));
        } else{
            if(this.team == 'w'){
                valid.add(upRight(boardlist)); 
                valid.add(upLeft(boardlist));
            } else{
                valid.add(downLeft(boardlist)); 
                valid.add(downRight(boardlist));
            }
        } 
        valid.removeIf(arr -> Arrays.equals(arr, new int[]{-1, -1}));
        return valid; 
    }

    private int[] upLeft(stone[][] boardlist) {
        int[] valid = {-1, -1}; 
        if(this.position[0]>0 && this.position[1]>0){
            //checks if player is in the way of up-left
            if(boardlist[this.position[0]-1][this.position[1]-1]==null){
                valid[0] = this.position[0]-1; 
                valid[1] = this.position[1]-1;
            } else{
                if(boardlist[this.position[0]-1][this.position[1]-1].team != this.getTeam()){
                    //checks if the enemy up-left can be killed or if the kill is blocked
                    if(this.position[0] > 1 && this.position[1] > 1){
                        if(boardlist[this.position[0]-2][this.position[1]-2] == null){
                            valid[0] = this.position[0]-1; 
                            valid[1] = this.position[1]-1;
                        }
                    }
                }
            }      
        }

        return valid; 
    }

    private int[] downLeft(stone[][] boardlist) {
        int[] valid = {-1, -1}; 
        if(this.position[0]<7 && this.position[1]>0){
            //checks if player is in the way of down-left
            if(boardlist[this.position[0]+1][this.position[1]-1]==null){
                valid[0] = this.position[0]+1; 
                valid[1] = this.position[1]-1;
            }else{
                if(boardlist[this.position[0]+1][this.position[1]-1].team != this.getTeam()){
                    //checks if the enemy down-left can be killed or if the kill is blocked
                    if(this.position[0] < 6 && this.position[1] > 1){
                        if(boardlist[this.position[0]+2][this.position[1]-2] == null){
                            valid[0] = this.position[0]+1; 
                            valid[1] = this.position[1]-1;
                        }
                    }
                }
            }

        }
        return valid;
    }

    private int[] upRight(stone[][] boardlist) {
        int[] valid = {-1, -1}; 
        if(this.position[0]>0 && this.position[1]<7){
            //checks if player is in the way of up-right
            if(boardlist[this.position[0]-1][this.position[1]+1]==null){
                valid[0] = this.position[0]-1; 
                valid[1] = this.position[1]+1;
            } else{
                if(boardlist[this.position[0]-1][this.position[1]+1].team != this.getTeam()){
                    //checks if the enemy top-right can be killed or if the kill is blocked
                    if(this.position[0] > 1 && this.position[1] < 6){
                        if(boardlist[this.position[0]-2][this.position[1]+2] == null){
                            valid[0] = this.position[0]-1; 
                            valid[1] = this.position[1]+1;
                        }
                    }
                }
            } 
        }

        return valid; 
    }

    private int[] downRight(stone[][] boardlist) {
        int[] valid = {-1, -1}; 
        if(this.position[0]<7 && this.position[1]<7){
            //checks if player is in the way of down-right
            if(boardlist[this.position[0]+1][this.position[1]+1]==null){
                valid[0] = this.position[0]+1; 
                valid[1] = this.position[1]+1;
            }else{
                if(boardlist[this.position[0]+1][this.position[1]+1].team != this.getTeam()){
                    //checks if the enemy down-right can be killed or if the kill is blocked
                    if(this.position[0] < 6 && this.position[1] < 6){
                        if(boardlist[this.position[0]+2][this.position[1]+2] == null){
                            valid[0] = this.position[0]+1; 
                            valid[1] = this.position[1]+1;
                        }
                    }
                }
            }

        }

        return valid; 
    }




}


