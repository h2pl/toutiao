/**
var oUpload = new Upload({
    targetEl: Object, 上传按钮
    name: String, 文件的name
    url: String, 上传路径
    data: Object, 发送的参数
    check: Function, 文件检查
    call: Function, 上传成功回调
    error: Function, 上传失败回调
    progress: Function, 上传进度回调
});
 */
(function (window, undefined) {
    var Upload = Base.createClass('main.component.Upload');
    var BaseUpload = Base.getClass('main.base.Upload');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');

    Base.mix(Upload, Component, {
        _tpl: '<div style="position:absolute;"><input class="js-upload" type="file" style="display:block;opacity:0;cursor:pointer;" /></div>',
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                var that = this;
                var oEl = that.getEl();
                that.fileEl = oEl.find('input.js-upload');
                that.initTarget();
                that.initInput();
            }
        }]
    }, {
        initialize: fInitialize,
        initTarget: fInitTarget,
        initInput: fInitInput,
        clear: fClear
    });

    function fInitialize(oConf) {
        var that = this;
        var oTargetEl = $(oConf.targetEl);
        oTargetEl.css('position', 'relative');
        oConf.renderTo = oTargetEl;
        Upload.superClass.initialize.apply(that, arguments);
    }

    function fInitTarget() {
        var that = this;
        var oConf = that.rawConfig;
        var oEl = that.getEl();
        var oTargetEl = $(oConf.targetEl);
        var nWidth = oTargetEl.outerWidth();
        var nHeight = oTargetEl.outerHeight();
        // 位置
        oEl.css({top:0, left: 0, width: nWidth, height: nHeight});
        // 调整大小
        that.fileEl.outerWidth(nWidth);
        that.fileEl.outerHeight(nHeight);
    }

    function fInitInput() {
        var that = this;
        var oConf = that.rawConfig;
        var oIpt = that.fileEl;
        oIpt.on('change', function (oEvent) {
            var aFiles = oIpt.get(0).files;
            var oFile = aFiles[0];
            if (!oFile) {
                return;
            }
            var sType = oFile.type;
            var nFileSize = oFile.size;
            // 检查是否能上传
            if (oConf.check && !oConf.check.call(that, oFile, sType, nFileSize)) {
                that.clear();
                return;
            }
            // 上传文件
            var oPopup;
            BaseUpload.uploadFile(oFile, {
                name: oConf.name,
                url: oConf.url,
                data: oConf.data,
                call: function () {
                    oPopup && oPopup.close();
                    oConf.call && oConf.call.apply(that, arguments);
                },
                error: function () {
                    oPopup && oPopup.close();
                    alert('出现错误，请重试');
                    oConf.error && oConf.error.apply(that, arguments);
                },
                progress: function (nProgress) {
                    if (!oPopup) {
                        oPopup = new Popup({
                            content: '<div class="js-progress" style="text-align:center;font-size:14px;padding:20px 0;">正在上传：' + nProgress + '%<div>',
                            hasNoHeader: true
                        });
                    }
                    oPopup.getEl().find('div.js-progress').html('正在上传：' + nProgress + '%');
                    oConf.progress && oConf.progress.apply(that, arguments);
                }
            });
            that.clear();
        });

        // 进入提示条
        var oPopup;
        new BaseUpload({
            input: that.fileEl,
            check: oConf.check,
            url: oConf.url,
            name: oConf.name,
            data: oConf.data,
            call: function () {
                oPopup && oPopup.close();
                oConf.call && oConf.call.apply(that, arguments);
            },
            error: function () {
                oPopup && oPopup.close();
                alert('出现错误，请重试');
                oConf.error && oConf.error.apply(that, arguments);
            },
            progress: function (nProgress) {
                if (!oPopup) {
                    oPopup = new Popup({
                        content: '<div class="js-progress" style="text-align:center;font-size:14px;padding:20px 0;">正在上传：' + nProgress + '%<div>',
                        hasNoHeader: true
                    });
                }
                oPopup.getEl().find('div.js-progress').html('正在上传：' + nProgress + '%');
                oConf.progress && oConf.progress.apply(that, arguments);
            }
        });
    }

    function fClear() {
        var that = this;
        that.fileEl.val('');
    }

})(window);