;var PLATFORM = PLATFORM || {};

// popup显示
PLATFORM.popupShow = function(dom){
	$(dom).show();
};
// popup隐藏
PLATFORM.popupClose = function(){
	$(".u-popup .btn-close").click(function(){
		$(this).parents(".u-popup").hide()
	});
};
// 顶部错误提示框
PLATFORM.headTip = function(msg,color){
	var headtip = $(".u-headTip");
	if(arguments.length==2){
	    headtip.find("p").css({
	        'background-color':color
	    });
	}else{
	    headtip.find("p").css({
            'background-color':"#b54449"
        });
	}
    headtip.find("p").html(msg);
    headtip.show();
    var timer = setTimeout(function(){
        headtip.hide();
        clearTimeout(timer);
        timer = null;
    }, 2000);
};
// tab
PLATFORM.tab = function(){
	$(".u-tabs .tab").click(function(){
		var $this = $(this),
			parent = $this.parents(".u-tabs"),
			tabs = parent.find(".tab-box .tab"),
			cnts = parent.find(".cnt-box .cnt"),
			idx = tabs.index($this);
		
		tabs.removeClass("sel");
		$this.addClass("sel");
		cnts.hide();
		cnts.eq(idx).show();
	});
};
// 日期选择 年-月-日
PLATFORM.datepickerRange = function(from, to){
    $(from).datepicker({
        dateFormat: "yy-mm-dd"
        /*onClose: function(selectedDate){
            $(to).datepicker("option", "minDate", selectedDate);
        }*/
    });
    $(to).datepicker({
        dateFormat: "yy-mm-dd"
        /*onClose: function(selectedDate){
            $(from).datepicker("option", "maxDate", selectedDate);
        }*/
    });
};
//在这里进行日期格式化操作
Date.prototype.Format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
// 日期选择 年-月-日，具有初始值
PLATFORM.datepickerRangeWithDefault = function(from, to, startdate, enddate ){
    $(from).datepicker({
        dateFormat: "yy-mm-dd",
        defaultDate:startdate
        /*onClose: function(selectedDate){
            $(to).datepicker("option", "minDate", selectedDate);
        }*/
    });
    $(to).datepicker({
        dateFormat: "yy-mm-dd",
        defaultDate:enddate
        /*onClose: function(selectedDate){
            $(from).datepicker("option", "maxDate", selectedDate);
        }*/
    });
};

// 日期选择 年-月-日 时:分:秒
PLATFORM.timepickerRange = function(from, to){
    $(from).datetimepicker({
        showSecond: true,
        timeFormat: 'hh:mm:ss',
        onClose: function(selectedDate){
            $(to).datepicker("option", "minDate", selectedDate);
        }
    });
    $(to).datetimepicker({
        showSecond: true,
        timeFormat: 'hh:mm:ss',
        onClose: function(selectedDate){
            $(from).datepicker("option", "maxDate", selectedDate);
        }
    });
};

// guid
PLATFORM.province = [
    {provinceCode: "110000", name:"北京市"},
    {provinceCode: "120000", name:"天津市"},
    {provinceCode: "130000", name:"河北省"},
    {provinceCode: "140000", name:"山西省"},
    {provinceCode: "150000", name:"内蒙古"},
    {provinceCode: "210000", name:"辽宁省"},
    {provinceCode: "220000", name:"吉林省"},
    {provinceCode: "230000", name:"黑龙江"},
    {provinceCode: "310000", name:"上海市"},
    {provinceCode: "320000", name:"江苏省"},
    {provinceCode: "330000", name:"浙江省"},
    {provinceCode: "340000", name:"安徽省"},
    {provinceCode: "350000", name:"福建省"},
    {provinceCode: "360000", name:"江西省"},
    {provinceCode: "370000", name:"山东省"},
    {provinceCode: "410000", name:"河南省"},
    {provinceCode: "420000", name:"湖北省"},
    {provinceCode: "430000", name:"湖南省"},
    {provinceCode: "440000", name:"广东省"},
    {provinceCode: "450000", name:"广  西"},
    {provinceCode: "460000", name:"海南省"},
    {provinceCode: "500000", name:"重庆市"},
    {provinceCode: "510000", name:"四川省"},
    {provinceCode: "520000", name:"贵州省"},
    {provinceCode: "530000", name:"云南省"},
    {provinceCode: "540000", name:"西  藏"},
    {provinceCode: "610000", name:"陕西省"},
    {provinceCode: "620000", name:"甘肃省"},
    {provinceCode: "630000", name:"青海省"},
    {provinceCode: "640000", name:"宁  夏"},
    {provinceCode: "650000", name:"新  疆"},
    {provinceCode: "710000", name:"台湾省"},
    {provinceCode: "810000", name:"香  港"},
    {provinceCode: "820000", name:"澳  门"}
];

// guid
PLATFORM.guid = function(prefix){
    return prefix + "-" + "xxxxxxxx-xxxx-xxxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0,
            v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
};

