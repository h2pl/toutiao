(function (window, undefined) {
    var Base = window.Base = fCreateClass('main.base.Base');
    $.extend(Base, {
        ready: fReady,
        tpl: fTpl,
        bind: fBind,
        createClass: fCreateClass,
        getClass: fGetClass,
        mix: fMix,
        inherit: fInherit
    });

    // 类型判断
    var aType = ['Array', 'Object', 'Function', 'String', 'Number', 'RegExp'];
    for (var i = 0, l = aType.length; i < l; i++) {
        (function (sName) {
            Base['is' + sName] = function (obj) {
                return Object.prototype.toString.call(obj) === '[object ' + sName + ']';
            };
        })(aType[i]);
    }

    function fReady(sName, oParam) {
        var that = this;
        var oSpecialMap = {'document': document, 'body': document.body, 'window': window};
        // 调整参数
        if (arguments.length === 1) {
            oParam = sName;
            sName = 'page' + (new Date().getTime()) + (Math.random());
        }
        // 每个页面的脚本作为一个对象处理
        var oClass = that.createClass('JSAction.' + sName);
        $.extend(oClass, oParam);
        $(function () {
            oClass.initialize.call(oClass);
            // 绑定的事件
            $.each(oClass.binds, function (sEventName, sCbName) {
                var aMatch = sEventName.match(/^(\S+)\s*(.*)$/);
                var sEvent = aMatch[1];
                var sSelector = aMatch[2];
                // 兼容字符串和函数回调
                if (that.isString(sCbName)) {
                    sCbName = oClass[sCbName];
                }
                // 绑定事件
                $(oSpecialMap[sSelector] || sSelector).on(sEvent, function (oEvent) {
                    sCbName.call(oClass, oEvent);
                });
            });
            // 代理的事件
            $.each(oClass.events, function (sCbName, sEventName) {
                var aMatch = sEventName.match(/^(\S+)\s*(.*)$/);
                var sEvent = aMatch[1];
                var sSelector = aMatch[2];
                // 兼容字符串和函数回调
                if (that.isString(sCbName)) {
                    sCbName = oClass[sCbName];
                }
                // 绑定事件
                $(document).on(sEvent, sSelector, function (oEvent) {
                    sCbName.call(oClass, oEvent);
                });
            });
        });
        return oClass;
    }

    function fTpl(sTpl, oData) {
        var that = this;
        sTpl = $.trim(sTpl);
        return sTpl.replace(/#{(.*?)}/g, function (sStr, sName) {
            return oData[sName] === undefined || oData[sName] === null ? '' : oData[sName];
        });
    }

    function fBind(f, oTarget) {
        var aArgs = [].slice.call(arguments, 2);
        return function () {
            var aCallArgs = aArgs.concat([].slice.call(arguments, 0));
            var oResult = f.apply(oTarget, aCallArgs);
            aCallArgs.length = 0;
            return oResult;
        };
    }

    function fCreateClass(sPackage, sClassName) {
        var Class = function () {
            var that = this.constructor === Class ? this : arguments.callee;
            if (that.initialize) {
                return that.initialize.apply(that, arguments);
            }
        };
        if (arguments.length === 0) {
            return Class;
        }

        var oParent;
        if (arguments.length === 2 && typeof sPackage !== 'string') {
            oParent = _fFixParent(sPackage);
            oParent[sClassName] = Class;
        } else {
            var sNamespace = sClassName ? (sPackage + '.' + sClassName) : sPackage;
            oParent = window;
            var aName = sNamespace.split('.');
            for (var i = 0, l = aName.length; i < l; i++) {
                var sName = aName[i];
                if (i + 1 === l) {
                    if (typeof oParent[sName] === 'function') {
                        Class = oParent[sName];
                    } else {
                        oParent[sName] = Class;
                    }
                } else {
                    oParent[sName] = _fFixParent(oParent[sName]);
                    oParent = oParent[sName];
                }
            }
        }
        return Class;

        function _fFixParent(oParent) {
            var sType = typeof oParent;
            if (sType === 'undefined') {
                oParent = {};
            } else if (sType === 'number' || sType === 'string' || sType === 'boolean') {
                oParent = new oParent.constructor(oParent);
            }
            return oParent;
        }
    }

    function fGetClass(sPackage, sClassName) {
        var Class;
        try {
            var sNamespace = sClassName ? (sPackage + '.' + sClassName) : sPackage;
            var aName = sNamespace.split('.');
            var oParent = window;

            for (var i = 0, l = aName.length; i < l; i++) {
                var sName = aName[i];
                if (i + 1 === l) {
                    Class = oParent[sName];
                } else {
                    oParent = oParent[sName];
                }
            }
            if (!Class) {
                throw new Error('找不到类:' + sNamespace);
            }
            return Class;
        } catch (e) {
            throw e;
        }
    }

    function fMix(oChild, oParent, oExtend, oExtendPrototype) {
        var that = this;
        if (!oChild || !oParent) {
            return;
        }
        oChild.superClass = oChild.superClass || {};
        $.each(oParent, function (sKey, oVal) {
            if (that.isFunction(oVal)) {
                if (!oChild.superClass[sKey]) {
                    oChild.superClass[sKey] = oVal;
                } else {
                    /* jshint ignore:start */
                    var _function = oChild.superClass[sKey];
                    oChild.superClass[sKey] = function (_property, fFunc) {
                        return function () {
                            fFunc.apply(this, arguments);
                            oParent[_property].apply(this, arguments);
                        };
                    }(sKey, _function);
                    /* jshint ignore:end */
                }
            } else {
                oChild.superClass[sKey] = oVal;
            }
            oChild[sKey] = oChild[sKey] || oVal;
        });

        oExtend && $.extend(oChild, oExtend);
        if (oParent.toString != oParent.constructor.prototype.toString) {
            oChild.superClass.toString = function () {
                oParent.toString.apply(oChild, arguments);
            };
        }
        oExtendPrototype && oChild.prototype && oParent.prototype && that.inherit(oChild, oParent, oExtendPrototype);
        return oChild;
    }

    function fInherit(oChild, oParent, oExtend) {
        var Inheritance = function() {};
        Inheritance.prototype = oParent.prototype;
        oChild.prototype = new Inheritance();
        oChild.prototype.constructor = oChild;
        oChild.superConstructor = oParent;
        oChild.superClass = oParent.prototype;
        oParent._onInherit && oParent._onInherit(oChild);
        oExtend && $.extend(oChild.prototype, oExtend);
    }

})(window);