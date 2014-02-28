<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<title>MIPS 64 Mini Compiler with Pipeline (Pipeline # 2)</title>
<!--Start Import 3rd Party JS Libs-->
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/jquery-1.11.0.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/underscore-min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/backbone-min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/backbone-validation-min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/bootstrap.min.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/jsp/libs/bootstrap-dialog.js"></script>
<script type="text/javascript" charset="utf-8"
	src="${pageContext.request.contextPath}/jsp/libs/ace/src-min-noconflict/ace.js"></script>
<!--End Import 3rd Party JS Libs-->

<!--Start Import 3rd Party CSS Libs-->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/jsp/css/bootstrap.min.css" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/jsp/css/font-awesome.min.css">
<!--End Import 3rd Party CSS Libs-->
<!--Favicon-->
<link rel="icon" type="img/png"
	href="${pageContext.request.contextPath}/jsp/img/favicon.png" />

<!--Start Custom CSS Import -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/jsp/css/style.css" />
<!--End Custom CSS Import -->

<!--Start Import App JS Files-->
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/script.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/models/EditorModel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/EditorView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/routes/MainRoute.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/main.js"></script>
	
<!--End Import App JS Files-->

<style type="text/css" media="screen">
#editorText {
	position: relative;
	top: 0px;
	right: 0;
	bottom: 0;
	left: 0;
	height: 300px;
	width: 900px;
}
</style>
</head>
<body>
	<div id="mainDiv" class="sectionMain">
		<div>
			<nav class="navbar navbar-default navbar-fixed-top navTop" role="navigation">
				<!-- We use the fluid option here to avoid overriding the fixed width of a normal container within the narrow content columns. -->
				<div class="container-fluid">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle" data-toggle="collapse"
							data-target="#bs-example-navbar-collapse-6">
							<span class="sr-only">Toggle navigation</span> <span
								class="icon-bar"></span> <span class="icon-bar"></span> <span
								class="icon-bar"></span>
						</button>
						<a class="navbar-brand" href="#"><i class="fa fa-rocket"></i> MIPS 64 Mini Compiler</a>
					</div>

					<div class="collapse navbar-collapse"
						id="bs-example-navbar-collapse-6">
						<ul class="nav navbar-nav" style="float: right;">
							<li>
								<div class="btn-group" style="position: relative;top: 7px; left: -60px;">
								  <button type="button" class="btn btn-default active"><i class="fa fa-flag-checkered"></i> Full Execution Mode</button>
								  <button type="button" class="btn btn-default"><i class="fa fa fa-chevron-right"></i> Step Through Execution Mode</button>
								</div>
							</li>
							<li><a href="#"><i class="fa fa-film"></i> Memory</a></li>
							<li><a href="#"><i class="fa fa-bars"></i> Registers</a></li>
						</ul>
					</div>
					<!-- /.navbar-collapse -->
				</div>
			</nav>
		</div>
		<div class="runToolbar" id="runToolbar">
			<button name="compile" type="button" class="btn btn-default btn-sm" style="border-radius:10px;">
				<span class="fa-stack fa-1x">
					<i class="fa fa-play fa-stack-1x"></i>
				</span>
			</button>
		</div>
		<div style="">
			<ul class="nav nav-tabs">
				<li class="active">
					<a href="#editor" data-toggle="tab">
						<span class="fa-stack fa-1x">
						  <i class="fa fa-square fa-stack-2x"></i>
						  <i class="fa fa-terminal fa-stack-1x fa-inverse"></i>
						</span> Editor
					</a>
				</li>
				<li><a href="#pipelineMap" data-toggle="tab"><span class="fa-stack fa-1x">
					<i class="fa fa-table fa-2x"></i> 
					</span> Pipeline Map</a></li>
				<li><a href="#internalRegisters" data-toggle="tab"><span class="fa-stack fa-1x">
					<i class="fa fa-cogs fa-2x"></i> 
					</span> MIPS 64 Internal Registers</a></li>
			</ul>
			<div class="tab-content">
				<div class="tab-pane fade in active" id="editor">
					<div>
					<div id="editorText"></div>

				
					</div>
					<div>
						<div class="panel panel-danger" style="position: relative;top: 10px; height: 230px;">
						  <div class="panel-heading">Error Console</div>
						  <div class="panel-body" style="font-family: Courier New;" id="errorConsole">
						    	
						  </div>
						</div>
					</div>
				</div>
				<div class="tab-pane fade in" id="pipelineMap">
					<div>
						<table class="table"></table>
					</div>
				</div>
				<div class="tab-pane fade in" id="internalRegisters">
				
				</div>
			</div>
		</div>
	</div>
</body>
</html>
