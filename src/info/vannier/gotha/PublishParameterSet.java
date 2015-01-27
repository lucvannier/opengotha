package info.vannier.gotha;

/**
 * Display and Print ParameterSet
 * @author Luc
 */
public class PublishParameterSet implements java.io.Serializable{   
    private boolean print = true;
    private boolean exportToLocalFile   = true;
    private boolean exportHFToOGSite = false;
    private boolean exportTFToOGSite = true;
    private boolean exportToUDSite = false;
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
    
    public void commonInit(){
        this.print = true;
        this.exportToLocalFile = true;
        this.exportHFToOGSite = false;
        this.exportTFToOGSite = false;
        this.exportToUDSite = false;
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
     * @return the exportHFToOGSite
     */
    public boolean isExportHFToOGSite() {
        return exportHFToOGSite;
    }

    /**
     * @param exportHFToOGSite the exportHFToOGSite to set
     */
    public void setExportHFToOGSite(boolean exportToOGSite) {
        this.exportHFToOGSite = exportToOGSite;
    }
    /**
     * @return the exportTFToOGSite
     */
    public boolean isExportTFToOGSite() {
        return exportTFToOGSite;
    }

    /**
     * @param exportTFToOGSite the exportHFToOGSite to set
     */
    public void setExportTFToOGSite(boolean exportTFToOGSite) {
        this.exportTFToOGSite = exportTFToOGSite;
    }

    /**
     * @return the exportToUDSite
     */
    public boolean isExportToUDSite() {
        return exportToUDSite;
    }

    /**
     * @param exportToUDSite the exportToUDSite to set
     */
    public void setExportToUDSite(boolean exportToUDSite) {
        this.exportToUDSite = exportToUDSite;
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
