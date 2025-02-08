package org.egov.model.report;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

public class CompleteBillRegisterReportDTO {
	
	private String billNumber;
    private String voucherNumber;
    private String payTo;
    private BigDecimal grossAmount;
    private BigDecimal deduction;
    private BigDecimal netPay;
    private BigDecimal paidAmount;
    private String description;
    private Date billDate;
    private String paymentVoucherNumber;
    private String paymentPexNumber;
    private String deductionvouchernumber;
    private String deductionpexnumber;
    private BigInteger vhid;
    private BigInteger payvhid;
    private BigInteger deducVhId;

    // Getters and Setters

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public String getPayTo() {
        return payTo;
    }

    public void setPayTo(String payTo) {
        this.payTo = payTo;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getDeduction() {
        return deduction;
    }

    public void setDeduction(BigDecimal deduction) {
        this.deduction = deduction;
    }

    public BigDecimal getNetPay() {
        return netPay;
    }

    public void setNetPay(BigDecimal netPay) {
        this.netPay = netPay;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getBillDate() {
        return billDate;
    }

    public void setBillDate(Date billDate) {
        this.billDate = billDate;
    }

    public String getPaymentVoucherNumber() {
        return paymentVoucherNumber;
    }

    public void setPaymentVoucherNumber(String paymentVoucherNumber) {
        this.paymentVoucherNumber = paymentVoucherNumber;
    }

    public String getPaymentPexNumber() {
        return paymentPexNumber;
    }

    public void setPaymentPexNumber(String paymentPexNumber) {
        this.paymentPexNumber = paymentPexNumber;
    }
    
	public String getDeductionvouchernumber() {
		return deductionvouchernumber;
	}

	public void setDeductionvouchernumber(String deductionvouchernumber) {
		this.deductionvouchernumber = deductionvouchernumber;
	}

	public String getDeductionpexnumber() {
		return deductionpexnumber;
	}

	public void setDeductionpexnumber(String deductionpexnumber) {
		this.deductionpexnumber = deductionpexnumber;
	}

	public BigInteger getVhid() {
		return vhid;
	}

	public void setVhid(BigInteger vhid) {
		this.vhid = vhid;
	}

	public BigInteger getPayvhid() {
		return payvhid;
	}

	public void setPayvhid(BigInteger payvhid) {
		this.payvhid = payvhid;
	}

	public BigInteger getDeducVhId() {
		return deducVhId;
	}

	public void setDeducVhId(BigInteger deducVhId) {
		this.deducVhId = deducVhId;
	}
    

}
