var maxSize = 52428800;
var inMB = maxSize/1024/1024;
var fileformatsinclude = ['doc','docx','xls','xlsx','rtf','pdf','jpeg','jpg','png','txt','zip','dxf'];

function addFileInputFieldUpload() {
    var uploaderTbl = document.getElementById("uploadertbl");
    var tbody = uploaderTbl.lastChild;
    var trNo = (tbody.childElementCount ? tbody.childElementCount : tbody.childNodes.length) + 1;
    var tempTrNo = trNo - 1;
    var curFieldValue = $("#file1" + tempTrNo).val();
    var documentsSize = parseFloat($("#documentsSize").val()) + parseFloat(trNo);
    if(curFieldValue == "") {
        bootbox.alert("Field is empty!");
        return;
    }

    var tr = document.createElement("tr");
    tr.setAttribute("id", "row"+trNo);
    var td = document.createElement("td");
    var inputFile = document.createElement("input");
    inputFile.setAttribute("type", "file");
    inputFile.setAttribute("name", "file1");
    inputFile.setAttribute("id", "file1" + trNo);
    inputFile.setAttribute("class", "padding-4");
    inputFile.setAttribute("style", "color:#000000;");
    inputFile.setAttribute("onchange", "isValidFile(this.id)");
    //Adding for delete file function
    var bt = document.createElement("input");//addedd
    bt.setAttribute("type", "button");
     bt.setAttribute("id", "row"+trNo);
     bt.setAttribute("class", "padding-4");
     //bt.setAttribute("name", "button"+trNo);
    bt.setAttribute("value", "Remove");
     
    bt.setAttribute("onclick", "deleteFileInputField1(this.id)");
    
    td.appendChild(inputFile);
    tr.appendChild(td);
    tr.appendChild(bt);//added
    tbody.appendChild(tr);
}
function deleteFileInputField1(id){
	//alert("id "+id);
	document.getElementById(id).remove();
}
function deleteFileInputField(id){
	//alert("id "+id);
    var uploaderTbl = document.getElementById("uploadertbl");
    uploaderTbl.deleteRow(document.getElementById(id));
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
    window.opener.$("#documentDetails").append($(uploaderTbl));
}
function deleteFileField(id,docId){
	//alert(" id   "+id+  "  file  "+docId);
	$.ajax({
		url: "/services/works/estimatePreparation/deletedocument",     
		type: "GET",
		data: {
			id : id,
			docId: docId
			
		},
		dataType: "json",
		success: function (response) {
			console.log("success  "+response);
			if(response==='success'){
				window.location.reload();
			}
			
		}, 
		error: function (response) {
			//bootbox.alert('json fail');
			console.log("failed");
		}
	});
}