var maxSize = 2097152;
var inMB = maxSize/1024/1024;
var fileformatsinclude = ['doc','docx','xls','xlsx','rtf','pdf','jpeg','jpg','png','txt','zip','dxf'];

function addFileInputField() {
	var uploaderTbl = document.getElementById("uploadertbl");
	var tbody = uploaderTbl.lastChild;
	var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
	var tempTrNo = trNo - 1; 
	var curFieldValue = $("#file" + tempTrNo).val();
	if(curFieldValue == "") {
		bootbox.alert("Field is empty!");
		return;
	}
	var tr = document.createElement("tr");
	tr.setAttribute("id", "row"+trNo);
	var td = document.createElement("td");
	var inputFile = document.createElement("input");
	inputFile.setAttribute("type", "file");
	inputFile.setAttribute("name", "file");
	inputFile.setAttribute("id", "file" + trNo);
	inputFile.setAttribute("class", "padding-10");
	inputFile.setAttribute("onchange", "isValidFile(this.id)");
	td.appendChild(inputFile);
	tr.appendChild(td);
	tbody.appendChild(tr);	
}

function getTotalFileSize() {
	var uploaderTbl = document.getElementById("uploadertbl");
	var tbody = uploaderTbl.lastChild;
	var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
	var totalSize = 0;
	for(var i = 1; i < trNo; i++) {
		totalSize += $("#file"+i)[0].files[0].size; // in bytes
		if(totalSize > maxSize) {
			bootbox.alert('File size should not exceed '+ inMB +' MB!');
			$("#file"+i).val('');
			return;
		}
	}
} 

function isValidFile(id) {
	var myfile= $("#"+id).val();
	var ext = myfile.split('.').pop();
	if($.inArray(ext.toLowerCase(), fileformatsinclude) > -1){
		getTotalFileSize();
	} else {
		bootbox.alert("Please upload .doc, .docx, .xls, .xlsx, .rtf, .pdf, jpeg, .jpg, .png, .txt, .zip and .dxf format documents only");
		$("#"+id).val('');
		return false;
	}
}

function deleteFileInputField(id){
	var uploaderTbl = document.getElementById("uploadertbl");
	uploaderTbl.deleteRow(document.getElementById(id));
}

function addSelectedFiles() {
	var uploaderTbl = $("#uploadertbl");
	window.opener.$("#apnimandiDocuments").append($(uploaderTbl));
}