// upload
PLATFORM.remote = {
    accessid: "2bhtPEFhSciCZC9w",
    accesskey: "ILTzxETxYGIwVvtA6Zt6Ec5Zc22c2K",
    host: "http://buylll.oss-cn-qingdao.aliyuncs.com"
};
PLATFORM.imgName = "";
PLATFORM.imgUpload = function(container, browseBtn, uploadBtn, single){
    var fileUploaded = null;
    if(single){
        fileUploaded = PLATFORM.fileUploadedSingle;
    }else{
        fileUploaded = PLATFORM.fileUploaded;
    }
    var policyText = {
            "expiration": "2020-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
            "conditions": [
                ["content-length-range", 0, 3072000] // 设置上传文件的大小限制 设置为3M 3072000
            ]
        },
        policyBase64 = Base64.encode(JSON.stringify(policyText)),
        bytes = Crypto.HMAC(Crypto.SHA1, policyBase64, PLATFORM.remote.accesskey, {asBytes: true}),
        signature = Crypto.util.bytesToBase64(bytes);
    
    var uploader = new plupload.Uploader({
        browse_button: browseBtn,
        url: PLATFORM.remote.host,
        runtimes: "html5,flash,html4",
        container: container,
        multi_selection: false,
        max_file_size: "3mb",
        filters: [
            {title: "Image files", extensions: "jpg,gif,png"}
        ],
        flash_swf_url: "js/verdor/plupload/jquery.ui.plupload/Moxie.swf",
        init: {
            PostInit: function(){
                $("#" + uploadBtn).click(function(){
                    uploader.start();
                    return false;
                });
            },
            FilesAdded: function(uploader, files){
                var $uploader = $("#" + uploader.settings.container),
                    file = files[0];
                    
                $uploader.find(".info-box .name").html(file.name).end().find(".info-box .process").html("0%").end().find(".info-box").show();
                
                PLATFORM.imgName = PLATFORM.guid("productmain");
                uploader.setOption("multipart_params", {
                    'Filename': PLATFORM.imgName,
                    'key': PLATFORM.imgName,
                    'policy': policyBase64,
                    'OSSAccessKeyId': PLATFORM.remote.accessid, 
                    'success_action_status' : '200', //让服务端返回200,不然，默认会返回204
                    'signature': signature
                });
            },
            QueueChanged: function(uploader){
                if(uploader.files.length > 1) uploader.files.shift();
            },
            UploadProgress: function(uploader, file){
                var $uploader = $("#" + uploader.settings.container);
                
                $uploader.find(".info-box .process").html(file.percent + "%");
            },
            FileUploaded: function(uploader, file, result){
                fileUploaded(uploader, file, result);
            },
            Error: function(uploader, error){
                alert(error.message);
            }
        }
    });
    
    uploader.init();
};
PLATFORM.fileUploaded = function(uploader, file, result){
    var $uploader = $("#" + uploader.settings.container);
    if(result.status == 200){
//      var img = new Image();
//      img.src = PLATFORM.remote.host + "/" + PLATFORM.imgName;
        
        var imgBox = $("<div class='img-opt'><div class='img-container'><a class='del'>×</a><img src='" + PLATFORM.remote.host + "/" + PLATFORM.imgName + "' /></div></div>");
        $uploader.find(".img-box").append(imgBox);
        $uploader.find(".info-box").hide();
    }else{
        alert("上传失败");
    }
};
PLATFORM.fileUploadedSingle = function(uploader, file, result){
    var $uploader = $("#" + uploader.settings.container);
    if(result.status == 200){
        var img = new Image();
        img.src = PLATFORM.remote.host + "/" + PLATFORM.imgName;
        $uploader.find(".img-box").html(img);
        $uploader.find(".info-box").hide();
    }else{
        alert("上传失败");
    }
};
PLATFORM.delImg = function(){
    $("body").on("click", ".u-uploadImg .img-container .del", function(){
        $(this).parents(".img-opt").remove();
    });
};

PLATFORM.getUrlArgs = function(){
    var args = {},
        query = location.search.substring(1),
        pairs = query.split("&"),
        pos = idx = argname = val = null;
        
    for(var i=0; i<pairs.length; i++){
        pos = pairs[i];
        idx = pos.indexOf("=");
        if(idx == -1) continue;
        argname = pos.substring(0, idx);
        val = decodeURIComponent(pos.substring(idx + 1));
        args[argname] = val;
    }
    
    return args;
};

avalon.filters.orderStatus = function(status){
    var val = "";
    if(status == 101){
        val = "待付款";
    }else if(status == 102){
        val = "待发货";
    }else if(status == 103){
        val = "待收货";
    }else if(status == 104){
        val = "待评价";
    }else if(status == 105){
        val = "退/换货";
    }else if(status == 106){
        val = "已完成";
    }else if(status == 100 || status == 107){
        val = "交易关闭";
    }
    return val;
};

$(function(){
	PLATFORM.popupClose();
//	PLATFORM.tab();
});
