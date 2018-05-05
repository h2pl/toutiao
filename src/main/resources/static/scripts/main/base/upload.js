(function (window, undefined) {
    var Upload = Base.createClass('main.base.Upload');
    $.extend(Upload, {
        support: fSupport,
        uploadFile: fUploadFile,
        uploadBlob: fUploadBlob,
        uploadFormData: fUploadFormData,
        parseJSON: fParseJSON
    });

    function fSupport() {
        return !!window.FormData && !!window.XMLHttpRequest && !!window.JSON && !!window.addEventListener;
    }

    function fUploadFile(oFile, oConf) {
        var that = this;
        var oData = new FormData();
        oData.append(oConf.name || 'file', oFile);
        that.uploadFormData(oData, oConf);
    }

    function fUploadBlob(oBlob, sFileName, oConf) {
        var that = this;
        var oData = new FormData();
        oData.append(oConf.name || 'file', oBlob, sFileName);
        that.uploadFormData(oData, oConf);
    }

    /**
     * 上传数据
     * @param   {Object} oData FormData 对象
     * @param   {Object} oConf 配置参数
     *  @param  {String} oConf.name 上传数据的name
     *  @param  {Object} oConf.data 其他额外需要发送给服务器的数据
     *  @param  {Function} oConf.progress 上传进度回调
     *  @param  {Function} oConf.call 上传成功回调
     *  @param  {Function} oConf.error 上传失败回调
     */
    function fUploadFormData(oData, oConf) {
        var that = this;
        var XMLHttpRequest = window.XMLHttpRequest;
        $.each(oConf.data, function (sKey, sVal) {
            sKey && sVal && oData.append(sKey, sVal);
        });
        var oXhr = new XMLHttpRequest();
        // 进度
        oXhr.upload.addEventListener('progress', function(oEvent) {
            oConf.progress && oConf.progress.call(null, Math.round(oEvent.loaded * 100 / (oEvent.total || 1)), oEvent.loaded, oEvent.total);
        }, false);
        // 结果
        oXhr.onreadystatechange = function(oEvent) {
            if (oXhr.readyState !== 4) {
                return;
            }

            if (oXhr.status == 200) {
                var oResult = that.parseJSON(oXhr.responseText);
                var fCb = oConf[oResult.code === 0 ? 'call' : 'error'];
                fCb && fCb.call(null, oResult);
            } else {
                oConf.error && oConf.error.call(that, {msg: '出现错误，请重试'});
            }
        };
        oXhr.open('POST', oConf.url, true);
        oXhr.send(oData);
    }

    function fParseJSON(sStr) {
        var oResult = {};
        try {
            oResult = JSON.parse(sStr);
        } catch (e) {}
        return oResult;
    }
})(window);