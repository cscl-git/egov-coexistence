/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.model.bills;

// Generated Mar 10, 2008 12:54:41 PM by Hibernate Tools 3.2.0.b9

import org.egov.commons.CVoucherHeader;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.utils.NumberToWord;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Miscbilldetail generated by hbm2java
 */
public class Miscbilldetail implements java.io.Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = -7203938023858665693L;

    private Long id;

    private CVoucherHeader billVoucherHeader;

    private CVoucherHeader payVoucherHeader;
    
    private String billnumber;

    private Date billdate;

    private BigDecimal billamount;

    private BigDecimal passedamount;

    private BigDecimal paidamount;

    private String paidto;

    private User paidby;

    private String amtInWords;

    public Miscbilldetail()
    {
    }

    public Miscbilldetail(final Long id, final BigDecimal amount,
            final BigDecimal passedamount, final String paidto, final String approvedby,
            final Date created)
    {
        this.id = id;
        this.passedamount = passedamount;
        this.paidto = paidto;
    }

    public Miscbilldetail(final Long id, final String billnumber, final Date billdate,
            final BigDecimal passedamount, final String paidto)
    {
        this.id = id;
        this.billnumber = billnumber;
        this.billdate = billdate;
        this.passedamount = passedamount;
        this.paidto = paidto;
    }

    public String getAmtInWords() {
        if (paidamount != null)
        {
            paidamount = paidamount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            amtInWords = NumberToWord.convertToWord(paidamount.toString());

        } else
            amtInWords = "";
        return amtInWords;

    }

    public void setAmtInWords(final String amtInWord) {

        amtInWords = amtInWords;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(final Long id)
    {
        this.id = id;
    }

    public String getBillnumber()
    {
        return billnumber;
    }

    public void setBillnumber(final String billnumber)
    {
        this.billnumber = billnumber;
    }

    public Date getBilldate()
    {
        return billdate;
    }

    public void setBilldate(final Date billdate)
    {
        this.billdate = billdate;
    }

    public BigDecimal getPassedamount()
    {
        return passedamount;
    }

    public void setPassedamount(final BigDecimal passedamount)
    {
        this.passedamount = passedamount;
    }

    public String getPaidto()
    {
        return paidto;
    }

    public void setPaidto(final String paidto)
    {
        this.paidto = paidto;
    }

    public CVoucherHeader getBillVoucherHeader() {
        return billVoucherHeader;
    }

    public void setBillVoucherHeader(final CVoucherHeader billVoucherHeader) {
        this.billVoucherHeader = billVoucherHeader;
    }

    public CVoucherHeader getPayVoucherHeader() {
        return payVoucherHeader;
    }

    public void setPayVoucherHeader(final CVoucherHeader payVoucherHeader) {
        this.payVoucherHeader = payVoucherHeader;
    }

    public BigDecimal getBillamount() {
        return billamount;
    }

    public void setBillamount(final BigDecimal billamount) {
        this.billamount = billamount;
    }

    public BigDecimal getPaidamount() {
        return paidamount;
    }

    public void setPaidamount(final BigDecimal paidamount) {
        this.paidamount = paidamount;
    }

    public User getPaidby() {
        return paidby;
    }

    public void setPaidby(final User paidby) {
        this.paidby = paidby;
    }


}
