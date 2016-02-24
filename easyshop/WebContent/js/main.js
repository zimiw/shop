/*
 * buylll.com
 * created by Jing
 * 2015-10-01
 */

;var Buylll = Buylll || {};

Buylll.upTop = function(){
    $(".to-top").click(function(){
       $('html, body').animate({scrollTop:0}, 'slow');
    });
};
Buylll.getUrlArgs = function(){
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
Buylll.addShoppingcart = function(id,typeId,number){
    $.ajax({
        type: "post",
        url: DI.addShoppingcart,
        dataType: "json",
        data: {
            productId: Number(id),
            productTypeId: Number(typeId),
            number: number
        },
        success: function(data){
            if(data.status == "sucess"){
                avalon.vmodels.common.header.productNum = data.number;
            }
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.addFavorite = function(id){
    $.ajax({
        type: "post",
        url: DI.addFavorite,
        dataType: "json",
        data: {
            productId: id
        },
        success: function(data){
            if(data.status == "success"){
                alert("收藏成功");
            }
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.modifySelected = function(type, shoppingcartIds){
    $.ajax({
        type: "post",
        url: DI.modifySelected,
        data: {
            shoppingcartIds: shoppingcartIds,
            operate: type
        },
        dataType: "json",
        success: function(data){
            location.href = "balance_writeOrder.html";
        },
        error: function(err){
            console.log(err);
        }
    });
};
Buylll.uploadPreview = function(){
    var _self = this;
    
    _self.getObjectURL = function(file){
        var url = null;
        if(window.createObjectURL != undefined) {
            url = window.createObjectURL(file);
        }else if (window.URL != undefined){
            url = window.URL.createObjectURL(file);
        }else if (window.webkitURL != undefined){
            url = window.webkitURL.createObjectURL(file);
        }
        return url;
    };
    $("input[type='file']").change(function(){
        var showimg = $(this).siblings("img"),
            _this = this;
            
        showimg.attr("src", _self.getObjectURL(_this.files[0]));
    });
};
Buylll.province = [
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

avalon.filters.area = function(country){
    var x = "";
    switch(country){
		case "hk":
		  x = "香港";
		  break;
		case "us":
		  x = "美国";
		  break;
		case "jp":
		  x = "日本";
		  break;
		case "it":
		  x = "意大利";
		  break;
		case "kp":
		  x = "韩国";
		  break;
		case "ch":
		  x = "瑞士";
		  break;
		case "th":
		  x = "泰国";
		  break;
		case "fr":
		  x = "法国";
		  break;
		case "uk":
		  x = "英国";
		  break;
		case "tw":
		  x = "台湾";
		  break;
		case "de":
		  x = "德国";
		  break;
		case "es":
		  x = "西班牙";
		  break;
		case "nl":
		  x = "荷兰";
		  break;
		case "ca":
		  x = "加拿大";
		  break;
		case "ar":
		  x = "阿根廷";
		  break;
		case "ru":
		  x = "俄罗斯";
		  break;
		case "br":
		  x = "巴西";
		  break;
	}
    return x;
};
avalon.filters.bind = function(level){
    if(level == 0){
        return "未绑定";
    }else if(level == 1){
        return "已绑定";
    }
};
avalon.filters.payway = function(way){
    if(way == "upacp_pc"){
        return "银联";
    }else if(way == "alipay_pc_direct"){
        return "支付宝";
    }else if(way == "wx_pub_qr"){
        return "微信";
    }
};
avalon.filters.safe = function(safe){
    if(safe == "低"){
        return "0";
    }else if(safe == "中"){
        return "2";
    }else{
        return "3";
    }
};
avalon.filters.popupSuccessStatus = function(status){
    return status ? "修改成功" : "修改失败";
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
    }
    return val;
};

Buylll.guid = function(prefix){
    return prefix + "-" + "xxxxxxxx-xxxx-xxxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0,
            v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
};
// upload
Buylll.remote = {
    accessid: "2bhtPEFhSciCZC9w",
    accesskey: "ILTzxETxYGIwVvtA6Zt6Ec5Zc22c2K",
    host: "http://buylll.oss-cn-qingdao.aliyuncs.com"
};
Buylll.imgName = "";
Buylll.imgUpload = function(container, browseBtn, uploadBtn, single){
    var fileUploaded = null;
    if(single){
        fileUploaded = Buylll.fileUploadedSingle;
    }else{
        fileUploaded = Buylll.fileUploaded;
    }
    var policyText = {
            "expiration": "2020-01-01T12:00:00.000Z", //设置该Policy的失效时间，超过这个失效时间之后，就没有办法通过这个policy上传文件了
            "conditions": [
                ["content-length-range", 0, 3072000] // 设置上传文件的大小限制 设置为3M 3072000
            ]
        },
        policyBase64 = Base64.encode(JSON.stringify(policyText)),
        bytes = Crypto.HMAC(Crypto.SHA1, policyBase64, Buylll.remote.accesskey, {asBytes: true}),
        signature = Crypto.util.bytesToBase64(bytes);
    
    var uploader = new plupload.Uploader({
        browse_button: browseBtn,
        url: Buylll.remote.host,
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
                
                Buylll.imgName = Buylll.guid("avatar");
                uploader.setOption("multipart_params", {
                    'Filename': Buylll.imgName,
                    'key': Buylll.imgName,
                    'policy': policyBase64,
                    'OSSAccessKeyId': Buylll.remote.accessid, 
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
Buylll.fileUploaded = function(uploader, file, result){
    var $uploader = $("#" + uploader.settings.container);
    if(result.status == 200){
        var img = new Image();
        img.src = Buylll.remote.host + "/" + Buylll.imgName;
        $uploader.find(".img-box").append(img);
        $uploader.find(".info-box").hide();
    }else{
        alert("上传失败");
    }
};
Buylll.fileUploadedSingle = function(uploader, file, result){
    var $uploader = $("#" + uploader.settings.container);
    if(result.status == 200){
        var img = new Image();
        img.src = Buylll.remote.host + "/" + Buylll.imgName;
        $uploader.find(".img-box").html(img);
        $uploader.find(".info-box").hide();
    }else{
        alert("上传失败");
    }
};

$(function(){
    Buylll.upTop();
    
    $("body").on("keyup", ".ipt_num", function(e){
        var reg = /^(48|49|50|51|52|53|54|55|56|57|96|97|98|99|100|101|102|103|104|105)$/;
        if(!reg.test(e.keyCode)) console.log("error");
    });
    
    var commonCtrl = avalon.define({
        $id: "common",
        header: {
            logged: false,
            uname: "",
            productNum: 0,
            keyword: "",
            keywords: [],
            search: function(){
                location.href = "list.html?brand=false&name=" + commonCtrl.header.keyword;
            },
            quit: function(){
                location.href = DI.logout;
            }
        },
        navigation: [],
        initHeader: function(){
            $.ajax({
                type: "post",
                url: DI.getHotdictByStatus,
                data: {
                    status: true
                },
                dataType: "json",
                success: function(data){
                   commonCtrl.header.keywords = data;
                },
                error: function(err){
                    console.log(err);
                }
            });
            $.ajax({
          	    type:"post",
        	    dataType:"json",
        	    url:DI.getLogedUser,
        	    success:function(data){
        		    if(data.status=="success"){
                      	commonCtrl.header.logged = true;
                        commonCtrl.header.uname = data.name; 
        		    }
        	    }
            });
//          $.ajax({
//        	    type:"post",
//      	    dataType:"json",
//      	    url:DI.getLogedUser,
//      	    success:function(data){
//      		    if(data.status=="success"){
//                    	commonCtrl.header.logged = true;
//                      commonCtrl.header.uname = data.name; 
//      		    }
//      	    }
//          });
        },
        initNav: function(){
            $.ajax({
                type: "post",
                url: DI.getItems,
                dataType: "json",
                success: function(data){
            	    commonCtrl.navigation = data.items;
                },
                error: function(err){
                    console.log(err);
                }
            });
        }
    });
    avalon.scan();
});
