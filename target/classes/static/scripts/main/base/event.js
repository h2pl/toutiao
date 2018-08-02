(function (window, undefined) {
    var Event = Base.createClass('main.base.Event');
    $.extend(Event, {
        on: fOn,
        emit: fEmit,
        unbind: fUnbind,
        unbindAll: fUnbindAll
    });

    function fOn(sName, fCb) {
        var that = this;
        if (!Base.isString(sName) || !Base.isFunction(fCb)) {
            return;
        }

        that._cep = that._cep || {};
        that._cep[sName] = that._cep[sName] || [];
        that._cep[sName].push(fCb);
    }

    function fEmit(sName) {
        var that = this;
        if (!that._cep || !that._cep[sName]) {
            return;
        }

        var aArg = [].slice.call(arguments, 1);
        $.each(that._cep[sName], function (_, fCb) {
            fCb.apply(that, aArg);
        });
    }

    function fUnbind(sName, fCb) {
        var that = this;
        if (!that._cep || !that._cep[sName]) {
            return;
        }

        if (!fCb) {
            that._cep[sName].length = 0;
            delete that._cep[sName];
            return;
        }

        var oPoll = that._cep;
        var aCb = oPoll[sName];
        for (var i = aCb.length - 1; i >= 0; i--) {
            if (aCb[i] === fCb) {
                aCb.splice(i, 1);
            }
        }
        aCb.length === 0 && (delete oPoll[sName]);
    }

    function fUnbindAll() {
        var that = this;
        var oPoll = that._cep;
        $.each(oPoll, function (sKey) {
            delete oPoll[sKey];
        });
    }
})(window);