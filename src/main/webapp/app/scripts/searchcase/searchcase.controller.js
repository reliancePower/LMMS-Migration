(function() {
	'use strict';

	angular
		.module('regCalApp.searchcase')
		.controller('SearchCaseController', SearchCaseController);

	SearchCaseController.$inject = ['$state', 'SearchCaseService',  'localStorageService','DTOptionsBuilder', 'DTColumnBuilder', 'DTColumnDefBuilder', '$scope', '$uibModal', '$uibModalStack', '$compile', '$window', '$filter', 'dashboardService', 'AddUserService', 'addCaseService', 'DTInstances', 'navbarService', '$timeout'];

	/* @ngInject */
	function SearchCaseController($state,  SearchCaseService,  localStorageService, DTOptionsBuilder, DTColumnBuilder, DTColumnDefBuilder, $scope, $uibModal, $uibModalStack, $compile, $window, $filter, dashboardService, AddUserService , addCaseService, DTInstances, navbarService, $timeout) {
		var vm = angular.extend(this, {
			viewCaseItem : viewCaseItem,
			editCaseItem : editCaseItem,			
			fetchAllMaster : fetchAllMaster,			
			listOfCases	 : [],		
			selected : '',
			company: '',
			business: '',
			vertical : '',
			forum : '',
			caseNo : '',
			petitioner : '',
			respondent : '',
			bench : '',
			aorOfCompany : '',
			counselOfCompany : '',
			aorOfRespondent : '',
			counselOfRespondent : '',
			lastDateOfHearing : '',
			nextDateOfHearing : '',
			furtherDates : '',
			businessRep : '',
			legalRep : '',
			caseStatus : '',
			subMatter : '',
			briefFacts : '',
			interimPrayer : '',
			finalPrayer : '',
			outcomeLast : '',
			outcomeNext : '',
			finImpact : '',
			caseCategory : '',
			enteredBy :'',
			dtOptions : {},
			caseType : '',			
			dtInstance : null,
			
			disableCaseItem : disableCaseItem,
			finImpTable : '',
			caseDetails : '',
			hearingTable : '',
			updateHearingItem : updateHearingItem,			
			form: {
				company: '',
				business: '',
				vertical : '',
				forum : '',
				forumCategory : '',
				bench : '',				
				lastDateOfHearing : '',
				nextDateOfHearing : '',
				counselOfCompany : '',
				counselOfRespondent : '',
				furtherDates : '',				
				caseStatus : 0,				
				caseType : '',
				outcomeLast : '',
				outcomeNext : '',
				startDate : '',
				endDate : '',
				caseID : '',
				caseNo : '',
				startDateLast : '',
				endDateLast : '',
			},
			
			resetFilter : resetFilter,			
			caseID : 0,			
			
			search : search,
			companyInfo : [],
			businessInfo : [],
			verticalInfo : [],
			forumInfo : [],
			benchInfo : [],			
			businessRepInfo : [],
			legalRepInfo :[],
			stateInfo : [],
			caseStatusInfo : [],
			caseCategoryInfo :[],	
			
			printTable : printTable,
			dtColumnDefs : [],
			generatePDF : generatePDF,
			today : new Date(),	
            accessCtrl : '',
            addDocItem : addDocItem,
            generatePDFWithPageNo : generatePDFWithPageNo,
            fetchForumInfo : fetchForumInfo,
            forumCategoryInfo : [],
            generatePDFWithPageNo2 : generatePDFWithPageNo2,
            getReviewReport : getReviewReport,
            getReviewReportWOFormat : getReviewReportWOFormat,
            sendNotes : sendNotes
			
				

		});	
		 (function activate() {
			 if(localStorageService.get('payrollNo') == undefined || localStorageService.get('payrollNo') == null){
				 $state.go('login');
				 return false;
			 }
             vm.userType = localStorageService.get('userType');
             vm.accessCtrl = localStorageService.get('accessCtrl');
   			 vm.verticalArr = [];
   			 vm.forumArr = [];
   			 vm.statusArr = [];
  			 vm.caseTypeArr = [];
  			 vm.stateArr = [];
   			 fetchAllMaster();
   			vm.dtColumnDefs = [
   			 DTColumnDefBuilder.newColumnDef(1).notVisible(),
      		 DTColumnDefBuilder.newColumnDef(2).notVisible(),
      		DTColumnDefBuilder.newColumnDef(5).notVisible(),
      	     DTColumnDefBuilder.newColumnDef(7).notVisible(),
      	     DTColumnDefBuilder.newColumnDef(11).notVisible(),
      	     DTColumnDefBuilder.newColumnDef(12).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(14).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(15).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(16).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(19).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(20).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(21).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(23).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(24).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(25).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(26).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(27).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(28).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(29).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(30).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(31).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(32).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(33).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(34).notVisible(),
   		   	 DTColumnDefBuilder.newColumnDef(35).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(36).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(37).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(38).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(39).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(40).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(41).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(42).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(43).notVisible(),
	   		 DTColumnDefBuilder.newColumnDef(44).notVisible(),
   		 
   		   	
   		   
   		  DTColumnDefBuilder.newColumnDef([16,17]).withOption('type', 'date-euro')
      	       ];
   			
   			
			 vm.dtOptions = DTOptionsBuilder.newOptions()			
	            .withPaginationType('full_numbers')
		             .withOption('order', [])
		             .withOption('autoWidth', true)
		              .withOption('deferRender', true)
	        .withButtons([
	        	 {
				        extend: "excelHtml5",
				        fileName:  "List of Cases",
				        title:"Law Matters Management Systems - List of Cases",
				        autoFilter : true,
				        exportOptions: {			        	
				        	format : {
					        	body: function ( data, row, column, node ) {
					        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
					        			var cont = node.textContent.substring(0, node.textContent.length -8);
					        			
					        			return  cont;
					        		}else if (node.id == 'caseID'){
					        			return node.textContent.trim();
					        		}
					        		else
					                return  data;
					            }
					        },
	                    
				            columns: [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42]
				        },
				        text : 'Excel Master Data',
				        
				        exportData: { decodeEntities: true }
				 },
				 {
				        extend: "colvis",			      
				        columns: ':not(.noVis)',
				       text:"Show/Hide Columns"
				        
				    
				 },
	        	
			    {
			        extend: "excelHtml5",
			        fileName:  "List of Cases",
			        title:"Law Matters Management Systems - List of Cases",
			        autoFilter : true,
			        exportOptions: {

			        	 format : {
			        	body: function ( data, row, column, node ) {
			        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
			        			var cont = node.textContent.substring(0, node.textContent.length -8);
			        			
			        			return  cont;
			        		}else if (node.id == 'caseID'){
			        			return node.textContent.trim();
				        		}
			        		else
			                return  data;
			            }
			        },
			             columns: ':visible:not(:last-child)'
			        },
			      
			        exportData: { decodeEntities: true }
			    },
	        	 {
				        extend: "pdfHtml5",
				        orientation : 'landscape',
			            pageSize : 'A2',
				        fileName:  "List of Cases",
				        title:"Law Matters Management Systems - List of Cases",

				        exportOptions: {

				        	 format : {
				        	body: function ( data, row, column, node ) {
				        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
				        			var cont = node.textContent.substring(0, node.textContent.length -8);
				        			
				        			return  cont;
				        		}else if (node.id == 'caseID'){
				        			return node.textContent.trim();
					        		}
				        		else
				                return  data;
				            }
				        },
				             columns: ':visible:not(:last-child)'
				        },
				        header: true,													       
				      
				        customize: function(doc, ev) {
				        	var cols = doc.content[1].table.body[0].length;
				        	if(cols > 15){
				        		alert('Only 15 columns are allowed to make PDF.');
				        		
				        		ev.stopPropagation();
				        		return false;
				        	}
				        
				        } ,
				        exportData: {decodeEntities:true}
				    
				    },{
				        extend: "print",	      
				      // orientation : 'landscape',
				        exportOptions: {

				        	 format : {
				        	body: function ( data, row, column, node ) {
				        		if(node.id == 'subMatter' || node.id == 'petitioners' || node.id == 'respondents'){
				        			var cont = node.textContent.substring(0, node.textContent.length -8);
				        			
				        			return  cont;
				        		}
				        		else
				                return  data;
				            }
				        },
				             columns: ':visible:not(:last-child)'
				        },
			        
				       autoPrint : true,
				       title:"List of Cases",
				       customize: function(doc, ev) {
							$.fn.columnCount = function() {
								    return $('th', $(this).find('thead')).length;
								};
								
								var colctr = $('#viewTable').columnCount();
								
								
					        	if(colctr > 16){
					        		alert('Only 15 columns are allowed to Print.');
					        		doc.close();
					        		ev.stopPropagation();
					        		return false;
					        	}else {
					        		var last = null;
					                var current = null;
					                var bod = [];
					 
					                var css = '@page { size: landscape; }',
					                    head = doc.document.head || doc.document.getElementsByTagName('head')[0],
					                    style = doc.document.createElement('style');
					 
					                style.type = 'text/css';
					                style.media = 'print';
					 
					                if (style.styleSheet)
					                {
					                  style.styleSheet.cssText = css;
					                }
					                else
					                {
					                  style.appendChild(doc.document.createTextNode(css));
					                }
					 
					                head.appendChild(style);
					        	}
					        
					        }     
				 }
				
				
				 ]);
   			
   	
   			
   		   	
         })();
		 
		 function sendNotes(val){
			 localStorageService.set('managenotes', val);
             $state.goToNewTab('managenotes', {});
		 }
		 
		 function getReviewReport(){
			 vm.regulatoryCases = _.where(vm.listOfCases, {caseTypeID: 13});
			 vm.legalCases = _.filter(vm.listOfCases, function(item){ return item.caseTypeID === 8 || item.caseTypeID === 11 || item.caseTypeID === 9 || item.caseTypeID === 10 || item.caseTypeID === 12 || item.caseTypeID === 16 })
			var ct = 1;
			 var endDate = $('#endDate').val();
			 var startDate = $('#startDate').val();
			 var com = $('#company option:selected').toArray().map(item => item.text).join();
			 
			
			
				var first = new Date();
			
				var day2 = ("0" + first.getDate()).slice(-2);
				var month2 = ("0" + (first.getMonth() + 1)).slice(-2);
				var days2 = (day2) + '-' + (month2) + '-' + first.getFullYear();
				var days = (day2) + '/' + (month2) + '/' + first.getFullYear()
			
			 var t1 = "<tr><td ><strong style = 'font-family: Arial;font-size:14px;'>A</strong></td><td colspan = '11'><strong style = 'font-size:14px;'>Regulatory</strong></td></tr>";
			 var t2 = "";
			 
			 vm.regulatoryCases = vm.regulatoryCases.sort(function(a, b) {
				 if (a.nextHearing === undefined) return 1;
                 else if (b.nextHearing === undefined) return -1;
                 else if (a.nextHearing == b.nextHearing) return 0;
                 else return new Date(a.nextHearing) - new Date(b.nextHearing)
             });
			 
			 vm.legalCases = vm.legalCases.sort(function(a, b) {
				 if (a.nextHearing === undefined) return 1;
                 else if (b.nextHearing === undefined) return -1;
                 else if (a.nextHearing == b.nextHearing) return 0;
                 else return new Date(a.nextHearing) - new Date(b.nextHearing)
             });
			 
			 
			 console.log(vm.regulatoryCases);
			 for(var i=0; i<vm.regulatoryCases.length; i++){
				 t2 += "<tr>";
//				 var caseTypeConSol = '';
//				 var ct1 = (vm.regulatoryCases[i].caseTypeName == undefined) ? "" : vm.regulatoryCases[i].caseTypeName;
//				 caseTypeConSol = (ct1 == '') ? '' : ct1 + ' / ';
//				 var cn = (vm.regulatoryCases[i].courtCaseID == undefined) ? "" : vm.regulatoryCases[i].courtCaseID 
//				 caseTypeConSol = (cn == '') ? (caseTypeConSol) : (caseTypeConSol + cn + ' / ');
//				 var cy = (vm.regulatoryCases[i].caseYear != 0) ? vm.regulatoryCases[i].caseYear : ""
//				caseTypeConSol = (cy == '') ? (caseTypeConSol) : (caseTypeConSol + cy);
//				console.log(caseTypeConSol);
				 var sp = vm.regulatoryCases[i].petitioners.split("P2.");				
				 var pet = sp[0].substring(3, sp[0].length) + ((vm.regulatoryCases[i].petitioners.indexOf("P2.") > -1) ? " and Ors,." : "");
				 var rsp = vm.regulatoryCases[i].respondents.split("R2.");				
				 var resp = rsp[0].substring(3, rsp[0].length)  + ((vm.regulatoryCases[i].respondents.indexOf("R2.") > -1) ? " and Ors,." : "");
				 
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+ct+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].caseID+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.regulatoryCases[i].vertical+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.regulatoryCases[i].forum+"</td>";				
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'> <strong> Bench : </strong> "+(vm.regulatoryCases[i].bench == '' ? 'NA' : vm.regulatoryCases[i].bench) +", <strong>AOR / Counsel of Petitioner : </strong> " +(vm.regulatoryCases[i].aorOfCompany == '' ? 'NA' : vm.regulatoryCases[i].aorOfCompany)+ " / " +(vm.regulatoryCases[i].counselOfCompany == '' ? 'NA' : vm.regulatoryCases[i].counselOfCompany)+" , <strong> AOR / Counsel of Respondent : </strong> " +(vm.regulatoryCases[i].aorOfRespondent  == '' ? 'NA' : vm.regulatoryCases[i].aorOfRespondent)+" / "+(vm.regulatoryCases[i].counselOfRespondent == '' ? 'NA' : vm.regulatoryCases[i].counselOfRespondent)+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+ vm.regulatoryCases[i].cNo +"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+pet+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+resp+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;mso-number-format:Short Date;'>"+((vm.regulatoryCases[i].nextHearingDate == undefined) ? "" : vm.regulatoryCases[i].nextHearingDate)+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].statusName+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].subMatter+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].finImpact+"</td>";
				 t2 += "</tr>";
				 ct++;
			 }
			 
			 
			 var t3 = "<tr><td ><strong style = 'font-family: Arial;font-size:14px;'>B</strong></td><td colspan = '11'><strong style = 'font-size:14px;'>Legal Matters</strong></td></tr>";
			 var t4 = "";
			 for(var j=0; j<vm.legalCases.length; j++){
				 t4 += "<tr>";
//				 var caseTypeConSol = '';
//				 var ct1 = (vm.legalCases[j].caseTypeName == undefined) ? "" : vm.legalCases[j].caseTypeName;
//				 caseTypeConSol = (ct1 == '') ? '' : ct1 + ' / ';
//				 var cn = (vm.legalCases[j].courtCaseID == undefined) ? "" : vm.legalCases[j].courtCaseID 
//				 caseTypeConSol = (cn == '') ? (caseTypeConSol) : (caseTypeConSol + cn + ' / ');
//				 var cy = (vm.legalCases[j].caseYear != 0) ? vm.legalCases[j].caseYear : ""
//				caseTypeConSol = (cy == '') ? (caseTypeConSol) : (caseTypeConSol + cy);
//				console.log(caseTypeConSol);
								 
				 
				 var sp1 = vm.legalCases[j].petitioners.split("P2.");				
				 var pet1 = sp1[0].substring(3, sp1[0].length) + ((vm.legalCases[j].petitioners.indexOf("P2.") > -1) ? " and Ors,." : "");
				 var rsp1 = vm.legalCases[j].respondents.split("R2.");				
				 var resp1 = rsp1[0].substring(3, rsp1[0].length)  + ((vm.legalCases[j].respondents.indexOf("R2.") > -1) ? " and Ors,." : "");
				//vnd.ms-excel.numberformat
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+ct+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].caseID+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.legalCases[j].vertical+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.legalCases[j].forum+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'> <strong> Bench : </strong>"+(vm.legalCases[j].bench == '' ? 'NA' : vm.legalCases[j].bench)+", <strong>AOR / Counsel of Petitioner : </strong> " +(vm.legalCases[j].aorOfCompany == '' ? 'NA' : vm.legalCases[j].aorOfCompany)+ " / " +(vm.legalCases[j].counselOfCompany == '' ? 'NA' : vm.legalCases[j].counselOfCompany)+", <strong> AOR / Counsel of Respondent : </strong> "  +(vm.legalCases[j].aorOfRespondent  == '' ? 'NA' : vm.legalCases[j].aorOfRespondent)+" / "+(vm.legalCases[j].counselOfRespondent == '' ? 'NA' : vm.legalCases[j].counselOfRespondent)+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>" + vm.legalCases[j].cNo +"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+pet1+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+resp1+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;mso-number-format:Short Date;'>"+((vm.legalCases[j].nextHearingDate == undefined) ? "" : vm.legalCases[j].nextHearingDate)+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].statusName+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].subMatter+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].finImpact+"</td>";
				 t4 += "</tr>";
				 ct++;
			 }
			 
			
			 $("#dynamicInfo").html(t1+t2+t3+t4);
			 
					 
			 var caption = '<strong style = "font-family:Arial;font-size:14px;border:1px;">'+ com+' MANAGEMENT REVIEW - REPORT OF REGULATORY & LEGAL MATTERS - '+ startDate +' TO '+endDate+' (SIGNIFICANT MATTERS)</strong>';											
			 $('#captionTable').html(caption);
			 
			var fileName = 'List of Important Regulatory & Legal Matters - '+days2;
			 
			var txt = $('#reviewReportTable').html()
			
			
			//tableToExcel('#reviewReportTable', '', fileName);
			
			var blob,
			template = txt;

		blob = new Blob([template], {
		    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
		});
		
		saveAs(blob, fileName+'.xls');
		
			
