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
PLATFORM.headTip = function(msg){
	var headtip = $(".u-headTip");
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
        dateFormat: "yy-mm-dd",
        onClose: function(selectedDate){
            $(to).datepicker("option", "minDate", selectedDate);
        }
    });
    $(to).datepicker({
        dateFormat: "yy-mm-dd",
        onClose: function(selectedDate){
            $(from).datepicker("option", "maxDate", selectedDate);
        }
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

$(function(){
	PLATFORM.popupClose();
	PLATFORM.tab();
});
