(function (window, undefined) {
    var PopupLogin = Base.getClass('main.component.PopupLogin');

    Base.ready({
        initialize: fInitialize
    });

    function fInitialize() {
        PopupLogin.show({
            listeners: {
                login: function () {
                    alert('登录');
                },
                register: function () {
                    alert('注册');
                }
            }
        });
    }

})(window);