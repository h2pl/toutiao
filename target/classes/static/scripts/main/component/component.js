(function (window, undefined) {
    var Component = Base.createClass('main.component.Component');
    var Event = Base.getClass('main.base.Event');
    $.extend(Component, {
        _cIndex: 1,
        _domQueue: [],
        _tpl: '<div></div>',
        setEvents: fStaticSetEvents
    });

    $.extend(Component.prototype, Event, {
        initialize: fInitialize,
        render: fRender,
        getEl: fGetEl,
        html: fHtml,
        getData: fGetData,
        // 禁止滚动
        forbidScroll: fForbidScroll,
        // 重写emit
        emit: fEmit,
        // 内部方法
        _setCustomEvent: _fSetCustomEvent,
        _setDomEvent: _fSetDomEvent
    });

    function fStaticSetEvents() {
        var that = this;
        var aQueue = Component._domQueue;
        var oQueue;
        while (aQueue.length) {
            oQueue = aQueue.shift();
            oQueue._setDomEvent();
            oQueue.emit('render');
        }
    }

    function fInitialize(oConf) {
        var that = this;
        that.rawConfig = oConf;
        that.domId = 'jsCpn' + (Component._cIndex++);
        that._setCustomEvent();
        Component._domQueue.push(that);
        oConf.renderTo && that.render();
    }

    function fRender() {
        var that = this;
        var oConf = that.rawConfig;
        var oRenderTo = $(oConf.renderTo);
        var sRenderBy = oConf.renderBy || 'append';
        var oEl = that.getEl();
        oRenderTo[sRenderBy](oEl);
        that._setDomEvent();
        that.emit('render');
    }

    function fGetEl() {
        var that = this;
        if (that.$el) {
            return that.$el;
        }
        var oEl = $('#' + that.domId);
        if (oEl.get(0)) {
            that.$el = oEl;
            return oEl;
        }

        var sHtml = that.html();
        that.$el = $(sHtml);
        return that.$el;
    }

    function fHtml() {
        var that = this;
        var oConf = that.rawConfig;
        var oConstructor = that.constructor;
        var sTpl = oConstructor._tpl || Component._tpl;
        var oData = that.getData(that.rawConfig);
        var sHtml = Base.tpl(sTpl, oData);
        // id 和 class
        /* jshint ignore:start */
        sHtml = sHtml.replace(/^(\<\w+)([ \>])/, '$1' + ' id="' + that.domId + '"$2');
        /* jshint ignore:end */
        sHtml = sHtml.replace('class="', 'class="' + (oConf.cls || '') + ' ');
        return sHtml;
    }

    function fGetData(oConf) {
        return oConf;
    }

    function fForbidScroll(oEl, bForbid) {
        $(oEl).css('overflow', bForbid === false ? 'auto' : 'hidden');
    }

    function fEmit(sName) {
        var that = this;
        if (sName === 'render') {
            if (that.rendered) {
                return;
            }
            that.rendered = true;
        }
        Event.emit.apply(that, arguments);
    }

    function _fSetCustomEvent() {
        var that = this;
        if (that._setedCustomEvent) {
            return;
        }
        that._setedCustomEvent = true;
        var oConf = that.rawConfig;
        var oConstructor = that.constructor;
        $.each(oConstructor.listeners, function (_, oEvent) {
            oEvent.type === 'custom' && oEvent.name && oEvent.handler && that.on(oEvent.name, oEvent.handler);
        });
        $.each(oConf.listeners, function (sName, fCb) {
            Base.isFunction(fCb) && that.on(sName, fCb);
        });
    }

    function _fSetDomEvent() {
        var that = this;
        if (that._setedDomEvent) {
            return;
        }
        that._setedDomEvent = true;
        var oConf = that.rawConfig;
        var oEl = that.getEl();
        var oConstructor = that.constructor;
        // 构造器上的事件
        $.each(oConstructor.listeners, function (_, oEvent) {
            oEvent.type !== 'custom' && _fBind(oEvent.name, oEvent);
        });
        // 配置上面的事件
        $.each(oConf.listeners, function (sName, oEvent) {
            Base.isObject(oEvent) && _fBind(sName, oEvent);
        });
        // 删除dom事件队列
        for (var i = Component._domQueue.length - 1; i >= 0; i--) {
            if (Component._domQueue[i] === that) {
                Component._domQueue.splice(i, 1);
            }
        }
        function _fBind(sName, oEvent) {
            var aMatch = sName.match(/^(\S+)\s*(.*)$/);
            var sEvent = $.trim(aMatch[1]);
            var sSelector = $.trim(aMatch[2]);
            var fHandler = oEvent.handler;
            if (Base.isFunction(fHandler)) {
                if (sSelector) {
                    oEvent.type === 'bind' && oEl.find(sSelector).on(sEvent, Base.bind(fHandler, that));
                    oEvent.type !== 'bind' && oEl.on(sEvent, sSelector, Base.bind(fHandler, that));
                } else {
                    oEl.on(sEvent, Base.bind(fHandler, that));
                }
            }
        }
    }

})(window);