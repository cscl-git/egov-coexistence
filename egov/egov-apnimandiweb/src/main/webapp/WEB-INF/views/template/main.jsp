<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<!DOCTYPE html>
<html>
<head>
    <spring:eval expression="@environment.getProperty('analytics.enabled')" scope="application" var="analyticsEnabled"/>
    <c:if test="${analyticsEnabled}">
        <spring:eval expression="@environment.getProperty('analytics.config')" scope="application"/>
    </c:if>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="description" content="eGov System"/>
    <meta name="author" content="eGovernments Foundation"/>

    <title><tiles:insertAttribute name="title"/></title>
    <link rel="icon" href="<cdn:url value='/resources/global/images/favicon.png' context='/services/egi'/>" sizes="32x32">
    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/font-icons/font-awesome/css/font-awesome.min.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/typeahead.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/egov/custom.css?rnd=${app_release_no}' context='/services/egi'/>">

    <script src="<cdn:url value='/resources/global/js/jquery/jquery.js' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap.js' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/bootstrap/bootbox.min.js' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.validate.min.js' context='/services/egi'/>"></script>
    <script type="text/javascript" src="<cdn:url value='/resources/global/js/bootstrap/typeahead.bundle.js' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/jquery/plugins/jquery.inputmask.bundle.min.js' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/egov/patternvalidation.js?rnd=${app_release_no}' context='/services/egi'/>"></script>
    <script src="<cdn:url value='/resources/global/js/egov/custom.js?rnd=${app_release_no}' context='/services/egi'/>"></script>

    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="/egi/resources/global/js/ie8/html5shiv.min.js"></script>
    <script src="/egi/resources/global/js/ie8/respond.min.js"></script>
    <![endif]-->
    <script>
        var googleapikey = '${sessionScope.googleApiKey}';
        var citylat = ${sessionScope.citylat};
        var citylng = ${sessionScope.citylng};
    </script>
</head>
<body class="page-body" oncontextmenu="return false;">
<div class="page-container">
    <%-- <tiles:insertAttribute name="header"/> --%>
    <div class="main-content">
        <spring:htmlEscape defaultHtmlEscape="true" />
        <tiles:insertAttribute name="body"/>
    </div>
    <%-- <tiles:insertAttribute name="footer"/> --%>
</div>
<input type="hidden" value="${sessionScope.citylat}" id="getcitylat">
<input type="hidden" value="${sessionScope.citylng}" id="getcitylng">
<div class="modal fade loader-class" data-backdrop="static">
    <div class="modal-dialog">
        <div class="modal-body">
            <div class="row spinner-margin text-center">
                <div class="col-md-12 ">
                    <div class="spinner">
                        <div class="rect1"></div>
                        <div class="rect2"></div>
                        <div class="rect3"></div>
                        <div class="rect4"></div>
                        <div class="rect5"></div>
                    </div>
                </div>

                <div class="col-md-12 spinner-text">
                    Processing your request. Please wait..
                </div>
            </div>
        </div>
    </div>
</div>
<div class="modal fade demo-class" data-backdrop="static" style="opacity:0.01">
    <div class="modal-dialog">
        <div class="modal-body">

        </div>
    </div>
</div>
</body>
</html>
