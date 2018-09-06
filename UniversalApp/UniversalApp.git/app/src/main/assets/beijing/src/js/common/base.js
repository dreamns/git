/**
 * Created by hufangqin on 17/4/11.
 */

//导航栏返回按钮
function clickReturnIcon (backUrl ) {
    //     $(".returnIcon").delegate(".returnIcon", 'click',function () {
    //         window.location = backUrl;
    //     });
    $('.g-navLeft').click(function () {
        window.location= backUrl;
    });
};

//获取url参数
function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) return decodeURI(r[2]);
    return null;
}

/* progress组件  */
function ProgressUI($ele,percent){
    $ele.html('<div class="progressbar"><div class="pro-colorpart">' +
        '<div class="pro-icon"></div>' +
        '</div></div>'
        // + '<div class="pro-text"></div>'
    );
    // this.$e = $ele;
    this.$colorpart = $ele.children('.progressbar').children('.pro-colorpart');
    this.$protext = $ele.children('.pro-text');

    if (percent >= 0) {
        this.$colorpart.css("width", String(percent) + "%"); //控制#loading div宽度

        var location = percent> 96 ? 96: percent;
        this.$protext.css("margin-left", String(location-4) + "%"); //显示百分比位置
        this.$protext.html(String(percent) + "%"); //显示百分比
    }
}

/* segment组件  */
function SegmentUi($ele){
    this.$e = $ele;

    this.list = this.$e.find('li');
    this.count = this.list.length;
    var width = 100.0/this.count + '%';
    this.list.css('width', width);;
    // this.$e.delegate('li', 'click', function(event){
    //
    // });

}
SegmentUi.prototype.init = function(_val, _text, fn){
    var _self = this;
    _self.list.removeClass('chosed');
    $(this).addClass('chosed');
    this.$e.find('li[data-id=' +_val +']').addClass('chosed');

    // this.$e.find('.intoval').val(_val);
    var text = _text || this.$e.find('li[data-id=' + _val + ']').html();
    // this.$e.find('.intotext').val(text);
    if(fn){
        fn(_val, text);
    }
};

SegmentUi.prototype.bindE = function(callback){
    var _self = this;

    this.$e.delegate('li', 'click', function(event){
        var _ev = event || window.event;
        if(_ev.stopPropagation){
            _ev.stopPropagation();
        }else{
            _ev.cancelBubble = true;
        }
        var $target = $(this), _val = $target.attr('data-id'), _text = $target.html();
        _self.list.removeClass('chosed');
        $(this).addClass('chosed');

        if(callback){
            callback(_val, _text);
        }
    })

};
