package domain.model;

/**
 * Class that represents a set of tokens.
 * @author Renjue and David
 */
public class TokenSet {

    private String[] tokenSet = new String[6];

    /**
     * Method for adding a token to the set
     * @param token to be added
     */
    public void addToken(String token){
        for(int i = 0; i < 6; i++){
            if(tokenSet[i] == null){
                tokenSet[i] = token;
                break;}
        }
    }

    /**
     * Method for removing a specified token from the set
     * @param token the token to remove from the set
     */
    public void removeToken(String token){
       for(int i = 0; i < 6; i++){
           if(tokenSet[i].equals(token)){
               tokenSet[i] = null;
               break;
           }
       }
    }

    /**
     * Method for finding the index of a specified token
     * @param token which index we wish to find
     * @return the index of the specified token
     */
    public int getTokenIndex(String token){

        int indexOfToken = -1;
        for(int i = 0; i < 6 ; i++){

            if(tokenSet[i] != null){
                if(tokenSet[i].equals(token)){
                    indexOfToken = i;
                }
            }
        }
        return indexOfToken;
    }


    /**
     * Method that return a token specified by its index in the TokenSet
     * @param index of the token
     * @return token
     */
    public String getToken(int index){return tokenSet[index];}

    /**
     * Method for checking if a specified token exists int the set
     * @param token to search for
     * @return boolean stating if it was found or not
     */
    public boolean findToken(String token){
        for (String str: tokenSet){
            if(str != null){
                if(str.equals(token)){return true;}}
            }
        return false;
    }


    /**
     * Method for finding the number of tokens in a tokenSet
     * @return an integer representing the number of tokens in the tokenSet
     */
    public int findNumberOfTokens(){

        int count = 0;
        for (String token : tokenSet){
            if(token != null){
                count++;
            }
        }
        return count;
    }

    /**
     * Getter for the actual set
     * @return String array of tokens
     */
    public String[] getTokenSet(){return tokenSet;}

}
