/**
var oPopup = new Popup({
   title: String, 标题
   content: String, 内容
   width: Number, 宽度
   close: Function, 关闭的回调
   hasNoHeader: Boolean, true 没有头部
});
 */
(function (window, undefined) {
    var Popup = Base.createClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    Base.mix(Popup, Component, {
        zIndex: 100,
        _tpl: [
            '<div class="pop-box">',
                '<div class="pop-title">',
                    '<a href="javascript:void(0);" class="pop-close js-close" title="关闭"></a>',
                    '<h1>#{title}</h1>',
                '</div>',
                '<div class="pop-content">#{content}</div>',
            '</div>'].join(''),
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                var that = this;
                var oConf = that.rawConfig;
                var oEl = that.getEl();
                // 常用元素
                that.contentEl = oEl.find('div.pop-content');
                // 调整大小
                oEl.outerWidth(oConf.width || 520);
                oConf.height && that.contentEl.outerHeight(oConf.height);
                // 禁止body滚动
                that.forbidScroll(document.body);
                // 创建遮罩层
                that.initMask();
                // 调整z-index
                oEl.css('zIndex', Popup.zIndex++);
                // 去掉头部
                oConf.hasNoHeader && oEl.find('div.pop-title').remove();
                // 位置居中
                that.fixPosition();
                // 绑定窗口变化事件
                that.resizeCb = Base.bind(that.fixPosition, that);
                $(window).resize(that.resizeCb);
            }
        }, {
            name: 'click .js-close',
            handler: function () {
                var that = this;
                that.close();
            }
        }]
    }, {
        initialize: fInitialize,
        initMask: fInitMask,
        fixPosition: fFixPosition,
        close: fClose,
        getData: fGetData
    });

    function fInitialize(oConf) {
        var that = this;
        var oBody = $(document.body);
        oConf.renderTo = oBody;
        that.isForbidScroll = oBody.css('overflow-y') === 'hidden';
        Popup.superClass.initialize.apply(that, arguments);
    }

    function fInitMask() {
        var that = this;
        var oConf = that.rawConfig;
        if (!that.maskEl) {
            that.maskEl = $('<div class="masklayer" style="z-index:' + (Popup.zIndex++) + '"></div>');
            oConf.renderTo.append(that.maskEl);
        }
    }

    function fFixPosition() {
        var that = this;
        var oEl = that.getEl();
        var oWin = $(window);
        var oDoc = $(document);
        var nElWidth = oEl.width();
        var nElHeight = oEl.height();
        var nWinWidth = oWin.width();
        var nWinHeight = oWin.height();
        var nScrollTop = Math.max(oWin.scrollTop() || oDoc.scrollTop());
        // 调整元素大小
        oEl.css({
            left: nWinWidth > nElWidth ? (nWinWidth - nElWidth) / 2 : 0,
            top: (nWinHeight > nElHeight ? (nWinHeight - nElHeight) / 2 : 0) + nScrollTop
        });
        // 调整遮罩层大小
        that.maskEl.css({
            width: '100%',
            height: nWinHeight,
            top: nScrollTop
        });
    }

    function fClose(bNoEmit) {
        var that = this;
        // 移除文件
        var oEl = that.getEl();
        oEl.remove();
        // 启动滚动
        !that.isForbidScroll && that.forbidScroll(document.body, false);
        // 移除遮罩层
        that.maskEl && that.maskEl.remove();
        // 取消窗口变化事件
        $(window).unbind('resize', that.resizeCb);
        !bNoEmit && that.emit('close');
    }

    function fGetData(oConf) {
        var that = this;
        return {
            title: oConf.title || '提示',
            content: oConf.content
        };
    }

})(window);