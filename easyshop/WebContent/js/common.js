;var storeUED = storeUED || {};

storeUED.cropper = null;
//图片裁剪
storeUED.cropPreview = function(){
    storeUED.cropper = new Cropper({
        aspectRatio: 'auto',
        element: document.getElementById('cropper-target'),
        previews: [
          document.getElementById('preview-large')
        ],
        onCroppedRectChange: function(rect) {
            $("#preview-x").val(rect.left);
            $("#preview-y").val(rect.top);
            $("#preview-width").val(rect.width);
            $("#preview-height").val(rect.height);            
        }
    });
    var input = document.getElementById('cropper-input');
        input.onchange = function() {
            if (typeof FileReader !== 'undefined') {
                var reader = new FileReader();
                reader.onload = function (event) {
                    storeUED.cropper.setImage(event.target.result);
                };
                if (input.files && input.files[0]) {
                    reader.readAsDataURL(input.files[0]);
                }
            } else { // IE10-
              input.select();
              input.blur();
              var src = document.selection.createRange().text;
              storeUED.cropper.setImage(src);
            }
        };
}
// 日期选择 年-月-日
storeUED.datepickerRange = function(from, to){
    $(from).datepicker({
        dateFormat: "yy-mm-dd",
        minDate:new Date(),
        /*onClose: function(selectedDate){
            $(to).datepicker("option", "minDate", selectedDate);
        }*/ 
            });
    $(to).datepicker({
        dateFormat: "yy-mm-dd",
        /*onClose: function(selectedDate){
            $(from).datepicker("option", "maxDate", selectedDate);
        }*/
    });
};

//获取地址栏参数
storeUED.GetQueryString=function(name)
{
     var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
     var r = window.location.search.substr(1).match(reg);
     if(r!=null)return  unescape(r[2]); return null;
}
