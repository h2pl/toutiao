(function (window, undefined) {
    var Util = Base.createClass('main.base.Util');
    $.extend(Util, {
        isEmail: fIsEmail
    });

    function fIsEmail(sEmail) {
        sEmail = $.trim(sEmail);
        return sEmail && /^([a-zA-Z0-9_\.\-\+])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/.test(sEmail);
    }
})(window);