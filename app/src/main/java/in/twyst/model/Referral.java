package in.twyst.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by rahuls on 19/8/15.
 */
public class Referral implements Serializable{

    @SerializedName("referral_code")
    private String referralCode;

    @SerializedName("source")
    private String source;

    public String getReferralCode() {
        return referralCode;
    }

    public void setReferralCode(String referralCode) {
        this.referralCode = referralCode;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