//			 var wb = XLSX.utils.table_to_book(document.getElementById('rvTable'),{raw:true, sheet:"Sheet 1"});
//		        var wbout = XLSX.write(wb, {bookType:'xlsx', cellDates:false, ignoreEC:true , bookSST:false, type: 'binary'});
//		        function s2ab(s) {
//		                        var buf = new ArrayBuffer(s.length);
//		                        var view = new Uint8Array(buf);
//		                        for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
//		                        return buf;
//		        }
//		       
//		        saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), fileName+'.xlsx');
//		       
			
		
			
			

			
			
		 }
		 
		 
		 function getReviewReportWOFormat(){
			 vm.regulatoryCases = _.where(vm.listOfCases, {caseTypeID: 13});
			 vm.legalCases = _.filter(vm.listOfCases, function(item){ return item.caseTypeID === 8 || item.caseTypeID === 11 || item.caseTypeID === 9 || item.caseTypeID === 10 || item.caseTypeID === 12 || item.caseTypeID === 16 })
			var ct = 1;
			 var endDate = $('#endDate').val();
			 var startDate = $('#startDate').val();
			 var com = $('#company option:selected').toArray().map(item => item.text).join();
			 
			
			
				var first = new Date();
			
				var day2 = ("0" + first.getDate()).slice(-2);
				var month2 = ("0" + (first.getMonth() + 1)).slice(-2);
				var days2 = (day2) + '-' + (month2) + '-' + first.getFullYear();
				var days = (day2) + '/' + (month2) + '/' + first.getFullYear()
			
			 var t1 = "<tr><td ><strong style = 'font-family: Arial;font-size:14px;'>A</strong></td><td colspan = '11'><strong style = 'font-size:14px;'>Regulatory</strong></td></tr>";
			 var t2 = "";
			 
			 vm.regulatoryCases = vm.regulatoryCases.sort(function(a, b) {
				 if (a.nextHearing === undefined) return 1;
                 else if (b.nextHearing === undefined) return -1;
                 else if (a.nextHearing == b.nextHearing) return 0;
                 else return new Date(a.nextHearing) - new Date(b.nextHearing)
             });
			 
			 vm.legalCases = vm.legalCases.sort(function(a, b) {
				 if (a.nextHearing === undefined) return 1;
                 else if (b.nextHearing === undefined) return -1;
                 else if (a.nextHearing == b.nextHearing) return 0;
                 else return new Date(a.nextHearing) - new Date(b.nextHearing)
             });
			 
			 
			 console.log(vm.regulatoryCases);
			 for(var i=0; i<vm.regulatoryCases.length; i++){
				 t2 += "<tr>";
				 var caseTypeConSol = '';
				 var ct1 = (vm.regulatoryCases[i].caseTypeName == undefined) ? "" : vm.regulatoryCases[i].caseTypeName;
				 caseTypeConSol = (ct1 == '') ? '' : ct1 + ' / ';
				 var cn = (vm.regulatoryCases[i].courtCaseID == undefined) ? "" : vm.regulatoryCases[i].courtCaseID 
				 caseTypeConSol = (cn == '') ? (caseTypeConSol) : (caseTypeConSol + cn + ' / ');
				 var cy = (vm.regulatoryCases[i].caseYear != 0) ? vm.regulatoryCases[i].caseYear : ""
				caseTypeConSol = (cy == '') ? (caseTypeConSol) : (caseTypeConSol + cy);
				console.log(caseTypeConSol)
				 var sp = vm.regulatoryCases[i].petitioners.split("P2.");				
				 var pet = sp[0].substring(3, sp[0].length) + ((vm.regulatoryCases[i].petitioners.indexOf("P2.") > -1) ? " and Ors,." : "");
				 var rsp = vm.regulatoryCases[i].respondents.split("R2.");				
				 var resp = rsp[0].substring(3, rsp[0].length)  + ((vm.regulatoryCases[i].respondents.indexOf("R2.") > -1) ? " and Ors,." : "");
				 
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+ct+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].caseID+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.regulatoryCases[i].vertical+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.regulatoryCases[i].forum+"</td>";				
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'> <strong> Bench : </strong> "+(vm.regulatoryCases[i].bench == '' ? 'NA' : vm.regulatoryCases[i].bench) +", <strong>AOR / Counsel of Petitioner : </strong> " +(vm.regulatoryCases[i].aorOfCompany == '' ? 'NA' : vm.regulatoryCases[i].aorOfCompany)+ " / " +(vm.regulatoryCases[i].counselOfCompany == '' ? 'NA' : vm.regulatoryCases[i].counselOfCompany)+" , <strong> AOR / Counsel of Respondent : </strong> "  +(vm.regulatoryCases[i].aorOfRespondent  == '' ? 'NA' : vm.regulatoryCases[i].aorOfRespondent)+" / "+(vm.regulatoryCases[i].counselOfRespondent == '' ? 'NA' : vm.regulatoryCases[i].counselOfRespondent)+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+ caseTypeConSol +"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+pet+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+resp+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+((vm.regulatoryCases[i].nextHearingDate == undefined) ? "" : vm.regulatoryCases[i].nextHearingDate)+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].statusName+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].subMatter+"</td>";
				 t2 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.regulatoryCases[i].finImpact+"</td>";
				 t2 += "</tr>";
				 ct++;
			 }
			 
			 
			 var t3 = "<tr><td ><strong style = 'font-family: Arial;font-size:14px;'>B</strong></td><td colspan = '11'><strong style = 'font-size:14px;'>Legal Matters</strong></td></tr>";
			 var t4 = "";
			 for(var j=0; j<vm.legalCases.length; j++){
				 t4 += "<tr>";
				 var caseTypeConSol = '';
				 var ct1 = (vm.legalCases[j].caseTypeName == undefined) ? "" : vm.legalCases[j].caseTypeName;
				 caseTypeConSol = (ct1 == '') ? '' : ct1 + ' / ';
				 var cn = (vm.legalCases[j].courtCaseID == undefined) ? "" : vm.legalCases[j].courtCaseID 
				 caseTypeConSol = (cn == '') ? (caseTypeConSol) : (caseTypeConSol + cn + ' / ');
				 var cy = (vm.legalCases[j].caseYear != 0) ? vm.legalCases[j].caseYear : ""
				caseTypeConSol = (cy == '') ? (caseTypeConSol) : (caseTypeConSol + cy);
				console.log(caseTypeConSol)
								 
				 
				 var sp1 = vm.legalCases[j].petitioners.split("P2.");				
				 var pet1 = sp1[0].substring(3, sp1[0].length) + ((vm.legalCases[j].petitioners.indexOf("P2.") > -1) ? " and Ors,." : "");
				 var rsp1 = vm.legalCases[j].respondents.split("R2.");				
				 var resp1 = rsp1[0].substring(3, rsp1[0].length)  + ((vm.legalCases[j].respondents.indexOf("R2.") > -1) ? " and Ors,." : "");
				//vnd.ms-excel.numberformat
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+ct+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].caseID+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.legalCases[j].vertical+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+vm.legalCases[j].forum+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'> <strong> Bench : </strong>"+(vm.legalCases[j].bench == '' ? 'NA' : vm.legalCases[j].bench)+", <strong>AOR / Counsel of Petitioner : </strong> " +(vm.legalCases[j].aorOfCompany == '' ? 'NA' : vm.legalCases[j].aorOfCompany)+ " / " +(vm.legalCases[j].counselOfCompany == '' ? 'NA' : vm.legalCases[j].counselOfCompany)+", <strong> AOR / Counsel of Respondent : </strong> "  +(vm.legalCases[j].aorOfRespondent  == '' ? 'NA' : vm.legalCases[j].aorOfRespondent)+" / "+(vm.legalCases[j].counselOfRespondent == '' ? 'NA' : vm.legalCases[j].counselOfRespondent)+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>" + caseTypeConSol +"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+pet1+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;width:150px;'>"+resp1+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+((vm.legalCases[j].nextHearingDate == undefined) ? "" : vm.legalCases[j].nextHearingDate)+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].statusName+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].subMatter+"</td>";
				 t4 += "<td style = 'font-family: Arial;font-size:14px;'>"+vm.legalCases[j].finImpact+"</td>";
				 t4 += "</tr>";
				 ct++;
			 }
			 
			
			 $("#dynamicInfo").html(t1+t2+t3+t4);
			 
					 
			 var caption = '<strong style = "font-family:Arial;font-size:14px;border:1px;">'+ com+' MANAGEMENT REVIEW - REPORT OF REGULATORY & LEGAL MATTERS - '+ startDate +' TO '+endDate+' (SIGNIFICANT MATTERS)</strong>';											
			 $('#captionTable').html(caption);
			 
			var fileName = 'List of Important Regulatory & Legal Matters - '+days2;
			 
			var txt = $('#reviewReportTable').html()
			
			
	
		
			
			 var wb = XLSX.utils.table_to_book(document.getElementById('rvTable'),{raw:true, sheet:"Sheet 1"});
		        var wbout = XLSX.write(wb, {bookType:'xlsx', bookSST:true, type: 'binary'});
		        function s2ab(s) {
		                        var buf = new ArrayBuffer(s.length);
		                        var view = new Uint8Array(buf);
		                        for (var i=0; i<s.length; i++) view[i] = s.charCodeAt(i) & 0xFF;
		                        return buf;
		        }
		       
		        saveAs(new Blob([s2ab(wbout)],{type:"application/octet-stream"}), fileName+'.xlsx');

