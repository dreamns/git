/**
 * Created by hufangqin on 17/3/1.
 */
window.mobileUtil = (function(win, doc) {
    var UA = navigator.userAgent,
        isAndroid = /android|adr/gi.test(UA),
        isIos = /iphone|ipod|ipad/gi.test(UA) && !isAndroid,
        isMobile = isAndroid || isIos;

    return {
        isAndroid: isAndroid,
        isIos: isIos,
        isMobile: isMobile,

        isNewsApp: /NewsApp\/[\d\.]+/gi.test(UA),
        isWeixin: /MicroMessenger/gi.test(UA),
        isQQ: /QQ\/\d/gi.test(UA),
        isYixin: /YiXin/gi.test(UA),
        isWeibo: /Weibo/gi.test(UA),
        isTXWeibo: /T(?:X|encent)MicroBlog/gi.test(UA),

        tapEvent: isMobile ? 'tap' : 'click',

        /**
         *
         */
        fixScreen: function() {
            var docEl = doc.documentElement,
                maxwidth = docEl.dataset.mw || 750, // 姣� dpr 鏈€澶ч〉闈㈠搴�
                dpr = isIos ? Math.min(win.devicePixelRatio, 3) : 1,
                scale = 1 / dpr,
                tid;

            docEl.removeAttribute('data-mw');
            docEl.dataset.dpr = dpr;
            metaEl = doc.createElement('meta');
            metaEl.name = 'viewport';
            metaEl.content = fillScale(scale);
            docEl.firstElementChild.appendChild(metaEl);

            var refreshRem = function() {
                var width = docEl.getBoundingClientRect().width;
                if (width / dpr > maxwidth) {
                    width = maxwidth * dpr;
                }
                console.log(width);
                console.log(dpr);
                var rem = width / 18.75;
                docEl.style.fontSize = rem + 'px';
            };

            win.addEventListener('resize', function() {
                clearTimeout(tid);
                tid = setTimeout(refreshRem, 300);
            }, false);
            win.addEventListener('pageshow', function(e) {
                if (e.persisted) {
                    clearTimeout(tid);
                    tid = setTimeout(refreshRem, 300);
                }
            }, false);

            refreshRem();

            function fillScale(scale) {
                return 'initial-scale=' + scale + ',maximum-scale=' + scale + ',minimum-scale=' + scale + ',user-scalable=no';
            }
        }
    };
})(window, document);

mobileUtil.fixScreen();