(window.webpackJsonp=window.webpackJsonp||[]).push([["chunk-6efb"],{"1V7f":function(t,e,a){"use strict";var r=a("bPU3");a.n(r).a},FZcH:function(t,e,a){},Fzjc:function(t,e,a){"use strict";a.d(e,"d",function(){return o}),a.d(e,"c",function(){return n}),a.d(e,"e",function(){return i}),a.d(e,"a",function(){return l}),a.d(e,"b",function(){return c});var r=a("t3Un");function o(t){return Object(r.a)({url:"reportExcel/preview",method:"post",data:t})}function n(t){return Object(r.a)({url:"reportExcel/exportExcel",method:"post",data:t})}function i(){return Object(r.a)({url:"dataSet/queryAllDataSet",method:"get"})}function l(t){return Object(r.a)({url:"dataSet/detailBysetId/"+t,method:"get"})}function c(t){return Object(r.a)({url:"reportExcel/detailByReportCode/"+t,method:"get"})}},HDHf:function(t,e,a){"use strict";a.d(e,"c",function(){return o}),a.d(e,"a",function(){return n}),a.d(e,"d",function(){return i}),a.d(e,"f",function(){return l}),a.d(e,"i",function(){return c}),a.d(e,"h",function(){return s}),a.d(e,"g",function(){return u}),a.d(e,"b",function(){return d}),a.d(e,"e",function(){return h});var r=a("t3Un");function o(t){return Object(r.a)({url:"/dataSet/detailBysetId/"+t.id,method:"get"})}function n(t){return Object(r.a)({url:"/dataSet",method:"post",data:t})}function i(t){return Object(r.a)({url:"/dataSet",method:"put",data:t})}function l(t){return Object(r.a)({url:"/dataSource/queryAllDataSource",method:"get",data:t})}function c(t){return Object(r.a)({url:"/dataSetParam/verification",method:"post",data:t})}function s(t){return Object(r.a)({url:"/dataSet/testTransform",method:"post",data:t})}function u(t){return Object(r.a)({url:"/report/pageList",method:"get",params:t})}function d(t){return Object(r.a)({url:"/reportExcel",method:"post",data:t})}function h(t){return Object(r.a)({url:"/reportExcel",method:"put",data:t})}},W7E0:function(t,e,a){"use strict";var r=a("ttk2");a.n(r).a},Xvat:function(t,e,a){"use strict";a.r(e);var r=a("omC7"),o=a.n(r),n=a("cLjf"),i=a.n(n),l=a("hDQ3"),c=a.n(l),s=a("MzXy"),u=a.n(s),d=a("Fzjc"),h=a("HDHf"),f={name:"ColorPicker",model:{prop:"value",event:"input"},props:{value:{type:[String],default:""}},data:function(){return{predefineColors:["#ff4500","#ff8c00","#ffd700","#90ee90","#00ced1","#1e90ff","#c71585"],colorValue:""}},watch:{value:function(t){this.colorValue=t||""}},mounted:function(){this.colorValue=this.value},methods:{changeColor:function(t){this.colorValue=t||"",this.$emit("input",this.colorValue),this.$emit("change",this.colorValue)}}},p=(a("W7E0"),a("ZrdR")),m=Object(p.a)(f,function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("el-input",{attrs:{clearable:"",placeholder:"请输入颜色",size:"mini"},on:{change:t.changeColor},model:{value:t.colorValue,callback:function(e){t.colorValue=e},expression:"colorValue"}},[a("template",{slot:"append"},[a("el-color-picker",{attrs:{predefine:t.predefineColors,"show-alpha":"",size:"mini"},on:{change:t.changeColor},model:{value:t.colorValue,callback:function(e){t.colorValue=e},expression:"colorValue"}})],1)],2)},[],!1,null,"57fe9f5c",null);m.options.__file="colorPicker.vue";var v=m.exports,g={name:"Excels",components:{draggable:u.a,ColorPicker:v},data:function(){return{activeName:"first",activeNames:["1"],reportId:null,accessKey:null,reportCode:"",options:[],sheet:{},sheetData:[],dataSetData:[],reportExcelDto:{id:null,jsonStr:"",setCodes:"",setParam:"",reportCode:""},multipleSelection:[],rightForm:{coordinate:"",value:"",r:"",c:"",auto:!1,autoIsShow:!1},qrCodeForm:{type:"QRCode",content:"https://www.baidu.com",width:125,height:125,colorDark:"#333333",colorLight:"#ffffff",ri:0,ci:0,currentSrc:""},barCodeForm:{type:"BarCode",format:"CODE39",displayValue:!0,content:"99999999999",lineColor:"#000",background:"#fff",width:100,height:40,ri:0,ci:0,currentSrc:""},formPrintSetting:{size:"",pixel1:"",pixel2:""},pixelList:[{paper:"A4",width:210,height:297},{paper:"A3",width:297,height:420},{paper:"Letter",width:216,height:279},{paper:"Legal",width:216,height:355},{paper:"Executive",width:184,height:266}],dataSet:[],outerVisible:!1,printVisible:!1,pop:!1,setCode:null,draggableFieldLabel:null,dialogVisible:!1,dialogBarCode:!1,paperList:[],paper:"",width:"",height:"",definition:"",isBackend:"",dpi:"",pxWidth:"",pxHeight:"",show:!1,config:"",settings:{},qrCodeList:[],moveDataelse:{x:null,y:null}}},mounted:function(){},created:function(){this.reportCode=this.$route.query.reportCode,this.accessKey=this.$route.query.accessKey,this.loadDataSet(),this.design()},methods:{handleClose:function(){this.printVisible=!1},handleChange:function(t){},handleClick:function(t,e){},design:function(){var t=this;return c()(i.a.mark(function e(){var a,r;return i.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,Object(d.b)(t.reportCode);case 2:a=e.sent,a.code,null!=(r=a.data)&&(t.reportId=r.id),t.sheetData=null==r?[{}]:JSON.parse(r.jsonStr),console.log(t.sheetData),t.createSheet(),null!=r&&null!=r.setCodes&&""!==r.setCodes&&r.setCodes.split("|").forEach(function(e){t.dataSetData.forEach(function(a){e===a.setCode&&t.detail(a.id)})});case 10:case"end":return e.stop()}},e,t)}))()},createSheet:function(){var t=this,e={container:"luckysheet",title:"",lang:"zh",plugins:["chart"],hook:{cellDragStop:function(e,a,r,o,n){luckysheet.setCellValue(a.r,a.c,t.draggableFieldLabel)},cellMousedown:function(e,a,r,o){t.rightForm.coordinate=a.r+","+a.c,t.rightForm.r=a.r,t.rightForm.c=a.c,t.rightForm.value=null==e?"":e.v,t.rightForm.autoIsShow=!0,null==e||void 0!=e.v&&-1!==e.v.indexOf("#{")?t.rightForm.auto=!1:(t.rightForm.autoIsShow=!0,null!=e.auto&&"1"==e.auto?t.rightForm.auto=!0:t.rightForm.auto=!1)}},data:[{name:"report",color:"",index:0,status:1,order:0,hide:0,row:36,column:18,defaultRowHeight:19,defaultColWidth:73,celldata:[],config:{merge:{},rowlen:{},columnlen:{},rowhidden:{},colhidden:{},borderInfo:{},authority:{}},scrollLeft:0,scrollTop:315,luckysheet_select_save:[],calcChain:[],isPivotTable:!1,pivotTable:{},filter_select:{},filter:null,luckysheet_alternateformat_save:[],luckysheet_alternateformat_save_modelCustom:[],luckysheet_conditionformat_save:{},frozen:{},chart:[],zoomRatio:1,image:[],showGridLines:1,dataVerification:{}}]};e.data=this.sheetData,$(function(){luckysheet.create(e)})},onStart:function(t,e){this.setCode=t;var a=e.item.innerText;this.draggableFieldLabel="#{"+this.setCode+"."+a+"}",console.log("evt",e),console.log("draggableFieldLabel",this.draggableFieldLabel)},loadDataSet:function(){var t=this;return c()(i.a.mark(function e(){var a,r,o;return i.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:return e.next=2,Object(d.e)();case 2:if(a=e.sent,r=a.code,o=a.data,t.dataSetData=o,"200"==r){e.next=8;break}return e.abrupt("return");case 8:case"end":return e.stop()}},e,t)}))()},doCancel:function(){this.pop=!1},save:function(){var t=this;return c()(i.a.mark(function e(){var a,r,n,l,c,s;return i.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:for(a=luckysheet.getAllSheets(),r=0;r<a.length;r++)a[r].data=[];if(t.reportExcelDto.jsonStr=o()(luckysheet.getAllSheets()),n=[],l={},t.dataSet.forEach(function(t){if(n.push(t.setCode),null!=t.dataSetParamDtoList&&t.dataSetParamDtoList.length>0){var e={};t.dataSetParamDtoList.forEach(function(t){e[t.paramName]=t.sampleItem}),l[t.setCode]=e}}),t.reportExcelDto.setParam=o()(l),t.reportExcelDto.setCodes=n.join("|"),t.reportExcelDto.reportCode=t.reportCode,null!=t.reportId){e.next=19;break}return e.next=12,Object(h.b)(t.reportExcelDto);case 12:if(c=e.sent,"200"==c.code){e.next=16;break}return e.abrupt("return");case 16:t.$message.success("保存成功"),e.next=27;break;case 19:return t.reportExcelDto.id=t.reportId,e.next=22,Object(h.e)(t.reportExcelDto);case 22:if(s=e.sent,"200"==s.code){e.next=26;break}return e.abrupt("return");case 26:t.$message.success("更新成功");case 27:case"end":return e.stop()}},e,t)}))()},detailByReportCode:function(t){var e=this;return c()(i.a.mark(function a(){var r,o;return i.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,Object(d.b)(t);case 2:r=a.sent,r.code,null!=(o=r.data)&&(e.reportId=o.id);case 6:case"end":return a.stop()}},a,e)}))()},preview:function(){var t=this;return c()(i.a.mark(function e(){var a;return i.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:a=t.$router.resolve({path:"/excelreport/viewer",query:{reportCode:t.reportCode}}),window.open(a.href,"_blank");case 2:case"end":return e.stop()}},e,t)}))()},queryAllDataSet:function(){var t=this;return c()(i.a.mark(function e(){return i.a.wrap(function(e){for(;;)switch(e.prev=e.next){case 0:t.outerVisible=!0;case 1:case"end":return e.stop()}},e,t)}))()},handleSelectionChange:function(t){this.multipleSelection=t},checkDataSet:function(){this.outerVisible=!1,this.multipleSelection.length>1?(this.$message({message:"一次最多勾选一个数据集",type:"warning"}),this.outerVisible=!0):this.detail(this.multipleSelection[0].id)},detail:function(t){var e=this;return c()(i.a.mark(function a(){var r,o,n,l;return i.a.wrap(function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,Object(d.a)(t);case 2:if(r=a.sent,o=r.code,n=r.data,200==o){a.next=7;break}return a.abrupt("return");case 7:l=!0,e.dataSet.forEach(function(t){t.setCode===n.setCode&&(l=!1)}),l&&e.dataSet.push(n);case 10:case"end":return a.stop()}},a,e)}))()},del:function(t){for(var e=0;e<this.dataSet.length;e++)this.dataSet[e].setCode===t.setCode&&this.dataSet.splice(e,1)},autoChangeFunc:function(t){t?luckysheet.setCellValue(this.rightForm.r,this.rightForm.c,{auto:"1"}):luckysheet.setCellValue(this.rightForm.r,this.rightForm.c,{auto:"0"})}}},b=(a("qkZ5"),a("1V7f"),Object(p.a)(g,function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"layout"},[a("div",{staticClass:"layout-left"},[a("div",{staticClass:"add-collection"},[t._v("\n      数据集管理\n      "),a("el-button",{attrs:{type:"text",icon:"el-icon-circle-plus-outline"},on:{click:function(e){return t.queryAllDataSet()}}})],1),t._v(" "),a("div",t._l(t.dataSet,function(e,r){return a("el-collapse",{key:r,on:{change:t.handleChange},model:{value:t.activeNames,callback:function(e){t.activeNames=e},expression:"activeNames"}},[a("el-collapse-item",{attrs:{title:e.setName,name:e.id}},[a("el-popconfirm",{attrs:{title:"确定删除"+e.setName+"吗？"},on:{confirm:function(a){return t.del(e)}}},[a("el-button",{staticClass:"delect-all",attrs:{slot:"reference",type:"text",icon:"el-icon-close"},slot:"reference"})],1),t._v(" "),a("draggable",{staticStyle:{"margin-left":"10px"},attrs:{sort:!1,group:"people"},on:{start:function(a){return t.onStart(e.setCode,a)}},model:{value:e.setParamList,callback:function(a){t.$set(e,"setParamList",a)},expression:"item.setParamList"}},t._l(e.setParamList,function(e,r){return a("div",{key:r,staticClass:"field-container"},[a("div",{staticClass:"aRow"},[a("span",[t._v(t._s(e))])])])}),0)],1)],1)}),1)]),t._v(" "),a("div",{staticClass:"layout-middle"},[a("div",{staticClass:"push_btn"},[a("el-tooltip",{staticClass:"item",attrs:{effect:"dark",content:"预览",placement:"bottom-start"}},[a("el-button",{attrs:{type:"text"},on:{click:function(e){return t.preview()}}},[a("i",{staticClass:"iconfont iconfuzhi"})])],1),t._v(" "),a("el-tooltip",{staticClass:"item",attrs:{effect:"dark",content:"保存",placement:"bottom-start"}},[a("el-button",{attrs:{type:"text"},on:{click:function(e){return t.save()}}},[a("i",{staticClass:"iconfont iconsave"})])],1)],1),t._v(" "),a("div",{staticStyle:{margin:"0px",padding:"0px",position:"absolute",width:"100%",height:"95vh",left:"0px",top:"30px",bottom:"0px"},attrs:{id:"luckysheet"}}),t._v(" "),a("div",{ref:"qrCodeDiv",attrs:{id:"qrCode"}}),t._v(" "),a("img",{attrs:{id:"barCode"}})]),t._v(" "),a("div",{staticClass:"layout-right"},[a("el-tabs",{on:{"tab-click":t.handleClick},model:{value:t.activeName,callback:function(e){t.activeName=e},expression:"activeName"}},[a("el-tab-pane",{attrs:{label:"基础配置",name:"first"}},[a("el-form",{ref:"rightForm",attrs:{model:t.rightForm,"label-width":"70px"}},[a("el-form-item",{attrs:{label:"坐标"}},[a("el-input",{model:{value:t.rightForm.coordinate,callback:function(e){t.$set(t.rightForm,"coordinate",e)},expression:"rightForm.coordinate"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"值"}},[a("el-input",{model:{value:t.rightForm.value,callback:function(e){t.$set(t.rightForm,"value",e)},expression:"rightForm.value"}})],1),t._v(" "),t.rightForm.autoIsShow?a("el-form-item",{attrs:{label:"自动扩展"}},[a("el-col",{attrs:{span:12}},[a("el-switch",{on:{change:function(e){return t.autoChangeFunc(e)}},model:{value:t.rightForm.auto,callback:function(e){t.$set(t.rightForm,"auto",e)},expression:"rightForm.auto"}})],1),t._v(" "),a("el-col",{attrs:{span:12}},[a("el-tooltip",{staticClass:"item",attrs:{effect:"dark",content:"只针对静态数据的单元格，具体参考文档",placement:"top"}},[a("i",{staticClass:"el-icon-question"})])],1)],1):t._e()],1)],1)],1)],1),t._v(" "),a("el-dialog",{attrs:{title:"数据集管理",visible:t.outerVisible},on:{"update:visible":function(e){t.outerVisible=e}}},[a("el-table",{ref:"multipleTable",staticStyle:{width:"100%",height:"60vh","overflow-y":"scroll"},attrs:{data:t.dataSetData,"tooltip-effect":"dark"},on:{"selection-change":t.handleSelectionChange}},[a("el-table-column",{attrs:{type:"selection",width:"55"}}),t._v(" "),a("el-table-column",{attrs:{label:"数据集名称",width:"120",prop:"setName"}}),t._v(" "),a("el-table-column",{attrs:{prop:"setDesc",label:"数据集描述",width:"180"}}),t._v(" "),a("el-table-column",{attrs:{prop:"setCode",label:"数据集编码","show-overflow-tooltip":""}})],1),t._v(" "),a("div",{staticClass:"dialog-footer",attrs:{slot:"footer"},slot:"footer"},[a("el-button",{on:{click:function(e){t.outerVisible=!1}}},[t._v("取 消")]),t._v(" "),a("el-button",{attrs:{type:"primary"},on:{click:function(e){return t.checkDataSet()}}},[t._v("确定 ")])],1)],1)],1)},[],!1,null,"1e4bd887",null));b.options.__file="index.vue";e.default=b.exports},bPU3:function(t,e,a){},qkZ5:function(t,e,a){"use strict";var r=a("FZcH");a.n(r).a},ttk2:function(t,e,a){}}]);