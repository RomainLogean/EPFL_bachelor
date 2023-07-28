package ch.epfl.tchu;

/**
 * @author Romain Logean (327230)
 * @author Shuli Jia (316620)
 */
public final class Preconditions {
    
    private Preconditions(){}
    
    /**
     * 
     * @param shouldBeTrue: boolean expression that should be true in order to not throw the exception
     * @throws IllegalArgumentException if the boolean expression is false
     */
    public static void checkArgument(boolean shouldBeTrue){
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }

}
