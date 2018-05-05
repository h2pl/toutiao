(function (window, undefined) {
    var PopupLogin = Base.getClass('main.component.PopupLogin');
    var PopupUpload = Base.getClass('main.component.PopupUpload');

    Base.ready({
        initialize: fInitialize,
        binds: {
            //.表示class #表示id
            'click .js-login': fClickLogin,
            'click .js-share': fClickShare
        }
    });

    function fInitialize() {
        if (window.loginpop > 0) {
            fClickLogin();
        }
    }
    function fClickShare() {
        var that = this;
            PopupUpload.show({
                listeners: {
                    done: function () {
                        //alert('login');
                        window.location.reload();
                    }
                }
            });
    }
    function fClickLogin() {
        var that = this;
        PopupLogin.show({
            listeners: {
                login: function () {
                    //alert('login');
                    window.location.reload();
                },
                register: function () {
                    //alert('reg');
                    window.location.reload();
                }
            }
        });
    }

})(window);