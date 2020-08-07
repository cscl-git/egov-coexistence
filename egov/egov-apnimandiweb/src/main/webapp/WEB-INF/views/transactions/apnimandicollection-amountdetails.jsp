<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div>
	<div class="panel-heading">
		<div class="panel-title">
			<spring:message code="lbl.receipt.head" />
		</div>
	</div>
	<div id="paymentDetailDiv">
		<c:if test="${ mode == 'view'}">
			<table width="100%" class="table table-bordered table-hover multiheadertbl dataTable">
				<thead>
					<tr>
						<th style="width:80%;">
							<span>Account Head</span>
						</th>
						<th style="width:20%;">
							<span>Amount (Rs.)</span>
						</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${apnimandiCollectionDetails.apnimandiCollectionAmountDetails}" var="apnimandiCollectionAmount">
						<tr>
							<td>${apnimandiCollectionAmount.accountHead}</td>
							<td>${apnimandiCollectionAmount.creditAmountDetail}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<c:if test="${ mode != 'view'}">
			<c:if test="${ mode == 'edit'}">
				<table id="paymentDetailTable" width="100%" class="table table-bordered table-hover multiheadertbl dataTable">
					<thead>
						<tr>
							<th style="width:80%;">
								<span>Account Head</span>
							</th>
							<th style="width:20%;">
								<span>Amount (Rs.)</span>
							</th>
						</tr>
					</thead>
					<tbody id="paymentDetailTbody">
						<c:forEach items="${apnimandiCollectionDetails.apnimandiCollectionAmountDetails}" var="apnimandiCollectionAmount" varStatus="acm">
							<tr id="paymentDetailTR_${acm.index}">
								<td>
									<input type="text" id="apnimandiCollectionAmountDetails[${acm.index}].accountHead" name="apnimandiCollectionAmountDetails[${acm.index}].accountHead"
										   readonly="readonly" class="form-control patternvalidation text-left" maxlength="100" value="${apnimandiCollectionAmount.accountHead}">
									<input type="hidden" id="apnimandiCollectionAmountDetails[${acm.index}].glCodeIdDetail" name="apnimandiCollectionAmountDetails[${acm.index}].glCodeIdDetail" value="${apnimandiCollectionAmount.glCodeIdDetail}">
									<input type="hidden" id="apnimandiCollectionAmountDetails[${acm.index}].amountType" name="apnimandiCollectionAmountDetails[${acm.index}].amountType" value="${apnimandiCollectionAmount.amountType}">
								</td>
								<td>
									<input type="text" id="apnimandiCollectionAmountDetails[${acm.index}].creditAmountDetail"
										   name="apnimandiCollectionAmountDetails[${acm.index}].creditAmountDetail" maxlength="10" class="form-control patternvalidation text-right"
										   data-pattern="number" onblur=";updateTotalAmount()" value="${apnimandiCollectionAmount.creditAmountDetail}">
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td align="center" class="tdfortotal" style="text-align: right;">
								<b>Total</b>&nbsp;&nbsp;&nbsp;
							</td>
							<td class="tdfortotal">
								<input type="text" id="totalcramount" name="totalcramount" readonly="true" class="form-control text-right"  value="${totalcramount}">
							</td>
						</tr>
					</tfoot>
				</table>
				<input type="hidden" id="receiptHeadCount" name="receiptHeadCount" value="${receiptHeadCount}"/>
			</c:if>
			<c:if test="${ mode != 'edit'}">
				<input type="hidden" id="receiptHeadCount" name="receiptHeadCount" value="0"/>
				<table id="paymentDetailTable" width="100%" class="table table-bordered table-hover multiheadertbl dataTable">
					<thead>
						<tr>
							<th style="width:80%;">
								<span>Account Head</span>
							</th>
							<th style="width:20%;">
								<span>Amount (Rs.)</span>
							</th>
						</tr>
					</thead>
					<tbody id="paymentDetailTbody">
						<tr id="paymentDetailTR_0">
							<td>
								<input type="text" id="apnimandiCollectionAmountDetails[0].accountHead" name="apnimandiCollectionAmountDetails[0].accountHead"
									   readonly="readonly" class="form-control patternvalidation text-left" maxlength="100">
								<input type="hidden" id="apnimandiCollectionAmountDetails[0].glCodeIdDetail" name="apnimandiCollectionAmountDetails[0].glCodeIdDetail" value="">
								<input type="hidden" id="apnimandiCollectionAmountDetails[0].amountType" name="apnimandiCollectionAmountDetails[0].amountType" value="">
							</td>
							<td>
								<input type="text" id="apnimandiCollectionAmountDetails[0].creditAmountDetail"
									   name="apnimandiCollectionAmountDetails[0].creditAmountDetail" maxlength="10" class="form-control patternvalidation text-right"
									   data-pattern="number" onblur=";updateTotalAmount()">
							</td>
						</tr>
					</tbody>
					<tfoot>
						<tr>
							<td align="center" class="tdfortotal" style="text-align: right;">
								<b>Total</b>&nbsp;&nbsp;&nbsp;
							</td>
							<td class="tdfortotal">
								<input type="text" id="totalcramount" name="totalcramount" readonly="true" class="form-control text-right">
							</td>
						</tr>
					</tfoot>
				</table>
			</c:if>
		</c:if>
	</div>
</div>