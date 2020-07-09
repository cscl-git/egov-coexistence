<%@ include file="/includes/taglibs.jsp"%>

<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<script>
			function refreshInbox() {
			    try {
			        var x=opener.top.opener;
			        if(x==null){
			            x=opener.top;
			        }
			        x.document.getElementById('inboxframe').contentWindow.egovInbox.from = 'Inbox';
				    x.document.getElementById('inboxframe').contentWindow.egovInbox.refresh();
			    } catch (e) {
			        //do nothing
				}
			}
		</script>
		<title>Collection Receipt</title>
	</head>
	<body onLoad="refreshInbox();">
		<s:form theme="simple" name="displayReceiptForm">
			<s:if test="%{hasErrors()}">
				<div class="errorstyle"><s:actionerror /> <s:fielderror /></div>
			</s:if>
			<s:if test="%{hasActionMessages()}">
				<div class="messagestyle"><s:actionmessage theme="simple" /></div>
			</s:if>
			<iframe src="/services/apnimandi/reportViewer?reportId=${reportId}" width="98%"
				height="600px">
				<p>Your browser does not support iframes.</p>
			</iframe>
			<br/>
			<script>
				refreshInbox();
			</script>
			<div class="buttonbottom">
				<input name="buttonClose" type="button" class="button"	id="buttonClose" value="<s:text name='lbl.close'/>" onclick="window.parent.postMessage('close','*');window.close()" />
			</div>
		</s:form>
	</body>
</html>
