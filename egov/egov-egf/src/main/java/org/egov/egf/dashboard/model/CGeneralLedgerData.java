package org.egov.egf.dashboard.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

public class CGeneralLedgerData {
    private Long id ;
    private String glcode;
    private String coaname;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String description;
    private Set<CGeneralLedgerDetailData> generalLedgerDetails = new HashSet<>();
    private Boolean isSubLedger;
    public CGeneralLedgerData(Long id, String glcode, String coaname,
    		BigDecimal debitAmount, BigDecimal creditAmount, String description, Boolean isSubLedger) {
        this.id = id;
        this.glcode = glcode;
        this.coaname = coaname;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
        this.description = description;
        this.isSubLedger = isSubLedger;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getGlcode() {
        return glcode;
    }
    public void setGlcode(String glcode) {
        this.glcode = glcode;
    }
    public String getCoaname() {
        return coaname;
    }
    public void setCoaname(String coaname) {
        this.coaname = coaname;
    }
 
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Set<CGeneralLedgerDetailData> getGeneralLedgerDetails() {
        return generalLedgerDetails;
    }
    public void setGeneralLedgerDetails(Set<CGeneralLedgerDetailData> generalLedgerDetails) {
        this.generalLedgerDetails = generalLedgerDetails;
    }
    public Boolean getIsSubLedger() {
        return isSubLedger;
    }
    public void setIsSubLedger(Boolean isSubLedger) {
        this.isSubLedger = isSubLedger;
    }
    public static class Builder {
        private Long id ;
        private String glcode;
        private String coaname;
        private BigDecimal debitAmount;
        private BigDecimal creditAmount;
        private String description;
        private Boolean isSubLedger;
        public Builder() {
            // TODO Auto-generated constructor stub
        }
        public Builder setId(Long id) {
            this.id = id;
            return this;
        }
        public Builder setGlcode(String glcode) {
            this.glcode = glcode;
            return this;
        }
        public Builder setCoaname(String coaname) {
            this.coaname = coaname;
            return this;
        }
        public Builder setDebitAmount(BigDecimal debitAmount) {
            this.debitAmount = debitAmount;
            return this;
        }
        public Builder setCreditAmount(BigDecimal creditAmount) {
            this.creditAmount = creditAmount;
            return this;
        }
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }
        public Builder setIsSubLedger(Boolean isSubLedger) {
            this.isSubLedger = isSubLedger;
            return this;
        }
        public CGeneralLedgerData build(){
            return new CGeneralLedgerData(id, glcode, coaname, debitAmount, creditAmount, description, isSubLedger);
        }
    }
	public BigDecimal getDebitAmount() {
		return debitAmount;
	}
	public void setDebitAmount(BigDecimal debitAmount) {
		this.debitAmount = debitAmount;
	}
	public BigDecimal getCreditAmount() {
		return creditAmount;
	}
	public void setCreditAmount(BigDecimal creditAmount) {
		this.creditAmount = creditAmount;
	}
}