//			TableToExcel.convert(document.getElementById("rvTable"), {
//				  name: fileName+".xlsx",
//				  sheet: {
//				    name: "Sheet 1"
//				  }
//				});
//			
			

			
			
		 }
		 
		 
		 function fetchForumInfo(item, value){
			 $('#forum').empty();
			 var jsonReq = {
						"type" : "fetchForumForSearch",
						"forumCategory" : item.toString(),					
						"userID" : localStorageService.get('payrollNo'),
						"sessID" : localStorageService.get('sessionID')
					}
			 addCaseService.fetchAllMasters(jsonReq)
				.then(function(result) {
					if(result.data.status == 'S'){
						alert(result.data.msg);
						if (localStorageService.get('payrollNo') == undefined
								|| localStorageService.get('payrollNo') == '') {
							$window.localStorage.clear();
							localStorageService.clearAll;
							localStorageService.cookie.clearAll;
							$state.go('login');
						} else {
							navbarService
									.logout()
									.then(
											function(result) {
												$window.localStorage.clear();
												localStorageService.clearAll;
												localStorageService.cookie.clearAll;
												$state.go('login');
											},
											function caseError(error) {
												localStorageService.clearAll;
												localStorageService.cookie.clearAll;
												$state.go('login');
												console
														.log('There seems some problem. Please try again later...');

											});
						}
					}

						console.log(result.data);
						vm.forumInfo = result.data.resultArray;
						   			
		    			 for (var i = 0; i < vm.forumInfo.length; i++) {
		                     $('#forum').append('<option value="'+vm.forumInfo[i].id+'">'+vm.forumInfo[i].name+'</option>'); 
		                 }
		    			
		    			 $("#forum").multiselect("rebuild");
		    			 if(value != undefined)
		    				 $("#forum").multiselect('select', value);	

					}, function loginError(error) {

						alert('Something went wrong... Pls try again...');
						return false;
					});
		 }
         
		 function generatePDF(){
			 var sampleJSON = [];
			 var title = ''
			 var sorted = []
			 DTInstances.getList().then(function(dtInstances) {
			      sampleJSON.push(dtInstances.viewTable.DataTable.context[0].aiDisplay)
			     
			      sorted = _.sortBy(vm.listOfCases, function(obj){
			    	
			    	    return _.indexOf(_.toArray(sampleJSON[0]), obj.id);
			    	});
			    	
			  var startDate = $('#startDate').val();
			  var endDate = $('#endDate').val();
			 
			      if(startDate != '' && endDate != '' && startDate != undefined && endDate != undefined){
			    	  title = 'List of Cases Scheduled for hearing between ' + startDate + ' and ' + endDate;
			    	  $('#dynamicTitle').text(title);
			      }
			 $('#downNew').empty();
			 var tab = '';
			 var tbody = '';
			var today =  $filter('date')(vm.today, "MMM dd, yyyy");
			var tableHeader = '<table id="viewTablePDF" class="table table-bordered tableCls noPadd" >  <thead> <tr> <th>Case ID</th>  <th>Vertical</th> <th>Forum</th> <th>Case Category</th> <th>Case No</th> <th>Year</th> <th>Petitioner</th><th>Respondent</th> <th>Next Hearing</th> <th>Status</th> <th>Subject Matter</th> </tr></thead> <tbody>';
			var tableFooter = '</tbody>  </table><br>';
				 _.each(sorted, function(item, key){
				 var tab1 = '';
				 var tab2 = '';
				 var tab3 = '';
				 var lastdate = (item.lastHearingDate == undefined) ? '' : $filter('date')(item.lastHearingDate, "dd/MM/yyyy");
				 var nextdate = (item.nextHearingDate == undefined) ? '' : $filter('date')(item.nextHearingDate, "dd/MM/yyyy");
				 var caseCategoryName = (item.caseCategoryName == undefined) ? '' : item.caseCategoryName;
				 var state = (item.state == undefined) ? '' : item.state;
				 var statusName = (item.statusName == undefined) ? '' : item.statusName;
				 var assessYear = (item.assessYear == undefined) ? '' : item.assessYear;
				 var amountAllow = '';
				 var titleDirect = '';
				 var bussLegal = '';
				 var casePri = '';
				 var loc = '';
				 var subMat = (item.subMatter.length > 50 ) ? item.subMatter.substring(0,50) + '...' : item.subMatter;
				 var pet = (item.petitioners.length > 50 ) ? item.petitioners.substring(0,50) + '...' : item.petitioners;
				 var resp = (item.respondents.length > 50 ) ? item.respondents.substring(0,50) + '...' : item.respondents;
				
				 tbody += '<tr ><td>'+item.caseID+'</td><td>'+item.vertical+'</td><td>'+ (item.forum != undefined ? item.forum : '')  +'</td><td>'+item.caseType+'</td><td style = "width:40px !important;">'
				 +item.caseNo+'</td><td>'+ (item.caseYear != 0 ? item.caseYear : '')   +'</td><td>'+pet+'</td><td>'+resp+'</td><td>'+nextdate+'</td><td>'+statusName+'</td><td>'+subMat+'</td></tr>';
				 
				  
				 if(item.caseTypeID != 1){
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td colspan = "4"> <b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact) + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td></tr>';
					 bussLegal = '<tr> <td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> ';
						
				 }else {
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td><b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact)  + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td><td class = "blueColor" style = "font-weight:600;"> Amount of Disallowance </td><td > '+item.amtOfDisallow + '</td></tr>';   				
					 bussLegal = '<tr><td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';				
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> '; 
				 }
				 
				 if(assessYear == ''){
					 titleDirect = '<div class = "col-sm-6" ></div>'
						 
				 }else {
					 titleDirect = '<div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>AY :</strong></div>'+
					   '<div class = "col-sm-4" style = "font-size:13px !important;">'+assessYear+' <strong class = "blueColor" style = "font-size:14px !important;margin-left:10px;">FY :</strong> <span style = "font-size:13px !important;">'+item.finYear+'</span></div>';
					 
				 }


				 tab1 = '<div id = "detail_'+key+'" class="text-center" style = "border-bottom :none;page-break-before: always;" > <strong style = "color:#005199 !important;">' +
				   'Case Details for case ID ' + 	item.caseID+ ' with Ref.no '+ item.refNo+ '</strong> </div><hr><div  "style = "padding-left:25px;padding-right:25px;" id = "viewAllMatter">' + 
				   '<div class = "row" > <div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Company :</strong></div> ' + 
				   '<div class = "col-sm-4" style = "font-size:13px !important;">'+item.company+'</div> <div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Case Category :</strong></div>'+
				   '<div class = "col-sm-4" style = "font-size:13px !important;">'+item.caseType+'</div></div><div class = "row" ><div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Business :</strong>'+
				   '</div><div class = "col-sm-4" style = "font-size:13px !important;">'+item.business+'</div>'+titleDirect+'</div><div class = "row" > <div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Vertical : </strong></div><div class = "col-sm-4" style = "font-size:13px !important;"> ' + item.vertical + '</div><div class = "col-sm-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Report as on : </strong> </div><div class = "col-sm-4" style = "font-size:13px !important;">'+ today+ '</div></div>';
				 

				 tab2 =  '<br><div class = "row"> <div class="col-sm-12"> <table class="table viewPopupTable" id = "viewPopupTable_'+key+'"> <tbody><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Court Case No </td> <td> ' +item.caseNo + 
					'</td><td class = "blueColor" style = "font-weight:600;">Case Year</td><td> ' +(item.caseYear != 0 ? item.caseYear : '')+ 
					'</td></tr><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Forum </td> <td> ' + (item.forum != undefined ? item.forum : '')  +
					'</td><td class = "blueColor" style = "font-weight:600;">Case Type</td><td> '+ (item.caseTypeName != undefined ? item.caseTypeName : '')   +
					'</td></tr><tr> <td class = "blueColor" style = "font-weight:600;">Petitioner</td> ' + 
					'<td class = "bindHTML">'+item.petitioner+' </td><td class = "blueColor" style = "font-weight:600;">Respondent</td> '+
					'<td class = "bindHTML"> '+item.respondent+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">AOR for Petitioner</td> ' + 
					'<td> '+item.aorOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">AOR for Respondent</td>  ' + 
					'<td>'+item.aorOfRespondent+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Counsel for Petitioner</td> ' + 
					'<td>'+item.counselOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">Counsel for Respondent</td> ' + 
					'<td>'+item.counselOfRespondent+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">Bench</td> ' + 
					'<td colspan = "4">'+item.bench+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Subject Matter</td>  ' + 
					'<td colspan = "4">'+item.subMatter+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Brief Facts</td> ' + 
					'<td colspan = "4">'+item.briefFacts+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Interim Prayer</td>  ' + 
					'<td colspan = "4"> '+item.interimPrayer+'</td></tr> ';
					
	
			
				 tab3 = '<tr>  <td class = "blueColor" style = "font-weight:600;">Final Prayer</td> ' + 
					'<td colspan = "4">'+item.finalPrayer+'</td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Last Hearing</td> ' + 
					'<td><strong>'+lastdate+'</strong></td>  <td class = "blueColor" style = "font-weight:600;">Outcome Of Last Hearing</td> ' + 
					'<td><strong> '+item.outcomeLast+'</strong></td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Next Hearing</td> ' + 
					'<td><strong>'+nextdate+' </strong> </td>  <td class = "blueColor" style = "font-weight:600;">Likely Outcome Of Next Hearing</td> ' + 
					'<td><strong> '+item.outcomeNext+'</strong></td> </tr>' +amountAllow + bussLegal + casePri + loc +'</tbody> </table> </div> </div></div>';
				 
				 tab += tab1+tab2+tab3;
			 
		 });
				 
				 var final =  tableHeader + tbody + tableFooter + tab;
				 $('#downNew').append(final);
				 

			   var w = window.open();
			   w.document.write(
			                   $("#print-headerNew").html()  + 
			                   $("#pdfDownloadNew").html() +
			                   $("#print-footerNew").html() 
			                   );
			   w.document.close();
			   w.focus();
			 
			 });	

		 }
		 
		 
		 
		 function generatePDFWithPageNo(){
			 var sampleJSON = [];
			 var title = ''
			 var sorted = [];
			 var criteria = '';
			 var selection = localStorageService.get('searchCaseDetails');
		

			 var selected =[]
			 criteria = '<br><div style = "border:1px solid #ccc;"><h3 style = "text-decoration:underline;text-align:center;font-size:13px;">Report Search Criteria</h3><br>';
				 
			 
			
			 if(selection.company == '')
				 selected.push({'title' : 'Company', 'value':'All'});
			 else if(selection.company == '0')
					 selected.push({'title' : 'Company', 'value':'All'});
			 else if(selection.company.indexOf(',') > -1){ 
				 var mulValues = []
				 var toArr = selection.company.split(',');
				 _.find(toArr, function(num){
					 var temp =  _.where(vm.companyInfo, {id:  parseInt(num)})
					 mulValues.push(temp[0].name);
				 })
						 selected.push({'title' : 'Company', 'value':mulValues.toString()})
			 }
					 else  {
						 var val = _.where(vm.companyInfo, {id: parseInt(selection.company)});
						 
						 selected.push({'title' : 'Company', 'value':val[0].name})
					 }
			 
			 if(selection.business == '')
				 selected.push({'title' : 'Business', 'value':'All'})
			else if(selection.business == '0')
				 selected.push({'title' : 'Business', 'value':'All'})
					
			 	 
			 else if(selection.business.indexOf(',') > -1) 
				
				 selected.push({'title' : 'Business', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.businessInfo, {id: parseInt(selection.business)});
						
						
						 selected.push({'title' : 'Business', 'value':val[0].name})
					 }
			 
			 if(selection.vertical == '' || (selection.vertical == '0' && selection.business == ''))
				 criteria += '';
			else if(selection.vertical == '0')
				 selected.push({'title' : 'Vertical', 'value':'All'})	 	 
			 else if(selection.vertical.indexOf(',') > -1) 
				 selected.push({'title' : 'Vertical', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.verticalInfo, {id: parseInt(selection.vertical)});
						
					
						 selected.push({'title' : 'Vertical', 'value':val[0].fullname})
					 }
			
			 if(selection.caseType == '')
				 selected.push({'title' : 'Case Category', 'value':'All'})	
			else if(selection.caseType == '0')
				selected.push({'title' : 'Case Category', 'value':'All'})	
			 else if(selection.caseType.indexOf(',') > -1) 
				 selected.push({'title' : 'Case Category', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.caseTypeInfo, {id: parseInt(selection.caseType)});
						
						
						 selected.push({'title' : 'Case Category', 'value':val[0].name})
					 }
			 
			 if(selection.forumCategory != ''){
				 selected.push({'title' : 'Forum Category', 'value':toTitleCase(selection.forumCategory)}) 
			      
			 }
			 
			 if(selection.forum == '')
				 criteria += '';
			else if(selection.forum == '100000000')
				selected.push({'title' : 'Forum', 'value':'All'})	
			 else if(selection.forum.indexOf(',') > -1) 
				 selected.push({'title' : 'Forum', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.forumInfo, {id: parseInt(selection.forum)});
						
						 selected.push({'title' : 'Forum', 'value':val[0].name})
					 }
			 
			 if(selection.caseStatus == '')
				 criteria += '';
			else if(selection.caseStatus == '0')
				selected.push({'title' : 'Case Status', 'value':'All'})	
				 
			 else if(selection.caseStatus.indexOf(',') > -1) 
				 selected.push({'title' : 'Case Status', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.caseStatusInfo, {id: parseInt(selection.caseStatus)});
						
						 selected.push({'title' : 'Case Status', 'value':val[0].name})
					 }
			 
			 if(selection.state == '')
				 criteria += '';
			else if(selection.state == '0')
				selected.push({'title' : 'State', 'value':'All'})	
			 else if(selection.state.indexOf(',') > -1) 
				 selected.push({'title' : 'State', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.stateInfo, {id: parseInt(selection.state)});
						
						 selected.push({'title' : 'State', 'value':val[0].name})
					 }
			 
			 if(selection.startDate != ''){
				 selected.push({'title' : 'Next Hearing - Start Date', 'value':selection.startDate}) 
		      }
			 
			 if(selection.endDate != ''){
				 selected.push({'title' : 'Next Hearing - End Date', 'value':selection.endDate}) 
			      
			 }
 if(selection.caseID != ''){
	 selected.push({'title' : 'Case ID', 'value':selection.caseID}) 
     		 
			 }
 if(selection.caseNo != ''){
	 selected.push({'title' : 'Case No', 'value':selection.caseNo}) 
      
 }
 if(selection.startDateLast != ''){
	 selected.push({'title' : 'Last Hearing - Start Date', 'value':selection.startDateLast}) 
      
 }
 if(selection.endDateLast != ''){
	 selected.push({'title' : 'Last Hearing - End Date', 'value':selection.endDateLast}) 
      
 }
 if(selection.judgePronCheck != ''){
	 selected.push({'title' : 'Judgement Pronounced Included', 'value':selection.judgePronCheck}) 
      
 }
 
 
	console.log(selected)		
	
	for(var p=0; p<selected.length; p += 2){
		if(selected.length % 2 == 0)
		 criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p+1].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p+1].value+'</div></div>';
		else if(p == selected.length-1)
			criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div></div><div class = "col-sm-3"></div><div class = "col-sm-3"></div>';
		else
			 criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p+1].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p+1].value+'</div></div>';
			
		
	}
			 
			 
			 criteria += '<br></div>';
			 
			 DTInstances.getList().then(function(dtInstances) {
			      sampleJSON.push(dtInstances.viewTable.DataTable.context[0].aiDisplay)
			     
			      sorted = _.sortBy(vm.listOfCases, function(obj){
			    	
			    	    return _.indexOf(_.toArray(sampleJSON[0]), obj.id);
			    	});
			    	
			
			 

			 $('#down').empty();
			 var tab = '';
			 var tbody = '';
			var today =  $filter('date')(vm.today, "MMM dd, yyyy");
			var todayWithFormat =  $filter('date')(vm.today, "dd/MM/yyyy HH:mm");
			var tableHeader = '<div class = "cabecalho" style = "display:none;">Report as on '+todayWithFormat+'</div><table id="viewTablePDF" class="table table-bordered tableCls noPadd viewTablePDF" >  <thead> <tr> <th style = "width:40px !important;">Case ID</th>  <th>Vertical</th> <th>Forum</th> <th>Case Category</th> <th style = "width:40px !important;">Case No</th> <th style = "width:40px !important;">Year</th> <th>Petitioner</th><th>Respondent</th> <th>Next Hearing</th> <th>Status</th> <th>Subject Matter</th>  <th style = "width:32px !important;">Page</th> </tr> </thead> <tbody class = "tableRowVal">';
			var tableFooter = '</tbody>  </table><br>';
				 _.each(sorted, function(item, key){
				 var tab1 = '';
				 var tab2 = '';
				 var tab3 = '';
				 var lastdate = (item.lastHearingDate == undefined) ? '' : $filter('date')(item.lastHearingDate, "dd/MM/yyyy");
				 var nextdate = (item.nextHearingDate == undefined) ? '' : $filter('date')(item.nextHearingDate, "dd/MM/yyyy");
				 var caseCategoryName = (item.caseCategoryName == undefined) ? '' : item.caseCategoryName;
				 var state = (item.state == undefined) ? '' : item.state;
				 var statusName = (item.statusName == undefined) ? '' : item.statusName;
				 var assessYear = (item.assessYear == undefined) ? '' : item.assessYear;
				 var amountAllow = '';
				 var titleDirect = '';
				 var bussLegal = '';
				 var casePri = '';
				 var loc = '';
				 
				 var subMat = (item.subMatter.length > 50 ) ? item.subMatter.substring(0,50) + '...' : item.subMatter;
				 var pet = (item.petitioners.length > 50 ) ? item.petitioners.substring(0,50) + '...' : item.petitioners;
				 var resp = (item.respondents.length > 50 ) ? item.respondents.substring(0,50) + '...' : item.respondents;
				 
				 tbody += '<tr ><td style = "width:40px !important;">'+item.caseID+'</td><td>'+item.vertical+'</td><td>'+ (item.forum != undefined ? item.forum : '')  +'</td><td>'+item.caseType+'</td><td style = "width:40px !important;">'
				 +item.caseNo+'</td><td style = "width:40px !important;">'+ (item.caseYear != 0 ? item.caseYear : '')   +'</td><td>'+pet+'</td><td>'+resp+'</td><td>'+nextdate+'</td><td>'+statusName+'</td><td>'+subMat+'</td><td style = "width:32px !important;"><a class = "pageref" href="#viewPage_'+key+'"></a></td></tr>';
				 if(item.caseTypeID != 1){
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td colspan = "4">  <b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact) + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td></tr>';
					 bussLegal = '<tr> <td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> ';
						
				 }else {
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td>  <b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact) + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td><td class = "blueColor" style = "font-weight:600;"> Amount of Disallowance </td><td > '+item.amtOfDisallow + '</td></tr>';   				
					 bussLegal = '<tr> <td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';				
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> '; 
				 }
				 
				 if(assessYear == ''){
// titleDirect = '<div class = "col-sm-6" ></div>'
					 titleDirect = '<div class = "col-sm-3 blueColor" style = "text-align: right;font-size:14px !important;visibility:hidden;"><strong>AY :</strong> NA</div>'+
					   '<div class = "col-sm-3" style = "font-size:14px !important;visibility:hidden;"> <strong class = "blueColor" style = "font-size:14px !important;margin-left:10px;">FY :</strong> <span style = "font-size:14px !important;">NA</span></div>';
					 
						 
				 }else {
					 titleDirect = '<div class = "col-sm-3 blueColor" style = "text-align: right;font-size:14px !important;"><strong>AY :</strong> '+assessYear+'</div>'+
					   '<div class = "col-sm-3" style = "font-size:14px !important;"> <strong class = "blueColor" style = "font-size:14px !important;margin-left:10px;">FY :</strong> <span style = "font-size:13px !important;">'+item.finYear+'</span></div>';
					 
				 }


				 tab1 = '<br><div id = "viewPage_'+key+'" class = "level1 detailView" ><br><div id = "detail_'+key+'" class="text-center" style = "text-align:center;" > <strong style = "color:#005199 !important;font-size:14px;">' +
				   'Case Details for case ID ' + 	item.caseID+ ' with Ref.no '+ item.refNo+ '</strong> </div><br><div  style = "padding-top:10px;border-top:1px solid #ccc;" id = "viewAllMatter_'+key+'">' + 
				   '<div class = "row" > <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Company :</strong></div> ' + 
				   '<div class = "col-4" style = "font-size:13px !important;">'+item.company+'</div> <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Case Category :</strong></div>'+
				   '<div class = "col-4" style = "font-size:13px !important;">'+item.caseType+'</div></div><div class = "row" ><div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Business :</strong>'+
				   '</div><div class = "col-4" style = "font-size:13px !important;">'+item.business+'</div>'+titleDirect+'</div><div class = "row" > <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Vertical : </strong></div><div class = "col-4" style = "font-size:13px !important;"> ' + item.vertical + '</div><div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Report as on : </strong> </div><div class = "col-4" style = "font-size:13px !important;">'+ today+ '</div></div>';
				 

				 tab2 =  '<br><div class = "row"> <div class="col-sm-12"> <table class="table  viewPopupTable" id = "viewPopupTable_'+key+'"> <tbody><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Court Case No </td> <td> ' +item.caseNo + 
					'</td><td class = "blueColor" style = "font-weight:600;">Case Year</td><td> ' +(item.caseYear != 0 ? item.caseYear : '')+ 
					'</td></tr><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Forum </td> <td> ' + (item.forum != undefined ? item.forum : '')  +
					'</td><td class = "blueColor" style = "font-weight:600;">Case Type</td><td> '+ (item.caseTypeName != undefined ? item.caseTypeName : '')   +
					'</td></tr><tr> <td class = "blueColor" style = "font-weight:600;">Petitioner</td> ' + 
					'<td >'+item.petitioners+' </td><td class = "blueColor" style = "font-weight:600;">Respondent</td> '+
					'<td "> '+item.respondents+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">AOR for Petitioner</td> ' + 
					'<td> '+item.aorOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">AOR for Respondent</td>  ' + 
					'<td>'+item.aorOfRespondent+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Counsel for Petitioner</td> ' + 
					'<td>'+item.counselOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">Counsel for Respondent</td> ' + 
					'<td>'+item.counselOfRespondent+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">Bench</td> ' + 
					'<td colspan = "4">'+item.bench+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Subject Matter</td>  ' + 
					'<td colspan = "4">'+item.subMatter+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Brief Facts</td> ' + 
					'<td colspan = "4">'+item.briefFacts+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Interim Prayer</td>  ' + 
					'<td colspan = "4"> '+item.interimPrayer+'</td></tr> ';
					
	
			
				 tab3 = '<tr>  <td class = "blueColor" style = "font-weight:600;">Final Prayer</td> ' + 
					'<td colspan = "4">'+item.finalPrayer+'</td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Last Hearing</td> ' + 
					'<td><strong>'+lastdate+'</strong></td>  <td class = "blueColor" style = "font-weight:600;">Outcome Of Last Hearing</td> ' + 
					'<td><strong> '+item.outcomeLast+'</strong></td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Next Hearing</td> ' + 
					'<td><strong>'+nextdate+' </strong> </td>  <td class = "blueColor" style = "font-weight:600;">Likely Outcome Of Next Hearing</td> ' + 
					'<td><strong> '+item.outcomeNext+'</strong></td> </tr>' +amountAllow + bussLegal + casePri + loc +'</tbody> </table> </div> </div></div></div>';
				 
				 tab += tab1+tab2+tab3;
			 
		 });
				 
				 var final =  tableHeader + tbody + tableFooter + criteria +tab;
				 $('#down').append(final);
				 
				 var html = $("#print-header").html()  + '   <script src="./app/lib/paged.polyfill.js"></script>  ' + $('#print-center').html() +
                 $("#pdfDownload").html() + ' ' +
                 $("#print-footer").html()
				
				
			
			   var w = window.open();
			   w.document.write(
					   html
			                   );
			   w.document.close();
			   w.focus();
			   
			  
			 
			 });	

		 }
		 
		 function resetFilter(){
			 $('.mulSel').multiselect('deselectAll', false);
			 $('.mulSel').multiselect('refresh');
			 $('#startDate').val('')
			  $('#forumCategory').val('')
			 $('#endDate').val('')
			  $('#caseID').val('')
			  $('#caseNo').val('')
			  $('#customCheck').prop('checked', false);
			  $('#judgePronCheck').prop('checked', false);
			  	vm.form.startDate = '';
			 	vm.form.endDate = '';
			 	vm.form.startDateLast = '';
			 	vm.form.endDateLast = '';
				vm.form.caseID = '';
				vm.form.caseNo = '';
				localStorageService.set('searchCaseDetails', {});
			  
			  
		 }
		 
		 function printTable(){
		
			 var title = '<img  style = "width: 200px;height: auto;padding: 16px 15px 0px 10px;" src = "app/img/logo.png" /><h5 style = "display: inline-block;padding-left: 50px;color : #005199 !important;font-weight:600;">' + $('#textTitle').text() + '</h5><hr>';
			   var w = window.open();
			   w.document.write(
			                   $("#print-header").html()  + title +
			                   $("#viewAllMatter").html() +
			                   $("#print-footer").html() 
			                   );
			   w.document.close();
			   w.focus();
		 }
		 
		 
		 
		 function search(form){
			
					var comp = '';
					var busi = '';
					var vert = '';
					var caseTy = '';
					var forum = '';
					var caseStatus = '';
					var state = '';
					var nextDateCheck = $('#customCheck').is(':checked');
					var judgePronCheck = $('#judgePronCheck').is(':checked');
					
					if(vm.companyInfo.length == $('#company').val().length)
						comp = '0';
					else
						comp = $('#company').val().toString();
					
					if(vm.businessInfo.length == $('#business').val().length)
						busi = '0';
					else
						busi = $('#business').val().toString();
					
					if(vm.verticalInfo.length == $('#vertical').val().length)
						vert = '0';
					else
						vert = $('#vertical').val().toString();
					
				
					if(vm.caseTypeInfo.length == $('#caseType').val().length)
						caseTy = '0';
					else
						caseTy = $('#caseType').val().toString();
					
// if(vm.forumInfo.length == $('#forum').val().length)
// forum = '100000000';
// else
						forum = $('#forum').val().toString();
					
					
					if(vm.caseStatusInfo.length == $('#caseStatus').val().length)
						caseStatus = '0';
					else
						caseStatus = $('#caseStatus').val().toString();
					

					if(vm.stateInfo.length == $('#state').val().length)
						state = '0';
					else
						state = $('#state').val().toString();
					
					if(nextDateCheck){
				 		vm.form.startDateNext = '';
				 		vm.form.endDateNext = '';
				 		
				 	}
				
					var stDate = vm.form.startDate;
					var endDate = vm.form.endDate;
					var caseID = vm.form.caseID;
					var caseNo = vm.form.caseNo;
					var stDateLast = vm.form.startDateLast;
					var endDateLast = vm.form.endDateLast;
					
					if((comp == "") && (busi == "0") && (vert == "0") && (caseTy == "") && (forum == "") && (caseStatus == "") && (state == "") && (stDate == "") && (endDate == "") 
							&& (caseID == "") && (caseNo == "") && (nextDateCheck == false) && (judgePronCheck == false) && (stDateLast == '') && (endDateLast == '') && (vm.form.forumCategory == '')){
						alert("Please select atleast one filter...");
						return false;
					}
					
					if((comp == "")  && (nextDateCheck == true)){
						alert("Company must be selected to fetch records...");
						return false;
					}
					var jsonReq = {"company" : comp.toString(), "business" : busi.toString(), "vertical" : vert.toString(), 
							"caseType" : caseTy, "userID" : localStorageService.get('payrollNo'), "forum" : forum, "caseStatus" : caseStatus, "state" : state, "forumCategory" : vm.form.forumCategory.toString(),
							"startDate" : stDate, "endDate" : endDate, "caseID" : (caseID == '') ? '' : caseID, "caseNo" : caseNo, "startDateLast" : stDateLast
									, "endDateLast" : endDateLast, "nextDateNA" : (nextDateCheck == true) ? 'Y' : 'N', "judgePronCheck" : (judgePronCheck == false) ? 'N' : 'Y' , "sessID" : localStorageService.get('sessionID')}
					localStorageService.set('searchCaseDetails', jsonReq);
					// alert(JSON.stringify(jsonReq));
					 SearchCaseService.viewAllCases(jsonReq)
			    		.then(function(result) {
			    			if(result.data.status == 'F'){
								alert(result.data.msg);
								return false;
							}
				   			 vm.listOfCases = result.data.listOfCases;	
				   			if(vm.listOfCases.length > 0) {
								_.each(vm.listOfCases, function(item, key){
									if(item.nextHearing != '' && item.nextHearing != undefined)
										item.nextHearingDate = new Date(item.nextHearing);
										
									if(item.lastHearing != '' && item.lastHearing != undefined)
										item.lastHearingDate = new Date(item.lastHearing);
// $('html, body').animate({
// scrollTop : $('#searchCaseTable').offset().top
// }, 2000);
									
								});
				    		}

				   			 $('#searchCaseTable').css('display','block');
				   			 

						}, function caseError(error){
					      alert('There seems some problem. Please try again later...');
					      return false;
					    });
				
		 }
		 

		 
		 function fetchAllMaster(){
			 var jsonReq = {"userID": localStorageService.get('payrollNo'), "type" : "fetchMasterForSearch"
				 ,
					"sessID" : localStorageService.get('sessionID')}
				addCaseService.fetchAllMasters(jsonReq)
				.then(function(result) {

					if(result.data.status == 'S'){
						alert(result.data.msg);
						
							$window.localStorage.clear();
							localStorageService.clearAll;
							localStorageService.cookie.clearAll;
							$state.go('login');
						} 
		 			vm.companyInfo =  result.data.companyMaster;		 			
		 			vm.caseTypeInfo = result.data.caseTypeMaster;
		 			vm.forumCategoryInfo = result.data.forumCategoryMaster;
		 						
		 			vm.caseStatusInfo = result.data.statusMaster;
		 			vm.stateInfo = result.data.stateArray;
		 			$(document).ready(function(){
		 				$('#company').multiselect({
		 					 templates: {
			 			          
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true, 		    		 	
		 		    		 	maxHeight : 250,
				 	        	includeSelectAllOption : true,
				 	        	numberDisplayed: 2,				 	        	
				 	        	nonSelectedText: 'Select Company',
				 	        	selectAllValue: 0,
				 	        	selectAllName: 'company-select',
				 	        	onChange : function(element, checked){
				 	        		
				 	        		$('ul.dropdown-menu').removeClass('show');
				 	        		var selComp = $('#company').val();
				 	        		if(selComp.length == 0)
				 	        			return false;
				 	        		vm.verticalInfo = [];
				 	        		$("#vertical").empty();
				 	        		$("#vertical").multiselect("rebuild");
				 	        		
				 	        		vm.businessInfo = [];
				 	        		// $("#business").val([]);
				 	        		$('#business').empty();
				 	        		$("#business").multiselect("refresh");
				 	        		
				 	        		var jsonReq = {"type" : "business", "id" : selComp.toString(), "userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID')}
				 	        		AddUserService.fetchBusiVeriView(jsonReq)
				 	        		.then(function(result) {
				 	        			if(result.data.status == 'F'){
				 	   					alert(result.data.msg);
				 	   					return false;
				 	   				}	
						 	       	vm.businessInfo = result.data.resultArray;
						 	       	 for (var i = 0; i < vm.businessInfo.length; i++) {
					                     $('#business').append('<option value="'+vm.businessInfo[i].id+'">'+vm.businessInfo[i].name+'</option>'); 
					                 }
						 	       	 $("#business").multiselect("rebuild");
						 	   		
						 	   		}, function caseError(error){
						 	   		      console.log('There seems some problem. Please try again later...');
						 	   		      return false;
						 	   		});
				 	        		
				 	        	},
				 	        	onSelectAll : function(){
				 	        		vm.verticalInfo = [];
				 	        		$('ul.dropdown-menu').removeClass('show');
				 	        		var selComp = $('#company').val();
				 	        		if(selComp.length == 0){
				 	        			vm.verticalInfo = [];
				 	        			$('#vertical').empty();
					 	        		$("#vertical").multiselect("rebuild");
					 	        		vm.businessInfo = [];
					 	        		$('#business').empty();
					 	        		$("#business").multiselect("refresh");
					 	        		return false;
					 	        		
				 	        		}
				 	        		vm.verticalInfo = [];
			 	        			$('#vertical').empty();
				 	        		$("#vertical").multiselect("rebuild");
				 	        		vm.businessInfo = [];
				 	        		$('#business').empty();
				 	        		$("#business").multiselect("refresh");
				 	        		
				 	        		var jsonReq = {"type" : "business", "id" : selComp.toString(), "userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID')}
				 	        		AddUserService.fetchBusiVeriView(jsonReq)
				 	        		.then(function(result) {
				 	        			if(result.data.status == 'F'){
				 	   					alert(result.data.msg);
				 	   					return false;
				 	   				}
				 	        			vm.businessInfo = result.data.resultArray;
							 	       	 for (var i = 0; i < vm.businessInfo.length; i++) {
						                     $('#business').append('<option value="'+vm.businessInfo[i].id+'">'+vm.businessInfo[i].name+'</option>'); 
						                 }
							 	       	 $("#business").multiselect("rebuild");
						 	   		
						 	   		}, function caseError(error){
						 	   		      console.log('There seems some problem. Please try again later...');
						 	   		      return false;
						 	   		});
				 	        	}
				 	        });
		 				
		 				$('#business').multiselect({
			 	        	
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,	 		    		 	
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select Business',
			 	        	selectAllValue: 0,
			 	        	selectAllName: 'business-select',
			 	        	onChange : function(element, checked){
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        		var selComp = $('#business').val();
			 	        		if(selComp.length == 0)
			 	        			return false;
			 	        		vm.verticalInfo = [];
			 	        		$('#vertical').empty();
			 	        		$("#vertical").multiselect("refresh");
			 	        		var jsonReq = {"type" : "vertical", "id" : selComp.toString(), "userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID')}
			 	        		AddUserService.fetchBusiVeriView(jsonReq)
			 	        		.then(function(result) {
			 	        			if(result.data.status == 'F'){
			 	   					alert(result.data.msg);
			 	   					return false;
			 	   				}
					 	       		vm.verticalInfo = result.data.resultArray;
					 	       	 for (var i = 0; i < vm.verticalInfo.length; i++) {
				                     $('#vertical').append('<option value="'+vm.verticalInfo[i].id+'">'+vm.verticalInfo[i].fullname+'</option>'); 
				                 }
					 	       		$("#vertical").multiselect("rebuild");
					 	   		
					 	   		}, function caseError(error){
					 	   		      console.log('There seems some problem. Please try again later...');
					 	   		      return false;
					 	   		});
			 	        		
			 	        	},
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        		var selComp = $('#business').val();
			 	        		if(selComp.length == 0)
			 	        			{
			 	        			$('#vertical').empty();
				 	        		$("#vertical").multiselect("refresh");
				 	        		return false;
			 	        			}
			 	        			
			 	        		
			 	        		$('#vertical').empty();
			 	        		$("#vertical").multiselect("refresh");
			 	        		var jsonReq = {"type" : "vertical", "id" : selComp.toString(), "userID" : localStorageService.get('payrollNo'), "sessID" : localStorageService.get('sessionID')}
			 	        		AddUserService.fetchBusiVeriView(jsonReq)
			 	        		.then(function(result) {
			 	        			if(result.data.status == 'F'){
			 	   					alert(result.data.msg);
			 	   					return false;
			 	   				}
					 	       		vm.verticalInfo = result.data.resultArray;
					 	       	for (var i = 0; i < vm.verticalInfo.length; i++) {
				                     $('#vertical').append('<option value="'+vm.verticalInfo[i].id+'">'+vm.verticalInfo[i].fullname+'</option>'); 
				                 }
					 	       		$("#vertical").multiselect("rebuild");
					 	   		
					 	   		}, function caseError(error){
					 	   		      console.log('There seems some problem. Please try again later...');
					 	   		      return false;
					 	   		});
			 	        	}
			 	        });
		 				
		 				$('#vertical').multiselect({
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select Vertical',
			 	        	selectAllValue: 0,
			 	        	selectAllName: 'vertical-select',
			 	        	onChange : function(element, checked){
			 	        		// $('ul.dropdown-menu').removeClass('show');
			 	        	},
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });
		 				
		 				$('#caseType').multiselect({
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,		    		 	
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 2,				 	        	
			 	        	nonSelectedText: 'Select Case Category',
			 	        	selectAllValue: 0,
			 	        	selectAllName: 'caseType-select',
			 	        	onChange : function(element, checked){
			 	        		// $('ul.dropdown-menu').removeClass('show');
			 	        	},
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });

		 				$('.dropdown-menu').click(function(event){
		 				     event.stopPropagation();
		 				 });
		 				$('#forum').multiselect({
		 					 templates: {
		 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
		 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
		 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
		 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
		 			        },
		 			        buttonContainer: '<div class="dropdown" />',
		 			        buttonClass: 'btn btn-white',
		 			        enableFiltering: true,
		 			        enableCaseInsensitiveFiltering: true,
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select Forum',
			 	        	selectAllValue: 100000000,
			 	        	selectAllName: 'forum-select',
			 	        	onChange : function(element, checked){
			 	        		// $('ul.dropdown-menu').removeClass('show');
			 	        	}
		 			        ,
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });
		 				
		 				$('#state').multiselect({
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select State',
			 	        	selectAllValue: 0,
			 	        	selectAllName: 'caseType-select',
			 	        	onChange : function(element, checked){
			 	        		// $('ul.dropdown-menu').removeClass('show');
			 	        	},
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });
		 				
		 				
		 				
		 				$('#forumCategory').multiselect({
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			       enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,
	 		    		 	maxHeight : 250,			 	        
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select Forum Category',
			 	        	
			 	        	onChange : function(element, checked){
			 	        		 $('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });
		 				
		 				$('#caseStatus').multiselect({
		 					 templates: {
			 			           // li: '<li><a class="dropdown-item"><label
									// class="m-0 pl-2 pr-0"></label></a></li>',
			 			            ul: ' <ul class="multiselect-container dropdown-menu p-1 m-0"></ul>',
			 			            button: '<button type="button" class="multiselect dropdown-toggle" data-toggle="dropdown" data-flip="false"><span class="multiselect-selected-text"></span> <b class="caret"></b></button>',
			 			            filter: '<li class="multiselect-item filter"><div class="input-group m-0"><input class="form-control multiselect-search" type="text"></div></li>',
			 			            filterClearBtn: '<span class="input-group-btn"><button type="button" class="btn btn-primary multiselect-clear-filter">&times;</button></span>'
			 			        },
			 			        buttonContainer: '<div class="dropdown" />',
			 			        buttonClass: 'btn btn-white',
			 			        enableFiltering: true,
			 			        enableCaseInsensitiveFiltering: true,
	 		    		 	maxHeight : 250,
			 	        	includeSelectAllOption : true,
			 	        	numberDisplayed: 1,				 	        	
			 	        	nonSelectedText: 'Select Status',
			 	        	selectAllValue: 0,
			 	        	selectAllName: 'caseType-select',
			 	        	onChange : function(element, checked){
			 	        		// $('ul.dropdown-menu').removeClass('show');
			 	        	},
			 	        	onSelectAll : function(){	
			 	        		$('ul.dropdown-menu').removeClass('show');
			 	        	}
			 	        	
			 	        });

		 			
		 			
		 			
	   			if(localStorageService.get('searchCaseDetails') != null && localStorageService.get('searchCaseDetails') != '' && localStorageService.get('searchCaseDetails') != undefined){
	   				var jsonReq = localStorageService.get('searchCaseDetails');
	
				 SearchCaseService.viewAllCases(jsonReq)
		    		.then(function(result) {
		    			if(result.data.status == 'F'){
							alert(result.data.msg);
							return false;
						}
		    			 vm.listOfCases = result.data.listOfCases;
		    			if(jsonReq.company != '0' && jsonReq.company != ''){
		   					var temp1 = jsonReq.company.split(',');
		   					$("#company").multiselect('select', temp1);	   					
		   					$('ul.inner').addClass('show');
		   				}else if(jsonReq.company != '')
		   					$("#company").multiselect('selectAll', false);
		   				
		   				if(jsonReq.business != '0' && jsonReq.business != ''){		   					
		   					var temp2 = jsonReq.business.split(',');	
		   					var resp = fetchCompanyMaster(temp1.toString(), temp2.split(','));
		   						   					
		   					
		   					
		   					$('ul.inner').addClass('show');
		   				}else if(jsonReq.business != '')
		   					$("#business").multiselect('selectAll', false);
		   				
		   				if(jsonReq.vertical != '0'  && jsonReq.vertical != ''){
		   					var temp3 = jsonReq.vertical.split(',');
		   					var resp1 = fetchVerticalMaster(temp2.toString(), temp3.split(',')); 
		   					$("#vertical").multiselect('select', temp3);
		   					 					
		   				}else if(jsonReq.vertical != '')
		   					$("#vertical").multiselect('selectAll', false);
		   				
		   				
		   				if(jsonReq.forum != '0' && jsonReq.forum != ''){
		   					var temp4 = jsonReq.forum.split(',');
		   					$("#forum").multiselect('select', temp4);
		   					 					
		   				}else if(jsonReq.forum != '')
		   					$("#forum").multiselect('selectAll', false);
		   				
		   				// $('ul.inner').addClass('show');
		   				if(jsonReq.caseType != '0' && jsonReq.caseType != ''){
		   					var temp5 = jsonReq.caseType.split(',');
		   					$("#caseType").multiselect('select', temp5);
		   					 					
		   				}else if(jsonReq.caseType != '')
		   					$("#caseType").multiselect('selectAll', false);
		   				
		   				if(jsonReq.forumCategory != '0' && jsonReq.forumCategory != ''){
		   					var temp99 = jsonReq.forumCategory.split(',');
		   					$("#forumCategory").multiselect('select', temp99);
		   					 					
		   				}
		   				
		   				
		   				if(jsonReq.caseStatus != '0' && jsonReq.caseStatus != ''){
		   					var temp6 = jsonReq.caseStatus.split(',');
		   					$("#caseStatus").multiselect('select', temp6);
		   					 					
		   				}else if(jsonReq.caseStatus != '')
		   					$("#caseStatus").multiselect('selectAll', false);
		   				
		   				
		   				if(jsonReq.state != '0' && jsonReq.state != ''){
		   					var temp7 = jsonReq.state.split(',');
		   					$("#state").multiselect('select', temp7);
		   					 					
		   				}else if(jsonReq.state != '')
		   					$("#state").multiselect('selectAll', false);
		   				
		   				
// if(jsonReq.forumCategory != undefined && jsonReq.forumCategory != ''){
// fetchForumInfo(jsonReq.forumCategory, jsonReq.forum.split(','));
// vm.form.forumCategory = jsonReq.forumCategory;
// }
		   				
		   				$("#company").multiselect('refresh')
		   				$("#business").multiselect('refresh')
		   				$("#vertical").multiselect('refresh')
		   				$("#forum").multiselect('refresh')
		   				$("#caseType").multiselect('refresh')
		   				$("#caseStatus").multiselect('refresh')
		   				$("#state").multiselect('refresh')
		   				$("#forumCategory").multiselect('refresh')
		   						   				
		   				vm.form.startDate =  jsonReq.startDate;
		   				vm.form.endDate = jsonReq.endDate;
		   				vm.form.caseID = jsonReq.caseID;
		   				vm.form.caseNo = jsonReq.caseNo;
		   				
		   			    vm.listOfCases = result.data.listOfCases;
// $('html, body').animate({
// scrollTop : $('#searchCaseTable').offset().top
// }, 2000);
			   							   			

			   			 $('#searchCaseTable').css('display','block');
			   			
					}, function caseError(error){
				      alert('There seems some problem. Please try again later...');
				      return false;
				    });
   			}

				}, function caseError(error){
			      alert('There seems some problem. Please try again later...');
			      return false;
			    });
				});
			}
		 
		 
		 function fetchCompanyMaster(val, temp2){
			 var jsonReq = {"userID": localStorageService.get('payrollNo'), "compID" : val.toString(), "type" : "fetchBusiness", 
						"sessID" : localStorageService.get('sessionID')}
				addCaseService.fetchAllMasters(jsonReq)
				.then(function(result) {   
					if(result.data.status == 'F'){
						alert(result.data.msg);
						return false;
					}
	    			vm.businessInfo = result.data.businessMaster;    			
	    			 for (var i = 0; i < vm.businessInfo.length; i++) {
	                     $('#business').append('<option value="'+vm.businessInfo[i].id+'">'+vm.businessInfo[i].name+'</option>'); 
	                 }
	    			
	    			 $("#business").multiselect("rebuild");
	    			 $("#business").multiselect('select', temp2);	
				}, function caseError(error){
			      alert('There seems some problem. Please try again later...');
			      return false;
			    });
			 
			 return true;
				

			}
			
			
			function fetchVerticalMaster(item, temp3){
				 var jsonReq = {"userID": localStorageService.get('payrollNo'), "compID" : item.toString(), "type" : "fetchVertical",

							"sessID" : localStorageService.get('sessionID')}
					addCaseService.fetchAllMasters(jsonReq)
					.then(function(result) {  
						if(result.data.status == 'F'){
							alert(result.data.msg);
							return false;
						}
			vm.selBusiness = item;
    			
	    			vm.verticalInfo = result.data.verticalMaster;
	    			
	    			
					 	       	for (var i = 0; i < vm.verticalInfo.length; i++) {
				                     $('#vertical').append('<option value="'+vm.verticalInfo[i].id+'">'+vm.verticalInfo[i].fullname+'</option>'); 
				                 }
					 	       $("#vertical").multiselect("rebuild");
					 	      $("#vertical").multiselect('select', temp3);	
				}, function caseError(error){
			      alert('There seems some problem. Please try again later...');
			      return false;
			    });
				return true;
			}
		
		 function disableCaseItem(item){
			 if(confirm('Are you sure to disable the Case - '+item.caseID)){
				 var jsonReq = {"type" : "delete", "caseID" : item.caseID , "sessID" : localStorageService.get('sessionID'), "userID" : localStorageService.get('payrollNo')
							};
				 SearchCaseService.disableCase(jsonReq)
			 		.then(function(result) {	    			
			 			if(result.data.status == 'T' ) {					
							alert('Case ID '+ item.caseID +' Disabled successfully');
							 var jsonReq1 = localStorageService.get('searchCaseDetails');
								
							 SearchCaseService.viewAllCases(jsonReq1)
					    		.then(function(result) {
					    			 vm.listOfCases = result.data.listOfCases;	    			
			                           
								}, function caseError(error){
							      alert('There seems some problem. Please try again later...');
							      return false;
			                    });
			                
													
						}else {
							alert(result.data.msg);							
							return false;
						}
				 			
		  
				 	}, function userError(error){
					      alert('There seems some problem. Please try again later...');
					      return false;
					 });
			 }else{
					console.log('false');
				}
		 }

		 
		 
		 function viewCaseItem(item){
			
			 SearchCaseService.viewCase(vm.listOfCases[item].caseID)
	    		.then(function(result) {    			
	    			if(result.data.status == 'F'){
						alert(result.data.msg);
						return false;
					}
	    			 vm.caseDetails = result.data;
	    			 localStorageService.set('viewCaseDetails' , vm.caseDetails )
	    			 $state.goToNewTab('viewcase', {});

			 }, function caseError(error){
			      alert('There seems some problem. Please try again later...');
			      return false;
			 });
			
			 
			
		 }
		 
		 function editCaseItem(item){
			
			 SearchCaseService.viewCase(item.caseID)
	    		.then(function(result) {	
	    			if(result.data.status == 'F'){
						alert(result.data.msg);
						return false;
					}
	    			 SearchCaseService.setViewCaseDetails(result.data);
	    			 $state.goToNewTab('editcase', {});
				}, function caseError(error){
			      alert('There seems some problem. Please try again later...');
			      return false;
			    });
			
		 }
		 

		 function updateHearingItem(val){                 
             localStorageService.set('updatehearing', val);
             $state.goToNewTab('updatehearing', {});			 	
         }
          
		 function addDocItem(val){
			 localStorageService.set('addAttachment', val);
             $state.goToNewTab('managedocs', {});
		 }
		
		 function generatePDFWithPageNo2(){
			 var sampleJSON = [];
			 var title = ''
			 var sorted = [];
			 var criteria = '';
			 var selection = localStorageService.get('searchCaseDetails');
		

			 var selected =[]
			 criteria = '<br><div style = "border:1px solid #ccc;"><h3 style = "text-decoration:underline;text-align:center;font-size:13px;">Report Search Criteria</h3><br>';
				 
			 
			
			 if(selection.company == '')
				 selected.push({'title' : 'Company', 'value':'All'});
			 else if(selection.company == '0')
					 selected.push({'title' : 'Company', 'value':'All'});
			 else if(selection.company.indexOf(',') > -1){ 
				 var mulValues = []
				 var toArr = selection.company.split(',');
				 _.find(toArr, function(num){
					 var temp =  _.where(vm.companyInfo, {id:  parseInt(num)})
					 mulValues.push(temp[0].name);
				 })
						 selected.push({'title' : 'Company', 'value':mulValues.toString()})
			 }
					 else  {
						 var val = _.where(vm.companyInfo, {id: parseInt(selection.company)});
						 
						 selected.push({'title' : 'Company', 'value':val[0].name})
					 }
			 
			 if(selection.business == '')
				 selected.push({'title' : 'Business', 'value':'All'})
			else if(selection.business == '0')
				 selected.push({'title' : 'Business', 'value':'All'})
					
			 	 
			 else if(selection.business.indexOf(',') > -1) 
				
				 selected.push({'title' : 'Business', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.businessInfo, {id: parseInt(selection.business)});
						
						
						 selected.push({'title' : 'Business', 'value':val[0].name})
					 }
			 
			 if(selection.vertical == '' || (selection.vertical == '0' && selection.business == ''))
				 criteria += '';
			else if(selection.vertical == '0')
				 selected.push({'title' : 'Vertical', 'value':'All'})	 	 
			 else if(selection.vertical.indexOf(',') > -1) 
				 selected.push({'title' : 'Vertical', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.verticalInfo, {id: parseInt(selection.vertical)});
						
					
						 selected.push({'title' : 'Vertical', 'value':val[0].fullname})
					 }
			
			 if(selection.caseType == '')
				 selected.push({'title' : 'Case Category', 'value':'All'})	
			else if(selection.caseType == '0')
				selected.push({'title' : 'Case Category', 'value':'All'})	
			 else if(selection.caseType.indexOf(',') > -1) 
				 selected.push({'title' : 'Case Category', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.caseTypeInfo, {id: parseInt(selection.caseType)});
						
						
						 selected.push({'title' : 'Case Category', 'value':val[0].name})
					 }
			 
			 if(selection.forumCategory != ''){
				 selected.push({'title' : 'Forum Category', 'value':toTitleCase(selection.forumCategory)}) 
			      
			 }
			 
			 if(selection.forum == '')
				 criteria += '';
			else if(selection.forum == '100000000')
				selected.push({'title' : 'Forum', 'value':'All'})	
			 else if(selection.forum.indexOf(',') > -1) 
				 selected.push({'title' : 'Forum', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.forumInfo, {id: parseInt(selection.forum)});
						
						 selected.push({'title' : 'Forum', 'value':val[0].name})
					 }
			 
			 if(selection.caseStatus == '')
				 criteria += '';
			else if(selection.caseStatus == '0')
				selected.push({'title' : 'Case Status', 'value':'All'})	
				 
			 else if(selection.caseStatus.indexOf(',') > -1) 
				 selected.push({'title' : 'Case Status', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.caseStatusInfo, {id: parseInt(selection.caseStatus)});
						
						 selected.push({'title' : 'Case Status', 'value':val[0].name})
					 }
			 
			 if(selection.state == '')
				 criteria += '';
			else if(selection.state == '0')
				selected.push({'title' : 'State', 'value':'All'})	
			 else if(selection.state.indexOf(',') > -1) 
				 selected.push({'title' : 'State', 'value':'Multiple Selection'})
					 else  {
						 var val = _.where(vm.stateInfo, {id: parseInt(selection.state)});
						
						 selected.push({'title' : 'State', 'value':val[0].name})
					 }
			 
			 if(selection.startDate != ''){
				 selected.push({'title' : 'Next Hearing - Start Date', 'value':selection.startDate}) 
		      }
			 
			 if(selection.endDate != ''){
				 selected.push({'title' : 'Next Hearing - End Date', 'value':selection.endDate}) 
			      
			 }
 if(selection.caseID != ''){
	 selected.push({'title' : 'Case ID', 'value':selection.caseID}) 
     		 
			 }
 if(selection.caseNo != ''){
	 selected.push({'title' : 'Case No', 'value':selection.caseNo}) 
      
 }
 if(selection.startDateLast != ''){
	 selected.push({'title' : 'Last Hearing - Start Date', 'value':selection.startDateLast}) 
      
 }
 if(selection.endDateLast != ''){
	 selected.push({'title' : 'Last Hearing - End Date', 'value':selection.endDateLast}) 
      
 }
 if(selection.judgePronCheck != ''){
	 selected.push({'title' : 'Judgement Pronounced Included', 'value':selection.judgePronCheck}) 
      
 }
 
 
	console.log(selected)		
	
	for(var p=0; p<selected.length; p += 2){
		if(selected.length % 2 == 0)
		 criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p+1].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p+1].value+'</div></div>';
		else if(p == selected.length-1)
			criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div></div><div class = "col-sm-3"></div><div class = "col-sm-3"></div>';
		else
			 criteria += '<div class = "row"><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p].value+'</div><div class = "col-sm-3" style = "text-align:right;word-break:break-word;"><strong>'+selected[p+1].title+' :</strong></div><div class = "col-sm-3" style = "text-align:left;">'+selected[p+1].value+'</div></div>';
			
		
	}
			 
			 
			 criteria += '<br></div>';
			 
			 DTInstances.getList().then(function(dtInstances) {
			      sampleJSON.push(dtInstances.viewTable.DataTable.context[0].aiDisplay)
			     
			      sorted = _.sortBy(vm.listOfCases, function(obj){
			    	
			    	    return _.indexOf(_.toArray(sampleJSON[0]), obj.id);
			    	});
			    	
			
			 

			 $('#down').empty();
			 var tab = '';
			 var tbody = '';
			var today =  $filter('date')(vm.today, "MMM dd, yyyy");
			var todayWithFormat =  $filter('date')(vm.today, "dd/MM/yyyy HH:mm");
			var tableHeader = '<div class = "cabecalho" style = "display:none;">Report as on '+todayWithFormat+'</div><table id="viewTablePDF" class="table table-bordered tableCls noPadd viewTablePDF" >  <thead> <tr> <th style = "width: 55%;">Case ID</th>  <th>Vertical</th> <th>Forum</th> <th style = "width: 55%;">Year</th> <th style = "width: 125%;">Petitioner</th><th style = "width: 125%;">Respondent</th> <th style = "width: 90%;">Next Hearing</th> <th>Status</th> <th style = "width: 145%;">Subject Matter</th>  <th style = "width: 55%;">Page</th> </tr> </thead> <tbody class = "tableRowVal">';
			var tableFooter = '</tbody>  </table><br>';
				 _.each(sorted, function(item, key){
				 var tab1 = '';
				 var tab2 = '';
				 var tab3 = '';
				 var lastdate = (item.lastHearingDate == undefined) ? '' : $filter('date')(item.lastHearingDate, "dd/MM/yyyy");
				 var nextdate = (item.nextHearingDate == undefined) ? '' : $filter('date')(item.nextHearingDate, "dd/MM/yyyy");
				 var caseCategoryName = (item.caseCategoryName == undefined) ? '' : item.caseCategoryName;
				 var state = (item.state == undefined) ? '' : item.state;
				 var statusName = (item.statusName == undefined) ? '' : item.statusName;
				 var assessYear = (item.assessYear == undefined) ? '' : item.assessYear;
				 var amountAllow = '';
				 var titleDirect = '';
				 var bussLegal = '';
				 var casePri = '';
				 var loc = '';
				 
				 var subMat = (item.subMatter.length > 50 ) ? item.subMatter.substring(0,50) + '...' : item.subMatter;
				// var pet = (item.petitioners.length > 50 ) ?
				// item.petitioners.substring(0,50) + '...' : item.petitioners;
				// var resp = (item.respondents.length > 50 ) ?
				// item.respondents.substring(0,50) + '...' : item.respondents;
				 var sp = item.petitioners.split('P2.');				
				 var pet = sp[0].substring(3, sp[0].length) + ((item.petitioners.indexOf('P2.') > -1) ? ' and Ors,.' : '');
				 var rsp = item.respondents.split('R2.');				
				 var resp = rsp[0].substring(3, rsp[0].length)  + ((item.respondents.indexOf('R2.') > -1) ? ' and Ors,.' : '');
				
				// var pet = (item.petitioners.contains('P2.')) ?
				// item.petitioners.substring(0,50) + '...' : item.petitioners;
				// var resp = (item.respondents.length > 50 ) ?
				// item.respondents.substring(0,50) + '...' : item.respondents;
				 
				 
				 tbody += '<tr id = "summary_'+key+'"><td style = "width: 55%;">'+item.caseID+'</td><td>'+item.vertical+'</td><td>'+ (item.forum != undefined ? item.forum : '')  +'</td><td style = "width: 55%;">'+ (item.caseYear != 0 ? item.caseYear : '')   +'</td><td style = "width: 125%;">'+pet+'</td><td style = "width: 125%;">'+resp+'</td><td style = "width: 90%;">'+nextdate+'</td><td>'+statusName+'</td><td style = "width: 145%;">'+subMat+'</td><td style = "width: 55%;"><a class = "pageref" href="#viewPage_'+key+'"></a></td></tr>';
				 if(item.caseTypeID != 1){
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td colspan = "4"> <b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact) + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td></tr>';
					 bussLegal = '<tr> <td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> ';
						
				 }else {
					 amountAllow = '<tr><td class = "blueColor" style = "font-weight:600;">Financial Impact </td><td> <b>One time :</b>'+ (item.finImpact == '' ? 'Nil' : item.finImpact) + ', <b>Recurring : </b>' + (item.finImpRecurring == undefined ? 'Nil' : item.finImpRecurring) + ', <b>Duration :</b>' + (item.finImaRecurringDuration == undefined ? 'NA' : item.finImaRecurringDuration) + '</td><td class = "blueColor" style = "font-weight:600;"> Amount of Disallowance </td><td > '+item.amtOfDisallow + '</td></tr>';   				
					 bussLegal = '<tr> <td class = "blueColor" style = "font-weight:600;">Business Team </td><td>'+item.businessRep+'</td><td class = "blueColor" style = "font-weight:600;">Legal Team </td><td>'+item.legalRep+'</td></tr>';
					 casePri = '<tr><td class = "blueColor" style = "font-weight:600;"> Case Status </td><td>'+statusName+'</td><td class = "blueColor" style = "font-weight:600;">Case Priority </td><td>'+caseCategoryName+'</td></tr> ';				
					 loc = '<tr><td class = "blueColor" style = "font-weight:600;"> Location </td><td>'+state+'</td><td class = "blueColor" style = "font-weight:600;"> Updated By </td><td>'+item.enteredBy+'</td></tr> '; 
				 }
				 
				 if(assessYear == ''){
// titleDirect = '<div class = "col-sm-6" ></div>'
					 titleDirect = '<div class = "col-sm-3 blueColor" style = "text-align: right;font-size:14px !important;visibility:hidden;"><strong>AY :</strong> NA</div>'+
					   '<div class = "col-sm-3" style = "font-size:14px !important;visibility:hidden;"> <strong class = "blueColor" style = "font-size:14px !important;margin-left:10px;">FY :</strong> <span style = "font-size:14px !important;">NA</span></div>';
					 
						 
				 }else {
					 titleDirect = '<div class = "col-sm-3 blueColor" style = "text-align: right;font-size:14px !important;"><strong>AY :</strong> '+assessYear+'</div>'+
					   '<div class = "col-sm-3" style = "font-size:14px !important;"> <strong class = "blueColor" style = "font-size:14px !important;margin-left:10px;">FY :</strong> <span style = "font-size:13px !important;">'+item.finYear+'</span></div>';
					 
				 }


				 tab1 = '<br><div id = "viewPage_'+key+'" class = "level1 detailView" ><br><div id = "detail_'+key+'" class="row text-center" style = "text-align:center;" > <div class = "col-11"><strong style = "color:#005199 !important;font-size:14px;">' +
				   'Case Details for case ID ' + 	item.caseID+ ' with Ref.no '+ item.refNo+ '</strong></div><div class = "col-1"><a class = "fas fa-arrow-up bn pageref" href="#summary_'+key+'"></a></div> </div><br><div  style = "padding-top:10px;border-top:1px solid #ccc;" id = "viewAllMatter_'+key+'">' + 
				   '<div class = "row" > <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Company :</strong></div> ' + 
				   '<div class = "col-4" style = "font-size:13px !important;">'+item.company+'</div> <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Case Category :</strong></div>'+
				   '<div class = "col-4" style = "font-size:13px !important;">'+item.caseType+'</div></div><div class = "row" ><div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"><strong>Business :</strong>'+
				   '</div><div class = "col-4" style = "font-size:13px !important;">'+item.business+'</div>'+titleDirect+'</div><div class = "row" > <div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Vertical : </strong></div><div class = "col-4" style = "font-size:13px !important;"> ' + item.vertical + '</div><div class = "col-2 blueColor" style = "text-align: right;font-size:14px !important;"> ' + 
				   '<strong>Report as on : </strong> </div><div class = "col-4" style = "font-size:13px !important;">'+ today+ '</div></div>';
				 

				 tab2 =  '<br><div class = "row"> <div class="col-sm-12"> <table class="table  viewPopupTable" id = "viewPopupTable_'+key+'"> <tbody><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Court Case No </td> <td> ' +item.caseNo + 
					'</td><td class = "blueColor" style = "font-weight:600;">Case Year</td><td> ' +(item.caseYear != 0 ? item.caseYear : '')+ 
					'</td></tr><tr> ' + 
					' <td class = "blueColor" style = "font-weight:600;"> Forum </td> <td> ' + (item.forum != undefined ? item.forum : '')  +
					'</td><td class = "blueColor" style = "font-weight:600;">Case Type</td><td> '+ (item.caseTypeName != undefined ? item.caseTypeName : '')   +
					'</td></tr><tr> <td class = "blueColor" style = "font-weight:600;">Petitioner</td> ' + 
					'<td >'+item.petitioners+' </td><td class = "blueColor" style = "font-weight:600;">Respondent</td> '+
					'<td "> '+item.respondents+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">AOR for Petitioner</td> ' + 
					'<td> '+item.aorOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">AOR for Respondent</td>  ' + 
					'<td>'+item.aorOfRespondent+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Counsel for Petitioner</td> ' + 
					'<td>'+item.counselOfCompany+'</td><td class = "blueColor" style = "font-weight:600;">Counsel for Respondent</td> ' + 
					'<td>'+item.counselOfRespondent+' </td></tr><tr><td class = "blueColor" style = "font-weight:600;">Bench</td> ' + 
					'<td colspan = "4">'+item.bench+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Subject Matter</td>  ' + 
					'<td colspan = "4">'+item.subMatter+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Brief Facts</td> ' + 
					'<td colspan = "4">'+item.briefFacts+'</td></tr><tr><td class = "blueColor" style = "font-weight:600;">Interim Prayer</td>  ' + 
					'<td colspan = "4"> '+item.interimPrayer+'</td></tr> ';
					
	
			
				 tab3 = '<tr>  <td class = "blueColor" style = "font-weight:600;">Final Prayer</td> ' + 
					'<td colspan = "4">'+item.finalPrayer+'</td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Last Hearing</td> ' + 
					'<td><strong>'+lastdate+'</strong></td>  <td class = "blueColor" style = "font-weight:600;">Outcome Of Last Hearing</td> ' + 
					'<td><strong> '+item.outcomeLast+'</strong></td> </tr> <tr>  <td class = "blueColor" style = "font-weight:600;">Next Hearing</td> ' + 
					'<td><strong>'+nextdate+' </strong> </td>  <td class = "blueColor" style = "font-weight:600;">Likely Outcome Of Next Hearing</td> ' + 
					'<td><strong> '+item.outcomeNext+'</strong></td> </tr>' +amountAllow + bussLegal + casePri + loc +'</tbody> </table> </div> </div></div></div>';
				 
				 tab += tab1+tab2+tab3;
			 
		 });
				 
				 var final =  tableHeader + tbody + tableFooter + criteria +tab;
				 $('#down').append(final);
				 
				 var html = $("#print-headerPg").html()  + '   <script src="./app/lib/paged.polyfill.js"></script>  ' + $('#print-centerPg').html() +
                 $("#pdfDownload").html() + ' ' +
                 $("#print-footerPg").html()
				
				
			
			   var w = window.open();
			   w.document.write(
					   html
			                   );
			   w.document.close();
			   w.focus();
			   
			  
			 
			 });	

		 }
		 
//		 var tableToExcel = (function () {
//				var uri = 'data:application/vnd.ms-excel;base64,'
//					//, template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><meta charset="utf-8"/><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>'
//			        , template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>'
//			        , base64 = function (s) { return window.btoa(unescape(encodeURIComponent(s))) }
//			        , format = function (s, c) { return s.replace(/{(\w+)}/g, function (m, p) { return c[p]; }) }
//			        return function (table, name, filename) {
////			            if (!table.nodeType) 
////			            	table = document.getElementById(table)
//			        	//alert($(table).html() )
//			            var ctx = { worksheet: name || '', table: $(table).html() }
//
//			            document.getElementById("dlink").href = uri + base64(format(template, ctx));
//			            document.getElementById("dlink").download = filename;
//			            document.getElementById("dlink").click();
//
//			        }
//			})()

		
	}
	
})();
function toTitleCase(str) {
    return str.replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
    });
}
var tableToExcel = (function () {
	var uri = 'data:application/vnd.ms-excel;base64,'
		, template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><meta charset="utf-8"/><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>'
        //, template = '<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:x="urn:schemas-microsoft-com:office:excel" xmlns="http://www.w3.org/TR/REC-html40"><head><!--[if gte mso 9]><xml><x:ExcelWorkbook><x:ExcelWorksheets><x:ExcelWorksheet><x:Name>{worksheet}</x:Name><x:WorksheetOptions><x:DisplayGridlines/></x:WorksheetOptions></x:ExcelWorksheet></x:ExcelWorksheets></x:ExcelWorkbook></xml><![endif]--></head><body><table>{table}</table></body></html>'
        , base64 = function (s) { return window.btoa(unescape(encodeURIComponent(s))) }
        , format = function (s, c) { return s.replace(/{(\w+)}/g, function (m, p) { return c[p]; }) }
        return function (table, name, filename) {
            if (!table.nodeType) table = document.getElementById(table)
            var ctx = { worksheet: name || '', table: table.innerHTML }

            document.getElementById("dlink").href = uri + base64(format(template, ctx));
            document.getElementById("dlink").download = filename;
            document.getElementById("dlink").click();

        }
})()

