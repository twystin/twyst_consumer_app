package in.twyst.model;

import java.io.Serializable;

/**
 * Created by anilkg on 25/8/15.
 */
public class CheckInSuccessData implements Serializable {

    private Double twyst_bucks;
    private String code;
    private String header;
    private String line1;

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    private String line2;
    private String outlet;

    public Double getTwyst_bucks() {
        return twyst_bucks;
    }

    public void setTwyst_bucks(Double twyst_bucks) {
        this.twyst_bucks = twyst_bucks;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getOutlet() {
        return outlet;
    }

    public void setOutlet(String outlet) {
        this.outlet = outlet;
    }
}
