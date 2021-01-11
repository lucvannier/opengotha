package info.vannier.gotha;

/**
 * Display and Print ParameterSet
 * @author Luc Vannier
 */
public class PublishParameterSet implements java.io.Serializable{   
    private boolean print = true;
    private boolean exportToLocalFile   = true;
    private boolean htmlAutoScroll = false;

    public PublishParameterSet() {             
    }
     
    public PublishParameterSet(PublishParameterSet dpps) { 

    }
    
    public void initForMM(){
        commonInit();
    }
    
    public void initForSwiss(){
        commonInit();
    }
    
    public void initForSwissCat(){
        commonInit();
    }
    
    public void initForCup(){
        commonInit();
    }

    public void commonInit(){
        this.print = true;
        this.exportToLocalFile = true;
    }

    /**
     * @return the print
     */
    public boolean isPrint() {
        return print;
    }

    /**
     * @param print the print to set
     */
    public void setPrint(boolean print) {
        this.print = print;
    }

    /**
     * @return the exportToLocalFile
     */
    public boolean isExportToLocalFile() {
        return exportToLocalFile;
    }

    /**
     * @param exportToLocalFile the exportToLocalFile to set
     */
    public void setExportToLocalFile(boolean exportToLocalFile) {
        this.exportToLocalFile = exportToLocalFile;
    }

    /**
     * @return the htmlAutoScroll
     */
    public boolean isHtmlAutoScroll() {
        return htmlAutoScroll;
    }

    /**
     * @param htmlAutoScroll the htmlAutoScroll to set
     */
    public void setHtmlAutoScroll(boolean htmlAutoScroll) {
        this.htmlAutoScroll = htmlAutoScroll;
    }

}
