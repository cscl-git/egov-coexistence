<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6 lt8"> <![endif]-->
<!--[if IE 7 ]> <html lang="en" class="no-js ie7 lt8"> <![endif]-->
<!--[if IE 8 ]> <html lang="en" class="no-js ie8 lt8"> <![endif]-->
<!--[if IE 9 ]> <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!-->
<html lang="en" class="no-js" id="page-top">
<!--<![endif]-->
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title><tiles:insertAttribute name="title"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/bootstrap/bootstrap.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/css/global.css?rnd=${app_release_no}' context='/dashboard'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/css/jquery.loadmask.css' context='/dashboard'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/global/css/font-icons/font-awesome/css/font-awesome.min.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/js/jquery/plugins/jquery-ui/jquery-ui.min.css' context='/dashboard'/>"/>
    <link rel="stylesheet" href="<cdn:url value='/resources/global/js/jquery/plugins/datatables/responsive/css/dataTables.bootstrap.css' context='/services/egi'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/js/jquery/plugins/datatable/extensions/responsive/css/dataTables.responsive.css' context='/dashboard'/>">
    <link rel="stylesheet" href="<cdn:url value='/resources/js/jquery/plugins/bootstrap-switch/bootstrap-switch.min.css' context='/dashboard'/>" con>

    <script src="<cdn:url value='/resources/global/js/jquery/jquery.js' context='/services/egi'/>"></script>

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
    <script src="../resources/js/html5shiv.js"></script>
    <script src="../resources/js/respond.min.js"></script>
    <![endif]-->
    <script>
        $(window).load(function () {
            $("#cover").delay(1000).slideUp(300);
            $("#preloader-container").delay(1000).slideUp(300);
        });
        var citylat = parseFloat("${sessionScope.citylat}");
        var citylng = parseFloat("${sessionScope.citylng}");
        console.log(citylat, citylng);
    </script>
</head>
<body data-spy="scroll">
<div class="cover" id="cover"></div>
<div class="preloader-container" id="preloader-container">
    <div class="preloader-logo">eGov<img src="<cdn:url value='/resources/images/mask.gif' context='/dashboard'/>">Dashboard</div>
</div>
<nav class="navbar navbar-inverse navbar-fixed-top">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>

            <%-- <a class="navbar-brand customer-logo" data-toggle="tooltip" data-placement="bottom" title="Dashboard Home" href="/dashboard/home">
                <img src="<c:url value='/downloadfile/logo' context='/services/egi'/>" height="55">
            </a> --%>
            <a class="navbar-brand visible-lg-block visible-md-block" data-toggle="tooltip" data-placement="bottom" title="Complaint Redressal Home" href="/pgr/dashboard/home?isdefault=true">Complaints Redressal</a>
            <span class="navbar-brand visible-sm-block visible-xs-block" style="color:#FFF"><span class="title"></span></span>
        </div>
        <span class="navbar-brand pull-right egov-logo-right" style="color:#FFF">
		    <!-- span class="title"></span-->
		    <a href="http://www.egovernments.org" target="_blank">
			   <img src="<cdn:url value='/resources/global/images/logo@2x.png' context='/services/egi'/>" title="Powered by eGovernments" height="20px">
			</a>
		    </span>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav">
                <li class="visible-sm-block visible-xs-block">
                    <a data-toggle="tooltip" data-placement="bottom" title="Home" href="/dashboard/home"><i class="fa fa-home fa-lg fa-fw"></i> Smart City Home</a>
                </li>
                <li id='deflt'>
                    <a href="javascript:void(0)" class="menu-item" data-fn="redressalEfficiency" data-win="performanceWin" id="pgrPerf"><i class="fa fa-bar-chart fa-lg fa-fw"></i> Efficiency</a>
                </li>
                <li>
                    <a href="javascript:void(0)" class="menu-item" data-fn="openCompPendency" data-win="slaWin" id="pgrSla"><i class="fa fa-pie-chart fa-lg fa-fw"></i> Pendency</a>
                </li>
                <li>
                    <a href="javascript:void(0)" class="menu-item" data-fn="compTypeDistribution" data-win="overviewWin" id="pgrOverview"><i class="fa fa-flash fa-lg fa-fw"></i> Type Distribution</a>
                </li>
                <li id='top5'>
                    <a href="javascript:void(0)" class="menu-item" data-fn="topFiveCompType" data-win="topFiveCompTypeWin" id="topFiveCompTypeview"><i class="fa fa-area-chart fa-lg fa-fw"></i> Top Five Complaint Types</a>
                </li>
                <li>
                    <a href="javascript:void(0)" class="menu-item" data-fn="wardwiseAnalysis" data-win="wardwiseAnalysisWin" id="wardwiseAnalysis"><i class="fa fa-map-marker fa-lg fa-fw"></i> GIS Analysis</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
<tiles:insertAttribute name="body"/>
<footer class="footer">
    <div style="margin-top: 20px;padding: 5px 19px;font-size: 12px;background: #F1F2F3;">Powered by <a href="http://egovernments.org/" target="_blank">eGovernments Foundation</a></div>
</footer>
</body>
<script src="<cdn:url value='/resources/global/js/bootstrap/bootstrap.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/js/highchart/highstock.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/js/highchart/modules/exporting.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/js/highchart/highcharts-more.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/js/highchart/modules/data.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/js/highchart/modules/drilldown.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/js/geoxml3.js' context='/dashboard'/>"></script>
<script src="https://maps.googleapis.com/maps/api/js?key=${sessionScope.googleApiKey}&v=3.exp&libraries=places"></script>
<script src="<cdn:url value='/resources/js/date.js' context='/dashboard'/>"></script>
<script src='<cdn:url value='/resources/js/jquery/plugins/jquery.loadmask.min.js' context='/dashboard'/>'></script>
<script src="<cdn:url value='/resources/js/jquery/plugins/jquery-ui/jquery-ui.min.js' context='/dashboard'/>"></script>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/jquery.dataTables.min.css' context='/services/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jquery/plugins/datatables/dataTables.bootstrap.min.css' context='/services/egi'/>">
<script src="<cdn:url value='/resources/js/jquery/plugins/datatable/js/jquery.dataTables.min.js' context='/dashboard'/>"></script>
<script src="<cdn:url value='/resources/global/js/jquery/plugins/datatables/dataTables.bootstrap.js' context='/services/egi'/>"></script>
<script src="<cdn:url value='/resources/js/jquery/plugins/datatable/extensions/responsive/js/dataTables.responsive.min.js' context='/dashboard'/>"></script>
<script src='<cdn:url value='/resources/js/jquery/plugins/bootstrap-switch/bootstrap-switch.min.js' context='/dashboard'/>'></script>
<script src="<cdn:url value='/resources/js/global.js?rnd=${app_release_no}' context='/dashboard'/>"></script>
<script src='../resources/js/app/dashboard.js?rnd=${app_release_no}'></script>
</html>
