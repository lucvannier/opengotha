package info.vannier.gotha;

/**
 *
 * @author Luc Vannier
 */
public class Country{
    String name;
    private String alpha2Code;

    /**
     * @return the alpha2Code
     */
    public String getAlpha2Code() {
        return alpha2Code;
    }

    /**
     * @param alpha2Code the alpha2Code to set
     */
    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }
}