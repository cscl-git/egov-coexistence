<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script>
	var isdefault = true;
</script>
<div id="performanceWin" class="container-win"  style="">
	<div class="row">
		<div class="col-md-6 col-lg-6 col-sm-12">
			<div id="performanceGIS" style="width:97%;min-height:500px;margin:0 auto;border:1px solid #41311d;" class="gm-style"></div>
		</div>
		<div class="col-md-6 col-lg-6 col-sm-12">
			<div id="performanceGraph" style="min-height:500px;margin:0 auto;" class="gm-style"></div>
		</div>
	</div>
	<div class="row">
		<div class="col-md-12 col-lg-12 col-sm-12">
			<div id="performanceTable" class="gm-style"></div>
		</div>
	</div>
</div>
<div id="slaWin" class="container-win" style="display:none;">
	<div class="row">
		<div class="col-md-6 col-lg-6 col-sm-12">
			<div id="slaGIS" style="min-height:500px;border:1px solid #41311d;" class="gm-style"></div>
		</div>
		<div class="col-md-6 col-lg-6 col-sm-12">
			<div id="slaGraph" class="gm-style"></div>
		</div>
	</div>
</div>
<div id="overviewWin" class="container-win" style="display:none;">
	<div id="overviewGIS" style="position:absolute;width:100%;min-height:100%;margin:0 auto;border:1px solid #41311d;" class="gm-style"></div>
</div>
<div id="topFiveCompTypeWin" class="container-win" style="display:none;">
	<div class="row" style="height:97%">
		<div class="col-md-12 col-sm-12 col-ms-12 col-lg-12" style="height:97%;">
			<div id="topFiveCompTypeGraph" style="width:100%;height:100%" class="gm-style"></div>
		</div>
	</div>
</div>
<div id="wardwiseAnalysisWin" class="container-win" style="display:none">
	<div class="row">
		<div class="col-lg-12 col-md-12 col-sm-12">
			<div class="make-switch switch-small pull-right label-toggle-switch" id="sync" data-text-label="SYNC" data-on="success" data-off="danger"> 
				<input type="checkbox" checked class="label-toggle-switch">
			</div>
		</div>
	</div>
	<div class="row" >
		<div class="col-md-4 col-sm-4 col-lg-4" style="height:500px;margin-bottom:50px">
			<div class="bs-callout bs-callout-info header-darkred">Registered</div> 
			<div id="wardwiseAnalysis1"  style="min-height:500px"></div>
		</div>
		<div class="col-md-4 col-sm-4 col-ms-4 col-lg-4" style="height:500px;margin-bottom:50px">
			<div class="bs-callout bs-callout-info header-blue">Redressed</div>
			<div id="wardwiseAnalysis2" style="min-height:500px"></div>
		</div>
		<div class="col-md-4 col-sm-4 col-ms-4 col-lg-4" style="height:500px;margin-bottom:50px">
			<div class="bs-callout bs-callout-info header-brown">Complaints per Property</div>
			<div id="wardwiseAnalysis3" style="height:500px"></div>
		</div>
	</div>
</div>