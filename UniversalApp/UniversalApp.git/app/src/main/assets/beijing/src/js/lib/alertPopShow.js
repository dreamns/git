/**
 * Created by hufangqin on 17/4/12.
 */

if (typeof $ === 'function') {
    $(function () {
        var BeAlert = {
            defaultConfig: {
                width: 16,
                height: 5,
                timer: 0,
                type: 'warning',
                showConfirmButton: true,
                showCancelButton: false,
                confirmButtonText: '确认',
                cancelButtonText: '取消'
            },
            html: '<div class="BeAlert_box">' +
            // '<div class="BeAlert_image"></div>' +
            '<div class="BeAlert_title"></div>' +
            '<div class="BeAlert_message"></div>' +
            '<div class="BeAlert_button">' +
            '<button class="BeAlert_cancel"></button>' +
            '<button class="BeAlert_confirm"></button>' +
            '</div>' +
            '</div>',
            overlay: '<div class="BeAlert_overlay"></div>',
            open: function (title, message, callback, o) {
                var opts = {}, that = this;
                $.extend(opts, that.defaultConfig, o);
                $('body').append(that.html).append(that.overlay);
                var box = $('.BeAlert_box');
                box.css({
                    'width': opts.width + 'rem',
                    'min-height': opts.height + 'rem',
                    'margin-left': -(opts.width / 2) + 'rem'
                });
                $('.BeAlert_image').addClass(opts.type);
                title && $('.BeAlert_title').html(title).show(),
                message && $('.BeAlert_message').html(message).show();
                var confirmBtn = $('.BeAlert_confirm'), cancelBtn = $('.BeAlert_cancel');
                opts.showConfirmButton && confirmBtn.text(opts.confirmButtonText).show(),
                opts.showCancelButton && cancelBtn.text(opts.cancelButtonText).show();
                $('.BeAlert_overlay').unbind('click').bind('click', function () {
                    that.close();
                });
                confirmBtn.unbind('click').bind('click', function () {
                    that.close();
                    typeof callback === 'function' && callback(true);
                });
                cancelBtn.unbind('click').bind('click', function () {
                    that.close();
                    typeof callback === 'function' && callback(false);
                });

                var htmlfontsize = parseInt(document.documentElement.style.fontSize) ;
                var h = box.height()/htmlfontsize;
                box.css({
                    'margin-top': -(Math.max(h, opts.height) / 2 + 5) + 'rem'
                });
            },
            close: function () {
                $(".BeAlert_overlay,.BeAlert_box").remove();
            }
        };
        window.alert = function (title, message, callback, opts) {
            BeAlert.open(title, message, callback, opts);
        };
        var _confirm = window.confirm;
        window.confirm = function (title, message, callback, opts) {
            opts = $.extend({type: 'question', showCancelButton: true}, opts);
            if (typeof callback === 'function') {
                BeAlert.open(title, message, callback, opts);
            } else {
                return _confirm(title);
            }
        }
    });
}

function webToast() {
    //默认设置
    var dcfg={
        msg:"提示信息",
        postion:"top",//展示位置，可能值：bottom,top,middle,默认top：是因为在mobile web环境下，输入法在底部会遮挡toast提示框
        time:3000,//展示时间
    };
    //*********************以下为参数处理******************
    var len = arguments.length;
    var arg0 =arguments[0];
    if(arguments.length>0){
        dcfg.msg =arguments[0];
        dcfg.msg=arg0;

        var arg1 =arguments[1];
        var regx = /(bottom|top|middle)/i;
        var numRegx = /[1-9]\d*/;
        if(regx.test(arg1)){
            dcfg.postion=arg1;
        }else if(numRegx.test(arg1)){
            dcfg.time=arg1;
        }

        var arg2 =arguments[2];
        var numRegx = /[1-9]\d*/;
        if(numRegx.test(arg2)){
            dcfg.time=arg2;
        }
    }
//*********************以上为参数处理******************
    var ret = "<div class='web_toast'><div class='cx_mask_transparent'></div>" + dcfg.msg + "</div>";
    if ($(".web_toast").length <= 0) {
        $("body").append(ret);
    } else {//如果页面有web_toast 先清除之前的样式
        $(".web_toast").css("left","");

        ret = "<div class='cx_mask_transparent'></div>" + dcfg.msg;
        $(".web_toast").html(ret);
    }
    var w = $(".web_toast").width(),ww = $(window).width();
    $(".web_toast").fadeIn();
    $(".web_toast").css("left",(ww-w)/2-20);
    if("bottom"==dcfg.postion){
        $(".web_toast").css("bottom",50);
        $(".web_toast").css("top","");//这里为什么要置空，自己琢磨，我就不告诉
    }else if("top"==dcfg.postion){
        $(".web_toast").css("bottom","");
        $(".web_toast").css("top",50);
    }else if("middle"==dcfg.postion){
        $(".web_toast").css("bottom","");
        $(".web_toast").css("top","");
        var h = $(".web_toast").height(),hh = $(window).height();
        $(".web_toast").css("bottom",(hh-h)/2-20);
    }
    setTimeout(function() {
        $(".web_toast").fadeOut();
    }, dcfg.time);
}