package com.Hamza.RLCheckers.CheckersLogic;


public class Move{
    stone stone;
    int[] move;
    

    public Move(stone s, int[] m){
        this.stone = s;
        this.move = m; 
    }

    public stone getStone() {
        return stone; 
    }

    public int[] getMove() {
        return move; 
    }


}