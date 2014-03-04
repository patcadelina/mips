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
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/app.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/script.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/models/EditorModel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/models/RegisterModel.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/models/collections/RegisterCollection.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/EditorView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/RegisterView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/MIPSInternalRegisterView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/MemoryView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/PipelineView.js"></script>
	<script type="text/javascript" src="${pageContext.request.contextPath}/jsp/js/app/views/MenuView.js"></script>
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
						<a class="navbar-brand" href="#">I <i class="fa fa-heart" style="color:pink;"></i> MIPS64
						<span id="statusSuccess" class="label label-success hidden"><i class="fa fa-thumbs-o-up"></i> Success</span>
						<span id="statusCompile" class="label label-info hidden"> <i class="fa fa-cog fa-spin"></i> Compling...</span></a>
						
					</div>
					<div class="collapse navbar-collapse"
						id="bs-example-navbar-collapse-6">
						<ul class="nav navbar-nav" style="float: right;">
							<li>
								<div class="btn-group" style="position: relative;top: 7px; left: -15px;">
								  <button id="fexe" name="fexe" type="button" class="btn btn-default active"><i class="fa fa-flag-checkered"></i> Full Execution Mode</button>
								  <button id="stexe" name="stexe" type="button" class="btn btn-default"><i class="fa fa fa-chevron-right"></i> Step Through Execution Mode</button>
								  <input type="hidden" id="mode" value="full">
								  <input type="hidden" id="lastInstruction" value="">
								</div>
								<div class="btn-group" style="position: relative;top: 7px; left: -4px;">
								    <button name="compile" type="button" class="btn btn-default">
											<i class="fa fa-cogs"></i> Compile
									</button>
								  <button disabled name="nextCc" id="nextCc"  type="button" class="btn btn-default ccControl"><i class="fa fa fa-arrow-circle-o-right"></i> Next</button>
								  <button name="clear" id="nextCc"  type="button" class="btn btn-default"><i class="fa fa fa-retweet"></i> Clear</button>
								</div>
							</li>
						</ul>
					</div>
					<!-- /.navbar-collapse -->
				</div>
			</nav>
		</div>
		<div style="">
		<span id="statusCc" class="label label-warning" style="position: relative; float: right; top: 15px; right: 44px; display:none;" data-placement="left" data-content="Opps... It seems the you reached the final cycle"><i class="fa fa-clock-o fa-2x"></i> 0</span>
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
				<li><a href="#memory" data-toggle="tab"><span class="fa-stack fa-1x"><i class="fa fa-film fa-2x"></i></span> Memory</a></li>
				<li><a href="#registers" data-toggle="tab"><span class="fa-stack fa-1x"><i class="fa fa-bars fa-2x"></i></span> Registers</a></li>
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
						<table class="table table-striped">
							<thead id="clockCycle">
							
							</thead>
							<tbody id="pipelineDetails">
							
							</tbody>
						</table>
					</div>
				</div>
				<div class="tab-pane fade in" id="internalRegisters">
					<div id="">
					<h3>Opcode Table</h3>
					<table class="table table-striped">
							<thead>
								<tr>
									<td>Line Number</td>
									<td>Instruction</td>
									<td>Opcode</td>
									<td>R (0-5)</td>
									<td>R (6-10)</td>
									<td>R (11-15)</td>
									<td>R (16-31)</td>
								</tr>
							</thead>
							<tbody id="opcodeTable">
							
							</tbody>
						</table>
						<br>
						<h3>Opcode Table</h3>
						<table class="table table-striped" id="internalRegistersTable">
						 
						</table>
					</div>
				</div>
				<div class="tab-pane fade in" id="memory">
				<form class="navbar-form navbar-left" role="search">
				  <div class="form-group">
				  	<span>Display Memory Address:</span>
				    <input id="memStart" type="text" class="form-control" placeholder="Start">
				    <input id="memEnd" type="text" class="form-control" placeholder="End">
				  </div>
				  <button name="fetchMem" type="button" class="btn btn-default">Display</button>
				</form>	
				<table class="table table-striped" id="memoryTable">
						 
				</table>
				</div>
				<div class="tab-pane fade in" id="registers">
					<div>
						<table class="table table-striped" id="registerTable">
						 
						</table>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
