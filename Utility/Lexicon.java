package Utility;

public class Lexicon{
    private static final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static int callNumber;
    private static int resetNumber;
    public Lexicon(){
        callNumber = 0;
        resetNumber = 0;
    }
    public String getNextName(){
        if(callNumber > 25){
            resetNumber++;
            return String.valueOf(resetNumber);
        }
        String name = String.valueOf(alphabet.charAt(callNumber));
        callNumber++;
        return name;
    }
}