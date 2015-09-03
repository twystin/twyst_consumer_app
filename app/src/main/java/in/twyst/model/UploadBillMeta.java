package in.twyst.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 6/8/15.
 */
public class UploadBillMeta implements Serializable{


    @SerializedName("outlet_name")
    private String outletName;

    @SerializedName("bill_date")
    private String billDate;

    @SerializedName("photo")
    private String photo;

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
