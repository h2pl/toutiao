(function (window, undefined) {
    var Action = Base.createClass('main.util.Action');
    $.extend(Action, {
        like: fLike,
        dislike: fDislike,
        post: fPost
    });

    /**
     * 喜欢
     * @param   {Object} oConf
     *  @param  {String} oConf.newsId 对象id
     *  @param  {Function} oConf.call 成功回调
     *  @param  {Function} oConf.error 失败回调
     *  @param  {Function} oConf.always 操作的回调
     */
    function fLike(oConf) {
        var that = this;
        that.post({
            url: '/like',
            data: {newsId: oConf.newsId},
            call: oConf.call,
            error: oConf.error,
            always: oConf.always
        });
    }

    /**
     * 不喜欢
     * @param   {Object} oConf
     *  @param  {String} oConf.newsId 对象id
     *  @param  {Function} oConf.call 成功回调
     *  @param  {Function} oConf.error 失败回调
     *  @param  {Function} oConf.always 操作的回调
     */
    function fDislike(oConf) {
        var that = this;
        that.post({
            url: '/dislike',
            data: {newsId: oConf.newsId},
            call: oConf.call,
            error: oConf.error,
            always: oConf.always
        });
    }

    /**
     * 简单的 ajax 请求封装
     * @param   {Object} oConf
     *  @param  {String} oConf.method 请求类型
     *  @param  {String} oConf.url 请求连接
     *  @param  {Object} oConf.data 发送参数
     *  @param  {Function} oConf.call 成功回调
     *  @param  {Function} oConf.error 失败回调
     *  @param  {Function} oConf.always 操作的回调
     */
    function fPost(oConf) {
        var that = this;
        $.ajax({
            method: oConf.method || 'POST',
            url: oConf.url,
            dataType: 'json',
            data: oConf.data
        }).done(function (oResult) {
            var nCode = oResult.code;
            nCode === 0 && oConf.call && oConf.call(oResult);
            nCode !== 0 && oConf.error && oConf.error(oResult);
        }).fail(oConf.error).always(oConf.always);
    }


})(window);